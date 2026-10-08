---
name: mnesa-design-system
description: MNESA Design System principles covering bright premium UI, accessibility, typography, consistent components, and motion guidelines.
---

# MNESA Design System Guidelines

## 1. Aesthetic Identity
- **Visual Tone:** Bright, premium, modern, and trustworthy. High-contrast typography and polished micro-interactions.
- **Color Tokens:**
  - Primary: Energetic Deep Indigo (`#2563EB`)
  - Accent / Urgency: Vibrant Amber / Coral for approaching deadlines (`#F59E0B`, `#EF4444`)
  - Success: Crisp Emerald (`#10B981`)
  - Surface: Pure Light / High-contrast Dark mode variants

## 2. Accessibility & Typography
- Minimum contrast ratio of 4.5:1 (WCAG AA compliant) for all text and UI controls.
- Touch targets must be at least 48x48dp on Android.
- Support dynamic system font scaling gracefully without text truncation.

## 3. Motion Principles
- Subtle, purposeful animations using Framer Motion (Web) and Material Motion (Android).
- Duration between 150ms and 300ms using ease-out easing curves for screen transitions and expand states.
