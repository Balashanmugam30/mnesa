// MNESA Production API Client with cold-start retry and JWT refresh handling

export interface User {
  id: string;
  email: string;
  fullName: string;
  role?: string;
  avatarUrl?: string;
}

export interface AuthResponseData {
  user: User;
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface OpportunitySummary {
  id: string;
  title: string;
  organization?: string;
  category: string;
  status: string;
  priority: string;
  deadlineAt?: string;
  sourceUrl?: string;
  tags?: string[];
  createdAt: string;
  updatedAt: string;
}

export interface OpportunityDetail extends OpportunitySummary {
  description?: string;
  notes?: string;
  isArchived: boolean;
  captureId?: string;
  location?: string;
}

export interface AiCandidate {
  id: string;
  title: string;
  organization: string;
  category: string;
  deadline_at?: string;
  confidence_score: number;
  reasoning?: string;
}

export interface AiExtraction {
  title?: string;
  organization?: string;
  category?: string;
  deadline_at?: string;
  confidence?: number;
  summary?: string;
  evidence?: string[];
  raw_text?: string;
}

export interface IntakeJobStatus {
  job_id: string;
  capture_id: string;
  status: 'RECEIVED' | 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  attempt_count: number;
  error_message?: string;
  extraction?: AiExtraction;
  candidates?: AiCandidate[];
  created_at: string;
  updated_at: string;
}

export interface IntakeSubmitResponse {
  captureId: string;
  jobId: string;
  status: string;
  sourceType: string;
  canonicalUrl?: string;
  message: string;
  duplicate: boolean;
  createdAt: string;
}

export interface ReminderItem {
  id: string;
  opportunityId: string;
  opportunityTitle?: string;
  title: string;
  scheduledTime: string;
  status: 'SCHEDULED' | 'TRIGGERED' | 'SNOOZED' | 'DISMISSED';
  notes?: string;
  urgencyLevel?: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface BackendHealth {
  status: string;
  version: string;
  environment: string;
  components?: Record<string, string>;
}

const DEFAULT_API_URL = 'https://mnesa-backend.onrender.com/api/v1';

class ApiClient {
  private baseUrl: string;
  private onWakingStateChange?: (isWaking: boolean) => void;

  constructor() {
    this.baseUrl = (process.env.NEXT_PUBLIC_API_URL || DEFAULT_API_URL).replace(/\/$/, '');
  }

  public setWakingCallback(callback: (isWaking: boolean) => void) {
    this.onWakingStateChange = callback;
  }

  public getBaseUrl(): string {
    return this.baseUrl;
  }

  public getAccessToken(): string | null {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem('mnesa_access_token');
  }

  public getRefreshToken(): string | null {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem('mnesa_refresh_token');
  }

  public getStoredUser(): User | null {
    if (typeof window === 'undefined') return null;
    const userStr = localStorage.getItem('mnesa_user');
    if (!userStr) return null;
    try {
      return JSON.parse(userStr);
    } catch {
      return null;
    }
  }

  public saveTokens(data: AuthResponseData) {
    if (typeof window === 'undefined') return;
    localStorage.setItem('mnesa_access_token', data.accessToken);
    localStorage.setItem('mnesa_refresh_token', data.refreshToken);
    if (data.user) {
      localStorage.setItem('mnesa_user', JSON.stringify(data.user));
    }
    window.dispatchEvent(new Event('mnesa-auth-changed'));
  }

  public clearTokens() {
    if (typeof window === 'undefined') return;
    localStorage.removeItem('mnesa_access_token');
    localStorage.removeItem('mnesa_refresh_token');
    localStorage.removeItem('mnesa_user');
    window.dispatchEvent(new Event('mnesa-auth-changed'));
  }

  private generateCorrelationId(): string {
    return 'pwa-' + Math.random().toString(36).substring(2, 10) + '-' + Date.now();
  }

