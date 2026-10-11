"use client";

import React from "react";
import { usePathname } from "next/navigation";
import { Navbar } from "@/components/Navbar";
import { Footer } from "@/components/Footer";

export const ShellWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const pathname = usePathname();
  const isPwa = pathname === "/app" || pathname?.startsWith("/app/");

  if (isPwa) {
    return <div className="min-h-screen flex flex-col">{children}</div>;
  }

  return (
    <>
      <Navbar />
      <main id="main-content" role="main" className="flex-1">
        {children}
      </main>
      <Footer />
    </>
  );
};
