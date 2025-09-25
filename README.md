# StoryPointer Platform

This repository contains a production-ready starter implementation for a video understanding service. The service accepts user videos, generates transcripts, runs an ONNX-based machine learning prediction, and returns enriched results alongside cited transcript snippets. A minimal React frontend is provided to exercise the API. Deployment assets target Azure Kubernetes Service (AKS).

## Project Layout

```
root/
  README.md               # This file
  Makefile                # Convenience commands for local dev
  backend/                # Spring Boot service
  frontend/               # React single-page app
  k8s/                    # AKS manifests
```

Refer to the READMEs under `backend/` and `frontend/` for stack-specific instructions.

## Quickstart

```bash
make dev
```

The `dev` profile uses in-memory storage, a mock transcription provider, and a deterministic mock model so the stack runs locally without external cloud services.

## Deployment

1. Build and push the container image to Azure Container Registry (ACR).
2. Provision AKS and configure Azure Blob Storage, Key Vault, and Google Cloud credentials.
3. Apply the manifests under `k8s/`.
4. Update DNS to point to the AKS ingress controller and optionally configure TLS via cert-manager.

See `backend/README.md` for detailed Azure CLI examples.

