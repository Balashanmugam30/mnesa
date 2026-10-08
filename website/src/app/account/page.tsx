"use client";

import React, { useState } from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Card } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Heading } from "@/components/ui/Heading";
import { Badge } from "@/components/ui/Badge";

const ALL_INTERESTS = [
  { id: "INTERNSHIP", label: "Internships" },
  { id: "JOB", label: "Full-Time Jobs" },
  { id: "HACKATHON", label: "Hackathons" },
  { id: "SCHOLARSHIP", label: "Scholarships" },
  { id: "COMPETITION", label: "Competitions" },
  { id: "EVENT", label: "Conferences & Events" },
  { id: "COURSE", label: "Courses & Certifications" },
  { id: "GRANT", label: "Grants & Research" },
];

export default function AccountPage() {
  const [selectedInterests, setSelectedInterests] = useState<string[]>([
    "INTERNSHIP",
    "JOB",
    "HACKATHON",
  ]);
  const [cadence, setCadence] = useState("STANDARD");
  const [pushEnabled, setPushEnabled] = useState(true);
  const [emailEnabled, setEmailEnabled] = useState(true);
  const [savedMessage, setSavedMessage] = useState(false);

  const toggleInterest = (id: string) => {
    setSelectedInterests((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  };

  const handleSavePreferences = () => {
    setSavedMessage(true);
    setTimeout(() => setSavedMessage(false), 3000);
  };

  const handleDeleteAccount = () => {
    if (confirm("Are you sure you want to permanently delete your MNESA account? All saved opportunities and reminders will be erased.")) {
      window.location.href = "/";
    }
  };

  return (
    <main className="min-h-screen py-12 bg-surface-base dark:bg-surface-dark-base">
      <Container size="md">
        {/* Navigation header */}
        <div className="flex items-center justify-between mb-8 pb-4 border-b border-border-subtle dark:border-border-dark-subtle">
          <Link href="/" className="inline-flex items-center gap-2">
            <span className="w-8 h-8 rounded-md bg-brand-primary text-white font-black text-base flex items-center justify-center">
              M
            </span>
            <span className="font-extrabold text-xl tracking-tight text-content-primary dark:text-content-dark-primary">
              MNESA
            </span>
          </Link>
          <div className="flex items-center gap-3">
            <Link href="/signin">
              <Button variant="outline" size="sm">
                Sign Out
              </Button>
            </Link>
          </div>
        </div>

        {/* Profile Card */}
        <Card variant="default" className="mb-8">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <div className="flex items-center gap-3">
                <Heading level={1} className="text-2xl font-black">
                  Alex Chen
                </Heading>
                <Badge variant="opportunity">ACTIVE</Badge>
              </div>
              <p className="mt-1 text-sm text-content-secondary dark:text-content-dark-secondary">
                alex.chen@example.com
              </p>
            </div>
            <div className="text-xs text-content-muted dark:text-content-dark-muted sm:text-right">
              Member since October 2026<br />
              Client: Native Android + Web Platform
            </div>
          </div>
        </Card>

        {/* Opportunity Interests */}
        <div className="space-y-6">
          <Card variant="default">
            <Heading level={2} className="text-lg font-bold mb-1">
              Opportunity Focus Areas
            </Heading>
            <p className="text-xs text-content-secondary dark:text-content-dark-secondary mb-4">
              MNESA tailors deadline urgency and extraction filters based on your selected categories.
            </p>

            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              {ALL_INTERESTS.map((interest) => {
                const isChecked = selectedInterests.includes(interest.id);
                return (
                  <button
                    key={interest.id}
                    type="button"
                    onClick={() => toggleInterest(interest.id)}
                    className={`min-h-[44px] px-3 py-2 text-xs font-semibold rounded-md border transition-all text-left flex items-center justify-between ${
                      isChecked
                        ? "bg-brand-primary text-white border-brand-primary shadow-sm"
                        : "bg-surface-card dark:bg-surface-dark-card text-content-primary dark:text-content-dark-primary border-border-subtle dark:border-border-dark-subtle hover:border-brand-primary/50"
                    }`}
                  >
                    <span>{interest.label}</span>
                    {isChecked && (
                      <svg className="w-4 h-4 ml-1 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                        <path
                          fillRule="evenodd"
                          d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                          clipRule="evenodd"
                        />
                      </svg>
                    )}
                  </button>
                );
              })}
            </div>
          </Card>

          {/* Reminder Cadence & Urgency */}
          <Card variant="default">
            <Heading level={2} className="text-lg font-bold mb-1">
              Default Reminder Urgency
            </Heading>
            <p className="text-xs text-content-secondary dark:text-content-dark-secondary mb-4">
              Configure how proactively MNESA prompts you prior to application deadlines.
            </p>

            <div className="space-y-3">
              {[
                {
                  id: "STANDARD",
                  title: "Standard Cadence (Recommended)",
                  desc: "Reminders at 1 week before, 3 days before, and 24 hours before the deadline.",
                },
                {
                  id: "AGGRESSIVE",
                  title: "High Urgency Cadence",
                  desc: "Daily reminders throughout the final week and continuous push updates.",
                },
                {
                  id: "IMMEDIATE",
                  title: "Immediate Follow-Through",
                  desc: "Initial reminder the morning after capture, plus 48 hours prior to deadline.",
                },
              ].map((opt) => (
                <label
                  key={opt.id}
                  className={`flex items-start gap-3 p-3 rounded-md border cursor-pointer transition-all ${
                    cadence === opt.id
                      ? "border-brand-primary bg-brand-primary/5 dark:bg-brand-primary/10"
                      : "border-border-subtle dark:border-border-dark-subtle hover:bg-slate-50 dark:hover:bg-slate-800/40"
                  }`}
                >
                  <input
                    type="radio"
                    name="cadence"
                    value={opt.id}
                    checked={cadence === opt.id}
                    onChange={(e) => setCadence(e.target.value)}
                    className="mt-1 h-4 w-4 text-brand-primary focus:ring-brand-primary"
                  />
                  <div>
                    <span className="block text-sm font-semibold text-content-primary dark:text-content-dark-primary">
                      {opt.title}
                    </span>
                    <span className="block text-xs text-content-secondary dark:text-content-dark-secondary mt-0.5">
                      {opt.desc}
                    </span>
                  </div>
                </label>
              ))}
            </div>
          </Card>

          {/* Notification Channels */}
          <Card variant="default">
            <Heading level={2} className="text-lg font-bold mb-1">
              Notification Channels
            </Heading>
            <p className="text-xs text-content-secondary dark:text-content-dark-secondary mb-4">
              Select where you receive deadline alerts and captured opportunity digests.
            </p>

            <div className="space-y-4">
              <label className="flex items-center justify-between cursor-pointer">
                <div>
                  <span className="block text-sm font-semibold text-content-primary dark:text-content-dark-primary">
                    Push Notifications (Android &amp; Web)
                  </span>
                  <span className="block text-xs text-content-secondary dark:text-content-dark-secondary">
                    Immediate capture receipts and urgent countdown reminders.
                  </span>
                </div>
                <input
                  type="checkbox"
                  checked={pushEnabled}
                  onChange={(e) => setPushEnabled(e.target.checked)}
                  className="h-5 w-5 rounded border-border-subtle text-brand-primary focus:ring-brand-primary"
                />
              </label>

              <label className="flex items-center justify-between cursor-pointer pt-3 border-t border-border-subtle dark:border-border-dark-subtle">
                <div>
                  <span className="block text-sm font-semibold text-content-primary dark:text-content-dark-primary">
                    Email Digest &amp; Summaries
                  </span>
                  <span className="block text-xs text-content-secondary dark:text-content-dark-secondary">
                    Weekly opportunities radar and upcoming deadline recap.
                  </span>
                </div>
                <input
                  type="checkbox"
                  checked={emailEnabled}
                  onChange={(e) => setEmailEnabled(e.target.checked)}
                  className="h-5 w-5 rounded border-border-subtle text-brand-primary focus:ring-brand-primary"
                />
              </label>
            </div>
          </Card>

          {/* Action buttons */}
          <div className="flex flex-col sm:flex-row items-center justify-between gap-4 pt-4">
            <Button onClick={handleSavePreferences} className="w-full sm:w-auto">
              Save Preferences
            </Button>

            {savedMessage && (
              <span className="text-sm font-semibold text-status-success flex items-center gap-1.5 animate-fade-in">
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                </svg>
                Preferences saved successfully
              </span>
            )}

            <Button
              variant="ghost"
              onClick={handleDeleteAccount}
              className="text-urgency-danger hover:text-red-700 dark:hover:text-red-400 w-full sm:w-auto text-xs font-semibold"
            >
              Delete Account
            </Button>
          </div>
        </div>
      </Container>
    </main>
  );
}
