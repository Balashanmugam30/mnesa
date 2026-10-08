import type { Config } from "tailwindcss";

const config: Config = {
  darkMode: "class",
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          primary: "#2563EB",
          "primary-hover": "#1D4ED8",
          "primary-light": "#EFF6FF",
          "on-primary": "#FFFFFF",
        },
        urgency: {
          warning: "#F59E0B",
          "warning-light": "#FEF3C7",
          danger: "#EF4444",
          "danger-light": "#FEE2E2",
        },
        status: {
          success: "#10B981",
          "success-light": "#D1FAE5",
          info: "#0EA5E9",
          "info-light": "#E0F2FE",
        },
        surface: {
          base: "#FFFFFF",
          card: "#F8FAFC",
          elevated: "#FFFFFF",
          "dark-base": "#0F172A",
          "dark-card": "#1E293B",
          "dark-elevated": "#334155",
        },
        border: {
          subtle: "#E2E8F0",
          strong: "#CBD5E1",
          "dark-subtle": "#334155",
          "dark-strong": "#475569",
        },
        content: {
          primary: "#0F172A",
          secondary: "#475569",
          muted: "#64748B",
          "dark-primary": "#F8FAFC",
          "dark-secondary": "#94A3B8",
          "dark-muted": "#64748B",
        },
      },
      borderRadius: {
        sm: "6px",
        md: "10px",
        lg: "16px",
        xl: "24px",
      },
      minHeight: {
        touch: "48px",
      },
      minWidth: {
        touch: "48px",
      },
    },
  },
  plugins: [],
};

export default config;
