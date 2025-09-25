# StoryPointer Backend

Spring Boot 3 service that ingests videos, transcribes audio, and performs ONNX-based predictions. The dev profile ships with mock integrations so the API can be exercised locally without cloud credentials.

## Requirements

- Java 21 (JDK)
- Apache Maven 3.9+
- (Optional) Whisper binary at `/usr/local/bin/whisper` for the Whisper transcription implementation
- Node.js 18+ (for the frontend dev server)

## Configuration

All configuration is driven by environment variables. The most important ones are listed below. Only the `dev` profile defaults are set in `application-dev.yaml`.

| Variable | Description |
|----------|-------------|
| `SPRING_PROFILES_ACTIVE` | Set to `dev` for local, omit for production-like. |
| `AZ_BLOB_CONN` | Azure Blob Storage connection string. Leave empty for in-memory dev store. |
| `BLOB_CONTAINER_VIDEOS` | Container name for stored videos. |
| `BLOB_CONTAINER_TRANSCRIPTS` | Container name for stored transcripts. |
| `TRANSCRIBER_IMPL` | `whisper` or `gcloud`. Defaults to `mock` in dev. |
| `GCLOUD_KEY_JSON` | JSON string of Google service account with Speech-to-Text access. |
| `MODEL_PATH` | Filesystem path to the ONNX model. Defaults to `model/model.onnx`. |

`application.yaml` defines the production defaults. `application-dev.yaml` overrides to use in-memory repositories, mock model, and canned transcripts.

## Running Locally

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The API is served on `http://localhost:8080`. OpenAPI UI lives at `/swagger-ui.html`.

### Sample Requests

```bash
# Upload from file
curl -F "file=@sample.mp4" http://localhost:8080/videos

# Upload from URL
curl -H "Content-Type: application/json" -d '{"sourceUrl":"https://example.com/video.mp4"}' http://localhost:8080/videos

# Poll status
curl http://localhost:8080/videos/{videoId}/status

# Fetch transcript (dev returns canned transcript)
curl http://localhost:8080/videos/{videoId}/transcript

# Run prediction
curl -H "Content-Type: application/json" -d '{"videoId":"{videoId}"}' http://localhost:8080/predict
```

## Building

```bash
./mvnw clean package
```

The resulting fat jar is produced at `target/storypointer-backend-0.1.0.jar`.

## Docker

```bash
docker build -t storypointer-backend:local .
docker run --rm -p 8080:8080 storypointer-backend:local
```

## Azure Deployment Outline

1. Build and push the image to ACR:
   ```bash
   az acr create --resource-group rg-storypointer --name storypointeracr --sku Basic
   az acr login --name storypointeracr
   docker tag storypointer-backend:local storypointeracr.azurecr.io/storypointer-backend:0.1.0
   docker push storypointeracr.azurecr.io/storypointer-backend:0.1.0
   ```
2. Provision AKS and connect:
   ```bash
   az aks create --resource-group rg-storypointer --name storypointer-aks --attach-acr storypointeracr --node-count 1
   az aks get-credentials --resource-group rg-storypointer --name storypointer-aks
   ```
3. Create Kubernetes secrets:
   ```bash
   kubectl create secret generic app-secrets \
     --from-literal=AZ_BLOB_CONN="<connection-string>" \
     --from-file=GCLOUD_KEY_JSON=./gcloud-key.json
   ```
4. Deploy manifests:
   ```bash
   kubectl apply -f ../k8s/
   ```
5. Configure DNS to point to the ingress controller and optionally add TLS using cert-manager.

## Testing

```bash
./mvnw test
```

Unit tests cover transcript augmentation, prediction inference wrapper, transcription service orchestration, and an integration smoke test for `/predict`.

