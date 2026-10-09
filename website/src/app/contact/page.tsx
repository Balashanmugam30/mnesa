"use client";

import React, { useState } from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import {
  Mail,
  Send,
  CheckCircle2,
  AlertCircle,
  ExternalLink,
  MessageSquare,
  ShieldAlert,
} from "lucide-react";

export default function ContactPage() {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    category: "GENERAL",
    message: "",
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name.trim() || !formData.email.trim() || !formData.message.trim()) {
      setErrorMsg("Please fill in all required fields.");
      return;
    }
    setErrorMsg("");
    setIsSubmitting(true);

    // Simulate reliable submission
    setTimeout(() => {
      setIsSubmitting(false);
      setSubmitted(true);
    }, 600);
  };

  return (
    <div className="py-12 sm:py-20">
      <Container size="lg">
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary dark:text-blue-300 text-xs font-semibold mb-6">
            <Mail className="w-3.5 h-3.5" />
            <span>Connect with Engineering & Support</span>
          </div>

          <Heading level={1} className="text-3xl sm:text-5xl font-extrabold tracking-tight">
            Contact MNESA Team
          </Heading>

          <p className="mt-4 text-base sm:text-lg text-content-secondary dark:text-content-dark-secondary leading-relaxed">
            Have questions about our Android preview, feedback on extraction accuracy, or want to report a security finding? We are here to help.
          </p>
        </div>

        {/* Two-column layout */}
        <div className="mt-16 grid grid-cols-1 lg:grid-cols-12 gap-10 max-w-5xl mx-auto">
          {/* Left Column: Direct channels */}
          <div className="lg:col-span-5 space-y-6">
            <div className="p-6 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
              <h2 className="text-base font-bold text-content-primary dark:text-content-dark-primary mb-4 flex items-center space-x-2">
                <MessageSquare className="w-5 h-5 text-brand-primary" />
                <span>Direct Contact Channels</span>
              </h2>

              <div className="space-y-4 text-xs">
                <div>
                  <span className="text-content-secondary dark:text-content-dark-secondary block">
                    General Inquiries & Early Access:
                  </span>
                  <a
                    href="mailto:support@mnesa.ai"
                    className="font-bold text-brand-primary hover:underline font-mono"
                  >
                    support@mnesa.ai
                  </a>
                </div>

                <div className="pt-3 border-t border-border-subtle dark:border-border-dark-subtle">
                  <span className="text-content-secondary dark:text-content-dark-secondary block">
                    Security & Vulnerability Disclosure:
                  </span>
                  <a
                    href="mailto:security@mnesa.ai"
                    className="font-bold text-brand-primary hover:underline font-mono"
                  >
                    security@mnesa.ai
                  </a>
                </div>

                <div className="pt-3 border-t border-border-subtle dark:border-border-dark-subtle">
                  <span className="text-content-secondary dark:text-content-dark-secondary block">
                    Public Bug Tracker & Issues:
                  </span>
                  <a
                    href="https://github.com/Balashanmugam30/mnesa/issues"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="font-bold text-brand-primary hover:underline inline-flex items-center space-x-1"
                  >
                    <span>GitHub Issues</span>
                    <ExternalLink className="w-3.5 h-3.5" />
                  </a>
                </div>
              </div>
            </div>

            {/* Quick guidance box */}
            <div className="p-6 rounded-2xl bg-blue-50/60 dark:bg-blue-950/40 border border-blue-200 dark:border-blue-900 text-xs text-content-secondary dark:text-content-dark-secondary space-y-2">
              <p className="font-bold text-content-primary dark:text-content-dark-primary">
                Response Times
              </p>
              <p>
                Our team responds to all incoming inquiries within 24 to 48 business hours. Security vulnerability disclosures receive accelerated triage within 12 hours.
              </p>
            </div>
          </div>

          {/* Right Column: Contact Form */}
          <div className="lg:col-span-7">
            <div className="p-8 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle shadow-sm">
              {submitted ? (
                <div className="text-center py-10 space-y-4">
                  <div className="w-14 h-14 rounded-full bg-emerald-50 dark:bg-emerald-950/60 border border-emerald-200 dark:border-emerald-800 text-emerald-600 dark:text-emerald-400 flex items-center justify-center mx-auto">
                    <CheckCircle2 className="w-8 h-8" />
                  </div>
                  <h3 className="text-xl font-bold text-content-primary dark:text-content-dark-primary">
                    Message Sent Successfully
                  </h3>
                  <p className="text-xs text-content-secondary dark:text-content-dark-secondary max-w-md mx-auto leading-relaxed">
                    Thank you for reaching out. We have received your inquiry and our engineering team will get back to you shortly at{" "}
                    <strong className="text-content-primary dark:text-content-dark-primary font-mono">{formData.email}</strong>.
                  </p>
                  <Button
                    variant="secondary"
                    size="sm"
                    onClick={() => {
                      setSubmitted(false);
                      setFormData({ name: "", email: "", category: "GENERAL", message: "" });
                    }}
                  >
                    Send Another Message
                  </Button>
                </div>
              ) : (
                <form onSubmit={handleSubmit} className="space-y-4">
                  <h2 className="text-lg font-bold text-content-primary dark:text-content-dark-primary mb-4">
                    Send Us a Message
                  </h2>

                  {errorMsg && (
                    <div className="p-3 rounded-lg bg-red-50 dark:bg-red-950/60 border border-red-200 dark:border-red-900 text-xs text-urgency-danger flex items-center space-x-2">
                      <AlertCircle className="w-4 h-4 flex-shrink-0" />
                      <span>{errorMsg}</span>
                    </div>
                  )}

                  <div>
                    <label
                      htmlFor="contact-name"
                      className="block text-xs font-bold uppercase tracking-wider text-content-secondary dark:text-content-dark-secondary mb-1.5"
                    >
                      Your Name *
                    </label>
                    <input
                      id="contact-name"
                      type="text"
                      required
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                      placeholder="Jane Doe"
                      className="w-full text-xs p-3 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle focus:ring-2 focus:ring-brand-primary focus:outline-none text-content-primary dark:text-content-dark-primary"
                    />
                  </div>

                  <div>
                    <label
                      htmlFor="contact-email"
                      className="block text-xs font-bold uppercase tracking-wider text-content-secondary dark:text-content-dark-secondary mb-1.5"
                    >
                      Email Address *
                    </label>
                    <input
                      id="contact-email"
                      type="email"
                      required
                      value={formData.email}
                      onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                      placeholder="jane@example.com"
                      className="w-full text-xs p-3 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle focus:ring-2 focus:ring-brand-primary focus:outline-none text-content-primary dark:text-content-dark-primary"
                    />
                  </div>

                  <div>
                    <label
                      htmlFor="contact-category"
                      className="block text-xs font-bold uppercase tracking-wider text-content-secondary dark:text-content-dark-secondary mb-1.5"
                    >
                      Inquiry Category
                    </label>
                    <select
                      id="contact-category"
                      value={formData.category}
                      onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                      className="w-full text-xs p-3 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle focus:ring-2 focus:ring-brand-primary focus:outline-none text-content-primary dark:text-content-dark-primary"
                    >
                      <option value="GENERAL">General Inquiry / Question</option>
                      <option value="FEEDBACK">Early Access Feedback</option>
                      <option value="BUG">Bug Report</option>
                      <option value="SECURITY">Security / Vulnerability Disclosure</option>
                      <option value="PARTNERSHIP">University / Cohort Partnership</option>
                    </select>
                  </div>

                  <div>
                    <label
                      htmlFor="contact-message"
                      className="block text-xs font-bold uppercase tracking-wider text-content-secondary dark:text-content-dark-secondary mb-1.5"
                    >
                      Message *
                    </label>
                    <textarea
                      id="contact-message"
                      rows={5}
                      required
                      value={formData.message}
                      onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                      placeholder="Share your question or feedback..."
                      className="w-full text-xs p-3 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle focus:ring-2 focus:ring-brand-primary focus:outline-none text-content-primary dark:text-content-dark-primary leading-relaxed"
                    />
                  </div>

                  <div className="pt-2">
                    <Button
                      variant="primary"
                      size="md"
                      type="submit"
                      disabled={isSubmitting}
                      className="w-full space-x-2"
                    >
                      <Send className="w-4 h-4" />
                      <span>{isSubmitting ? "Sending..." : "Submit Inquiry"}</span>
                    </Button>
                  </div>
                </form>
              )}
            </div>
          </div>
        </div>
      </Container>
    </div>
  );
}
