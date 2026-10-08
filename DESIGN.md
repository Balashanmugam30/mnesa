# MNESA Design System Specification

## 1. Aesthetic Identity & Design Philosophy

The MNESA visual design system embodies four core qualities:
1. **Bright & Premium:** Clean, high-clarity surfaces with refined elevation and energetic accent accents.
2. **Instant & Responsive:** Instantaneous visual feedback (< 200ms) for high-urgency user actions like capture.
3. **Calm & Focused:** Information-dense cards that reduce cognitive load rather than creating notification fatigue.
4. **Accessible by Default:** Strict WCAG 2.1 AA compliance (minimum 4.5:1 contrast for normal text, 3:1 for large text and UI controls).

---

## 2. Color Palette & Semantic Tokens

### 2.1 Brand & Core Tokens
| Token Name | Hex Code | Purpose & Usage |
| :--- | :--- | :--- |
| `brand-primary-600` | `#2563EB` | **Primary Brand Color.** Call-to-actions, active navigation, key brand accents. |
| `brand-primary-700` | `#1D4ED8` | Primary hover and pressed state. |
| `brand-primary-50`  | `#EFF6FF` | Primary light background tint for tags and selection rings. |
| `urgency-warning-500`| `#F59E0B`| **Deadline Imminent.** Approaching deadlines (3 to 7 days remaining). |
| `urgency-danger-500` | `#EF4444`| **Critical Deadline.** Deadlines expiring within 48 hours. |
| `status-success-500`| `#10B981` | Completed actions, verified extractions, comfortable deadlines (> 7 days). |
| `status-info-500`   | `#0EA5E9` | Informational badges and processing indicators. |

### 2.2 Neutral Scales (Slate)
| Token Name | Light Mode Hex | Dark Mode Hex | Usage |
| :--- | :--- | :--- | :--- |
| `surface-base` | `#FFFFFF` | `#0F172A` | Base window and page background |
| `surface-card` | `#F8FAFC` | `#1E293B` | Card surfaces and dialog backgrounds |
| `surface-elevated` | `#FFFFFF` | `#334155` | Elevated modals, dropdowns, capture bottom sheets |
| `border-subtle` | `#E2E8F0` | `#334155` | Card borders, dividers, list separators |
| `border-strong` | `#CBD5E1` | `#475569` | Input field borders, active outlines |
| `text-primary` | `#0F172A` | `#F8FAFC` | High-contrast body text and headings |
| `text-secondary` | `#475569` | `#94A3B8` | Subtitles, metadata, timestamps |
| `text-muted` | `#64748B` | `#64748B` | Placeholder text, disabled labels |

---

## 3. Typography Scale

MNESA uses modern system sans-serif typography (`Inter`, `-apple-system`, `Roboto`, `Segoe UI`):

| Level | Size (Web) | Size (Android) | Weight | Line Height | Tracking |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Display** | 36px (`text-4xl`) | 36sp | Bold (700) | 1.2 | -0.02em |
| **Heading 1** | 28px (`text-3xl`) | 28sp | Bold (700) | 1.25 | -0.015em |
| **Heading 2** | 22px (`text-2xl`) | 22sp | SemiBold (600) | 1.3 | -0.01em |
| **Heading 3** | 18px (`text-xl`) | 18sp | SemiBold (600) | 1.35 | 0 |
| **Body Large** | 16px (`text-base`) | 16sp | Regular / Medium | 1.5 | 0 |
| **Body Regular** | 14px (`text-sm`) | 14sp | Regular / Medium | 1.5 | 0 |
| **Caption** | 12px (`text-xs`) | 12sp | Medium (500) | 1.4 | +0.01em |
| **Overline / Badge** | 11px (`text-[11px]`) | 11sp | SemiBold (600) | 1.2 | +0.05em (UPPERCASE) |

---

## 4. Spacing, Radii & Elevation Tokens

### Spacing Scale
- `space-1`: `4px` / `4dp`
- `space-2`: `8px` / `8dp`
- `space-3`: `12px` / `12dp`
- `space-4`: `16px` / `16dp` (standard card padding)
- `space-5`: `20px` / `20dp`
- `space-6`: `24px` / `24dp` (section spacing)
- `space-8`: `32px` / `32dp`
- `space-12`: `48px` / `48dp` (**Minimum touch target dimension**)
- `space-16`: `64px` / `64dp`

