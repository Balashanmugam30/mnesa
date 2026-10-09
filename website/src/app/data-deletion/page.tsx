import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import { Trash2, ShieldCheck, CheckCircle2, ArrowRight, Smartphone, Globe, Mail } from "lucide-react";

export const metadata = {
  title: "Account & Data Deletion — MNESA",
  description: "Learn how to delete your MNESA account and permanently purge your captured opportunities and data.",
};

export default function DataDeletionPage() {
  return (
    <div className="py-12 sm:py-20">
      <Container size="lg">
        {/* Header */}
        <div className="border-b border-border-subtle dark:border-border-dark-subtle pb-8">
          <div className="inline-flex items-center space-x-2 text-xs font-semibold text-urgency-danger uppercase tracking-wider mb-3">
            <Trash2 className="w-4 h-4" />
            <span>User Data Sovereignty</span>
          </div>
          <Heading level={1} className="text-3xl sm:text-4xl font-extrabold tracking-tight">
            Account & Data Deletion
          </Heading>
          <p className="mt-3 text-base text-content-secondary dark:text-content-dark-secondary max-w-2xl leading-relaxed">
            MNESA respects your privacy and data ownership. You have the absolute right to permanently delete your account and all associated captures, reminders, screenshots, and metadata at any time.
          </p>
        </div>

        {/* What gets deleted */}
        <div className="mt-10">
          <h2 className="text-lg font-bold text-content-primary dark:text-content-dark-primary mb-4">
            What Happens When You Delete Your Account
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
              <h3 className="font-semibold text-sm text-content-primary dark:text-content-dark-primary flex items-center space-x-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
                <span>Immediate Database Cascading Purge</span>
              </h3>
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                Your profile, email, authentication tokens, opportunity library, notes, checklist items, and reminder schedules are instantly purged from our primary PostgreSQL database via foreign key cascade.
              </p>
            </div>

            <div className="p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
              <h3 className="font-semibold text-sm text-content-primary dark:text-content-dark-primary flex items-center space-x-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
                <span>Object Storage Deletion</span>
              </h3>
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                All raw uploaded screenshots and OCR artifact files stored in encrypted MinIO object buckets are permanently unlinked and deleted.
              </p>
            </div>

            <div className="p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
              <h3 className="font-semibold text-sm text-content-primary dark:text-content-dark-primary flex items-center space-x-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
                <span>Push Notification Unregistration</span>
              </h3>
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                Firebase Cloud Messaging (FCM) device registration tokens are revoked, stopping all scheduled push notifications.
              </p>
            </div>

            <div className="p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
              <h3 className="font-semibold text-sm text-content-primary dark:text-content-dark-primary flex items-center space-x-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
                <span>On-Device SQLite Cleanup</span>
              </h3>
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                Upon confirming account deletion, the Android app clears the local Room database and encrypted shared preferences before logging out.
              </p>
            </div>
          </div>
        </div>

        {/* Step-by-step methods */}
        <div className="mt-12 space-y-6">
          <h2 className="text-lg font-bold text-content-primary dark:text-content-dark-primary">
            How to Delete Your Data
          </h2>

          {/* Method 1: Web */}
          <div className="p-6 rounded-xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
            <div className="flex items-start space-x-3">
              <div className="p-2.5 rounded-lg bg-blue-50 dark:bg-blue-950/60 text-brand-primary">
                <Globe className="w-5 h-5" />
              </div>
              <div>
                <h3 className="font-bold text-sm text-content-primary dark:text-content-dark-primary">
                  Method 1: Directly from the Web Dashboard
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  Navigate to your Account settings page while signed in, scroll to the Danger Zone section, and click &quot;Delete Account & All Data&quot;.
                </p>
              </div>
            </div>
            <Link href="/account">
              <Button variant="danger" size="sm">
                Go to Account Settings
              </Button>
            </Link>
          </div>

          {/* Method 2: Android */}
          <div className="p-6 rounded-xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
            <div className="flex items-start space-x-3">
              <div className="p-2.5 rounded-lg bg-emerald-50 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400">
                <Smartphone className="w-5 h-5" />
              </div>
              <div>
                <h3 className="font-bold text-sm text-content-primary dark:text-content-dark-primary">
                  Method 2: In the Android Application
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  Open MNESA → Tap Settings (Gear icon) → Select &quot;Account & Privacy&quot; → Tap &quot;Delete Account&quot; → Confirm your password.
                </p>
              </div>
            </div>
            <span className="text-xs font-mono px-3 py-1.5 rounded bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle text-content-secondary dark:text-content-dark-secondary">
              Instant Action
            </span>
          </div>

          {/* Method 3: Email Request */}
          <div className="p-6 rounded-xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
            <div className="flex items-start space-x-3">
              <div className="p-2.5 rounded-lg bg-purple-50 dark:bg-purple-950/60 text-purple-600 dark:text-purple-400">
                <Mail className="w-5 h-5" />
              </div>
              <div>
                <h3 className="font-bold text-sm text-content-primary dark:text-content-dark-primary">
                  Method 3: Email Deletion Request
                </h3>
                <p className="text-xs text-content-secondary dark:text-content-dark-secondary mt-1">
                  If you no longer have access to the app or web dashboard, send an email from your registered account email to <strong className="text-content-primary dark:text-content-dark-primary">privacy@mnesa.ai</strong> with the subject &quot;Account Deletion Request&quot;.
                </p>
              </div>
            </div>
            <a
              href="mailto:privacy@mnesa.ai?subject=Account%20Deletion%20Request"
              className="text-xs font-bold text-brand-primary hover:underline flex-shrink-0"
            >
              Email Privacy Team
            </a>
          </div>
        </div>

        {/* Processing timeline notice */}
        <div className="mt-10 p-4 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle text-xs text-content-secondary dark:text-content-dark-secondary">
          <p>
            <strong>Processing Timeline:</strong> In-app and web deletion requests take effect immediately upon submission. Email-based deletion requests are processed manually within 48 hours following identity verification.
          </p>
        </div>
      </Container>
    </div>
  );
}
