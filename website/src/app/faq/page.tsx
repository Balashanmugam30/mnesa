import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import { HelpCircle, ChevronDown, Sparkles, ArrowRight, ShieldCheck } from "lucide-react";

export const metadata = {
  title: "Frequently Asked Questions — MNESA",
  description: "Find clear answers about MNESA's opportunity capture flow, AI confidence scoring, Android system requirements, and offline synchronization.",
};

export default function FAQPage() {
  const faqCategories = [
    {
      category: "Opportunity Capture & Sharing",
      items: [
        {
          q: "How does the Android Share Target capture opportunities in under 200 milliseconds?",
          a: "MNESA registers an ACTION_SEND intent filter directly in AndroidManifest.xml. When you tap share from Chrome, LinkedIn, Twitter, or any app, the intent data is received by our native Java CaptureActivity, safely persisted to on-device Room SQLite, and immediately acknowledged with a success feedback toast, allowing you to continue browsing without interruption.",
        },
        {
          q: "Can I capture opportunities from flyers or screenshots?",
          a: "Yes. MNESA accepts image files (PNG, JPEG, WEBP) shared from your photo gallery or screenshot tool. Our OCR pipeline extracts text with decompression-bomb guards and magic byte verification, feeding the structured text into our AI extraction engine.",
        },
        {
          q: "What types of opportunities does MNESA support best?",
          a: "MNESA is optimized for high-stakes, deadline-driven opportunities including internships, full-time jobs, hackathons, research fellowships, grants, scholarships, paper submission deadlines, and student competitions.",
        },
      ],
    },
    {
      category: "AI Extraction, Confidence & Evidence",
      items: [
        {
          q: "Can the AI extraction make mistakes with deadlines?",
          a: "Yes. While Google Gemini and structured Pydantic schemas provide high accuracy, AI models can occasionally misinterpret ambiguous phrasing (such as 'rolling deadline' or poorly formatted timezones). Because of this, MNESA highlights verbatim evidence quotes and provides a calibrated confidence score so you can verify critical dates at the original source.",
        },
        {
          q: "How is the confidence score calculated?",
          a: "Confidence scores reflect the presence and clarity of critical fields (Title, Host, Deadline, Criteria) and the exactness of matched evidence sentences in the raw text. If an extraction lacks a clear deadline or contains ungrounded text, the score is penalized and flagged for user review.",
        },
        {
          q: "Do you use my private captures to train public AI models?",
          a: "No. Our integration with the Google Gemini API uses enterprise API terms where inputs and outputs are not retained or utilized to train Google's foundation models. Your data remains your private property.",
        },
      ],
    },
    {
      category: "Offline Functionality & Sync",
      items: [
        {
          q: "Does MNESA work when I am offline or in airplane mode?",
          a: "Yes. MNESA is built offline-first. Your entire opportunity library, notes, checklists, and reminder schedules are stored locally in Room SQLite. You can view, search, and edit everything without internet. When connectivity returns, changes synchronize automatically with our PostgreSQL cloud backend.",
        },
        {
          q: "Will scheduled deadline reminders fire if my phone is offline?",
          a: "Yes. Local Android alarm reminders are scheduled directly in the Android system alarm framework. They will fire on time even if your phone has no internet connection.",
        },
      ],
    },
    {
      category: "Device Support & Platform",
      items: [
        {
          q: "What Android versions are supported?",
          a: "MNESA supports Android 8.0 Oreo (API Level 26) through Android 15 (API Level 35). It is written in pure native Java 21 with AndroidX Material Components.",
        },
        {
          q: "Is there an iOS version of MNESA available?",
          a: "Currently, our primary mobile focus is delivering an ultra-fast, native experience on Android. A web-based dashboard is available for cross-platform access, and native iOS support is planned for future phases.",
        },
      ],
    },
  ];

  return (
    <div className="py-12 sm:py-20">
      <Container size="lg">
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary dark:text-blue-300 text-xs font-semibold mb-6">
            <HelpCircle className="w-3.5 h-3.5" />
            <span>Answers & Guidance</span>
          </div>

          <Heading level={1} className="text-3xl sm:text-5xl font-extrabold tracking-tight">
            Frequently Asked Questions
          </Heading>

          <p className="mt-4 text-base sm:text-lg text-content-secondary dark:text-content-dark-secondary leading-relaxed">
            Everything you need to know about capturing opportunities, AI calibration, privacy guarantees, and mobile system support.
          </p>
        </div>

        {/* Categories */}
        <div className="mt-16 space-y-12 max-w-4xl mx-auto">
          {faqCategories.map((cat, idx) => (
            <div key={idx} className="space-y-4">
              <h2 className="text-lg font-bold text-content-primary dark:text-content-dark-primary border-b border-border-subtle dark:border-border-dark-subtle pb-2">
                {cat.category}
              </h2>

              <div className="space-y-3">
                {cat.items.map((item, itemIdx) => (
                  <details
                    key={itemIdx}
                    className="group p-4 rounded-xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle transition-all [&_summary::-webkit-details-marker]:hidden"
                  >
                    <summary className="flex items-center justify-between cursor-pointer font-semibold text-sm text-content-primary dark:text-content-dark-primary focus:outline-none focus-visible:ring-2 focus-visible:ring-brand-primary rounded">
                      <span>{item.q}</span>
                      <ChevronDown className="w-4 h-4 text-content-secondary dark:text-content-dark-secondary transition-transform group-open:rotate-180 flex-shrink-0 ml-2" />
                    </summary>
                    <p className="mt-3 text-xs text-content-secondary dark:text-content-dark-secondary leading-relaxed pt-2 border-t border-border-subtle dark:border-border-dark-subtle">
                      {item.a}
                    </p>
                  </details>
                ))}
              </div>
            </div>
          ))}
        </div>

        {/* Still have questions banner */}
        <div className="mt-16 p-8 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle text-center max-w-2xl mx-auto">
          <h3 className="text-base font-bold text-content-primary dark:text-content-dark-primary">
            Still have questions?
          </h3>
          <p className="mt-2 text-xs text-content-secondary dark:text-content-dark-secondary">
            Our engineering and support team is ready to help you get started or address specific technical inquiries.
          </p>
          <div className="mt-4">
            <Link href="/contact">
              <Button variant="primary" size="sm">
                Contact MNESA Team
              </Button>
            </Link>
          </div>
        </div>
      </Container>
    </div>
  );
}
