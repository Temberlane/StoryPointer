.PHONY: dev backend frontend docker-build docker-run test

dev: backend frontend

backend:
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

frontend:
cd frontend && npm install && npm run dev -- --host

docker-build:
docker build -t storypointer-backend:local ./backend

docker-run: docker-build
docker run --rm -p 8080:8080 storypointer-backend:local

test:
cd backend && ./mvnw test
