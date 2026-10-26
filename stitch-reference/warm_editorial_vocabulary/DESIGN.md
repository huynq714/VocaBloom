---
name: Warm Editorial Vocabulary
colors:
  surface: '#fbf9f6'
  surface-dim: '#dbdad7'
  surface-bright: '#fbf9f6'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f5f3f0'
  surface-container: '#efeeeb'
  surface-container-high: '#eae8e5'
  surface-container-highest: '#e4e2df'
  on-surface: '#1b1c1a'
  on-surface-variant: '#5a4139'
  inverse-surface: '#30312f'
  inverse-on-surface: '#f2f0ed'
  outline: '#8d7168'
  outline-variant: '#e2bfb5'
  surface-tint: '#ad3300'
  primary: '#a93200'
  on-primary: '#ffffff'
  primary-container: '#ce4613'
  on-primary-container: '#fffbff'
  inverse-primary: '#ffb59e'
  secondary: '#5b5f5d'
  on-secondary: '#ffffff'
  secondary-container: '#dde0dd'
  on-secondary-container: '#606361'
  tertiary: '#a83200'
  on-tertiary: '#ffffff'
  tertiary-container: '#cc4918'
  on-tertiary-container: '#fffbff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdbd0'
  primary-fixed-dim: '#ffb59e'
  on-primary-fixed: '#390b00'
  on-primary-fixed-variant: '#842500'
  secondary-fixed: '#e0e3e0'
  secondary-fixed-dim: '#c4c7c4'
  on-secondary-fixed: '#181c1b'
  on-secondary-fixed-variant: '#444845'
  tertiary-fixed: '#ffdbd0'
  tertiary-fixed-dim: '#ffb59d'
  on-tertiary-fixed: '#390b00'
  on-tertiary-fixed-variant: '#842500'
  background: '#fbf9f6'
  on-background: '#1b1c1a'
  surface-variant: '#e4e2df'
typography:
  display-lg:
    fontFamily: Literata
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Literata
    fontSize: 26px
    fontWeight: '600'
    lineHeight: 34px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Literata
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 30px
    letterSpacing: 0em
  headline-sm:
    fontFamily: Literata
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 26px
    letterSpacing: 0em
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.01em
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 22px
    letterSpacing: 0.01em
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 18px
    letterSpacing: 0.02em
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.02em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.03em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.04em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system blends an editorial literary sensibility with modern Android Material 3 ergonomics. Designed primarily for focused language acquisition on compact and standard mobile viewports (calibrated specifically for the Google Pixel 4a form factor), it evokes the tactility of a beautifully typeset pocket dictionary combined with the fluid, rewarding feedback of a contemporary digital tutor.

### Target Audience & Tone
- **Audience:** Vietnamese learners, university students, and self-directed bilinguals who appreciate focused, uncluttered study spaces.
- **Personality:** Scholarly yet lively, dependable, warm, and encouraging.
- **Emotional Response:** Inspires calm cognitive retention, deep focus without visual fatigue, and deliberate micro-rewards through warm chromatic feedback.

### Visual Aesthetic
A refined blend of **Editorial Modernism** and **Tactile Material**:
- High-contrast typography featuring warm humanist serifs for terms and definitions, paired with ultra-legible geometric grotesques for body mechanics and functional metadata.
- Warm paper-like surfaces (`#FAF8F5`, `#F7F4EE`) that replace clinical cold whites, softening blue-light glare during evening learning rituals.
- High-intent burnt orange accents (`#E05320`) to communicate progress, action, and mnemonic emphasis.

## Colors

The palette grounds itself in natural, bookish warmth while maintaining accessibility compliance (minimum WCAG AA, targeting AAA for core reading copy).

