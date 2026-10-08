import type { Metadata } from "next";
import "./globals.css";
import { ThemeToggle } from "@/components/ui/ThemeToggle";

export const metadata: Metadata = {
  title: "MNESA — Capture it. We'll remember.",
  description:
    "AI-powered opportunity capture and follow-through platform. Never miss an internship, job, hackathon, or scholarship again.",
  metadataBase: new URL("https://mnesa.ai"),
  openGraph: {
    title: "MNESA — Capture it. We'll remember.",
    description: "Never miss what matters.",
    siteName: "MNESA",
    type: "website",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className="font-sans min-h-screen flex flex-col bg-surface-base dark:bg-surface-dark-base text-content-primary dark:text-content-dark-primary antialiased">
        {/* Skip to Content Link for WCAG Accessibility */}
        <a
          href="#main-content"
          className="sr-only focus:not-sr-only focus:absolute focus:top-4 focus:left-4 z-50 bg-brand-primary text-white px-4 py-2 rounded-md font-semibold shadow-lg"
        >
          Skip to main content
        </a>

        {/* Global Navigation Shell */}
        <header role="banner" className="border-b border-border-subtle dark:border-border-dark-subtle bg-surface-base/80 dark:bg-surface-dark-base/80 backdrop-blur-md sticky top-0 z-40">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="w-9 h-9 rounded-lg bg-brand-primary flex items-center justify-center text-white font-extrabold text-xl shadow-sm">
                M
              </div>
              <span className="font-bold text-xl tracking-tight text-content-primary dark:text-content-dark-primary">
                MNESA
              </span>
            </div>

            <nav aria-label="Main Navigation" className="flex items-center space-x-4">
              <span className="hidden sm:inline-block text-xs font-semibold px-2.5 py-1 rounded-full bg-brand-primary-light dark:bg-blue-950 text-brand-primary dark:text-blue-300">
                Phase 01 Active
              </span>
              <ThemeToggle />
            </nav>
          </div>
        </header>

        {/* Main Content Landmark */}
        <main id="main-content" role="main" className="flex-1">
          {children}
        </main>

        {/* Global Footer Landmark */}
        <footer role="contentinfo" className="border-t border-border-subtle dark:border-border-dark-subtle py-8 bg-surface-card dark:bg-surface-dark-card">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col sm:flex-row items-center justify-between text-xs text-content-secondary dark:text-content-dark-secondary">
            <p>© 2026 MNESA Inc. All rights reserved.</p>
            <p className="mt-2 sm:mt-0 font-medium">Capture it. We&apos;ll remember.</p>
          </div>
        </footer>
      </body>
    </html>
  );
}
