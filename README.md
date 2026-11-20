# Pi Searcher

A mobile-first progressive web app for searching and exploring the decimal digits of π.

## Features

- Search numeric sequences, with overlapping matches and configurable result limits.
- Convert words to number sequences using A=01 through Z=26; spaces become 00.
- Search 10K, 100K, 1M, 5M, 10M, or up to 100M digits using a local dataset.
- Match snippets, exact positions, elapsed scan time, copy and export results.
- Digit explorer with direct position jumps, paging, copying and bookmarks.
- Digit laboratory with frequency counts, a chi-square comparison and common adjacent pairs.
- Local notebook for saved searches and positions, with JSON export.
- IndexedDB cache for downloaded 100,000-digit blocks.
- Seven selectable visual themes: Midnight, Hacker, Christmas, GTA VI, Deep Ocean, Sunset and Light.\n- Animated theme effects including falling Christmas snow, Hacker terminal rain and Ocean rain.\n- Installable PWA shell and responsive mobile layout.

## Run it

The app is static HTML, CSS and JavaScript. It has no build step or package dependencies.

- Open the GitHub Pages site after deployment, or
- Serve the repository root with any static web server.

The GitHub Actions workflow in `.github/workflows/pages.yml` deploys the site automatically whenever code is pushed to `main`. In GitHub, open **Settings → Pages** and set the build/deployment source to **GitHub Actions** once if it is not already selected.

## Digit source and storage

Digits are requested from the pi.delivery API in 100,000-digit blocks. The API's `content` is decimal digits only, and its `start=0` is the first digit after the decimal point (3. is not included). Downloaded blocks are stored in IndexedDB and reused on later visits. First-time downloads require internet. The 10M option is a selectable search range; the full 10 million digits are not bundled into the repository.

The pi.delivery API is an experimental community-operated service with no availability SLA. If it is unavailable, the app will show an error rather than silently returning incorrect results.

## Privacy

Searches and notebook entries stay in browser storage. Queries are not sent to a search backend; only digit-range requests are sent to pi.delivery.


## Large local dataset: 100 million digits

The app includes a 100M search-depth option and custom searches up to 100M. These use a local TXT dataset selected by the user, processed as a stream in the browser. The dataset is not uploaded to Pi Searcher. This avoids attempting to allocate a billion-character JavaScript string or store a 1GB file inside GitHub Pages.

To use these modes:
1. Obtain the required Pi digit data and extract the ZIP archive(s) to plain text.
2. Select a TXT file containing at least 100,000,000 decimal digits.
3. Choose 500M or 1B and run the search.

PILookup publishes downloadable 100-million-digit ZIP segments. One segment is enough for the 100M search option. The published segment list is at https://pilookup.com/download.html. Check the provider's terms and verify the downloaded files before relying on search results.

Large-file search is limited by the phone's available storage, browser file handling, and processing speed. A 100M-digit TXT file is approximately 100MB before whitespace and file overhead. It streams the file and keeps only a small overlap between read blocks. It does not pre-download the dataset automatically.
