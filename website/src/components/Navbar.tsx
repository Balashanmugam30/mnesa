"use client";

import React, { useState, useEffect } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { ThemeToggle } from "@/components/ui/ThemeToggle";
import { Menu, X, ArrowRight, ShieldCheck, Download, Sparkles } from "lucide-react";

export const Navbar: React.FC = () => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const pathname = usePathname();

  // Close mobile menu on route change
  useEffect(() => {
    setMobileMenuOpen(false);
  }, [pathname]);

  // Prevent scroll when mobile menu is open
  useEffect(() => {
    if (mobileMenuOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "unset";
    }
    return () => {
      document.body.style.overflow = "unset";
    };
  }, [mobileMenuOpen]);

  const navLinks = [
    { label: "How It Works", href: "/how-it-works" },
    { label: "Features", href: "/features" },
    { label: "AI Engine", href: "/ai" },
    { label: "Live Demo", href: "/demo", highlight: true },
    { label: "Security & Privacy", href: "/security" },
    { label: "Pricing", href: "/pricing" },
    { label: "FAQ", href: "/faq" },
    { label: "Download", href: "/download" },
  ];

  return (
    <header
      role="banner"
      className="border-b border-border-subtle dark:border-border-dark-subtle bg-surface-base/90 dark:bg-surface-dark-base/90 backdrop-blur-md sticky top-0 z-40"
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand / Logo */}
        <Link
          href="/"
          className="flex items-center space-x-3 group focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-primary rounded-lg p-1"
          aria-label="MNESA Home"
        >
          <div className="w-9 h-9 rounded-lg bg-brand-primary flex items-center justify-center text-white font-extrabold text-xl shadow-sm transition-transform group-hover:scale-105">
            M
          </div>
          <div className="flex flex-col">
            <span className="font-extrabold text-xl tracking-tight text-content-primary dark:text-content-dark-primary">
              MNESA
            </span>
          </div>
        </Link>

        {/* Desktop Navigation */}
        <nav
          aria-label="Main Navigation"
          className="hidden lg:flex items-center space-x-1 xl:space-x-2"
        >
          {navLinks.map((link) => {
            const isActive = pathname === link.href;
            return (
              <Link
                key={link.href}
                href={link.href}
                className={`px-3 py-2 rounded-md text-xs font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-primary ${
                  isActive
                    ? "text-brand-primary bg-brand-primary-light dark:bg-blue-950/80 font-bold"
                    : "text-content-secondary dark:text-content-dark-secondary hover:text-content-primary dark:hover:text-content-dark-primary hover:bg-slate-100 dark:hover:bg-slate-800"
                } ${link.highlight ? "inline-flex items-center text-brand-primary font-bold" : ""}`}
              >
                {link.highlight && <Sparkles className="w-3.5 h-3.5 mr-1 text-brand-primary" />}
                {link.label}
              </Link>
            );
          })}
        </nav>

        {/* Action Controls & CTA */}
        <div className="hidden sm:flex items-center space-x-3">
          <ThemeToggle />
          <Link
            href="/signin"
            className="text-xs font-semibold px-3 py-2 rounded-md text-content-secondary dark:text-content-dark-secondary hover:text-content-primary dark:hover:text-content-dark-primary hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
          >
            Sign In
          </Link>
          <Link
            href="/download"
            className="inline-flex items-center space-x-1.5 bg-brand-primary hover:bg-brand-primary-hover active:bg-blue-800 text-white text-xs font-bold px-3.5 py-2 rounded-md shadow-sm transition-all hover:shadow"
          >
            <Download className="w-3.5 h-3.5" />
            <span>Get Android App</span>
          </Link>
        </div>

        {/* Mobile Menu Button */}
        <div className="flex items-center space-x-2 sm:hidden">
          <ThemeToggle />
          <button
            type="button"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="p-2 rounded-md text-content-secondary dark:text-content-dark-secondary hover:text-content-primary dark:hover:text-content-dark-primary hover:bg-slate-100 dark:hover:bg-slate-800 focus:outline-none focus:ring-2 focus:ring-brand-primary"
            aria-expanded={mobileMenuOpen}
            aria-label="Toggle navigation menu"
          >
            {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>
        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div className="sm:hidden border-b border-border-subtle dark:border-border-dark-subtle bg-surface-base dark:bg-surface-dark-base px-4 pt-3 pb-6 space-y-2">
          <nav aria-label="Mobile Navigation" className="flex flex-col space-y-1">
            {navLinks.map((link) => (
              <Link
                key={link.href}
                href={link.href}
                className="px-3 py-2.5 rounded-md text-sm font-medium text-content-primary dark:text-content-dark-primary hover:bg-slate-100 dark:hover:bg-slate-800 flex items-center justify-between"
              >
                <span>{link.label}</span>
                {link.highlight && <Sparkles className="w-4 h-4 text-brand-primary" />}
              </Link>
            ))}
          </nav>
          <div className="pt-4 border-t border-border-subtle dark:border-border-dark-subtle flex flex-col space-y-2">
            <Link
              href="/signin"
              className="w-full text-center py-2.5 px-4 text-sm font-semibold rounded-md border border-border-subtle dark:border-border-dark-subtle text-content-primary dark:text-content-dark-primary hover:bg-slate-50 dark:hover:bg-slate-800"
            >
              Sign In
            </Link>
            <Link
              href="/download"
              className="w-full text-center py-2.5 px-4 text-sm font-bold rounded-md bg-brand-primary text-white hover:bg-brand-primary-hover shadow-sm inline-flex items-center justify-center space-x-2"
            >
              <Download className="w-4 h-4" />
              <span>Get Android App</span>
            </Link>
          </div>
        </div>
      )}
    </header>
  );
};
