# Pi Searcher

A mobile-friendly progressive web app for searching the digits of π.

## Features
- Search numeric sequences and display every found position (up to the selected result limit).
- Encode letters with A=01 through Z=26; spaces become 00.
- Search 10K, 100K, 1M, 5M, 10M or a custom amount (up to 10 million digits).
- Highlight matches with surrounding context.
- Digit explorer with position jump and copy.
- Random number, recent search history and favourites/save-to-history.
- IndexedDB chunk cache for previously loaded digits and offline reuse.
- Installable PWA shell.

## Run
Open `index.html` from a static web host (GitHub Pages works). The app retrieves digits in 100,000-digit chunks from the pi.delivery API and caches them in the browser's IndexedDB. First-time downloads require internet. The 10M option means the app loads the selected range on demand; it does not ship a 10MB digit dump in the Git repository.

## Deploy with GitHub Pages
In repository Settings → Pages, choose Deploy from a branch, select `main` and `/ (root)`, then save.

## Digit indexing
Positions are counted from the first digit after the decimal point: position 1 is the 1 in 3.14.
