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
  RefreshCw,
  CheckCircle2,
  AlertCircle,
  Share2,
  Plus,
  Loader2,
  Sparkles,
} from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { api, OpportunityDetail, ReminderItem } from "@/lib/api-client";

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
        <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
        <p className="text-xs text-slate-500 font-medium">Loading opportunity details...</p>
      </div>
    );
  }

  if (error || !opp) {
    return (
      <div className="p-6 bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-900 rounded-2xl text-center space-y-3">
        <AlertCircle className="w-8 h-8 text-red-600 mx-auto" />
        <p className="text-xs text-red-700 dark:text-red-300 font-medium">{error || "Opportunity not found"}</p>
        <Link
          href="/app"
          className="inline-flex items-center space-x-2 text-xs font-bold text-blue-600 underline"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Feed</span>
        </Link>
      </div>
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
          className="p-2 -ml-2 rounded-xl text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white flex items-center space-x-1 text-xs font-semibold"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back</span>
        </Link>

        <div className="flex items-center space-x-1">
          <button
            onClick={handleArchive}
            disabled={updating}
            className="p-2 rounded-xl text-slate-500 hover:text-slate-900 dark:hover:text-white transition-colors"
            title={opp.isArchived ? "Restore" : "Archive"}
          >
            <Archive className="w-4 h-4" />
          </button>
          <button
            onClick={handleDelete}
            disabled={updating}
            className="p-2 rounded-xl text-red-500 hover:text-red-700 transition-colors"
            title="Delete"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Main Details Card */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-sm space-y-4">
        {/* Category & Status Row */}
        <div className="flex items-center justify-between flex-wrap gap-2">
          <span className="px-2.5 py-1 rounded-md text-[10px] font-black uppercase tracking-wider bg-blue-50 text-blue-700 dark:bg-blue-950 dark:text-blue-300 border border-blue-200 dark:border-blue-800">
            {opp.category || "GENERAL"}
          </span>

          {/* Status Selector */}
          <select
            value={opp.status}
            onChange={(e) => handleStatusChange(e.target.value)}
            disabled={updating}
            className="text-xs font-bold px-2.5 py-1 rounded-lg border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-600"
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
          <h1 className="text-lg font-black text-slate-900 dark:text-white leading-snug">
            {opp.title}
          </h1>
          {opp.organization && (
            <p className="text-xs text-slate-500 flex items-center space-x-1.5 mt-1 font-medium">
              <Building2 className="w-3.5 h-3.5" />
              <span>{opp.organization}</span>
            </p>
          )}
        </div>

        {/* Deadline Information */}
        <div
          className={`p-3.5 rounded-xl border flex items-center justify-between ${
            isExpired
              ? "bg-slate-50 dark:bg-slate-800/40 border-slate-200 dark:border-slate-700"
              : "bg-blue-50/50 dark:bg-blue-950/30 border-blue-200/60 dark:border-blue-900/40"
          }`}
        >
          <div className="flex items-center space-x-3">
            <div
              className={`p-2 rounded-lg ${
                isExpired ? "bg-slate-200 dark:bg-slate-700 text-slate-500" : "bg-blue-600 text-white"
              }`}
            >
              <Calendar className="w-4 h-4" />
            </div>
            <div>
              <p className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Deadline</p>
              <p className="text-xs font-black text-slate-900 dark:text-white">
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
            <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-slate-200 text-slate-700 dark:bg-slate-700 dark:text-slate-300">
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
            className="w-full py-2.5 px-3 rounded-xl border border-blue-200 dark:border-blue-900 bg-blue-50/40 dark:bg-blue-950/20 text-blue-700 dark:text-blue-300 font-bold text-xs flex items-center justify-center space-x-2 hover:bg-blue-50 transition-colors"
          >
            <span>Open Original Source</span>
            <ExternalLink className="w-3.5 h-3.5" />
          </a>
        )}

        {/* Notes / Description */}
        {(opp.notes || opp.description) && (
          <div className="pt-3 border-t border-slate-100 dark:border-slate-800">
            <h3 className="text-xs font-bold text-slate-700 dark:text-slate-300 mb-1.5">
              Extracted Notes & Details
            </h3>
            <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed whitespace-pre-wrap">
              {opp.notes || opp.description}
            </p>
          </div>
        )}
      </div>

      {/* Reminders Section */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-sm space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Bell className="w-4 h-4 text-blue-600 dark:text-blue-400" />
            <h2 className="text-xs font-bold text-slate-900 dark:text-white uppercase tracking-wider">
              Smart Reminders
            </h2>
          </div>
          <button
            onClick={() => setShowReminderModal(true)}
            className="inline-flex items-center space-x-1 text-[11px] font-bold text-blue-600 hover:text-blue-700 dark:text-blue-400"
          >
            <Plus className="w-3.5 h-3.5" />
            <span>Add Reminder</span>
          </button>
        </div>

        {reminders.length === 0 ? (
          <p className="text-xs text-slate-500 py-2">
            No reminders scheduled yet. Tap &apos;Add Reminder&apos; to schedule an alert before this deadline.
          </p>
        ) : (
          <div className="space-y-2 pt-1">
            {reminders.map((r) => (
              <div
                key={r.id}
                className="p-3 rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40 flex items-center justify-between text-xs"
              >
                <div>
                  <p className="font-bold text-slate-900 dark:text-white">{r.title}</p>
                  <p className="text-[11px] text-slate-500 mt-0.5">
                    {new Date(r.scheduledTime).toLocaleString(undefined, {
                      month: "short",
                      day: "numeric",
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </p>
                </div>
                <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200">
                  {r.status}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Schedule Reminder Modal */}
      {showReminderModal && (
        <div className="fixed inset-0 z-50 bg-black/50 backdrop-blur-sm flex items-end sm:items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 rounded-2xl p-5 max-w-sm w-full space-y-4 border border-slate-200 dark:border-slate-800 shadow-xl">
            <h3 className="text-sm font-bold text-slate-900 dark:text-white">
              Schedule New Reminder
            </h3>
            <form onSubmit={handleCreateReminder} className="space-y-3">
              <div>
                <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1">
                  Reminder Label
                </label>
                <input
                  type="text"
                  value={newReminderTitle}
                  onChange={(e) => setNewReminderTitle(e.target.value)}
                  placeholder="e.g. Submit application draft"
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-700 dark:text-slate-300 mb-1">
                  Alert Date & Time *
                </label>
                <input
                  type="datetime-local"
                  required
                  value={newReminderTime}
                  onChange={(e) => setNewReminderTime(e.target.value)}
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white"
                />
              </div>

              <div className="flex space-x-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowReminderModal(false)}
                  className="flex-1 py-2 rounded-xl border border-slate-200 dark:border-slate-800 text-xs font-bold text-slate-700 dark:text-slate-300"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={creatingReminder}
                  className="flex-1 py-2 rounded-xl bg-blue-600 text-white text-xs font-bold shadow-md shadow-blue-500/20 disabled:opacity-50"
                >
                  {creatingReminder ? "Saving..." : "Set Reminder"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
