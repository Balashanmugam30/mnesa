"use client";

import React, { useState } from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import { Badge, OpportunityBadgeType } from "@/components/ui/Badge";
import { Card } from "@/components/ui/Card";
import {
  Sparkles,
  Play,
  CheckCircle2,
  AlertCircle,
  Clock,
  ShieldCheck,
  FileText,
  Cpu,
  Layers,
  CheckSquare,
  ArrowRight,
  ExternalLink,
} from "lucide-react";

interface PresetItem {
  id: string;
  name: string;
  category: OpportunityBadgeType;
  rawText: string;
  result: {
    title: string;
    organization: string;
    category: OpportunityBadgeType;
    deadline: string;
    daysLeft: number;
    urgency: "CRITICAL" | "WARNING" | "NORMAL";
    confidenceScore: number;
    evidence: {
      field: string;
      quote: string;
      score: number;
    }[];
    checklist: string[];
    portalUrl: string;
  };
}

const PRESETS: PresetItem[] = [
  {
    id: "fellowship",
    name: "Summer ML Research Fellowship",
    category: "INTERNSHIP",
    rawText: `Applications for the Summer 2026 Stanford ML Research Fellowship are now open! Undergraduate and Master's students in Computer Science, Applied Math, or related disciplines are encouraged to apply. Selected fellows receive a $10,000 stipend and work directly with AI lab faculty. All application materials, including your academic transcript, CV, and a 1-page statement of purpose, must be submitted before April 15, 2026 at 23:59 PST. Late submissions will not be reviewed. Apply via the online portal.`,
    result: {
      title: "Stanford Summer ML Research Fellowship 2026",
      organization: "Stanford AI Lab",
      category: "INTERNSHIP",
      deadline: "April 15, 2026 at 23:59 PST",
      daysLeft: 12,
      urgency: "WARNING",
      confidenceScore: 0.96,
      evidence: [
        {
          field: "Deadline",
          quote: "All application materials ... must be submitted before April 15, 2026 at 23:59 PST.",
          score: 0.98,
        },
        {
          field: "Stipend",
          quote: "Selected fellows receive a $10,000 stipend and work directly with AI lab faculty.",
          score: 0.96,
        },
        {
          field: "Eligibility",
          quote: "Undergraduate and Master's students in Computer Science, Applied Math, or related disciplines.",
          score: 0.94,
        },
      ],
      checklist: [
        "Prepare 1-page Statement of Purpose",
        "Update academic CV",
        "Obtain official academic transcript",
        "Submit via online fellowship portal",
      ],
      portalUrl: "https://stanford.edu/ml-fellowship-2026",
    },
  },
  {
    id: "hackathon",
    name: "Global Collegiate AI Hackathon",
    category: "HACKATHON",
    rawText: `Join 2,000+ builders worldwide for the Global Collegiate AI Hackathon 2026 hosted by AI Frontiers! Prizes exceed $50,000 across tracks including Autonomous Agents, Healthcare AI, and Edge Computing. Teams of 2 to 4 eligible university students. Project submissions and registration close on March 28, 2026 at 18:00 UTC. Mentorship sessions kick off on March 20. Register your team on Devpost.`,
    result: {
      title: "Global Collegiate AI Hackathon 2026",
      organization: "AI Frontiers",
      category: "HACKATHON",
      deadline: "March 28, 2026 at 18:00 UTC",
      daysLeft: 3,
      urgency: "CRITICAL",
      confidenceScore: 0.94,
      evidence: [
        {
          field: "Deadline",
          quote: "Project submissions and registration close on March 28, 2026 at 18:00 UTC.",
          score: 0.97,
        },
        {
          field: "Prize Pool",
          quote: "Prizes exceed $50,000 across tracks including Autonomous Agents, Healthcare AI, and Edge Computing.",
          score: 0.96,
        },
      ],
      checklist: [
        "Form team of 2-4 university students",
        "Select track (Agents, Healthcare, or Edge)",
        "Register team on Devpost",
        "Submit repository and project demo",
      ],
      portalUrl: "https://devpost.com/hackathons/global-ai-2026",
    },
  },
  {
    id: "scholarship",
    name: "Future Tech Diversity Scholarship",
    category: "SCHOLARSHIP",
    rawText: `The Future Tech Diversity Scholarship aims to empower underrepresented engineers pursuing degrees in STEM. Awardees receive $7,500 towards tuition plus an assigned industry mentor from a top technology company. Open to all full-time undergraduate students worldwide. The application portal closes on May 20, 2026 at 17:00 EST. Required: 500-word essay on overcoming barriers in tech, resume, and one recommendation letter.`,
    result: {
      title: "Future Tech Diversity Scholarship 2026",
      organization: "Future Tech Foundation",
      category: "SCHOLARSHIP",
      deadline: "May 20, 2026 at 17:00 EST",
      daysLeft: 42,
      urgency: "NORMAL",
      confidenceScore: 0.98,
      evidence: [
        {
          field: "Deadline",
          quote: "The application portal closes on May 20, 2026 at 17:00 EST.",
          score: 0.99,
        },
        {
          field: "Award",
          quote: "Awardees receive $7,500 towards tuition plus an assigned industry mentor.",
          score: 0.98,
        },
        {
          field: "Requirements",
          quote: "Required: 500-word essay on overcoming barriers in tech, resume, and one recommendation letter.",
          score: 0.97,
        },
      ],
      checklist: [
        "Draft 500-word essay on tech barriers",
        "Request letter of recommendation from professor/mentor",
        "Submit current resume",
        "Review final application by May 20",
      ],
      portalUrl: "https://futuretech.org/scholarships/2026",
    },
  },
];

