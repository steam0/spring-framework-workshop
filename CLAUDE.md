# Spring Framework Workshop

## Workflow

1. **presentation.md** is the manuscript and source of truth. Each `#` heading is a topic/page. All content changes go here first.
2. When the user asks to **render**, read `presentation.md` and generate the static website:
   - `index.html` — landing page with topic navigation buttons
   - One HTML file per `#` heading (e.g. `ioc.html`, `beans.html`)
   - `styles.css` — shared stylesheet
   - Each page has: header, page title, content, prev/next navigation
3. If **design.md** exists, read it and apply its design instructions to the rendered HTML (colors, fonts, spacing, component styles, etc.).
4. Never update HTML files without first updating `presentation.md` (unless the change is purely design/CSS).

## File Structure

- `presentation.md` — source of truth (manuscript)
- `design.md` — design system instructions
- `styles.css` — shared CSS
- `index.html` — landing page with topic buttons
- `<topic>.html` — one page per topic/slide
