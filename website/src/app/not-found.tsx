import React from "react";
import Link from "next/link";
import { Container } from "@/components/ui/Container";
import { Heading } from "@/components/ui/Heading";
import { Button } from "@/components/ui/Button";
import { AlertCircle, ArrowLeft, Home, Compass } from "lucide-react";

export default function NotFound() {
  return (
    <div className="py-20 lg:py-32 flex items-center justify-center">
      <Container size="md" className="text-center">
        <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-blue-50 dark:bg-blue-950/60 border border-blue-200 dark:border-blue-800 text-brand-primary mb-6">
          <AlertCircle className="w-8 h-8" />
        </div>

        <span className="text-xs font-mono font-bold uppercase tracking-widest text-brand-primary">
          Error 404
        </span>

        <Heading level={1} className="text-3xl sm:text-4xl font-extrabold mt-2 tracking-tight">
          Page Not Found
        </Heading>

        <p className="mt-4 text-base text-content-secondary dark:text-content-dark-secondary max-w-lg mx-auto leading-relaxed">
          The opportunity or destination you are searching for does not exist, has moved, or the link may have expired.
        </p>

        <div className="mt-8 flex flex-wrap justify-center gap-4">
          <Link href="/">
            <Button variant="primary" size="md" className="space-x-2">
              <Home className="w-4 h-4" />
              <span>Return to Home</span>
            </Button>
          </Link>
          <Link href="/features">
            <Button variant="secondary" size="md" className="space-x-2">
              <Compass className="w-4 h-4" />
              <span>Explore Features</span>
            </Button>
          </Link>
        </div>

        <div className="mt-12 pt-8 border-t border-border-subtle dark:border-border-dark-subtle text-xs text-content-secondary dark:text-content-dark-secondary">
          Need assistance? Visit our{" "}
          <Link href="/contact" className="text-brand-primary font-semibold hover:underline">
            Contact Support
          </Link>{" "}
          or review our{" "}
          <Link href="/faq" className="text-brand-primary font-semibold hover:underline">
            FAQ
          </Link>.
        </div>
      </Container>
    </div>
  );
}