export default function DemoPage() {
  const [selectedPreset, setSelectedPreset] = useState<PresetItem>(PRESETS[0]);
  const [inputText, setInputText] = useState<string>(PRESETS[0].rawText);
  const [isProcessing, setIsProcessing] = useState<boolean>(false);
  const [hasExtracted, setHasExtracted] = useState<boolean>(false);
  const [processingStep, setProcessingStep] = useState<number>(0);

  const handleSelectPreset = (preset: PresetItem) => {
    setSelectedPreset(preset);
    setInputText(preset.rawText);
    setHasExtracted(false);
  };

  const handleSimulateExtraction = () => {
    setIsProcessing(true);
    setHasExtracted(false);
    setProcessingStep(1); // Sanitization

    setTimeout(() => {
      setProcessingStep(2); // AI Structuring
    }, 400);

    setTimeout(() => {
      setProcessingStep(3); // Confidence & Evidence Validation
    }, 800);

    setTimeout(() => {
      setIsProcessing(false);
      setHasExtracted(true);
    }, 1100);
  };

  const currentResult = selectedPreset.result;

  return (
    <div className="py-12 sm:py-20">
      <Container size="lg">
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary dark:text-blue-300 text-xs font-semibold mb-6">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Interactive Web Simulator</span>
          </div>

          <Heading level={1} className="text-3xl sm:text-5xl font-extrabold tracking-tight">
            Try MNESA Live in Your Browser
          </Heading>

          <p className="mt-4 text-base sm:text-lg text-content-secondary dark:text-content-dark-secondary leading-relaxed">
            Experience how MNESA transforms messy announcement text into grounded, structured opportunities with verified evidence and calibrated confidence scoring.
          </p>

          <div className="mt-4 inline-flex items-center space-x-2 text-xs font-mono px-3 py-1 rounded bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle text-content-secondary dark:text-content-dark-secondary">
            <span>Simulation Mode • Powered by MNESA Client Engine</span>
          </div>
        </div>

        {/* Demo Interface */}
        <div className="mt-12 grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          {/* Left Column: Input & Presets */}
          <div className="lg:col-span-5 space-y-4">
            <div className="p-6 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle">
              <label className="block text-xs font-bold uppercase tracking-wider text-content-secondary dark:text-content-dark-secondary mb-3">
                1. Choose a Sample Opportunity
              </label>
              <div className="grid grid-cols-1 gap-2 mb-4">
                {PRESETS.map((preset) => (
                  <button
                    key={preset.id}
                    onClick={() => handleSelectPreset(preset)}
                    className={`w-full text-left p-3 rounded-lg text-xs font-semibold border transition-all flex items-center justify-between ${
                      selectedPreset.id === preset.id
                        ? "bg-brand-primary-light dark:bg-blue-950/80 border-brand-primary text-brand-primary dark:text-blue-200"
                        : "bg-surface-base dark:bg-surface-dark-base border-border-subtle dark:border-border-dark-subtle text-content-primary dark:text-content-dark-primary hover:border-slate-400"
                    }`}
                  >
                    <span>{preset.name}</span>
                    <Badge type={preset.category} />
                  </button>
                ))}
              </div>

              <label
                htmlFor="demo-raw-text"
                className="block text-xs font-bold uppercase tracking-wider text-content-secondary dark:text-content-dark-secondary mb-2"
              >
                2. Raw Unstructured Content
              </label>
              <textarea
                id="demo-raw-text"
                rows={7}
                value={inputText}
                onChange={(e) => setInputText(e.target.value)}
                className="w-full text-xs p-3 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle focus:ring-2 focus:ring-brand-primary focus:outline-none text-content-primary dark:text-content-dark-primary leading-relaxed"
                placeholder="Paste raw announcement text or URL..."
              />

              <div className="mt-4">
                <Button
                  variant="primary"
                  size="md"
                  className="w-full space-x-2"
                  onClick={handleSimulateExtraction}
                  disabled={isProcessing}
                >
                  <Play className="w-4 h-4" />
                  <span>{isProcessing ? "Processing Pipeline..." : "Simulate MNESA Extraction"}</span>
                </Button>
              </div>
            </div>

            {/* Pipeline progress feedback */}
            {isProcessing && (
              <div className="p-4 rounded-xl bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-xs space-y-2">
                <div className="flex items-center space-x-2 font-bold text-brand-primary dark:text-blue-300">
                  <Cpu className="w-4 h-4 animate-spin" />
                  <span>Executing MNESA Extraction Pipeline:</span>
                </div>
                <div className="space-y-1 text-content-secondary dark:text-content-dark-secondary pl-6 text-[11px]">
                  <p className={processingStep >= 1 ? "text-emerald-600 dark:text-emerald-400 font-bold" : ""}>
                    ✓ 1. Zero-Trust Ingestion & SSRF Sanity Check
                  </p>
                  <p className={processingStep >= 2 ? "text-emerald-600 dark:text-emerald-400 font-bold" : ""}>
                    ✓ 2. Schema Normalization & Pydantic Validation
                  </p>
                  <p className={processingStep >= 3 ? "text-emerald-600 dark:text-emerald-400 font-bold" : ""}>
                    ✓ 3. Verbatim Evidence Matching & Calibration
                  </p>
                </div>
              </div>
            )}
          </div>

          {/* Right Column: Output Result Card */}
          <div className="lg:col-span-7">
            {hasExtracted ? (
              <div className="space-y-4">
                <div className="p-6 rounded-2xl bg-surface-card dark:bg-surface-dark-card border-2 border-brand-primary/40 shadow-lg">
                  {/* Top Bar */}
                  <div className="flex flex-wrap items-center justify-between gap-2 mb-4">
                    <Badge type={currentResult.category} />
                    <div className="flex items-center space-x-2">
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold ${
                          currentResult.urgency === "CRITICAL"
                            ? "bg-urgency-danger-light text-urgency-danger dark:bg-red-950 dark:text-red-300"
                            : currentResult.urgency === "WARNING"
                            ? "bg-urgency-warning-light text-urgency-warning dark:bg-amber-950 dark:text-amber-300"
                            : "bg-status-success-light text-status-success dark:bg-emerald-950 dark:text-emerald-300"
                        }`}
                      >
                        <Clock className="w-3.5 h-3.5 mr-1" />
                        {currentResult.daysLeft} days left ({currentResult.urgency})
                      </span>
                      <span className="text-xs font-mono font-bold px-2 py-0.5 rounded bg-emerald-50 text-emerald-700 dark:bg-emerald-950 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800">
                        {Math.round(currentResult.confidenceScore * 100)}% Confidence
                      </span>
                    </div>
                  </div>

                  {/* Title & Organization */}
                  <h2 className="text-xl font-extrabold text-content-primary dark:text-content-dark-primary">
                    {currentResult.title}
                  </h2>
                  <p className="text-xs font-semibold text-content-secondary dark:text-content-dark-secondary mt-1">
                    Host: <span className="text-brand-primary">{currentResult.organization}</span>
                  </p>

                  {/* Deadline Box */}
                  <div className="mt-4 p-3 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle flex items-center justify-between text-xs">
                    <div>
                      <span className="text-content-secondary dark:text-content-dark-secondary block">
                        Official Submission Deadline:
                      </span>
                      <strong className="text-content-primary dark:text-content-dark-primary font-mono text-sm">
                        {currentResult.deadline}
                      </strong>
                    </div>
                    <span className="px-2 py-1 rounded bg-blue-50 dark:bg-blue-950 text-brand-primary text-[11px] font-bold">
                      Calibrated
                    </span>
                  </div>

                  {/* Evidence Drawer */}
                  <div className="mt-6">
                    <h3 className="text-xs font-bold uppercase tracking-wider text-content-secondary dark:text-content-dark-secondary mb-2 flex items-center space-x-1.5">
                      <ShieldCheck className="w-4 h-4 text-brand-primary" />
                      <span>Verbatim Extracted Evidence Quotes</span>
                    </h3>
                    <div className="space-y-2">
                      {currentResult.evidence.map((ev, i) => (
                        <div
                          key={i}
                          className="p-3 rounded-lg bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle text-xs"
                        >
                          <div className="flex items-center justify-between mb-1">
                            <span className="font-semibold text-brand-primary text-[11px]">{ev.field} Citation</span>
                            <span className="font-mono text-[10px] text-emerald-600 dark:text-emerald-400">
                              Match: {Math.round(ev.score * 100)}%
                            </span>
                          </div>
                          <p className="italic text-content-secondary dark:text-content-dark-secondary bg-slate-50 dark:bg-slate-900/60 p-2 rounded text-[11px]">
                            &ldquo;{ev.quote}&rdquo;
                          </p>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Checklist */}
                  <div className="mt-6">
                    <h3 className="text-xs font-bold uppercase tracking-wider text-content-secondary dark:text-content-dark-secondary mb-2 flex items-center space-x-1.5">
                      <CheckSquare className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
                      <span>Automated Application Checklist</span>
                    </h3>
                    <ul className="space-y-1.5 text-xs text-content-secondary dark:text-content-dark-secondary">
                      {currentResult.checklist.map((item, idx) => (
                        <li key={idx} className="flex items-center space-x-2">
                          <CheckCircle2 className="w-3.5 h-3.5 text-brand-primary flex-shrink-0" />
                          <span>{item}</span>
                        </li>
                      ))}
                    </ul>
                  </div>

                  {/* CTA */}
                  <div className="mt-6 pt-4 border-t border-border-subtle dark:border-border-dark-subtle flex items-center justify-between">
                    <span className="text-[11px] text-content-secondary dark:text-content-dark-secondary">
                      Ready to apply in 1-tap on Android
                    </span>
                    <a
                      href={currentResult.portalUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="inline-flex items-center space-x-1 text-xs font-bold text-brand-primary hover:underline"
                    >
                      <span>Visit Application Portal</span>
                      <ExternalLink className="w-3.5 h-3.5" />
                    </a>
                  </div>
                </div>
              </div>
            ) : (
              <div className="p-12 rounded-2xl bg-surface-card dark:bg-surface-dark-card border border-dashed border-border-subtle dark:border-border-dark-subtle text-center flex flex-col items-center justify-center min-h-[380px]">
                <div className="w-12 h-12 rounded-xl bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary flex items-center justify-center mb-4">
                  <Play className="w-6 h-6 ml-0.5" />
                </div>
                <h3 className="text-base font-bold text-content-primary dark:text-content-dark-primary">
                  Ready to Extract
                </h3>
                <p className="mt-2 text-xs text-content-secondary dark:text-content-dark-secondary max-w-sm leading-relaxed">
                  Select an opportunity preset or customize the input text on the left, then click &quot;Simulate MNESA Extraction&quot;.
                </p>
              </div>
            )}
          </div>
        </div>

        {/* Footer Banner */}
        <div className="mt-16 text-center">
          <p className="text-xs text-content-secondary dark:text-content-dark-secondary mb-4">
            Want this automatic extraction directly in your Android share sheet?
          </p>
          <Link href="/download">
            <Button variant="primary" size="md">
              Download Android App Preview
            </Button>
          </Link>
        </div>
      </Container>
    </div>
  );
}
