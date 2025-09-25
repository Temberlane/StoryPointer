# StoryPointer Frontend

React single-page application built with Vite to exercise the StoryPointer API. The app supports uploading a video or referencing a URL, polling status, viewing transcripts, running predictions, and issuing simple QA requests.

## Prerequisites

- Node.js 18+

## Development

```bash
npm install
npm run dev
```

The dev server proxies API requests to `http://localhost:8080`.

## Build

```bash
npm run build
```

The build output is generated in `dist/`. To serve the static bundle from the Spring Boot app, copy the contents of `dist/` into `backend/src/main/resources/static/` or use the provided Dockerfile stage that copies the build output.

