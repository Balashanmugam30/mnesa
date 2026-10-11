"use client";

import React, { Suspense, useEffect, useState, useRef } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { Sparkles, Loader2, AlertCircle, ArrowRight } from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { api } from "@/lib/api-client";
import { GlassCard, GlassButton } from "@/components/glass";

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
        <GlassCard elevated={true} className="p-6 max-w-sm w-full space-y-3 !border-red-500/30">
          <AlertCircle className="w-8 h-8 text-red-500 mx-auto" />
          <p className="text-xs text-red-600 dark:text-red-400 font-semibold">{error}</p>
          <GlassButton
            onClick={() => router.push("/app/capture")}
            variant="primary"
            size="md"
            className="w-full"
          >
            Manual Capture
          </GlassButton>
        </GlassCard>
      ) : (
        <GlassCard elevated={true} className="p-8 max-w-sm w-full space-y-4">
          <div className="w-16 h-16 rounded-3xl bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center text-white mx-auto shadow-lg shadow-blue-500/30 border border-white/40">
            <Loader2 className="w-8 h-8 animate-spin" />
          </div>
          <div className="space-y-1">
            <h2 className="font-extrabold text-base text-slate-900 dark:text-white tracking-tight">
              Ingesting Shared Opportunity
            </h2>
            <p className="text-xs text-slate-500 dark:text-slate-400 font-medium">
              {status}
            </p>
          </div>
          <div className="w-full bg-slate-200/50 dark:bg-slate-800/50 rounded-full h-1.5 overflow-hidden">
            <div className="bg-gradient-to-r from-blue-600 to-cyan-500 h-full rounded-full animate-pulse w-4/5" />
          </div>
        </GlassCard>
      )}
    </div>
  );
}

export default function ShareTargetPage() {
  return (
    <Suspense
      fallback={
        <div className="flex items-center justify-center min-h-[60vh]">
          <Loader2 className="w-8 h-8 text-blue-600 animate-spin" />
        </div>
      }
    >
      <ShareTargetHandler />
    </Suspense>
  );
}