### Primary System Roles
- **Primary Accent (`#E05320` - Burnt Orange):** Action triggers, active audio playback states, streaks, and flashcard completion states.
- **Primary Interactive Deep (`#C84615`):** Pressed/hover state for primary surfaces, focused underlines, and milestone badges.
- **Secondary Tone (`#2C302E` - Deep Charcoal Muted):** Structural outlines, secondary actions, auxiliary metadata, and pill chip borders.
- **Neutral Canvas Surface (`#FAF8F5` - Warm Ivory Base):** The primary window viewport background.
- **Neutral Surface Container (`#F7F4EE` - Warm Ivory Tone):** App bars, bottom sheets, navigation bars, and search rails.
- **Neutral Surface Card (`#FFFFFF` - Pure White):** Discrete card surfaces, interactive flashcards, and modal sheets.

### Semantic & Contrast Hierarchy
- **On-Background / Heading Text (`#1E2320`):** High-contrast deep charcoal, ensuring razor-sharp rendering on OLED displays without the harshness of `#000000`.
- **Body & Subtitle Copy (`#3A403C`):** Softened deep charcoal, maintaining legibility across dense linguistic explanations.
- **Surface Outline / Divider (`#E5E0D8`):** Organic, low-contrast structural borders separating cards and section rows.
- **Mnemonic Success (`#2E6B47`):** Muted laurel green for correct answers and mastery indicators.
- **Mnemonic Error (`#B83227`):** Warm crimson for review requirements and incorrect attempts.

## Typography

The typographic hierarchy uses **Literata** for key word displays, phonetic blocks, and literary context sentences, lending literary authority to vocabulary study. **Plus Jakarta Sans** powers navigational wayfinding, interactive UI elements, and Vietnamese descriptive copy, ensuring diacritics (dấu thanh, dấu mũ, móc) sit naturally without vertical clipping.

### Font Roles & Tone
- **Display & Headlines (Literata):** Used for vocabulary headwords, daily quote titles, and milestone banners. Renders tone marks cleanly with classical bookish proportions.
- **Body & Captions (Plus Jakarta Sans):** Applied to Vietnamese definitions, grammatical notes, sample sentences, and system prompts. Generous x-height prevents eye strain during sustained reading.
- **Labels & Microcopy (Plus Jakarta Sans):** Capitalized or semi-bold tags (e.g., *DANH TỪ*, *ĐỘNG TỪ*, *CẤP ĐỘ B2*), navigation items, and action buttons.

## Layout & Spacing

Calibrated around a strict **4dp/8dp base grid** tailored to a 393×851dp viewport (Pixel 4a at 1080×2340 physical resolution):

### Viewport & Grid Rhythm
- **Outer Margins:** Fixed 16dp (`1rem`) lateral margin ensures comfortable single-thumb operation while preventing content encroachment on gesture edges.
- **Gutters:** 16dp (`1rem`) horizontal space between split flashcards or grid tiles.
- **Vertical Flow:** Standard 8dp micro-steps (`space-sm`) for compact metadata grouping; 16dp (`space-md`) between distinct content modules; 24dp (`space-lg`) between logical lesson phases.
- **Ergonomic Safe Zones:** Bottom navigation bar maintains a 56dp height stacked over Android 34 gesture navigation bars with an explicit 16dp clearance buffer.

## Elevation & Depth

Visual depth follows a tactile, print-inspired depth model: layered cards set upon warm surface canvases rather than generic stark drop shadows.

### Elevation Scales
- **Level 0 (Flat / Canvas):** Neutral background (`#FAF8F5`). Borders only (`1dp solid #E5E0D8`) for inline dividers and disabled elements.
- **Level 1 (Resting Cards & Tiles):** Pure white surfaces (`#FFFFFF`) with a dual-layer ambient shadow:
  - `0px 2px 6px rgba(30, 35, 32, 0.04), 0px 1px 2px rgba(30, 35, 32, 0.03)`
  - Encased with a delicate bounding border: `1dp solid #EEE9DF`.
- **Level 2 (Active Flashcards & Interactive Sheets):** Raised learning items and active selections:
  - `0px 6px 14px rgba(30, 35, 32, 0.08), 0px 2px 4px rgba(30, 35, 32, 0.04)`
  - Bounding border: `1dp solid #E5E0D8`.
