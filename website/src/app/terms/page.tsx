import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { FileText, AlertTriangle, Scale, ShieldAlert, CheckCircle } from "lucide-react";

export const metadata = {
  title: "Terms of Service — MNESA",
  description: "Terms and conditions governing the use of MNESA mobile application and web platform.",
};

export default function TermsOfServicePage() {
  return (
    <div className="py-12 sm:py-20">
      <Container size="lg">
        {/* Header */}
        <div className="border-b border-border-subtle dark:border-border-dark-subtle pb-8">
          <div className="inline-flex items-center space-x-2 text-xs font-semibold text-brand-primary uppercase tracking-wider mb-3">
            <FileText className="w-4 h-4" />
            <span>Legal Agreement</span>
          </div>
          <Heading level={1} className="text-3xl sm:text-4xl font-extrabold tracking-tight">
            MNESA Terms of Service
          </Heading>
          <p className="mt-3 text-sm text-content-secondary dark:text-content-dark-secondary">
            Last Updated: October 9, 2026 • Version 1.0 (Early Access Preview)
          </p>

          <div className="mt-4 p-4 rounded-lg bg-amber-50 dark:bg-amber-950/40 border border-amber-200 dark:border-amber-900 text-xs text-urgency-warning dark:text-amber-300 flex items-start space-x-2">
            <AlertTriangle className="w-4 h-4 mt-0.5 flex-shrink-0" />
            <span>
              <strong>Crucial AI Deadline Disclaimer:</strong> MNESA provides automated AI extraction to help you organize opportunities. You acknowledge and agree that automated algorithms can occasionally misinterpret ambiguous dates or formatting. You are solely responsible for independently verifying critical application deadlines directly with the originating program or organization.
            </span>
          </div>
        </div>

        {/* Sections */}
        <div className="mt-10 space-y-12 text-sm text-content-primary dark:text-content-dark-primary leading-relaxed">
          {/* Section 1 */}
          <section aria-labelledby="section-acceptance">
            <h2 id="section-acceptance" className="text-xl font-bold mb-4 flex items-center space-x-2">
              <span className="text-brand-primary font-mono text-base">01.</span>
              <span>Acceptance of Terms</span>
            </h2>
            <p className="text-content-secondary dark:text-content-dark-secondary">
              By downloading, installing, accessing, or using the MNESA mobile application, web dashboard, or associated APIs (collectively, the &quot;Service&quot;), you agree to be bound by these Terms of Service. If you do not agree to these terms, you must discontinue using MNESA and uninstall the application.
            </p>
          </section>

          {/* Section 2 */}
          <section aria-labelledby="section-description">
            <h2 id="section-description" className="text-xl font-bold mb-4 flex items-center space-x-2">
              <span className="text-brand-primary font-mono text-base">02.</span>
              <span>Description of Service & Early Access</span>
            </h2>
            <p className="text-content-secondary dark:text-content-dark-secondary">
              MNESA is an opportunity capture and follow-through tool that allows users to ingest links, screenshots, and text to extract deadline schedules, track application status, and receive reminder notifications. The Service is currently provided as an early-access preview build. Features and interfaces may be enhanced or updated over time.
            </p>
          </section>

          {/* Section 3 */}
          <section aria-labelledby="section-ai-disclaimer">
            <h2 id="section-ai-disclaimer" className="text-xl font-bold mb-4 flex items-center space-x-2">
              <span className="text-brand-primary font-mono text-base">03.</span>
              <span>Automated Extraction & User Verification Obligations</span>
            </h2>
            <div className="p-4 rounded-lg bg-surface-card dark:bg-surface-dark-card border border-border-subtle dark:border-border-dark-subtle space-y-3">
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary">
                • <strong>Evidence Grounding:</strong> MNESA highlights extracted source sentences with confidence scores to facilitate user review. However, MNESA does not guarantee 100% accuracy of machine learning models.
              </p>
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary">
                • <strong>No Fiduciary or Advisory Relationship:</strong> MNESA is not an academic advisor, career counselor, or legal representative. We do not submit applications on your behalf unless explicitly directed via user-initiated integrations.
              </p>
              <p className="text-xs text-content-secondary dark:text-content-dark-secondary">
                • <strong>User Responsibility:</strong> Missing an application deadline due to AI parsing delays, incorrect timezone interpretation, device notification suppression, or network failure is not the legal responsibility of MNESA Inc.
              </p>
            </div>
          </section>

          {/* Section 4 */}
          <section aria-labelledby="section-acceptable-use">
            <h2 id="section-acceptable-use" className="text-xl font-bold mb-4 flex items-center space-x-2">
              <span className="text-brand-primary font-mono text-base">04.</span>
              <span>Acceptable Use Policy</span>
            </h2>
            <p className="text-content-secondary dark:text-content-dark-secondary mb-3">
              You agree not to misuse the Service. Specifically, you may not:
            </p>
            <ul className="list-disc list-inside space-y-2 text-xs text-content-secondary dark:text-content-dark-secondary">
              <li>Submit URLs that point to malware, phishing schemes, or private internal network endpoints.</li>
              <li>Attempt to reverse-engineer, decompile, or extract the source code of our backend or AI service.</li>
              <li>Bypass rate limits, security headers, or authentication tokens.</li>
              <li>Inject adversarial prompt attacks or harmful instructions into the extraction parser.</li>
            </ul>
          </section>

          {/* Section 5 */}
          <section aria-labelledby="section-intellectual-property">
            <h2 id="section-intellectual-property" className="text-xl font-bold mb-4 flex items-center space-x-2">
              <span className="text-brand-primary font-mono text-base">05.</span>
              <span>User Content & Intellectual Property</span>
            </h2>
            <p className="text-content-secondary dark:text-content-dark-secondary">
              You retain full ownership of all links, notes, images, and opportunities you capture within MNESA. You grant MNESA a limited license to process and display this content solely to provide the Service to you. MNESA software, branding, and design tokens remain the exclusive intellectual property of MNESA Inc.
            </p>
          </section>

          {/* Section 6 */}
          <section aria-labelledby="section-limitation-liability">
            <h2 id="section-limitation-liability" className="text-xl font-bold mb-4 flex items-center space-x-2">
              <span className="text-brand-primary font-mono text-base">06.</span>
              <span>Limitation of Liability</span>
            </h2>
            <p className="text-content-secondary dark:text-content-dark-secondary">
              TO THE MAXIMUM EXTENT PERMITTED BY LAW, MNESA AND ITS AFFILIATES SHALL NOT BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, CONSEQUENTIAL, OR PUNITIVE DAMAGES, OR ANY LOSS OF PROFITS, DATA, OR OPPORTUNITIES RESULTING FROM YOUR ACCESS TO OR INABILITY TO USE THE SERVICE.
            </p>
          </section>

          {/* Section 7 */}
          <section aria-labelledby="section-termination">
            <h2 id="section-termination" className="text-xl font-bold mb-4 flex items-center space-x-2">
              <span className="text-brand-primary font-mono text-base">07.</span>
              <span>Termination & Modifications</span>
            </h2>
            <p className="text-content-secondary dark:text-content-dark-secondary">
              We reserve the right to suspend or terminate accounts that violate this policy. You may terminate your account at any time by utilizing the data deletion tool in your account settings.
            </p>
          </section>

          {/* Section 8 */}
          <section aria-labelledby="section-contact-terms">
            <h2 id="section-contact-terms" className="text-xl font-bold mb-4 flex items-center space-x-2">
              <span className="text-brand-primary font-mono text-base">08.</span>
              <span>Contact Information</span>
            </h2>
            <p className="text-content-secondary dark:text-content-dark-secondary">
              For questions regarding these Terms of Service, contact our legal team at{" "}
              <a href="mailto:legal@mnesa.ai" className="text-brand-primary font-semibold hover:underline">
                legal@mnesa.ai
              </a>.
            </p>
          </section>
        </div>
      </Container>
    </div>
  );
}
