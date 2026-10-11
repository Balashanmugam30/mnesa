"use client";

import React, { useEffect, useState, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
  Bell,
  Clock,
  CheckCircle2,
  AlertCircle,
  RefreshCw,
  Calendar,
  Layers,
  ChevronRight,
  Loader2,
} from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { api, ReminderItem } from "@/lib/api-client";

export default function RemindersPage() {
  const router = useRouter();
  const { isAuthenticated, isLoading: authLoading } = useAuth();

  const [reminders, setReminders] = useState<ReminderItem[]>([]);
  const [filter, setFilter] = useState("upcoming");
  const [loading, setLoading] = useState(true);
  const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const fetchReminders = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await api.listReminders(filter);
      setReminders(res.content || []);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Failed to load reminders";
      setError(msg);
    } finally {
      setLoading(false);
    }
  }, [filter]);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push("/app/login");
      return;
    }
    if (isAuthenticated) {
      fetchReminders();
    }
  }, [isAuthenticated, authLoading, router, fetchReminders]);

  const handleSnooze = async (id: string, minutes: number) => {
    setActionLoadingId(id);
    try {
      await api.snoozeReminder(id, minutes);
      await fetchReminders();
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Failed to snooze reminder";
      alert(msg);
    } finally {
      setActionLoadingId(null);
    }
  };

  const handleDismiss = async (id: string) => {
    setActionLoadingId(id);
    try {
      await api.dismissReminder(id);
      await fetchReminders();
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Failed to dismiss reminder";
      alert(msg);
    } finally {
      setActionLoadingId(null);
    }
  };

  return (
    <div className="space-y-4">
      {/* Title */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-lg font-black text-slate-900 dark:text-white flex items-center space-x-2">
            <Bell className="w-5 h-5 text-blue-600 dark:text-blue-400" />
            <span>Reminders</span>
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Never miss upcoming deadlines, rounds, or application milestones.
          </p>
        </div>
        <button
          onClick={() => fetchReminders()}
          className="p-2 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-400"
          aria-label="Refresh reminders"
        >
          <RefreshCw className="w-4 h-4" />
        </button>
      </div>

      {/* Filter Tabs */}
      <div className="flex space-x-1.5 text-xs">
        {[
          { label: "Upcoming", id: "upcoming" },
          { label: "All Reminders", id: "all" },
          { label: "Snoozed", id: "snoozed" },
          { label: "Dismissed", id: "completed" },
        ].map((tab) => (
          <button
            key={tab.id}
            onClick={() => setFilter(tab.id)}
            className={`px-3 py-1.5 rounded-full font-semibold transition-all text-[11px] ${
              filter === tab.id
                ? "bg-blue-600 text-white shadow-sm"
                : "bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-400 border border-slate-200 dark:border-slate-800"
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Reminders List */}
      {loading ? (
        <div className="space-y-3 pt-2">
          {[1, 2].map((n) => (
            <div
              key={n}
              className="bg-white dark:bg-slate-900 p-4 rounded-2xl border border-slate-200 dark:border-slate-800 animate-pulse space-y-3"
            >
              <div className="h-4 bg-slate-200 dark:bg-slate-800 rounded w-2/3" />
              <div className="h-3 bg-slate-100 dark:bg-slate-800 rounded w-1/3" />
            </div>
          ))}
        </div>
      ) : error ? (
        <div className="bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-900 rounded-2xl p-4 text-center space-y-2">
          <p className="text-xs text-red-700 dark:text-red-300 font-medium">{error}</p>
          <button
            onClick={() => fetchReminders()}
            className="text-xs font-bold text-red-600 underline"
          >
            Retry
          </button>
        </div>
      ) : reminders.length === 0 ? (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-8 text-center space-y-3 shadow-sm">
          <div className="w-12 h-12 bg-blue-50 dark:bg-blue-950 text-blue-600 dark:text-blue-400 rounded-full flex items-center justify-center mx-auto">
            <CheckCircle2 className="w-6 h-6" />
          </div>
          <div>
            <h3 className="font-bold text-sm text-slate-900 dark:text-white">
              No reminders in this view
            </h3>
            <p className="text-xs text-slate-500 mt-1 max-w-xs mx-auto">
              Schedule reminders on any tracked opportunity to receive alerts.
            </p>
          </div>
          <Link
            href="/app"
            className="inline-flex items-center space-x-1.5 bg-blue-600 text-white px-3.5 py-2 rounded-xl text-xs font-bold"
          >
            <Layers className="w-3.5 h-3.5" />
            <span>View Opportunities</span>
          </Link>
        </div>
      ) : (
        <div className="space-y-3">
          {reminders.map((r) => {
            const isActing = actionLoadingId === r.id;
            const timeDate = new Date(r.scheduledTime);
            return (
              <div
                key={r.id}
                className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm space-y-3"
              >
                <div className="flex items-start justify-between">
                  <div>
                    <h3 className="text-sm font-bold text-slate-900 dark:text-white">{r.title}</h3>
                    {r.opportunityTitle && (
                      <Link
                        href={`/app/opportunities/${r.opportunityId}`}
                        className="text-xs text-blue-600 dark:text-blue-400 font-medium hover:underline flex items-center space-x-1 mt-0.5"
                      >
                        <span>{r.opportunityTitle}</span>
                        <ChevronRight className="w-3 h-3" />
                      </Link>
                    )}
                  </div>
                  <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-blue-50 text-blue-700 dark:bg-blue-950 dark:text-blue-300">
                    {r.status}
                  </span>
                </div>

                <div className="flex items-center space-x-1.5 text-xs text-slate-500">
                  <Clock className="w-3.5 h-3.5 text-slate-400" />
                  <span>
                    {timeDate.toLocaleString(undefined, {
                      weekday: "short",
                      month: "short",
                      day: "numeric",
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </span>
                </div>

                {/* Snooze / Dismiss Controls */}
                <div className="flex items-center space-x-2 pt-2 border-t border-slate-100 dark:border-slate-800 text-xs">
                  <button
                    onClick={() => handleSnooze(r.id, 60)}
                    disabled={isActing}
                    className="flex-1 py-1.5 rounded-lg border border-slate-200 dark:border-slate-700 text-slate-700 dark:text-slate-300 font-semibold hover:bg-slate-50 transition-colors disabled:opacity-50 text-[11px]"
                  >
                    +1h Snooze
                  </button>
                  <button
                    onClick={() => handleSnooze(r.id, 1440)}
                    disabled={isActing}
                    className="flex-1 py-1.5 rounded-lg border border-slate-200 dark:border-slate-700 text-slate-700 dark:text-slate-300 font-semibold hover:bg-slate-50 transition-colors disabled:opacity-50 text-[11px]"
                  >
                    +1d Snooze
                  </button>
                  <button
                    onClick={() => handleDismiss(r.id)}
                    disabled={isActing}
                    className="flex-1 py-1.5 rounded-lg bg-emerald-50 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300 font-bold hover:bg-emerald-100 transition-colors disabled:opacity-50 text-[11px]"
                  >
                    Dismiss
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
