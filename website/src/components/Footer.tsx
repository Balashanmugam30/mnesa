import React from "react";
import Link from "next/link";
import { ShieldCheck, CheckCircle2, Lock, Smartphone, Terminal, ExternalLink } from "lucide-react";

export const Footer: React.FC = () => {
  return (
    <footer
      role="contentinfo"
      className="border-t border-border-subtle dark:border-border-dark-subtle bg-surface-card dark:bg-surface-dark-card text-content-secondary dark:text-content-dark-secondary text-sm"
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 lg:py-16">
        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-8 lg:gap-12">
          {/* Brand & Overview */}
          <div className="col-span-2">
            <div className="flex items-center space-x-3">
              <div className="w-8 h-8 rounded-lg bg-brand-primary flex items-center justify-center text-white font-extrabold text-lg shadow-sm">
                M
              </div>
              <span className="font-extrabold text-xl tracking-tight text-content-primary dark:text-content-dark-primary">
                MNESA
              </span>
            </div>
            <p className="mt-3 text-sm text-content-secondary dark:text-content-dark-secondary max-w-sm leading-relaxed">
              AI-powered opportunity capture and follow-through platform. Transform scattered links, deadlines, and screenshots into structured, actionable opportunities.
            </p>
            <div className="mt-4 flex flex-wrap items-center gap-3 text-xs font-medium text-content-secondary dark:text-content-dark-secondary">
              <span className="inline-flex items-center space-x-1 bg-surface-base dark:bg-surface-dark-base px-2.5 py-1 rounded-md border border-border-subtle dark:border-border-dark-subtle">
                <ShieldCheck className="w-3.5 h-3.5 text-emerald-600 dark:text-emerald-400 mr-1" />
                Zero-Trust Ingestion
              </span>
              <span className="inline-flex items-center space-x-1 bg-surface-base dark:bg-surface-dark-base px-2.5 py-1 rounded-md border border-border-subtle dark:border-border-dark-subtle">
                <Smartphone className="w-3.5 h-3.5 text-brand-primary mr-1" />
                Native Android 8+
              </span>
            </div>
          </div>

          {/* Product Links */}
          <div>
            <h3 className="font-bold text-xs uppercase tracking-wider text-content-primary dark:text-content-dark-primary mb-4">
              Product
            </h3>
            <ul className="space-y-2.5 text-xs">
              <li>
                <Link href="/how-it-works" className="hover:text-brand-primary transition-colors">
                  How It Works
                </Link>
              </li>
              <li>
                <Link href="/features" className="hover:text-brand-primary transition-colors">
                  Features & Workflow
                </Link>
              </li>
              <li>
                <Link href="/ai" className="hover:text-brand-primary transition-colors">
                  AI Intelligence & Evidence
                </Link>
              </li>
              <li>
                <Link href="/demo" className="hover:text-brand-primary transition-colors font-semibold text-brand-primary">
                  Interactive Live Demo
                </Link>
              </li>
              <li>
                <Link href="/download" className="hover:text-brand-primary transition-colors">
                  Android APK Download
                </Link>
              </li>
              <li>
                <Link href="/pricing" className="hover:text-brand-primary transition-colors">
                  Early Access Pricing
                </Link>
              </li>
            </ul>
          </div>

          {/* Security & System */}
          <div>
            <h3 className="font-bold text-xs uppercase tracking-wider text-content-primary dark:text-content-dark-primary mb-4">
              Trust & System
            </h3>
            <ul className="space-y-2.5 text-xs">
              <li>
                <Link href="/security" className="hover:text-brand-primary transition-colors">
                  Security Architecture
                </Link>
              </li>
              <li>
                <Link href="/security#privacy" className="hover:text-brand-primary transition-colors">
                  Data Isolation & Storage
                </Link>
              </li>
              <li>
                <Link href="/security#disclosure" className="hover:text-brand-primary transition-colors">
                  Vulnerability Disclosure
                </Link>
              </li>
              <li>
                <Link href="/faq" className="hover:text-brand-primary transition-colors">
                  Frequently Asked Questions
                </Link>
              </li>
              <li>
                <Link href="/maintenance" className="hover:text-brand-primary transition-colors">
                  System Maintenance State
                </Link>
              </li>
              <li>
                <Link href="/offline" className="hover:text-brand-primary transition-colors">
                  Offline Sync Architecture
                </Link>
              </li>
            </ul>
          </div>

          {/* Legal & Compliance */}
          <div>
            <h3 className="font-bold text-xs uppercase tracking-wider text-content-primary dark:text-content-dark-primary mb-4">
              Legal & Policies
            </h3>
            <ul className="space-y-2.5 text-xs">
              <li>
                <Link href="/privacy" className="hover:text-brand-primary transition-colors">
                  Privacy Policy
                </Link>
              </li>
              <li>
                <Link href="/terms" className="hover:text-brand-primary transition-colors">
                  Terms of Service
                </Link>
              </li>
              <li>
                <Link href="/data-deletion" className="hover:text-brand-primary transition-colors">
                  Account & Data Deletion
                </Link>
              </li>
              <li>
                <Link href="/about" className="hover:text-brand-primary transition-colors">
                  About MNESA
                </Link>
              </li>
              <li>
                <Link href="/contact" className="hover:text-brand-primary transition-colors">
                  Contact & Inquiries
                </Link>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="mt-12 pt-8 border-t border-border-subtle dark:border-border-dark-subtle flex flex-col sm:flex-row items-center justify-between text-xs text-content-secondary dark:text-content-dark-secondary gap-4">
          <p>© 2026 MNESA Inc. All rights reserved.</p>
          <p className="font-medium text-content-primary dark:text-content-dark-primary">
            Capture it. We&apos;ll remember.
          </p>
          <div className="flex items-center space-x-4">
            <span className="text-[11px] px-2 py-0.5 rounded bg-surface-base dark:bg-surface-dark-base border border-border-subtle dark:border-border-dark-subtle font-mono">
              v0.9.0-preview
            </span>
            <a
              href="https://github.com/Balashanmugam30/mnesa"
              target="_blank"
              rel="noopener noreferrer"
              className="hover:text-brand-primary inline-flex items-center space-x-1"
            >
              <span>GitHub</span>
              <ExternalLink className="w-3 h-3" />
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
};