- **Level 3 (Modals & Bottom Drawers):** Vocabulary lookup drawers and context definitions:
  - `0px 12px 28px rgba(30, 35, 32, 0.12), 0px 4px 8px rgba(30, 35, 32, 0.06)`.

## Shapes

The shape system employs roundedness scale **2**, instilling a friendly, approachable atmosphere while preserving a disciplined editorial structure.

### Component Radii Mapping
- **Buttons (Primary & Secondary):** 16dp (`rounded-lg`) corner radii, creating a modern rounded-slab profile that maximizes touch surface clarity.
- **Pill Chips & Badges:** Full-radius pill containers (`9999px`) for grammatical roles, topic tags, and audio pronunciation toggles.
- **Flashcards & Study Modules:** 16dp (`rounded-lg`) corner radii for central learning cards, providing balanced soft framing.
- **Input Fields & Search Rails:** 12dp rounded surfaces for tactile, distinct form elements.

## Components

All components respect a **minimum 48×48dp interactive bounding box** conforming to Android Touch Ergonomics.

### Buttons
- **Primary Action (e.g., "Tiếp tục", "Kiểm tra"):**
  - Height: 52dp. Full horizontal width or fluid modal width.
  - Surface: Burnt Orange (`#E05320`), text: Pure White (`#FFFFFF`, `label-lg`).
  - Active/Pressed: `#C84615`. Elevation: Level 1 with 1dp border inset (`rgba(255,255,255,0.2)`).
- **Secondary Action (e.g., "Bỏ qua", "Nghe lại"):**
  - Height: 48dp.
  - Surface: Warm Ivory Tone (`#F7F4EE`), text: Deep Charcoal (`#1E2320`, `label-lg`).
  - Border: 1dp solid `#E5E0D8`.

### Chips & Badges
- **Part-of-Speech Pill (e.g., "Danh từ", "Động từ"):**
  - Height: 28dp. Padding: 4dp horizontal, 10dp vertical.
  - Surface: `#F7F4EE`, Border: 1dp solid `#DCD6CB`.
  - Text: Deep Charcoal Muted (`#2C302E`, `label-sm`).
- **Streak & Mastery Badge:**
  - Surface: Burnt Orange wash (`rgba(224, 83, 32, 0.12)`).
  - Text: `#E05320` (`label-md`). Icon: 16dp inline fire/blossom icon.

### Flashcard & Study Card
- **Vocabulary Card:**
  - Base: `#FFFFFF`, elevation: Level 1, border: 1dp solid `#EEE9DF`.
  - Padding: 20dp internal.
  - Headword: Literata 26px (`headline-lg`), `#1E2320`.
  - Phonetic transcript: Plus Jakarta Sans 14px (`body-md`), `#E05320`.
  - Vietnamese definition: Plus Jakarta Sans 16px (`body-lg`), `#3A403C`.

### Lists & Vocabulary Items
- **List Item Row:**
  - Minimum height: 64dp.
  - Padding: 12dp horizontal, 12dp vertical.
  - Border-bottom: 1dp solid `#F0EBE2`.
  - Right accessory: Pronunciation speaker button (48×48dp touch container with 24dp vector icon).

### Inputs & Search Bars
- **Search Field:**
  - Height: 52dp. Surface: `#FFFFFF`. Border: 1.5dp solid `#E5E0D8`.
  - Placeholder: "Tìm từ vựng, ngữ pháp..." in `#717873`.
  - Focus state: Border color transitions to `#E05320` with zero layout shift.

### Checkboxes & Multiple Choice Option Selectors
- **Option Container:**
  - Minimum height: 56dp. Border: 1.5dp solid `#E5E0D8`. Radius: 12dp.
  - Unselected: Background `#FFFFFF`, text `#1E2320`.
  - Selected state: Background `rgba(224, 83, 32, 0.06)`, border `2dp solid #E05320`, text `#E05320`.