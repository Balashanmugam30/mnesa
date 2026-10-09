import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { ShieldCheck, Lock, Database, Trash2, Mail, AlertCircle } from "lucide-react";

export const metadata = {
  title: "Privacy Policy — MNESA",
  description: "Learn how MNESA collects, processes, and protects your opportunity data, captures, and identity.",
};

export default function PrivacyPolicyPage() {
  return (
    <div className="py-12 sm:py-20">
      <Container size="lg">
        {/* Header */}
        <div className="border-b border-border-subtle dark:border-border-dark-subtle pb-8">
          <div className="inline-flex items-center space-x-2 text-xs font-semibold text-brand-primary uppercase tracking-wider mb-3">
            <ShieldCheck className="w-4 h-4" />
            <span>Data Protection & Privacy</span>
          </div>
          <Heading level={1} className="text-3xl sm:text-4xl font-extrabold tracking-tight">
            MNESA Privacy Policy
          </Heading>
          <p className="mt-3 text-sm text-content-secondary dark:text-content-dark-secondary">
            Last Updated: October 9, 2026 • Version 1.0 (Early Access Preview)
          </p>

          <div className="mt-4 p-4 rounded-lg bg-blue-50 dark:bg-blue-950/40 border border-blue-200 dark:border-blue-900 text-xs text-brand-primary dark:text-blue-300 flex items-start space-x-2">
            <AlertCircle className="w-4 h-4 mt-0.5 flex-shrink-0" />
            <span>
              <strong>Transparency Notice:</strong> This policy reflects the actual technical architecture of the MNESA Android application, Spring Boot backend, and AI extraction service. We do not sell your personal data or use your private opportunity captures to train public foundation models.
            </span>
          </div>
        </div>

        {/* Content sections */}
        <div className="mt-10 space-y-12 text-sm text-content-primary dark:text-content-dark-primary leading-relaxed">
          {/* Section 1 */}
          <section aria-labelledby="section-info-collected">
            <h2 id="section-info-collected" className="text-xl font-bold mb-4 flex items-center space-x-2">
              <span className="text-brand-primary font-mono text-base">01.</span>
              <span>Information We Collect</span>
            </h2>
            <p className="text-content-secondary dark:text-content-dark-secondary mb-4">
              MNESA collects only the information necessary to provide automated opportunity capture, deadline extraction, and scheduled notifications:
            </p>
            <div className="space-y-4">
              <div className="p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
                <h3 className="font-semibold text-content-primary dark:text-content-dark-primary">
                  A. Account & Identity Information
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  When you register, we collect your full name, email address, and a cryptographically salted password hash (Argon2/BCrypt). We never store plaintext passwords.
                </p>
              </div>

              <div className="p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
                <h3 className="font-semibold text-content-primary dark:text-content-dark-primary">
                  B. Opportunity Capture Data
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  When you share links or screenshots via the Android Share Target or web interface, we process the destination URL, normalized HTML text, and uploaded screenshot images.
                </p>
              </div>

              <div className="p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
                <h3 className="font-semibold text-content-primary dark:text-content-dark-primary">
                  C. Extracted Opportunity Metadata
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  Structured metadata produced by our extraction pipeline, including opportunity title, hosting organization, category, deadline timestamps, eligibility criteria, and action checklists.
                </p>
              </div>

              <div className="p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
                <h3 className="font-semibold text-content-primary dark:text-content-dark-primary">
                  D. Device Tokens for Notifications
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  To deliver time-sensitive reminder notifications, we securely store Firebase Cloud Messaging (FCM) registration tokens associated with your authenticated user ID.
                </p>
              </div>
            </div>
          </section>

          {/* Section 2 */}
          <section aria-labelledby="section-how-we-use">
            <h2 id="section-how-we-use" className="text-xl font-bold mb-4 flex items-center space-x-2">
              <span className="text-brand-primary font-mono text-base">02.</span>
              <span>How We Use Your Information</span>
            </h2>
            <ul className="list-disc list-inside space-y-2 text-content-secondary dark:text-content-dark-secondary">
              <li>
                <strong className="text-content-primary dark:text-content-dark-primary">Automated Extraction:</strong> To analyze shared webpage content or screenshots and populate deadlines and requirements into your personal library.
              </li>
              <li>
                <strong className="text-content-primary dark:text-content-dark-primary">Smart Reminder Delivery:</strong> To calculate intelligent notification schedules (e.g. 7 days before, 48 hours before, 12 hours before deadline) and trigger Android push notifications.
              </li>
              <li>
                <strong className="text-content-primary dark:text-content-dark-primary">Multi-Device Synchronization:</strong> To synchronize your local Android Room database with our secure PostgreSQL backend.
              </li>
              <li>
                <strong className="text-content-primary dark:text-content-dark-primary">Personal AI Assistant:</strong> To answer user-initiated questions specifically scoped to your saved opportunities without cross-user context leakage.
              </li>
            </ul>
          </section>

          {/* Section 3 */}
          <section aria-labelledby="section-ai-processing">
            <h2 id="section-ai-processing" className="text-xl font-bold mb-4 flex items-center space-x-2">
              <span className="text-brand-primary font-mono text-base">03.</span>
              <span>AI Processing & Third-Party Service Providers</span>
            </h2>
            <p className="text-content-secondary dark:text-content-dark-secondary mb-3">
              MNESA employs dedicated artificial intelligence services to structure unstructured opportunity text:
            </p>
            <div className="p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle space-y-2">
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary">
                • <strong>Google Gemini API:</strong> Utilized for structured schema extraction and multimodal vision analysis. Inputs sent to Gemini API are processed in accordance with enterprise API data terms, where inputs and outputs are not used to train Google models.
              </p>
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary">
                • <strong>Firebase Cloud Messaging (Google):</strong> Used strictly for mobile push notification delivery. No user opportunity data is stored within Firebase Firestore or other Firebase database services.
              </p>
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary">
                • <strong>No Data Brokering:</strong> We never sell, rent, monetize, or disclose your personal data or opportunities to third-party data brokers or advertising networks.
              </p>
            </div>
          </section>

          {/* Section 4 */}
          <section aria-labelledby="section-data-security">
            <h2 id="section-data-security" className="text-xl font-bold mb-4 flex items-center space-x-2">
              <span className="text-brand-primary font-mono text-base">04.</span>
              <span>Data Isolation & Technical Security</span>
            </h2>
            <p className="text-content-secondary dark:text-content-dark-secondary mb-3">
              MNESA implements defense-in-depth security across our entire technology stack:
            </p>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
                <h3 className="font-semibold text-xs text-content-primary dark:text-content-dark-primary flex items-center space-x-1.5">
                  <Database className="w-4 h-4 text-brand-primary" />
                  <span>Tenant Isolation</span>
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  All opportunities, reminders, and intake files are strictly partitioned by authenticated <code className="font-mono text-[11px]">user_id</code> in PostgreSQL. Cross-tenant queries are prevented at the JPA repository layer.
                </p>
              </div>

              <div className="p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
                <h3 className="font-semibold text-xs text-content-primary dark:text-content-dark-primary flex items-center space-x-1.5">
                  <Lock className="w-4 h-4 text-brand-primary" />
                  <span>Zero Client Secrets</span>
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  Neither the Android APK nor the web bundle contains database passwords, private encryption keys, or AI provider credentials.
                </p>
              </div>
            </div>
          </section>

          {/* Section 5 */}
          <section aria-labelledby="section-user-rights">
            <h2 id="section-user-rights" className="text-xl font-bold mb-4 flex items-center space-x-2">
              <span className="text-brand-primary font-mono text-base">05.</span>
              <span>User Rights & Account Deletion</span>
            </h2>
            <p className="text-content-secondary dark:text-content-dark-secondary mb-3">
              You maintain total sovereignty over your opportunity data. You may request data export or permanent account deletion at any time:
            </p>
            <div className="p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
              <div>
                <h3 className="font-semibold text-content-primary dark:text-content-dark-primary text-xs">
                  Permanent Account & Data Deletion
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  Deleting your account immediately purges your profile, all opportunities, scheduled reminders, and uploaded screenshots from our database.
                </p>
              </div>
              <Link
                href="/data-deletion"
                className="inline-flex items-center space-x-1.5 text-xs font-bold text-urgency-danger bg-red-50 dark:bg-red-950/60 border border-red-200 dark:border-red-900 px-3 py-2 rounded-md hover:bg-red-100 transition-colors flex-shrink-0"
              >
                <Trash2 className="w-3.5 h-3.5" />
                <span>Deletion Guide & Tools</span>
              </Link>
            </div>
          </section>

          {/* Section 6 */}
          <section aria-labelledby="section-contact-privacy">
            <h2 id="section-contact-privacy" className="text-xl font-bold mb-4 flex items-center space-x-2">
              <span className="text-brand-primary font-mono text-base">06.</span>
              <span>Contact Privacy Officer</span>
            </h2>
            <p className="text-content-secondary dark:text-content-dark-secondary">
              If you have any questions, concerns, or requests regarding this Privacy Policy or your personal information, please reach out to us:
            </p>
            <div className="mt-3 p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle inline-flex items-center space-x-3 text-xs">
              <Mail className="w-4 h-4 text-brand-primary" />
              <span>Email: <strong className="text-content-primary dark:text-content-dark-primary">privacy@mnesa.ai</strong></span>
              <span className="text-content-secondary dark:text-content-dark-secondary">• Response time: Within 48 hours</span>
            </div>
          </section>
        </div>
      </Container>
    </div>
  );
}
