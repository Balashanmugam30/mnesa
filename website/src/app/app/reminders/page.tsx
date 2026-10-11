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
import {
  GlassCard,
  GlassBadge,
  GlassButton,
} from "@/components/glass";

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
          <h1 className="text-lg font-black text-slate-900 dark:text-white flex items-center space-x-2 tracking-tight">
            <Bell className="w-5 h-5 text-blue-600 dark:text-blue-400" />
            <span>Reminders</span>
          </h1>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5 font-medium">
            Never miss upcoming deadlines, application rounds, or milestones.
          </p>
        </div>
        <button
          onClick={() => fetchReminders()}
          className="liquid-glass-btn-secondary w-9 h-9 rounded-2xl flex items-center justify-center text-slate-600 dark:text-slate-300 active:scale-95 transition-transform"
          aria-label="Refresh reminders"
        >
          <RefreshCw className="w-4 h-4" />
        </button>
      </div>

      {/* Filter Tabs */}
      <div className="flex space-x-2 text-xs overflow-x-auto pb-1 scrollbar-none">
        {[
          { label: "Upcoming", id: "upcoming" },
          { label: "All Reminders", id: "all" },
          { label: "Snoozed", id: "snoozed" },
          { label: "Dismissed", id: "completed" },
        ].map((tab) => (
          <button
            key={tab.id}
            onClick={() => setFilter(tab.id)}
            className={`px-3.5 py-1.5 rounded-full font-bold transition-all text-[11px] whitespace-nowrap tracking-tight ${
              filter === tab.id
                ? "bg-gradient-to-r from-blue-600 to-indigo-600 text-white shadow-md shadow-blue-500/25 border border-white/30"
                : "liquid-glass-pill bg-white/60 dark:bg-slate-900/60 text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white"
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Reminders List */}
      {loading ? (
        <div className="space-y-3 pt-1">
          {[1, 2].map((n) => (
            <GlassCard key={n} className="animate-pulse space-y-3">
              <div className="h-4 bg-slate-200/60 dark:bg-slate-700/60 rounded-lg w-2/3" />
              <div className="h-3 bg-slate-100/60 dark:bg-slate-800/60 rounded-lg w-1/3" />
            </GlassCard>
          ))}
        </div>
      ) : error ? (
        <GlassCard className="p-4 text-center space-y-2 !border-red-400/40">
          <p className="text-xs text-red-600 dark:text-red-400 font-semibold">{error}</p>
          <button
            onClick={() => fetchReminders()}
            className="text-xs font-bold text-red-600 dark:text-red-400 underline underline-offset-2"
          >
            Retry Connection
          </button>
        </GlassCard>
      ) : reminders.length === 0 ? (
        <GlassCard className="p-8 text-center space-y-4">
          <div className="w-14 h-14 bg-gradient-to-tr from-blue-500/20 to-indigo-500/20 text-blue-600 dark:text-blue-400 rounded-3xl flex items-center justify-center mx-auto border border-white/40 shadow-inner">
            <CheckCircle2 className="w-7 h-7" />
          </div>
          <div>
            <h3 className="font-extrabold text-base text-slate-900 dark:text-white tracking-tight">
              No reminders in this view
            </h3>
            <p className="text-xs text-slate-500 dark:text-slate-400 mt-1 max-w-xs mx-auto leading-relaxed">
              Schedule smart reminders on any tracked opportunity to receive alerts ahead of time.
            </p>
          </div>
          <Link href="/app" className="inline-block">
            <GlassButton variant="primary" size="md" leftIcon={<Layers className="w-4 h-4" />}>
              View Opportunities
            </GlassButton>
          </Link>
        </GlassCard>
      ) : (
        <div className="space-y-3">
          {reminders.map((r) => {
            const isActing = actionLoadingId === r.id;
            const timeDate = new Date(r.scheduledTime);
            return (
              <GlassCard key={r.id} className="space-y-3">
                <div className="flex items-start justify-between gap-2">
                  <div className="flex-1 min-w-0">
                    <h3 className="text-sm font-bold text-slate-900 dark:text-white truncate tracking-tight">
                      {r.title}
                    </h3>
                    {r.opportunityTitle && (
                      <Link
                        href={`/app/opportunities/${r.opportunityId}`}
                        className="text-xs text-blue-600 dark:text-blue-400 font-semibold hover:underline flex items-center space-x-1 mt-0.5"
                      >
                        <span className="truncate">{r.opportunityTitle}</span>
                        <ChevronRight className="w-3.5 h-3.5 shrink-0" />
                      </Link>
                    )}
                  </div>
                  <GlassBadge variant="blue" size="xs">
                    {r.status}
                  </GlassBadge>
                </div>

                <div className="flex items-center space-x-1.5 text-xs text-slate-500 dark:text-slate-400 font-medium">
                  <Clock className="w-3.5 h-3.5 opacity-70" />
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
                <div className="flex items-center space-x-2 pt-2.5 border-t border-slate-200/50 dark:border-white/10 text-xs">
                  <button
                    onClick={() => handleSnooze(r.id, 60)}
                    disabled={isActing}
                    className="flex-1 py-1.5 rounded-xl liquid-glass-btn-secondary text-slate-700 dark:text-slate-300 font-bold disabled:opacity-50 text-[11px]"
                  >
                    +1h Snooze
                  </button>
                  <button
                    onClick={() => handleSnooze(r.id, 1440)}
                    disabled={isActing}
                    className="flex-1 py-1.5 rounded-xl liquid-glass-btn-secondary text-slate-700 dark:text-slate-300 font-bold disabled:opacity-50 text-[11px]"
                  >
                    +1d Snooze
                  </button>
                  <button
                    onClick={() => handleDismiss(r.id)}
                    disabled={isActing}
                    className="flex-1 py-1.5 rounded-xl bg-emerald-500/15 border border-emerald-400/30 text-emerald-700 dark:text-emerald-300 font-bold hover:bg-emerald-500/25 transition-colors disabled:opacity-50 text-[11px]"
                  >
                    Dismiss
                  </button>
                </div>
              </GlassCard>
            );
          })}
        </div>
      )}
    </div>
  );
}