### Corner Radii
- `radius-sm`: `6px` / `6dp` (badges, chips)
- `radius-md`: `10px` / `10dp` (input fields, buttons)
- `radius-lg`: `16px` / `16dp` (cards, dialogs)
- `radius-xl`: `24px` / `24dp` (bottom sheets)
- `radius-full`: `9999px` (pills, avatars)

### Elevation & Shadow Tokens
- `elevation-0`: Flat, bordered (`border border-subtle`)
- `elevation-1`: Subtle (`0 1px 3px rgba(0, 0, 0, 0.08)`) — Standard opportunity cards
- `elevation-2`: Raised (`0 4px 6px -1px rgba(0, 0, 0, 0.1)`) — Hovered cards, floating action buttons
- `elevation-3`: Modal (`0 10px 15px -3px rgba(0, 0, 0, 0.15)`) — Capture bottom sheet, dialogs

---

## 5. Reusable Component Primitives

### 5.1 Buttons
- **Primary:** Filled `#2563EB`, text `#FFFFFF`, radius `10px`, min-height `48px`. Hover `#1D4ED8`.
- **Secondary:** Surface `#F8FAFC`, border `#E2E8F0`, text `#0F172A`.
- **Ghost:** Transparent background, text `#2563EB`, hover `bg-blue-50`.
- **Danger:** Filled `#EF4444`, text `#FFFFFF`.

### 5.2 Opportunity Card
- **Layout:**
  - Header: Opportunity Type Badge (e.g., `INTERNSHIP`, `HACKATHON`) + Urgency / Deadline Badge.
  - Body: Opportunity Title (max 2 lines, truncated with ellipsis) + Organization name.
  - Footer: Extracted action link button + Reminder status indicator.
- **Urgency Pill Indicators:**
  - `> 7 days`: Green pill (`bg-emerald-50 text-emerald-700 border-emerald-200`)
  - `3 - 7 days`: Amber pill (`bg-amber-50 text-amber-700 border-amber-200`)
  - `< 48 hours`: Red pill with pulsing alert icon (`bg-rose-50 text-rose-700 border-rose-200`)

### 5.3 Opportunity Type Taxonomy
Standard badges formatted with consistent styling:
- `INTERNSHIP` — Indigo
- `JOB` — Blue
- `HACKATHON` — Purple
- `SCHOLARSHIP` — Emerald
- `COMPETITION` — Amber
- `CONFERENCE` — Cyan
- `COURSE` — Teal
- `EVENT` — Orange
- `OTHER` — Slate

---

## 6. Cross-Platform Mapping Reference

| Token Description | Android XML Resource Identifier | Next.js Tailwind Utility |
| :--- | :--- | :--- |
| Primary Color | `@color/mnesa_primary` | `bg-brand-primary` / `text-brand-primary` |
| Primary Dark / Pressed | `@color/mnesa_primary_variant` | `bg-brand-primary-hover` |
| Surface Background | `@color/mnesa_surface` | `bg-surface-base dark:bg-surface-dark-base` |
| Card Background | `@color/mnesa_card_surface` | `bg-surface-card dark:bg-surface-dark-card` |
| Card Border | `@color/mnesa_border_subtle` | `border-border-subtle` |
| Primary Text | `@color/mnesa_text_primary` | `text-text-primary dark:text-text-dark-primary` |
| Secondary Text | `@color/mnesa_text_secondary` | `text-text-secondary dark:text-text-dark-secondary` |
| Warning Accent | `@color/mnesa_urgency_warning`| `text-amber-500 bg-amber-50` |
| Danger Accent | `@color/mnesa_urgency_danger` | `text-red-500 bg-red-50` |
| Success Accent | `@color/mnesa_status_success` | `text-emerald-500 bg-emerald-50` |
| Standard Button Style | `@style/Widget.Mnesa.Button.Primary` | `<Button variant="primary">` |
| Opportunity Card Style | `@style/Widget.Mnesa.Card.Opportunity` | `<Card variant="opportunity">` |

---

## 7. Motion & Micro-Interactions

- **Duration:** 150ms to 250ms for micro-interactions (button presses, tag toggles); 250ms to 350ms for sheet entrances.
- **Easing:** `cubic-bezier(0.16, 1, 0.3, 1)` (ease-out spring feel).
- **Reduced Motion:** Honor `prefers-reduced-motion: reduce` on Web and system animation scale `0` on Android by removing transforms and using subtle opacity fades.
