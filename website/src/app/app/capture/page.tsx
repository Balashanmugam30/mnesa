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
  CheckCircle2,
  ExternalLink,
} from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { api } from "@/lib/api-client";

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
          setProcessStage("Extraction complete! Preparing review...");
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
    <div className="space-y-5">
      {/* Title */}
      <div>
        <h1 className="text-xl font-black text-slate-900 dark:text-white flex items-center space-x-2">
          <Sparkles className="w-5 h-5 text-blue-600 dark:text-blue-400" />
          <span>Capture Opportunity</span>
        </h1>
        <p className="text-xs text-slate-500 mt-1">
          Paste any job posting, scholarship, hackathon, or grant. MNESA AI extracts titles,
          deadlines, and actions automatically.
        </p>
      </div>

      {/* Processing State Card */}
      {isProcessing ? (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-8 text-center space-y-4 shadow-sm">
          <div className="w-14 h-14 bg-blue-50 dark:bg-blue-950/60 text-blue-600 dark:text-blue-400 rounded-full flex items-center justify-center mx-auto">
            <Loader2 className="w-7 h-7 animate-spin" />
          </div>
          <div>
            <h3 className="font-bold text-sm text-slate-900 dark:text-white">Analyzing Opportunity</h3>
            <p className="text-xs text-slate-500 mt-2 font-medium">{processStage}</p>
          </div>
          <div className="w-full bg-slate-100 dark:bg-slate-800 rounded-full h-1.5 overflow-hidden">
            <div className="bg-blue-600 h-full rounded-full animate-pulse w-3/4" />
          </div>
        </div>
      ) : (
        <form onSubmit={handleCapture} className="space-y-4">
          {error && (
            <div className="p-3 rounded-xl bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-900/60 text-red-700 dark:text-red-300 text-xs flex items-start space-x-2">
              <AlertCircle className="w-4 h-4 shrink-0 mt-0.5" />
              <span className="leading-relaxed">{error}</span>
            </div>
          )}

          {/* Input Box */}
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm space-y-3">
            <div className="flex items-center justify-between">
              <label className="text-xs font-bold text-slate-700 dark:text-slate-300">
                Opportunity Link or Text
              </label>
              <button
                type="button"
                onClick={handlePasteClipboard}
                className="text-[11px] font-bold text-blue-600 hover:text-blue-700 dark:text-blue-400"
              >
                Paste from Clipboard
              </button>
            </div>

            <textarea
              rows={4}
              value={inputContent}
              onChange={(e) => handleInputChange(e.target.value)}
              placeholder="https://example.com/job/software-engineer or paste raw job requirements with deadline..."
              className="w-full p-3 text-xs rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800/40 text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-600 resize-none font-mono"
            />

            {/* Quick Type Pills */}
            <div className="flex items-center space-x-2 pt-1 text-[11px]">
              <span className="text-slate-400 text-[10px]">Detected:</span>
              <span
                className={`px-2 py-0.5 rounded-md font-bold uppercase text-[10px] ${
                  isUrl(inputContent)
                    ? "bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200"
                    : "bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300"
                }`}
              >
                {isUrl(inputContent) ? "Web URL" : "Raw Text / Snippet"}
              </span>
            </div>
          </div>

          {/* Submit Action */}
          <button
            type="submit"
            className="w-full py-3.5 px-4 rounded-xl bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white font-bold text-sm shadow-md shadow-blue-500/25 transition-all flex items-center justify-center space-x-2"
          >
            <Sparkles className="w-4 h-4" />
            <span>Analyze & Extract with AI</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </form>
      )}

      {/* Helpful Quick Tip */}
      <div className="bg-blue-50/50 dark:bg-blue-950/20 border border-blue-200/50 dark:border-blue-900/40 rounded-2xl p-4 text-xs space-y-1.5">
        <p className="font-bold text-blue-900 dark:text-blue-200 flex items-center space-x-1.5">
          <span>💡</span>
          <span>Pro-tip: Android Share Sheet</span>
        </p>
        <p className="text-slate-600 dark:text-slate-400 leading-relaxed text-[11px]">
          Once installed to your Android home screen, you can tap <strong>Share</strong> in Chrome or any
          app and select <strong>MNESA</strong> to capture opportunities directly!
        </p>
      </div>
    </div>
  );
}
