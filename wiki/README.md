# Local Wiki

This directory contains a DokuWiki setup so the project has a second, tutorial-heavy documentation layer separate from the GitHub Pages site.

## Start the wiki

```bash
cd wiki
docker compose up
```

Then open:

- `http://localhost:8080`

## Content location

- `wiki/content/pages/` contains the DokuWiki page source files.

## Why there is both a docs site and a wiki

- The GitHub Pages docs are structured and reviewable.
- The DokuWiki layer is more exploratory and tutorial-like.
