"use client";

import React, { Suspense, useEffect, useState, useRef } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { Sparkles, Loader2, AlertCircle, ArrowRight } from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { api } from "@/lib/api-client";

function ShareTargetHandler() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { isAuthenticated, isLoading: authLoading } = useAuth();

  const [status, setStatus] = useState("Processing shared opportunity...");
  const [error, setError] = useState<string | null>(null);
  const processedRef = useRef(false);

  useEffect(() => {
    if (authLoading || processedRef.current) return;

    const title = searchParams.get("title") || "";
    const text = searchParams.get("text") || "";
    const rawUrl = searchParams.get("url") || "";

    // Extract URL from query params or embedded text
    let targetUrl = rawUrl.trim();
    if (!targetUrl && text) {
      const urlMatch = text.match(/https?:\/\/[^\s]+/i);
      if (urlMatch) {
        targetUrl = urlMatch[0];
      }
    }

    const payload = {
      url: targetUrl || undefined,
      text: text || title || undefined,
      sourceType: targetUrl ? "URL" : "TEXT",
    };

    if (!payload.url && !payload.text) {
      setError("No link or text was received from the share sheet.");
      return;
    }

    if (!isAuthenticated) {
      // Preserve for after login
      sessionStorage.setItem("mnesa_pending_share", JSON.stringify(payload));
      router.push("/app/login?redirect=/app/share-target");
      return;
    }

    processedRef.current = true;
    setStatus("Ingesting shared link into MNESA pipeline...");

    api
      .submitIntake(payload)
      .then(async (res) => {
        setStatus("AI analyzing opportunity details...");
        let attempts = 0;
        const maxAttempts = 20;

        while (attempts < maxAttempts) {
          attempts++;
          try {
            const job = await api.getIntakeJob(res.jobId);
            if (job.status === "COMPLETED") {
              setStatus("Extraction ready! Opening review...");
              setTimeout(() => {
                router.replace(`/app/review?jobId=${res.jobId}`);
              }, 400);
              return;
            }
            if (job.status === "FAILED") {
              throw new Error(job.error_message || "Extraction failed");
            }
          } catch (e: unknown) {
            if (attempts >= maxAttempts) throw e;
          }
          await new Promise((r) => setTimeout(r, 1500));
        }

        // On timeout, go to review anyway
        router.replace(`/app/review?jobId=${res.jobId}`);
      })
      .catch((err: unknown) => {
        const msg = err instanceof Error ? err.message : "Failed to process shared opportunity";
        setError(msg);
      });
  }, [searchParams, isAuthenticated, authLoading, router]);

  return (
    <div className="flex flex-col items-center justify-center min-h-[70vh] text-center px-4 space-y-4">
      {error ? (
        <div className="bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-900 rounded-2xl p-6 max-w-sm space-y-3">
          <AlertCircle className="w-8 h-8 text-red-600 dark:text-red-400 mx-auto" />
          <p className="text-xs text-red-700 dark:text-red-300 font-medium">{error}</p>
          <button
            onClick={() => router.push("/app/capture")}
            className="w-full py-2.5 px-4 rounded-xl bg-blue-600 text-white text-xs font-bold"
          >
            Go to Capture Screen
          </button>
        </div>
      ) : (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-8 max-w-sm w-full space-y-4 shadow-sm">
          <div className="w-14 h-14 bg-blue-50 dark:bg-blue-950/60 text-blue-600 dark:text-blue-400 rounded-full flex items-center justify-center mx-auto">
            <Loader2 className="w-7 h-7 animate-spin" />
          </div>
          <div>
            <h2 className="text-base font-bold text-slate-900 dark:text-white">
              Shared Opportunity Received
            </h2>
            <p className="text-xs text-slate-500 mt-2 font-medium">{status}</p>
          </div>
          <div className="w-full bg-slate-100 dark:bg-slate-800 rounded-full h-1.5 overflow-hidden">
            <div className="bg-blue-600 h-full rounded-full animate-pulse w-3/4" />
          </div>
        </div>
      )}
    </div>
  );
}

export default function ShareTargetPage() {
  return (
    <Suspense
      fallback={
        <div className="flex items-center justify-center min-h-[60vh]">
          <Loader2 className="w-6 h-6 animate-spin text-blue-600" />
        </div>
      }
    >
      <ShareTargetHandler />
    </Suspense>
  );
}