  public async request<T>(
    endpoint: string,
    options: RequestInit = {},
    retryCount = 0
  ): Promise<T> {
    const url = `${this.baseUrl}${endpoint.startsWith('/') ? endpoint : `/${endpoint}`}`;
    const token = this.getAccessToken();

    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      'X-Correlation-ID': this.generateCorrelationId(),
      ...(options.headers as Record<string, string>),
    };

    if (token && !headers['Authorization']) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    try {
      const response = await fetch(url, {
        ...options,
        headers,
      });

      // Cold start / sleeping Render instance handler (502, 503, 504)
      if ([502, 503, 504].includes(response.status) && retryCount < 3) {
        if (this.onWakingStateChange) this.onWakingStateChange(true);
        const backoffMs = (retryCount + 1) * 3500;
        await new Promise((resolve) => setTimeout(resolve, backoffMs));
        return this.request<T>(endpoint, options, retryCount + 1);
      }

      // If token expired, attempt refresh once
      if (response.status === 401 && retryCount === 0 && !endpoint.includes('/auth/')) {
        const refreshed = await this.tryRefreshToken();
        if (refreshed) {
          return this.request<T>(endpoint, options, 1);
        } else {
          this.clearTokens();
          throw new Error('Session expired. Please sign in again.');
        }
      }

      if (this.onWakingStateChange) this.onWakingStateChange(false);

      if (!response.ok) {
        const errorJson = await response.json().catch(() => null);
        const message =
          errorJson?.message ||
          errorJson?.detail ||
          `Request failed with status ${response.status}`;
        throw new Error(message);
      }

      const json = await response.json();
      return (json.data !== undefined ? json.data : json) as T;
    } catch (err: unknown) {
      const isNetworkError =
        err instanceof TypeError ||
        (err instanceof Error &&
          (err.message.includes('fetch') || err.message.includes('network') || err.message.includes('Failed')));

      // Auto-retry network errors during potential Render cold-start
      if (isNetworkError && retryCount < 3) {
        if (this.onWakingStateChange) this.onWakingStateChange(true);
        const backoffMs = (retryCount + 1) * 4000;
        await new Promise((resolve) => setTimeout(resolve, backoffMs));
        return this.request<T>(endpoint, options, retryCount + 1);
      }

      if (this.onWakingStateChange) this.onWakingStateChange(false);
      throw err;
    }
  }

  private async tryRefreshToken(): Promise<boolean> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) return false;

