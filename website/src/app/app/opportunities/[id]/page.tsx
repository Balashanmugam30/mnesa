"use client";

import React, { useEffect, useState, use } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
  ArrowLeft,
  Calendar,
  Clock,
  Building2,
  ExternalLink,
  Bell,
  Trash2,
  Archive,
  CheckCircle2,
  AlertCircle,
  Plus,
  Loader2,
  Sparkles,
  X,
} from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { api, OpportunityDetail, ReminderItem } from "@/lib/api-client";
import {
  GlassCard,
  GlassButton,
  GlassBadge,
  GlassSurface,
} from "@/components/glass";

export default function OpportunityDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const resolvedParams = use(params);
  const id = resolvedParams.id;
  const router = useRouter();
  const { isAuthenticated, isLoading: authLoading } = useAuth();

  const [opp, setOpp] = useState<OpportunityDetail | null>(null);
  const [reminders, setReminders] = useState<ReminderItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // New reminder modal state
  const [showReminderModal, setShowReminderModal] = useState(false);
  const [newReminderTime, setNewReminderTime] = useState("");
  const [newReminderTitle, setNewReminderTitle] = useState("");
  const [creatingReminder, setCreatingReminder] = useState(false);

  const loadData = React.useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.getOpportunity(id);
      setOpp(data);
      try {
        const rems = await api.getOpportunityReminders(id);
        setReminders(rems || []);
      } catch {
        // Reminders might be empty or 404
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Failed to load opportunity";
      setError(msg);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push("/app/login");
      return;
    }
    if (isAuthenticated && id) {
      loadData();
    }
  }, [id, isAuthenticated, authLoading, router, loadData]);

  const handleStatusChange = async (newStatus: string) => {
    if (!opp) return;
    setUpdating(true);
    try {
      const updated = await api.updateStatus(opp.id, newStatus);
      setOpp(updated);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Failed to update status";
      alert(msg);
    } finally {
      setUpdating(false);
    }
  };

  const handleArchive = async () => {
    if (!opp) return;
    setUpdating(true);
    try {
      if (opp.isArchived) {
        const updated = await api.restoreOpportunity(opp.id);
        setOpp(updated);
      } else {
        const updated = await api.archiveOpportunity(opp.id);
        setOpp(updated);
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Failed to update archive status";
      alert(msg);
    } finally {
      setUpdating(false);
    }
  };

  const handleDelete = async () => {
    if (!opp) return;
    if (!confirm("Are you sure you want to permanently delete this opportunity?")) return;
    setUpdating(true);
    try {
      await api.deleteOpportunity(opp.id);
      router.push("/app");
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Failed to delete";
      alert(msg);
      setUpdating(false);
    }
  };

  const handleCreateReminder = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!opp || !newReminderTime) return;
    setCreatingReminder(true);
    try {
      const iso = new Date(newReminderTime).toISOString();
      const rem = await api.createReminder(opp.id, {
        scheduledTime: iso,
        title: newReminderTitle || `Deadline for ${opp.title}`,
      });
      setReminders([...reminders, rem]);
      setShowReminderModal(false);
      setNewReminderTime("");
      setNewReminderTitle("");
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Failed to create reminder";
      alert(msg);
    } finally {
      setCreatingReminder(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-3">
        <Loader2 className="w-8 h-8 animate-spin text-blue-600 dark:text-blue-400" />
        <p className="text-xs text-slate-500 dark:text-slate-400 font-medium">Loading opportunity details...</p>
      </div>
    );
  }

  if (error || !opp) {
    return (
      <GlassCard className="p-6 text-center space-y-3 !border-red-500/30">
        <AlertCircle className="w-8 h-8 text-red-500 mx-auto" />
        <p className="text-xs text-red-600 dark:text-red-400 font-semibold">{error || "Opportunity not found"}</p>
        <Link
          href="/app"
          className="inline-flex items-center space-x-2 text-xs font-bold text-blue-600 dark:text-blue-400 underline"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Feed</span>
        </Link>
      </GlassCard>
    );
  }

  const deadlineDate = opp.deadlineAt ? new Date(opp.deadlineAt) : null;
  const isExpired = deadlineDate ? deadlineDate.getTime() < Date.now() : false;

  return (
    <div className="space-y-4">
      {/* Top Bar with Back & Actions */}
      <div className="flex items-center justify-between">
        <Link
          href="/app"
          className="p-2 -ml-2 rounded-2xl text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white flex items-center space-x-1.5 text-xs font-bold"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back</span>
        </Link>

        <div className="flex items-center space-x-1.5">
          <button
            onClick={handleArchive}
            disabled={updating}
            className="w-9 h-9 rounded-2xl liquid-glass-btn-secondary flex items-center justify-center text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white transition-colors"
            title={opp.isArchived ? "Restore" : "Archive"}
          >
            <Archive className="w-4 h-4" />
          </button>
          <button
            onClick={handleDelete}
            disabled={updating}
            className="w-9 h-9 rounded-2xl bg-red-500/10 border border-red-500/20 flex items-center justify-center text-red-600 dark:text-red-400 hover:bg-red-500/20 transition-colors"
            title="Delete"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Main Details Liquid Glass Card */}
      <GlassCard elevated={true} className="p-5 space-y-4">
        {/* Category & Status Row */}
        <div className="flex items-center justify-between flex-wrap gap-2">
          <GlassBadge variant="blue" size="sm">
            {opp.category || "GENERAL"}
          </GlassBadge>

          {/* Status Selector */}
          <select
            value={opp.status}
            onChange={(e) => handleStatusChange(e.target.value)}
            disabled={updating}
            className="text-xs font-bold px-3 py-1.5 rounded-xl liquid-glass-input text-slate-900 dark:text-white focus:outline-none"
          >
            <option value="NEW">Status: NEW</option>
            <option value="APPLIED">Status: APPLIED</option>
            <option value="INTERVIEW">Status: INTERVIEW</option>
            <option value="OFFER">Status: OFFER</option>
            <option value="REJECTED">Status: REJECTED</option>
            <option value="COMPLETED">Status: COMPLETED</option>
          </select>
        </div>

        {/* Title & Organization */}
        <div>
          <h1 className="text-lg font-black text-slate-900 dark:text-white leading-snug tracking-tight">
            {opp.title}
          </h1>
          {opp.organization && (
            <p className="text-xs text-slate-500 dark:text-slate-400 flex items-center space-x-1.5 mt-1 font-semibold">
              <Building2 className="w-3.5 h-3.5 opacity-70" />
              <span>{opp.organization}</span>
            </p>
          )}
        </div>

        {/* Deadline Information */}
        <div
          className={`p-3.5 rounded-2xl border flex items-center justify-between backdrop-blur-md ${
            isExpired
              ? "bg-slate-500/10 border-slate-500/20 text-slate-400"
              : "bg-blue-500/10 border-blue-400/30 text-blue-900 dark:text-blue-100"
          }`}
        >
          <div className="flex items-center space-x-3">
            <div
              className={`p-2 rounded-xl ${
                isExpired
                  ? "bg-slate-500/20 text-slate-400"
                  : "bg-gradient-to-tr from-blue-600 to-indigo-600 text-white shadow-sm"
              }`}
            >
              <Calendar className="w-4 h-4" />
            </div>
            <div>
              <p className="text-[10px] font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                Deadline
              </p>
              <p className="text-xs font-black text-slate-900 dark:text-white mt-0.5">
                {deadlineDate
                  ? deadlineDate.toLocaleDateString(undefined, {
                      weekday: "short",
                      month: "short",
                      day: "numeric",
                      year: "numeric",
                      hour: "2-digit",
                      minute: "2-digit",
                    })
                  : "No deadline specified"}
              </p>
            </div>
          </div>
          {isExpired && (
            <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-slate-500/20 text-slate-400">
              Expired
            </span>
          )}
        </div>

        {/* External Link */}
        {opp.sourceUrl && (
          <a
            href={opp.sourceUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="w-full py-2.5 px-3.5 rounded-xl liquid-glass-btn-secondary text-blue-600 dark:text-blue-400 font-bold text-xs flex items-center justify-center space-x-2 transition-all"
          >
            <span>Open Original Source Link</span>
            <ExternalLink className="w-3.5 h-3.5" />
          </a>
        )}

        {/* Notes / Description */}
        {(opp.notes || opp.description) && (
          <div className="pt-3 border-t border-slate-200/50 dark:border-white/10">
            <h3 className="text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
              Extracted Notes & Details
            </h3>
            <p className="text-xs text-slate-600 dark:text-slate-300 leading-relaxed whitespace-pre-wrap font-medium">
              {opp.notes || opp.description}
            </p>
          </div>
        )}
      </GlassCard>

      {/* Reminders Section */}
      <GlassCard className="p-5 space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Bell className="w-4 h-4 text-blue-600 dark:text-blue-400" />
            <h2 className="text-xs font-extrabold text-slate-900 dark:text-white uppercase tracking-wider">
              Smart Reminders
            </h2>
          </div>
          <button
            onClick={() => setShowReminderModal(true)}
            className="inline-flex items-center space-x-1 text-[11px] font-bold text-blue-600 dark:text-blue-400"
          >
            <Plus className="w-3.5 h-3.5" />
            <span>Add Reminder</span>
          </button>
        </div>

        {reminders.length === 0 ? (
          <p className="text-xs text-slate-500 dark:text-slate-400 py-2 font-medium">
            No reminders scheduled yet. Tap &apos;Add Reminder&apos; to schedule an alert before this deadline.
          </p>
        ) : (
          <div className="space-y-2 pt-1">
            {reminders.map((r) => (
              <div
                key={r.id}
                className="p-3 rounded-xl bg-slate-500/10 border border-slate-400/20 flex items-center justify-between text-xs backdrop-blur-md"
              >
                <div>
                  <p className="font-bold text-slate-900 dark:text-white">{r.title}</p>
                  <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5 font-medium">
                    {new Date(r.scheduledTime).toLocaleString(undefined, {
                      month: "short",
                      day: "numeric",
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </p>
                </div>
                <GlassBadge variant="blue" size="xs">
                  {r.status}
                </GlassBadge>
              </div>
            ))}
          </div>
        )}
      </GlassCard>

      {/* Schedule Reminder Liquid Glass Modal */}
      {showReminderModal && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-md flex items-end sm:items-center justify-center p-4">
          <GlassSurface
            refract={true}
            variant="surface"
            scale={-112}
            chroma={6}
            mapBlur={12}
            radius={28}
            className="p-5 max-w-sm w-full space-y-4 border border-white/30 shadow-2xl"
          >
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-extrabold text-slate-900 dark:text-white">
                Schedule New Reminder
              </h3>
              <button
                onClick={() => setShowReminderModal(false)}
                className="p-1 rounded-full text-slate-400 hover:text-slate-600 dark:hover:text-slate-200"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleCreateReminder} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Reminder Label
                </label>
                <input
                  type="text"
                  value={newReminderTitle}
                  onChange={(e) => setNewReminderTitle(e.target.value)}
                  placeholder="e.g. Submit application draft"
                  className="w-full px-3 py-2 text-xs rounded-xl liquid-glass-input text-slate-900 dark:text-white focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Alert Date & Time *
                </label>
                <input
                  type="datetime-local"
                  required
                  value={newReminderTime}
                  onChange={(e) => setNewReminderTime(e.target.value)}
                  className="w-full px-3 py-2 text-xs rounded-xl liquid-glass-input text-slate-900 dark:text-white focus:outline-none"
                />
              </div>

              <div className="flex space-x-2 pt-2">
                <GlassButton
                  type="button"
                  variant="secondary"
                  size="sm"
                  onClick={() => setShowReminderModal(false)}
                  className="flex-1"
                >
                  Cancel
                </GlassButton>
                <GlassButton
                  type="submit"
                  variant="primary"
                  size="sm"
                  isLoading={creatingReminder}
                  className="flex-1"
                >
                  Set Reminder
                </GlassButton>
              </div>
            </form>
          </GlassSurface>
        </div>
      )}
    </div>
  );
}
