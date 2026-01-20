# Task Manager API - DevOps CI/CD Project

A production-grade **Task Manager REST API** built with **Spring Boot**, featuring a comprehensive **CI/CD pipeline** using **GitHub Actions** with integrated **security scanning**, **quality gates**, and **Kubernetes deployment**.

## Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [CI/CD Pipeline](#cicd-pipeline)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Kubernetes Deployment](#kubernetes-deployment)
- [Configuration](#configuration)

---

## Project Overview

This project demonstrates a complete DevOps workflow implementing:

- **Continuous Integration** with automated builds, testing, and security scanning
- **Continuous Deployment** to Kubernetes with health checks and rollback support
- **DevSecOps** practices with shift-left security integration
- **Container security** scanning and runtime validation

### Application Features

- RESTful API for task management (CRUD operations)
- Task filtering by status (completed/pending)
- Search functionality
- Health monitoring endpoints
- H2 in-memory database for easy testing

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         CI/CD Pipeline                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐            │
│  │ Checkout │→ │ Linting  │→ │  SAST    │→ │   SCA    │            │
│  │  Setup   │  │Checkstyle│  │  CodeQL  │  │Dep Check │            │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘            │
│       │                                                              │
│       ▼                                                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐            │
│  │  Unit    │→ │  Build   │→ │  Docker  │→ │  Image   │            │
│  │  Tests   │  │  JAR     │  │  Build   │  │  Scan    │            │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘            │
│                                    │                                 │
│                                    ▼                                 │
│                           ┌──────────────┐                          │
│                           │Runtime Test  │                          │
│                           │(Smoke Test)  │                          │
│                           └──────────────┘                          │
│                                    │                                 │
│                                    ▼                                 │
│                           ┌──────────────┐                          │
│                           │ Push to      │                          │
│                           │ DockerHub    │                          │
│                           └──────────────┘                          │
│                                    │                                 │
│                    ┌───────────────┴───────────────┐                │
│                    ▼                               ▼                │
│           ┌──────────────┐                ┌──────────────┐          │
│           │ K8s Deploy   │                │    DAST      │          │
│           │ (Staging)    │                │  (ZAP Scan)  │          │
│           └──────────────┘                └──────────────┘          │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Build Tool | Maven |
| Database | H2 (In-memory) |
| Container | Docker |
| CI/CD | GitHub Actions |
| Orchestration | Kubernetes |
| Security Scanning | CodeQL, OWASP Dependency Check, Trivy |
| Code Quality | Checkstyle, JaCoCo |

---

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker
- kubectl (for Kubernetes deployment)

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/task-manager-api.git
   cd task-manager-api
   ```

2. **Build the application**
   ```bash
   mvn clean package
   ```

3. **Run locally**
   ```bash
   java -jar target/task-manager-api-1.0.0.jar
   ```

4. **Access the API**
   - Application: http://localhost:8080
   - Health Check: http://localhost:8080/health
   - H2 Console: http://localhost:8080/h2-console

### Running with Docker

1. **Build the Docker image**
   ```bash
   docker build -t task-manager-api:latest .
   ```

2. **Run the container**
   ```bash
   docker run -p 8080:8080 task-manager-api:latest
   ```

---

## CI/CD Pipeline

### CI Pipeline Stages

| Stage | Tool | Purpose | Why It Matters |
|-------|------|---------|----------------|
| **Checkout** | GitHub Actions | Retrieve source code | Foundation for all subsequent stages |
| **Setup** | actions/setup-java | Install Java runtime | Consistent build environment |
| **Linting** | Checkstyle | Enforce coding standards | Prevents technical debt |
| **SAST** | CodeQL | Static security analysis | Detects OWASP Top 10 vulnerabilities |
| **SCA** | OWASP Dependency Check | Dependency scanning | Identifies supply-chain risks |
| **Unit Tests** | JUnit 5 + JaCoCo | Test execution & coverage | Prevents regressions |
| **Build** | Maven | Package application | Creates deployable artifact |
| **Docker Build** | Docker | Container image creation | Enables consistent deployment |
| **Image Scan** | Trivy | Container vulnerability scan | Prevents vulnerable images |
| **Runtime Test** | curl | Container smoke test | Validates image is runnable |
| **Push** | Docker | Publish to DockerHub | Enables downstream CD |

### CD Pipeline Stages

| Stage | Purpose |
|-------|---------|
| **Deploy** | Deploy to Kubernetes cluster |
| **DAST** | Dynamic security testing with OWASP ZAP |
| **Smoke Test** | Validate deployment health |

### Triggering the Pipeline

**Automatic Triggers:**
- Push to `master` or `main` branch
- Pull requests to `master` or `main`

**Manual Trigger:**
- Use "Run workflow" in GitHub Actions tab

---

## API Documentation

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Welcome message |
| GET | `/health` | Health check |
| GET | `/api/tasks` | Get all tasks |
| GET | `/api/tasks/{id}` | Get task by ID |
| POST | `/api/tasks` | Create new task |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |
| GET | `/api/tasks/completed` | Get completed tasks |
| GET | `/api/tasks/pending` | Get pending tasks |
| GET | `/api/tasks/search?keyword=` | Search tasks |
| PUT | `/api/tasks/{id}/toggle` | Toggle task status |

### Example Requests

**Create a Task:**
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "Learn DevOps", "description": "Complete CI/CD project"}'
```

**Get All Tasks:**
```bash
curl http://localhost:8080/api/tasks
```

**Update a Task:**
```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Learn DevOps", "description": "Project completed!", "completed": true}'
```

---

## Security

### Security Measures Implemented

1. **Static Application Security Testing (SAST)**
   - CodeQL analysis for code vulnerabilities
   - Results visible in GitHub Security tab

2. **Software Composition Analysis (SCA)**
   - OWASP Dependency Check for vulnerable dependencies
   - Fail build on CVSS score >= 7

3. **Container Security**
   - Trivy scanning for OS and library vulnerabilities
   - Non-root user in Docker container
   - Multi-stage build for minimal attack surface

4. **Dynamic Application Security Testing (DAST)**
   - OWASP ZAP baseline scan in CD pipeline

### Security Best Practices

- Secrets managed via GitHub Secrets (never hardcoded)
- Container runs as non-root user
- Health checks for container orchestration
- Input validation with Bean Validation

---

## Kubernetes Deployment

### Prerequisites

1. A running Kubernetes cluster
2. kubectl configured with cluster access
3. DockerHub credentials

### Deployment Files

```
k8s/
├── deployment.yaml    # Application deployment
├── service.yaml       # LoadBalancer service
├── configmap.yaml     # Configuration
└── hpa.yaml          # Horizontal Pod Autoscaler
```

### Manual Deployment

```bash
# Create namespace
kubectl create namespace task-manager

# Apply manifests
kubectl apply -f k8s/ -n task-manager

# Check deployment status
kubectl get pods -n task-manager
kubectl get services -n task-manager
```

---

## Configuration

### GitHub Secrets Required

| Secret | Description |
|--------|-------------|
| `DOCKERHUB_USERNAME` | DockerHub username |
| `DOCKERHUB_TOKEN` | DockerHub access token |
| `KUBE_CONFIG` | Base64 encoded kubeconfig (for CD) |

### Setting Up Secrets

1. Go to repository **Settings** > **Secrets and variables** > **Actions**
2. Click **New repository secret**
3. Add each secret with appropriate values

**Generate DockerHub Token:**
1. Log in to DockerHub
2. Go to Account Settings > Security > Access Tokens
3. Create a new access token with read/write permissions

**Generate KUBE_CONFIG:**
```bash
cat ~/.kube/config | base64
```

---

## Project Structure

```
project-root/
├── .github/
│   └── workflows/
│       ├── ci.yml              # CI pipeline
│       └── cd.yml              # CD pipeline
├── k8s/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── configmap.yaml
│   └── hpa.yaml
├── src/
│   ├── main/
│   │   ├── java/com/taskmanager/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   └── test/
├── Dockerfile
├── pom.xml
├── checkstyle.xml
└── README.md
```

---

## Running Tests

```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

---

## Quality Gates

- **Checkstyle**: Enforces Google Java Style Guide
- **JaCoCo**: Code coverage reporting
- **Dependency Check**: Fails on CVSS >= 7
- **Trivy**: Reports HIGH and CRITICAL vulnerabilities

---

## Troubleshooting

### Common Issues

**Build fails on Checkstyle:**
- Run `mvn checkstyle:check` locally to see violations
- Fix formatting issues before pushing

**Docker build fails:**
- Ensure JAR is built first: `mvn clean package -DskipTests`
- Check Dockerfile syntax

**Kubernetes deployment fails:**
- Verify image is pushed to DockerHub
- Check secrets are configured correctly
- Review pod logs: `kubectl logs -l app=task-manager-api -n task-manager`

---

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Ensure all tests pass
5. Submit a pull request

---

## License

This project is created for educational purposes as part of the DevOps CI/CD assessment.

---

## Author

**Student Name**
3rd Year Computer Science
Scaler Academy

---

## Acknowledgments

- Scaler Academy DevOps Program
- Spring Boot Documentation
- GitHub Actions Documentation
- OWASP Security Guidelines
