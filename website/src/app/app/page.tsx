"use client";

import React, { useEffect, useState, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
  Search,
  Clock,
  Sparkles,
  RefreshCw,
  Building2,
  ChevronRight,
  Plus,
  Flame,
  CheckCircle2,
  Layers,
} from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { api, OpportunitySummary } from "@/lib/api-client";
import {
  GlassCard,
  GlassBadge,
  GlassButton,
} from "@/components/glass";

export default function OpportunitiesDashboard() {
  const router = useRouter();
  const { isAuthenticated, isLoading: authLoading } = useAuth();

  const [opportunities, setOpportunities] = useState<OpportunitySummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [activeFilter, setActiveFilter] = useState<string>("ALL");
  const [error, setError] = useState<string | null>(null);

  const fetchOpportunities = useCallback(async (isRefresh = false) => {
    if (isRefresh) setRefreshing(true);
    else setLoading(true);
    setError(null);

    try {
      const res = await api.listOpportunities({
        q: searchQuery.trim() || undefined,
        status: activeFilter === "URGENT" || activeFilter === "ALL" ? undefined : activeFilter,
        size: 50,
      });
      setOpportunities(res.content || []);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Failed to load opportunities";
      setError(msg);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [searchQuery, activeFilter]);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push("/app/login");
      return;
    }

    if (isAuthenticated) {
      fetchOpportunities();
    }
  }, [isAuthenticated, authLoading, router, fetchOpportunities]);

  const getDeadlineInfo = (deadlineStr?: string) => {
    if (!deadlineStr) return { text: "No deadline", isUrgent: false, isExpired: false };
    const deadline = new Date(deadlineStr);
    const now = new Date();
    const diffMs = deadline.getTime() - now.getTime();
    const diffDays = Math.ceil(diffMs / (1000 * 60 * 60 * 24));

    if (diffMs < 0) {
      return { text: "Expired", isUrgent: false, isExpired: true };
    }
    if (diffDays <= 2) {
      return {
        text: diffDays === 0 ? "Today" : diffDays === 1 ? "Tomorrow" : "2 days left",
        isUrgent: true,
        isExpired: false,
      };
    }
    if (diffDays <= 7) {
      return { text: `${diffDays} days left`, isUrgent: true, isExpired: false };
    }
    return { text: `${diffDays} days left`, isUrgent: false, isExpired: false };
  };

  const filteredOpportunities = opportunities.filter((item) => {
    if (activeFilter === "URGENT") {
      const info = getDeadlineInfo(item.deadlineAt);
      return info.isUrgent && !info.isExpired;
    }
    return true;
  });

  const activeCount = opportunities.filter((o) => o.status !== "COMPLETED" && o.status !== "ARCHIVED").length;
  const urgentCount = opportunities.filter((o) => {
    const info = getDeadlineInfo(o.deadlineAt);
    return info.isUrgent && !info.isExpired;
  }).length;
  const completedCount = opportunities.filter((o) => o.status === "COMPLETED").length;

  return (
    <div className="space-y-4">
      {/* Liquid Search Bar & Refresh */}
      <div className="flex items-center space-x-2">
        <div className="relative flex-1">
          <Search className="w-4 h-4 absolute left-3.5 top-3.5 text-slate-400 dark:text-slate-500 pointer-events-none" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && fetchOpportunities()}
            placeholder="Search opportunities or companies..."
            className="w-full liquid-glass-input text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-500 text-xs font-semibold pl-10 pr-4 py-3 min-h-[46px] outline-none"
          />
        </div>
        <button
          onClick={() => fetchOpportunities(true)}
          disabled={refreshing}
          className="liquid-glass-btn-secondary w-11 h-11 rounded-2xl flex items-center justify-center text-slate-600 dark:text-slate-300 active:scale-95 transition-transform"
          aria-label="Refresh feed"
        >
          <RefreshCw className={`w-4 h-4 ${refreshing ? "animate-spin text-blue-600 dark:text-blue-400" : ""}`} />
        </button>
      </div>

      {/* Liquid Metric Cards */}
      <div className="grid grid-cols-3 gap-2.5">
        <GlassCard className="p-3 text-center flex flex-col justify-center items-center">
          <div className="flex items-center gap-1 text-[11px] font-semibold text-slate-500 dark:text-slate-400">
            <Layers className="w-3 h-3 text-blue-500" />
            <span>Active</span>
          </div>
          <p className="text-xl font-black text-slate-900 dark:text-white mt-1 tracking-tight">
            {activeCount}
          </p>
        </GlassCard>

        <GlassCard className="p-3 text-center flex flex-col justify-center items-center">
          <div className="flex items-center gap-1 text-[11px] font-semibold text-amber-600 dark:text-amber-400">
            <Flame className="w-3 h-3" />
            <span>Due Soon</span>
          </div>
          <p className="text-xl font-black text-amber-600 dark:text-amber-400 mt-1 tracking-tight">
            {urgentCount}
          </p>
        </GlassCard>

        <GlassCard className="p-3 text-center flex flex-col justify-center items-center">
          <div className="flex items-center gap-1 text-[11px] font-semibold text-emerald-600 dark:text-emerald-400">
            <CheckCircle2 className="w-3 h-3" />
            <span>Done</span>
          </div>
          <p className="text-xl font-black text-emerald-600 dark:text-emerald-400 mt-1 tracking-tight">
            {completedCount}
          </p>
        </GlassCard>
      </div>

      {/* Filter Glass Pills */}
      <div className="flex space-x-2 overflow-x-auto pb-1 scrollbar-none text-xs">
        {[
          { label: "All Feed", id: "ALL" },
          { label: "🔥 Due Soon", id: "URGENT" },
          { label: "New", id: "NEW" },
          { label: "Applied", id: "APPLIED" },
          { label: "Interview", id: "INTERVIEW" },
          { label: "Completed", id: "COMPLETED" },
        ].map((filter) => (
          <button
            key={filter.id}
            onClick={() => setActiveFilter(filter.id)}
            className={`px-3.5 py-1.5 rounded-full font-bold whitespace-nowrap transition-all duration-150 text-[11px] tracking-tight ${
              activeFilter === filter.id
                ? "bg-gradient-to-r from-blue-600 to-indigo-600 text-white shadow-md shadow-blue-500/25 border border-white/30"
                : "liquid-glass-pill bg-white/60 dark:bg-slate-900/60 text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white"
            }`}
          >
            {filter.label}
          </button>
        ))}
      </div>

      {/* Feed Content */}
      {loading ? (
        <div className="space-y-3 pt-1">
          {[1, 2, 3].map((n) => (
            <GlassCard key={n} className="animate-pulse space-y-3">
              <div className="h-4 bg-slate-200/60 dark:bg-slate-700/60 rounded-lg w-3/4" />
              <div className="h-3 bg-slate-100/60 dark:bg-slate-800/60 rounded-lg w-1/2" />
              <div className="flex space-x-2 pt-1">
                <div className="h-5 bg-slate-200/60 dark:bg-slate-700/60 rounded-full w-16" />
                <div className="h-5 bg-slate-200/60 dark:bg-slate-700/60 rounded-full w-20" />
              </div>
            </GlassCard>
          ))}
        </div>
      ) : error ? (
        <GlassCard className="p-5 text-center space-y-2 !border-red-400/40">
          <p className="text-xs text-red-600 dark:text-red-400 font-semibold">{error}</p>
          <button
            onClick={() => fetchOpportunities()}
            className="text-xs font-bold text-red-600 dark:text-red-400 underline underline-offset-2"
          >
            Retry Connection
          </button>
        </GlassCard>
      ) : filteredOpportunities.length === 0 ? (
        <GlassCard className="p-8 text-center space-y-4">
          <div className="w-14 h-14 bg-gradient-to-tr from-blue-500/20 to-indigo-500/20 text-blue-600 dark:text-blue-400 rounded-3xl flex items-center justify-center mx-auto shadow-inner border border-white/40">
            <Sparkles className="w-7 h-7" />
          </div>
          <div>
            <h3 className="font-extrabold text-base text-slate-900 dark:text-white tracking-tight">
              {searchQuery ? "No matching opportunities" : "No opportunities captured yet"}
            </h3>
            <p className="text-xs text-slate-500 dark:text-slate-400 mt-1 max-w-xs mx-auto leading-relaxed">
              {searchQuery
                ? "Try searching for a different company or clear your filters."
                : "Share links from Chrome or paste any opportunity text to extract actionable deadlines."}
            </p>
          </div>
          <Link href="/app/capture" className="inline-block">
            <GlassButton variant="primary" size="md" leftIcon={<Plus className="w-4 h-4" />}>
              Capture New Opportunity
            </GlassButton>
          </Link>
        </GlassCard>
      ) : (
        <div className="space-y-3">
          {filteredOpportunities.map((opp) => {
            const deadline = getDeadlineInfo(opp.deadlineAt);
            const statusVariant =
              opp.status === "COMPLETED"
                ? "emerald"
                : opp.status === "APPLIED"
                ? "purple"
                : "blue";

            return (
              <Link
                key={opp.id}
                href={`/app/opportunities/${opp.id}`}
                className="block group focus:outline-none"
              >
                <GlassCard interactive={true} className="group-hover:border-blue-400/50">
                  <div className="flex items-start justify-between gap-3">
                    <div className="flex-1 min-w-0">
                      <h2 className="text-sm font-bold text-slate-900 dark:text-white truncate group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors tracking-tight">
                        {opp.title}
                      </h2>
                      {opp.organization && (
                        <p className="text-xs text-slate-500 dark:text-slate-400 flex items-center space-x-1.5 mt-1 font-medium">
                          <Building2 className="w-3.5 h-3.5 shrink-0 opacity-70" />
                          <span className="truncate">{opp.organization}</span>
                        </p>
                      )}
                    </div>
                    <div className="w-8 h-8 rounded-full bg-slate-500/10 flex items-center justify-center shrink-0 text-slate-400 group-hover:text-blue-600 dark:group-hover:text-blue-400 group-hover:translate-x-0.5 transition-all">
                      <ChevronRight className="w-4 h-4" />
                    </div>
                  </div>

                  <div className="flex items-center justify-between gap-2 mt-3.5 pt-3 border-t border-slate-200/50 dark:border-white/10 text-[11px]">
                    {/* Category & Status */}
                    <div className="flex items-center space-x-2 flex-wrap gap-y-1">
                      <GlassBadge variant="slate" size="xs">
                        {opp.category || "GENERAL"}
                      </GlassBadge>
                      <GlassBadge variant={statusVariant} size="xs" dot={true}>
                        {opp.status}
                      </GlassBadge>
                    </div>

                    {/* Deadline Urgency */}
                    <div
                      className={`flex items-center space-x-1 px-2.5 py-0.5 rounded-full font-bold text-[10px] tracking-tight border ${
                        deadline.isExpired
                          ? "bg-slate-500/10 text-slate-400 border-slate-500/20"
                          : deadline.isUrgent
                          ? "bg-amber-500/15 text-amber-700 dark:text-amber-300 border-amber-400/30"
                          : "bg-slate-500/10 text-slate-600 dark:text-slate-400 border-slate-400/20"
                      }`}
                    >
                      <Clock className="w-3 h-3" />
                      <span>{deadline.text}</span>
                    </div>
                  </div>
                </GlassCard>
              </Link>
            );
          })}
        </div>
      )}
    </div>
  );
}
