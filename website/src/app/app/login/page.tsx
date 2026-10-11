"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { LogIn, AlertCircle, ArrowRight, Sparkles, Lock, Mail } from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { GlassCard, GlassButton, GlassInput } from "@/components/glass";

export default function LoginPage() {
  const router = useRouter();
  const { login } = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) {
      setError("Please fill in both email and password");
      return;
    }

    setError(null);
    setLoading(true);

    try {
      await login(email, password);
      router.push("/app");
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Authentication failed";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col justify-center min-h-[80vh] px-2">
      {/* Brand Header */}
      <div className="text-center mb-6">
        <div className="w-16 h-16 rounded-3xl bg-gradient-to-tr from-blue-600 via-indigo-600 to-cyan-400 flex items-center justify-center text-white font-black text-2xl mx-auto mb-3 shadow-xl shadow-blue-500/30 border border-white/60">
          M
        </div>
        <h1 className="text-2xl font-black tracking-tight text-slate-900 dark:text-white">
          Welcome to MNESA
        </h1>
        <p className="text-xs text-slate-500 dark:text-slate-400 mt-1 font-medium">
          Liquid Opportunity Copilot — Track deadlines with zero friction
        </p>
      </div>

      {/* Liquid Glass Login Card */}
      <GlassCard elevated={true} className="p-6">
        {error && (
          <div className="mb-4 p-3.5 rounded-2xl bg-red-500/10 border border-red-500/20 text-red-700 dark:text-red-300 text-xs flex items-start space-x-2.5 backdrop-blur-md">
            <AlertCircle className="w-4 h-4 shrink-0 mt-0.5 text-red-500" />
            <span className="leading-relaxed font-medium">{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <GlassInput
            label="Email Address"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="harshabala47@gmail.com"
            required
            autoComplete="email"
            leftIcon={<Mail className="w-4 h-4" />}
          />

          <GlassInput
            label="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
            required
            autoComplete="current-password"
            leftIcon={<Lock className="w-4 h-4" />}
          />

          <GlassButton
            type="submit"
            variant="primary"
            size="lg"
            isLoading={loading}
            className="w-full mt-2"
            rightIcon={<ArrowRight className="w-4 h-4" />}
          >
            Sign In to MNESA
          </GlassButton>
        </form>

        <div className="mt-6 pt-5 border-t border-slate-200/50 dark:border-white/10 text-center">
          <p className="text-xs text-slate-500 dark:text-slate-400 font-medium">
            Don&apos;t have an account?{" "}
            <Link
              href="/app/register"
              className="font-bold text-blue-600 hover:text-blue-700 dark:text-blue-400 transition-colors"
            >
              Create Account
            </Link>
          </p>
        </div>
      </GlassCard>
    </div>
  );
}
