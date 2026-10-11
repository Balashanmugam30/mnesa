"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import {
  Sparkles,
  Link as LinkIcon,
  FileText,
  ArrowRight,
  AlertCircle,
  Loader2,
  Clipboard,
  Lightbulb,
} from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { api } from "@/lib/api-client";
import {
  GlassCard,
  GlassButton,
  GlassBadge,
} from "@/components/glass";

export default function CapturePage() {
  const router = useRouter();
  const { isAuthenticated, isLoading: authLoading } = useAuth();

  const [inputContent, setInputContent] = useState("");
  const [sourceType, setSourceType] = useState<"URL" | "TEXT">("URL");
  const [isProcessing, setIsProcessing] = useState(false);
  const [processStage, setProcessStage] = useState<string>("");
  const [error, setError] = useState<string | null>(null);

  const isUrl = (val: string) => {
    try {
      const u = new URL(val.trim());
      return u.protocol === "http:" || u.protocol === "https:";
    } catch {
      return false;
    }
  };

  const handleInputChange = (val: string) => {
    setInputContent(val);
    if (isUrl(val)) {
      setSourceType("URL");
    }
  };

  const pollJobStatus = async (jobId: string) => {
    let attempts = 0;
    const maxAttempts = 25;

    while (attempts < maxAttempts) {
      attempts++;
      setProcessStage(`Extracting opportunity data with AI (step ${attempts}/25)...`);

      try {
        const job = await api.getIntakeJob(jobId);
        if (job.status === "COMPLETED") {
          setProcessStage("Extraction complete! Preparing liquid review...");
          setTimeout(() => {
            router.push(`/app/review?jobId=${jobId}`);
          }, 400);
          return;
        }

        if (job.status === "FAILED") {
          throw new Error(job.error_message || "AI extraction failed to extract opportunity");
        }
      } catch (err: unknown) {
        if (attempts >= maxAttempts) {
          throw err;
        }
      }

      await new Promise((resolve) => setTimeout(resolve, 1500));
    }

    // If polling timed out, proceed to review anyway so user can fill manually
    router.push(`/app/review?jobId=${jobId}`);
  };

  const handleCapture = async (e: React.FormEvent) => {
    e.preventDefault();
    const content = inputContent.trim();
    if (!content) {
      setError("Please paste a link or enter opportunity details");
      return;
    }

    setError(null);
    setIsProcessing(true);
    setProcessStage("Submitting capture to MNESA pipeline...");

    try {
      const isContentUrl = isUrl(content);
      const res = await api.submitIntake({
        url: isContentUrl ? content : undefined,
        text: isContentUrl ? undefined : content,
        sourceType: isContentUrl ? "URL" : "TEXT",
      });

      setProcessStage("Capture accepted. Running intelligent extraction...");
      await pollJobStatus(res.jobId);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Failed to capture opportunity";
      setError(msg);
      setIsProcessing(false);
    }
  };

  const handlePasteClipboard = async () => {
    try {
      const text = await navigator.clipboard.readText();
      if (text) {
        handleInputChange(text);
      }
    } catch {
      // Clipboard permission denied or unavailable
    }
  };

  return (
    <div className="space-y-4">
      {/* Title */}
      <div className="space-y-1">
        <h1 className="text-xl font-black text-slate-900 dark:text-white flex items-center space-x-2 tracking-tight">
          <div className="w-8 h-8 rounded-xl bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center text-white shadow-md shadow-blue-500/20">
            <Sparkles className="w-4 h-4" />
          </div>
          <span>Capture Opportunity</span>
        </h1>
        <p className="text-xs text-slate-500 dark:text-slate-400 leading-relaxed font-medium">
          Paste any job posting, scholarship, hackathon, or grant. MNESA extracts titles,
          deadlines, and actions with zero friction.
        </p>
      </div>

      {/* Processing State Card */}
      {isProcessing ? (
        <GlassCard elevated={true} className="p-8 text-center space-y-4">
          <div className="w-16 h-16 rounded-3xl bg-gradient-to-tr from-blue-500/20 to-indigo-500/20 text-blue-600 dark:text-blue-400 flex items-center justify-center mx-auto border border-white/40 shadow-inner">
            <Loader2 className="w-8 h-8 animate-spin" />
          </div>
          <div className="space-y-1">
            <h3 className="font-extrabold text-base text-slate-900 dark:text-white tracking-tight">
              Analyzing Opportunity
            </h3>
            <p className="text-xs text-slate-500 dark:text-slate-400 font-medium">
              {processStage}
            </p>
          </div>
          <div className="w-full bg-slate-200/50 dark:bg-slate-800/50 rounded-full h-1.5 overflow-hidden">
            <div className="bg-gradient-to-r from-blue-600 to-cyan-500 h-full rounded-full animate-pulse w-3/4" />
          </div>
        </GlassCard>
      ) : (
        <form onSubmit={handleCapture} className="space-y-4">
          {error && (
            <div className="p-3.5 rounded-2xl bg-red-500/10 border border-red-500/20 text-red-700 dark:text-red-300 text-xs flex items-start space-x-2.5 backdrop-blur-md">
              <AlertCircle className="w-4 h-4 shrink-0 mt-0.5 text-red-500" />
              <span className="leading-relaxed font-medium">{error}</span>
            </div>
          )}

          {/* Liquid Input Card */}
          <GlassCard className="p-4 space-y-3">
            <div className="flex items-center justify-between">
              <label className="text-xs font-bold text-slate-700 dark:text-slate-200 tracking-wide flex items-center gap-1.5">
                <FileText className="w-3.5 h-3.5 text-blue-500" />
                <span>Opportunity Source</span>
              </label>
              <button
                type="button"
                onClick={handlePasteClipboard}
                className="text-[11px] font-bold text-blue-600 hover:text-blue-700 dark:text-blue-400 flex items-center gap-1"
              >
                <Clipboard className="w-3 h-3" />
                <span>Paste Clipboard</span>
              </button>
            </div>

            <textarea
              rows={4}
              value={inputContent}
              onChange={(e) => handleInputChange(e.target.value)}
              placeholder="https://example.com/job/software-engineer or paste raw posting details..."
              className="w-full p-3.5 text-xs rounded-xl liquid-glass-input text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-slate-500 focus:outline-none resize-none font-mono leading-relaxed"
            />

            {/* Quick Detection Pill */}
            <div className="flex items-center space-x-2 pt-1 text-[11px]">
              <span className="text-slate-400 dark:text-slate-500 text-[10px] font-medium">Type:</span>
              <GlassBadge variant={isUrl(inputContent) ? "blue" : "slate"} size="xs" dot={true}>
                {isUrl(inputContent) ? "Web URL" : "Raw Text / Snippet"}
              </GlassBadge>
            </div>
          </GlassCard>

          {/* Submit Action */}
          <GlassButton
            type="submit"
            variant="primary"
            size="lg"
            className="w-full"
            leftIcon={<Sparkles className="w-4 h-4" />}
            rightIcon={<ArrowRight className="w-4 h-4" />}
          >
            Extract & Analyze with AI
          </GlassButton>
        </form>
      )}

      {/* Helpful Quick Tip in Glass Pill */}
      <GlassCard className="p-4 space-y-1.5">
        <p className="font-bold text-xs text-blue-600 dark:text-blue-400 flex items-center space-x-1.5">
          <Lightbulb className="w-4 h-4" />
          <span>Pro-tip: Android Share Sheet</span>
        </p>
        <p className="text-slate-500 dark:text-slate-400 leading-relaxed text-[11px] font-medium">
          Once installed to your Android home screen, you can tap <strong>Share</strong> in Chrome or any
          app and select <strong>MNESA</strong> to capture opportunities directly with zero typing!
        </p>
      </GlassCard>
    </div>
  );
}
