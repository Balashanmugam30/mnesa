import type { Metadata } from "next";
import "./globals.css";
import { Navbar } from "@/components/Navbar";
import { Footer } from "@/components/Footer";

export const metadata: Metadata = {
  title: "MNESA — Capture it. We'll remember.",
  description:
    "AI-powered opportunity capture and follow-through platform. Transform messy links, screenshots, and deadlines into structured, actionable opportunities in seconds.",
  metadataBase: new URL("https://mnesa.ai"),
  openGraph: {
    title: "MNESA — Capture it. We'll remember.",
    description: "Never miss another career or academic opportunity. Capture it. We'll remember.",
    url: "https://mnesa.ai",
    siteName: "MNESA",
    locale: "en_US",
    type: "website",
  },
  twitter: {
    card: "summary_large_image",
    title: "MNESA — Capture it. We'll remember.",
    description: "AI-powered opportunity capture and follow-through platform.",
  },
  robots: {
    index: true,
    follow: true,
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
        <Navbar />

        {/* Main Content Landmark */}
        <main id="main-content" role="main" className="flex-1">
          {children}
        </main>

        {/* Global Footer Landmark */}
        <Footer />
      </body>
    </html>
  );
}