    try {
      const res = await fetch(`${this.baseUrl}/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
      });
      if (res.ok) {
        const json = await res.json();
        const data = json.data as AuthResponseData;
        if (data && data.accessToken) {
          this.saveTokens(data);
          return true;
        }
      }
    } catch {
      // Ignore refresh network errors
    }
    return false;
  }

  // --- Auth endpoints ---

  public async login(email: string, password: string): Promise<AuthResponseData> {
    const data = await this.request<AuthResponseData>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    });
    this.saveTokens(data);
    return data;
  }

  public async register(email: string, password: string, fullName: string): Promise<AuthResponseData> {
    const data = await this.request<AuthResponseData>('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ email, password, fullName }),
    });
    this.saveTokens(data);
    return data;
  }

  public async logout(): Promise<void> {
    const refreshToken = this.getRefreshToken();
    try {
      await this.request('/auth/logout', {
        method: 'POST',
        body: JSON.stringify({ refreshToken }),
      });
    } catch {
      // Ignore logout errors
    } finally {
      this.clearTokens();
    }
  }

  // --- Health probe ---

  public async checkHealth(): Promise<BackendHealth> {
    return this.request<BackendHealth>('/health');
  }

  // --- Opportunities ---

  public async listOpportunities(params?: {
    q?: string;
    category?: string;
    status?: string;
    priority?: string;
    page?: number;
    size?: number;
    sort?: string;
  }): Promise<PageResponse<OpportunitySummary>> {
    const query = new URLSearchParams();
    if (params?.q) query.set('q', params.q);
    if (params?.category && params.category !== 'ALL') query.set('category', params.category);
    if (params?.status && params.status !== 'ALL') query.set('status', params.status);
    if (params?.priority && params.priority !== 'ALL') query.set('priority', params.priority);
    if (params?.page !== undefined) query.set('page', params.page.toString());
    if (params?.size !== undefined) query.set('size', params.size.toString());
    if (params?.sort) query.set('sort', params.sort);

    const queryString = query.toString() ? `?${query.toString()}` : '';
    return this.request<PageResponse<OpportunitySummary>>(`/opportunities${queryString}`);
  }

  public async getOpportunity(id: string): Promise<OpportunityDetail> {
    return this.request<OpportunityDetail>(`/opportunities/${id}`);
  }

  public async updateOpportunity(id: string, updates: Partial<OpportunityDetail>): Promise<OpportunityDetail> {
    return this.request<OpportunityDetail>(`/opportunities/${id}`, {
      method: 'PATCH',
      body: JSON.stringify(updates),
    });
  }

  public async updateStatus(id: string, status: string, reason?: string): Promise<OpportunityDetail> {
    return this.request<OpportunityDetail>(`/opportunities/${id}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status, reason }),
    });
  }

  public async archiveOpportunity(id: string): Promise<OpportunityDetail> {
    return this.request<OpportunityDetail>(`/opportunities/${id}/archive`, {
      method: 'POST',
    });
  }

  public async restoreOpportunity(id: string): Promise<OpportunityDetail> {
    return this.request<OpportunityDetail>(`/opportunities/${id}/restore`, {
      method: 'POST',
    });
  }

  public async deleteOpportunity(id: string): Promise<void> {
    return this.request<void>(`/opportunities/${id}`, {
      method: 'DELETE',
    });
  }

  // --- Intake & Processing ---

  public async submitIntake(data: {
    text?: string;
    url?: string;
    sourceType?: string;
    idempotencyKey?: string;
  }): Promise<IntakeSubmitResponse> {
    const idempotencyKey =
      data.idempotencyKey ||
      'pwa-capture-' + Date.now() + '-' + Math.random().toString(36).substring(2, 8);

    return this.request<IntakeSubmitResponse>('/intake', {
      method: 'POST',
      body: JSON.stringify({
        idempotencyKey,
        text: data.text,
        url: data.url,
        sourceType: data.sourceType || (data.url ? 'URL' : 'TEXT'),
      }),
    });
  }

  public async getIntakeJob(jobId: string): Promise<IntakeJobStatus> {
    return this.request<IntakeJobStatus>(`/intake/jobs/${jobId}`);
  }

  public async confirmIntakeJob(
    jobId: string,
    data: {
      title?: string;
      organization?: string;
      category?: string;
      deadline_at?: string;
      candidate_id?: string;
      notes?: string;
    }
  ): Promise<OpportunityDetail> {
    return this.request<OpportunityDetail>(`/intake/jobs/${jobId}/confirm`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  // --- Reminders ---

  public async listReminders(filter: string = 'upcoming'): Promise<PageResponse<ReminderItem>> {
    return this.request<PageResponse<ReminderItem>>(`/reminders?filter=${encodeURIComponent(filter)}`);
  }

  public async getOpportunityReminders(opportunityId: string): Promise<ReminderItem[]> {
    return this.request<ReminderItem[]>(`/opportunities/${opportunityId}/reminders`);
  }

  public async createReminder(
    opportunityId: string,
    data: {
      scheduledTime: string;
      title?: string;
      notes?: string;
    }
  ): Promise<ReminderItem> {
    return this.request<ReminderItem>(`/opportunities/${opportunityId}/reminders`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  public async snoozeReminder(id: string, minutes: number = 60): Promise<ReminderItem> {
    return this.request<ReminderItem>(`/reminders/${id}/snooze`, {
      method: 'POST',
      body: JSON.stringify({ snoozeDurationMinutes: minutes }),
    });
  }

  public async dismissReminder(id: string): Promise<ReminderItem> {
    return this.request<ReminderItem>(`/reminders/${id}/dismiss`, {
      method: 'POST',
    });
  }
}

export const api = new ApiClient();
