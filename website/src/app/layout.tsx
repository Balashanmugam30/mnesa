import type { Metadata, Viewport } from "next";
import "./globals.css";
import { ShellWrapper } from "@/components/ShellWrapper";

export const viewport: Viewport = {
  themeColor: "#2563EB",
  width: "device-width",
  initialScale: 1,
  maximumScale: 5,
  viewportFit: "cover",
};

export const metadata: Metadata = {
  title: "MNESA — Capture it. We'll remember.",
  description:
    "AI-powered opportunity capture and follow-through platform. Transform messy links, screenshots, and deadlines into structured, actionable opportunities in seconds.",
  metadataBase: new URL("https://mnesa.ai"),
  manifest: "/manifest.json",
  icons: {
    icon: [
      { url: "/favicon.svg", type: "image/svg+xml" },
      { url: "/icons/icon-192.png", sizes: "192x192", type: "image/png" },
    ],
    apple: [
      { url: "/icons/apple-touch-icon.png", sizes: "180x180", type: "image/png" },
    ],
  },
  appleWebApp: {
    capable: true,
    statusBarStyle: "default",
    title: "MNESA",
  },
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
        <ShellWrapper>{children}</ShellWrapper>
      </body>
    </html>
  );
}
