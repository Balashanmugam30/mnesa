"use client";

import React, { useEffect, useState, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
  Search,
  Filter,
  Calendar,
  Clock,
  Sparkles,
  ArrowRight,
  RefreshCw,
  ExternalLink,
  CheckCircle2,
  AlertTriangle,
  Building2,
  ChevronRight,
  Plus,
} from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { api, OpportunitySummary } from "@/lib/api-client";

export default function OpportunitiesDashboard() {
  const router = useRouter();
  const { user, isAuthenticated, isLoading: authLoading } = useAuth();

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
      return { text: `${diffDays === 0 ? "Today" : diffDays === 1 ? "Tomorrow" : "2 days left"}`, isUrgent: true, isExpired: false };
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
      {/* Search & Refresh Bar */}
      <div className="flex items-center space-x-2">
        <div className="relative flex-1">
          <Search className="w-4 h-4 absolute left-3 top-3 text-slate-400" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && fetchOpportunities()}
            placeholder="Search opportunities or companies..."
            className="w-full pl-9 pr-3 py-2 text-xs rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-600"
          />
        </div>
        <button
          onClick={() => fetchOpportunities(true)}
          disabled={refreshing}
          className="p-2 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white transition-colors"
          aria-label="Refresh feed"
        >
          <RefreshCw className={`w-4 h-4 ${refreshing ? "animate-spin" : ""}`} />
        </button>
      </div>

      {/* Metrics / Stats row */}
      <div className="grid grid-cols-3 gap-2">
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-3 text-center">
          <p className="text-[11px] font-medium text-slate-500">Active</p>
          <p className="text-lg font-black text-blue-600 dark:text-blue-400 mt-0.5">{activeCount}</p>
        </div>
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-3 text-center">
          <p className="text-[11px] font-medium text-amber-600 dark:text-amber-400">Due Soon</p>
          <p className="text-lg font-black text-amber-600 dark:text-amber-400 mt-0.5">{urgentCount}</p>
        </div>
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-3 text-center">
          <p className="text-[11px] font-medium text-emerald-600 dark:text-emerald-400">Completed</p>
          <p className="text-lg font-black text-emerald-600 dark:text-emerald-400 mt-0.5">{completedCount}</p>
        </div>
      </div>

      {/* Filter Chips */}
      <div className="flex space-x-1.5 overflow-x-auto pb-1 scrollbar-none text-xs">
        {[
          { label: "All", id: "ALL" },
          { label: "🔥 Due Soon", id: "URGENT" },
          { label: "New", id: "NEW" },
          { label: "Applied", id: "APPLIED" },
          { label: "Interview", id: "INTERVIEW" },
          { label: "Completed", id: "COMPLETED" },
        ].map((filter) => (
          <button
            key={filter.id}
            onClick={() => setActiveFilter(filter.id)}
            className={`px-3 py-1.5 rounded-full font-semibold whitespace-nowrap transition-all text-[11px] ${
              activeFilter === filter.id
                ? "bg-blue-600 text-white shadow-sm"
                : "bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-400 border border-slate-200 dark:border-slate-800"
            }`}
          >
            {filter.label}
          </button>
        ))}
      </div>

      {/* Feed Content */}
      {loading ? (
        <div className="space-y-3 pt-2">
          {[1, 2, 3].map((n) => (
            <div
              key={n}
              className="bg-white dark:bg-slate-900 p-4 rounded-2xl border border-slate-200 dark:border-slate-800 animate-pulse space-y-3"
            >
              <div className="h-4 bg-slate-200 dark:bg-slate-800 rounded w-3/4" />
              <div className="h-3 bg-slate-100 dark:bg-slate-800 rounded w-1/2" />
              <div className="flex space-x-2 pt-1">
                <div className="h-5 bg-slate-200 dark:bg-slate-800 rounded-full w-16" />
                <div className="h-5 bg-slate-200 dark:bg-slate-800 rounded-full w-20" />
              </div>
            </div>
          ))}
        </div>
      ) : error ? (
        <div className="bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-900 rounded-2xl p-4 text-center space-y-2">
          <p className="text-xs text-red-700 dark:text-red-300 font-medium">{error}</p>
          <button
            onClick={() => fetchOpportunities()}
            className="text-xs font-bold text-red-600 underline"
          >
            Retry
          </button>
        </div>
      ) : filteredOpportunities.length === 0 ? (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-8 text-center space-y-4">
          <div className="w-12 h-12 bg-blue-50 dark:bg-blue-950 text-blue-600 dark:text-blue-400 rounded-full flex items-center justify-center mx-auto">
            <Sparkles className="w-6 h-6" />
          </div>
          <div>
            <h3 className="font-bold text-sm text-slate-900 dark:text-white">
              {searchQuery ? "No matches found" : "No opportunities captured yet"}
            </h3>
            <p className="text-xs text-slate-500 mt-1 max-w-xs mx-auto">
              {searchQuery
                ? "Try searching for a different keyword or clear filters."
                : "Share links from Chrome or paste any opportunity to extract and track deadlines."}
            </p>
          </div>
          <Link
            href="/app/capture"
            className="inline-flex items-center space-x-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-xl text-xs font-bold shadow-md shadow-blue-500/20"
          >
            <Plus className="w-4 h-4" />
            <span>Capture Opportunity</span>
          </Link>
        </div>
      ) : (
        <div className="space-y-3">
          {filteredOpportunities.map((opp) => {
            const deadline = getDeadlineInfo(opp.deadlineAt);
            return (
              <Link
                key={opp.id}
                href={`/app/opportunities/${opp.id}`}
                className="block bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 hover:border-blue-400 dark:hover:border-blue-600 rounded-2xl p-4 shadow-sm hover:shadow transition-all group"
              >
                <div className="flex items-start justify-between gap-3">
                  <div className="flex-1 min-w-0">
                    <h2 className="text-sm font-bold text-slate-900 dark:text-white truncate group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
                      {opp.title}
                    </h2>
                    {opp.organization && (
                      <p className="text-xs text-slate-500 flex items-center space-x-1 mt-0.5">
                        <Building2 className="w-3 h-3 shrink-0" />
                        <span className="truncate">{opp.organization}</span>
                      </p>
                    )}
                  </div>
                  <ChevronRight className="w-4 h-4 text-slate-400 group-hover:translate-x-0.5 transition-transform shrink-0" />
                </div>

                <div className="flex items-center justify-between gap-2 mt-3 pt-3 border-t border-slate-100 dark:border-slate-800/80 text-[11px]">
                  {/* Category & Status */}
                  <div className="flex items-center space-x-1.5 flex-wrap gap-y-1">
                    <span className="px-2 py-0.5 rounded-md font-semibold bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 uppercase tracking-wider text-[10px]">
                      {opp.category || "GENERAL"}
                    </span>
                    <span
                      className={`px-2 py-0.5 rounded-md font-bold text-[10px] ${
                        opp.status === "COMPLETED"
                          ? "bg-emerald-50 text-emerald-700 dark:bg-emerald-950 dark:text-emerald-400"
                          : opp.status === "APPLIED"
                          ? "bg-purple-50 text-purple-700 dark:bg-purple-950 dark:text-purple-400"
                          : "bg-blue-50 text-blue-700 dark:bg-blue-950 dark:text-blue-400"
                      }`}
                    >
                      {opp.status}
                    </span>
                  </div>

                  {/* Deadline Urgency Pill */}
                  <div
                    className={`flex items-center space-x-1 px-2 py-0.5 rounded-md font-bold ${
                      deadline.isExpired
                        ? "text-slate-400 bg-slate-100 dark:bg-slate-800"
                        : deadline.isUrgent
                        ? "text-amber-700 bg-amber-50 dark:text-amber-400 dark:bg-amber-950/60"
                        : "text-slate-600 dark:text-slate-400 bg-slate-50 dark:bg-slate-800/60"
                    }`}
                  >
                    <Clock className="w-3 h-3" />
                    <span>{deadline.text}</span>
                  </div>
                </div>
              </Link>
            );
          })}
        </div>
      )}
    </div>
  );
}
