"use client";

import React, { Suspense, useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import {
  Sparkles,
  CheckCircle2,
  Calendar,
  Building2,
  Tag,
  AlertCircle,
  Clock,
  ArrowRight,
  ShieldCheck,
  Edit3,
  Loader2,
} from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { api, IntakeJobStatus } from "@/lib/api-client";

function ReviewContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const jobId = searchParams.get("jobId");

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Form fields
  const [title, setTitle] = useState("");
  const [organization, setOrganization] = useState("");
  const [category, setCategory] = useState("INTERNSHIP");
  const [deadlinestr, setDeadlineStr] = useState("");
  const [notes, setNotes] = useState("");
  const [confidence, setConfidence] = useState<number | null>(null);
  const [summary, setSummary] = useState("");
  const [candidateId, setCandidateId] = useState<string | undefined>(undefined);

  useEffect(() => {
    if (!jobId) {
      setError("No job ID provided for review");
      setLoading(false);
      return;
    }

    api
      .getIntakeJob(jobId)
      .then((job) => {
        const ext = job.extraction;
        const candidate = job.candidates?.[0];

        const initialTitle = ext?.title || candidate?.title || "Captured Opportunity";
        const initialOrg = ext?.organization || candidate?.organization || "";
        const initialCat = ext?.category || candidate?.category || "INTERNSHIP";
        const initialDeadline = ext?.deadline_at || candidate?.deadline_at || "";

        setTitle(initialTitle);
        setOrganization(initialOrg);
        setCategory(initialCat);
        setNotes(ext?.summary || "");
        setSummary(ext?.summary || "");
        setCandidateId(candidate?.id);

        if (initialDeadline) {
          // Format for datetime-local input
          try {
            const d = new Date(initialDeadline);
            const iso = d.toISOString().substring(0, 16);
            setDeadlineStr(iso);
          } catch {
            setDeadlineStr("");
          }
        }

        const conf = ext?.confidence || candidate?.confidence_score || 0.9;
        setConfidence(Math.round(conf <= 1 ? conf * 100 : conf));
      })
      .catch((err: unknown) => {
        const msg = err instanceof Error ? err.message : "Failed to load extraction details";
        setError(msg);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [jobId]);

  const handleConfirm = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!jobId) return;

    if (!title.trim()) {
      setError("Title is required");
      return;
    }

    setError(null);
    setSubmitting(true);

    try {
      let deadlineAtIso: string | undefined = undefined;
      if (deadlinestr) {
        deadlineAtIso = new Date(deadlinestr).toISOString();
      }

      const opp = await api.confirmIntakeJob(jobId, {
        title: title.trim(),
        organization: organization.trim() || undefined,
        category: category || "GENERAL",
        deadline_at: deadlineAtIso,
        candidate_id: candidateId,
        notes: notes.trim() || undefined,
      });

      router.push(`/app/opportunities/${opp.id || ""}`);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Failed to confirm opportunity";
      setError(msg);
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-3">
        <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
        <p className="text-xs text-slate-500 font-medium">Loading extracted proposal...</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Header */}
      <div>
        <div className="flex items-center justify-between">
          <h1 className="text-lg font-black text-slate-900 dark:text-white flex items-center space-x-2">
            <CheckCircle2 className="w-5 h-5 text-emerald-600 dark:text-emerald-400" />
            <span>Review Extraction</span>
          </h1>
          {confidence !== null && (
            <span className="inline-flex items-center space-x-1 px-2.5 py-1 rounded-full text-[11px] font-bold bg-blue-50 text-blue-700 dark:bg-blue-950/80 dark:text-blue-300 border border-blue-200 dark:border-blue-800">
              <ShieldCheck className="w-3.5 h-3.5 text-blue-600 dark:text-blue-400" />
              <span>{confidence}% Confidence</span>
            </span>
          )}
        </div>
        <p className="text-xs text-slate-500 mt-1">
          Verify and adjust the details extracted by AI before saving to your opportunities.
        </p>
      </div>

      {error && (
        <div className="p-3 rounded-xl bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-900/60 text-red-700 dark:text-red-300 text-xs flex items-start space-x-2">
          <AlertCircle className="w-4 h-4 shrink-0 mt-0.5" />
          <span className="leading-relaxed">{error}</span>
        </div>
      )}

      {/* Review Form Card */}
      <form onSubmit={handleConfirm} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 shadow-sm space-y-4">
        {/* Title */}
        <div>
          <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
            Opportunity Title *
          </label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
            placeholder="e.g. Google Software Engineering Summer Internship 2026"
            className="w-full px-3 py-2 text-xs font-semibold rounded-xl border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800/40 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-600"
          />
        </div>

        {/* Organization / Company */}
        <div>
          <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
            Organization / Host
          </label>
          <div className="relative">
            <Building2 className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
            <input
              type="text"
              value={organization}
              onChange={(e) => setOrganization(e.target.value)}
              placeholder="e.g. Google / Unstop / MIT"
              className="w-full pl-9 pr-3 py-2 text-xs rounded-xl border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800/40 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-600"
            />
          </div>
        </div>

        {/* Category & Deadline Grid */}
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
              Category
            </label>
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="w-full px-2.5 py-2 text-xs font-medium rounded-xl border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800/40 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-600"
            >
              <option value="INTERNSHIP">Internship</option>
              <option value="JOB">Full-Time Job</option>
              <option value="HACKATHON">Hackathon</option>
              <option value="SCHOLARSHIP">Scholarship</option>
              <option value="GRANT">Grant / Funding</option>
              <option value="COMPETITION">Competition</option>
              <option value="GENERAL">General</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
              Deadline
            </label>
            <input
              type="datetime-local"
              value={deadlinestr}
              onChange={(e) => setDeadlineStr(e.target.value)}
              className="w-full px-2.5 py-2 text-xs font-medium rounded-xl border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800/40 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-600"
            />
          </div>
        </div>

        {/* Summary / Notes */}
        <div>
          <label className="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
            Notes & Extracted Summary
          </label>
          <textarea
            rows={3}
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            placeholder="Key eligibility criteria, stipend, rounds, or links..."
            className="w-full p-2.5 text-xs rounded-xl border border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-800/40 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-600 resize-none"
          />
        </div>

        {/* Submit Button */}
        <button
          type="submit"
          disabled={submitting}
          className="w-full py-3.5 px-4 rounded-xl bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white font-bold text-sm shadow-md shadow-blue-500/20 transition-all flex items-center justify-center space-x-2 disabled:opacity-50"
        >
          {submitting ? (
            <span className="inline-flex items-center space-x-2">
              <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              <span>Saving to MNESA...</span>
            </span>
          ) : (
            <>
              <CheckCircle2 className="w-4 h-4" />
              <span>Confirm & Save Opportunity</span>
            </>
          )}
        </button>
      </form>
    </div>
  );
}

export default function ReviewPage() {
  return (
    <Suspense
      fallback={
        <div className="flex items-center justify-center min-h-[60vh]">
          <Loader2 className="w-6 h-6 animate-spin text-blue-600" />
        </div>
      }
    >
      <ReviewContent />
    </Suspense>
  );
}
