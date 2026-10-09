import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import {
  ShieldCheck,
  Lock,
  Database,
  Key,
  Globe,
  AlertCircle,
  FileCheck,
  Mail,
  CheckCircle2,
  ExternalLink,
} from "lucide-react";

export const metadata = {
  title: "Security & Privacy Architecture — MNESA",
  description: "Detailed, honest breakdown of MNESA's security model: zero client secrets, SSRF protection, tenant isolation, and responsible disclosure.",
};

export default function SecurityPage() {
  return (
    <div className="py-12 sm:py-20">
      <Container size="lg">
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary dark:text-blue-300 text-xs font-semibold mb-6">
            <ShieldCheck className="w-3.5 h-3.5" />
            <span>Rigorous & Grounded Security</span>
          </div>

          <Heading level={1} className="text-3xl sm:text-5xl font-extrabold tracking-tight">
            Security & Privacy Architecture
          </Heading>

          <p className="mt-4 text-base sm:text-lg text-content-secondary dark:text-content-dark-secondary leading-relaxed">
            We avoid marketing hyperbole like &quot;military grade&quot; or &quot;unhackable&quot;. Instead, we document our exact threat model, architectural boundaries, and verifiable security controls.
          </p>
        </div>

        {/* Security Controls Grid */}
        <div className="mt-16 grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Card 1 */}
          <div className="p-6 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
            <div className="flex items-center space-x-3 mb-3">
              <div className="w-9 h-9 rounded-lg bg-blue-50 dark:bg-blue-950 text-brand-primary flex items-center justify-center font-bold">
                <Globe className="w-5 h-5" />
              </div>
              <h2 className="text-base font-bold text-content-primary dark:text-content-dark-primary">
                Zero-Trust Ingestion & SSRF Defense
              </h2>
            </div>
            <p className="text-xs text-content-secondary dark:text-content-dark-secondary leading-relaxed">
              When processing user-shared URLs, our content fetcher inspects destination IP addresses before issuing requests. It deterministically blocks loopback addresses (127.0.0.1, ::1), private subnet blocks (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16), and cloud metadata services (169.254.169.254).
            </p>
          </div>

          {/* Card 2 */}
          <div className="p-6 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
            <div className="flex items-center space-x-3 mb-3">
              <div className="w-9 h-9 rounded-lg bg-blue-50 dark:bg-blue-950 text-brand-primary flex items-center justify-center font-bold">
                <Key className="w-5 h-5" />
              </div>
              <h2 className="text-base font-bold text-content-primary dark:text-content-dark-primary">
                Zero Secrets in Client Bundles
              </h2>
            </div>
            <p className="text-xs text-content-secondary dark:text-content-dark-secondary leading-relaxed">
              Neither the native Android APK nor the Next.js web client contains backend database strings, private signing keys, or AI model provider credentials. All privileged interactions are brokered through authenticated Spring Boot API gateways.
            </p>
          </div>

          {/* Card 3 */}
          <div className="p-6 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
            <div className="flex items-center space-x-3 mb-3">
              <div className="w-9 h-9 rounded-lg bg-blue-50 dark:bg-blue-950 text-brand-primary flex items-center justify-center font-bold">
                <Database className="w-5 h-5" />
              </div>
              <h2 className="text-base font-bold text-content-primary dark:text-content-dark-primary">
                Tenant Partitioning & Data Isolation
              </h2>
            </div>
            <p className="text-xs text-content-secondary dark:text-content-dark-secondary leading-relaxed">
              Every opportunity, reminder schedule, and uploaded screenshot is partitioned strictly by the authenticated <code className="font-mono text-[11px]">user_id</code> in PostgreSQL. Spring Security and JPA query boundaries enforce isolation on every database query.
            </p>
          </div>

          {/* Card 4 */}
          <div className="p-6 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
            <div className="flex items-center space-x-3 mb-3">
              <div className="w-9 h-9 rounded-lg bg-blue-50 dark:bg-blue-950 text-brand-primary flex items-center justify-center font-bold">
                <FileCheck className="w-5 h-5" />
              </div>
              <h2 className="text-base font-bold text-content-primary dark:text-content-dark-primary">
                Prompt Injection Resistance
              </h2>
            </div>
            <p className="text-xs text-content-secondary dark:text-content-dark-secondary leading-relaxed">
              Raw text is sanitized for delimiter collision before ingestion. The AI extractor enforces strict JSON schema decoding; malicious instructions embedded within web pages cannot override system safety boundaries or alter extraction instructions.
            </p>
          </div>
        </div>

        {/* Section: Known Limitations & Scoping */}
        <div className="mt-16 p-6 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle space-y-4">
          <div className="flex items-center space-x-2 font-bold text-base text-content-primary dark:text-content-dark-primary">
            <AlertCircle className="w-5 h-5 text-urgency-warning" />
            <span>Honest Security Disclosures & Known Limitations</span>
          </div>
          <p className="text-xs text-content-secondary dark:text-content-dark-secondary leading-relaxed">
            In our commitment to full transparency, we outline known boundaries and operating assumptions:
          </p>
          <ul className="space-y-2 text-xs text-content-secondary dark:text-content-dark-secondary">
            <li className="flex items-start space-x-2">
              <CheckCircle2 className="w-4 h-4 text-brand-primary mt-0.5 flex-shrink-0" />
              <span>
                <strong>Early Access Scope:</strong> MNESA is currently in preview release. Security controls are under continuous hardening and review.
              </span>
            </li>
            <li className="flex items-start space-x-2">
              <CheckCircle2 className="w-4 h-4 text-brand-primary mt-0.5 flex-shrink-0" />
              <span>
                <strong>External AI Services:</strong> We rely on upstream provider availability (Google Gemini API). If upstream services degrade, MNESA falls back to local heuristic extraction or queues tasks for background retry.
              </span>
            </li>
            <li className="flex items-start space-x-2">
              <CheckCircle2 className="w-4 h-4 text-brand-primary mt-0.5 flex-shrink-0" />
              <span>
                <strong>User Device Security:</strong> Local offline storage relies on Android sandbox permissions and hardware-backed device encryption (FBE). Users are advised to keep device lock screens enabled.
              </span>
            </li>
          </ul>
        </div>

        {/* Vulnerability Disclosure */}
        <div id="disclosure" className="mt-16 p-8 rounded-2xl bg-blue-50/60 dark:bg-blue-950/40 border border-blue-200 dark:border-blue-900">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <h2 className="text-lg font-bold text-content-primary dark:text-content-dark-primary flex items-center space-x-2">
                <Mail className="w-5 h-5 text-brand-primary" />
                <span>Vulnerability Disclosure Program</span>
              </h2>
              <p className="mt-2 text-xs text-content-secondary dark:text-content-dark-secondary max-w-xl leading-relaxed">
                We welcome responsible security research. If you discover a vulnerability in our API endpoints, Android application, or AI ingestion pipeline, please report it directly to our security engineering team.
              </p>
              <p className="mt-3 text-xs font-mono text-brand-primary">
                Contact: security@mnesa.ai
              </p>
            </div>
            <a
              href="mailto:security@mnesa.ai?subject=Vulnerability%20Disclosure"
              className="inline-flex items-center space-x-1.5 bg-brand-primary text-white text-xs font-bold px-4 py-2.5 rounded-lg shadow-sm hover:bg-brand-primary-hover flex-shrink-0"
            >
              <span>Submit Security Report</span>
              <ExternalLink className="w-3.5 h-3.5" />
            </a>
          </div>
        </div>
      </Container>
    </div>
  );
}
