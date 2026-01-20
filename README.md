# Task Manager API

[![CI Pipeline](https://github.com/KoolDrip/task-manager-api/actions/workflows/ci.yml/badge.svg)](https://github.com/KoolDrip/task-manager-api/actions/workflows/ci.yml)
[![CD Pipeline](https://github.com/KoolDrip/task-manager-api/actions/workflows/cd.yml/badge.svg)](https://github.com/KoolDrip/task-manager-api/actions/workflows/cd.yml)
[![Docker Hub](https://img.shields.io/badge/DockerHub-task--manager--api-blue)](https://hub.docker.com/r/kooldrip/task-manager-api)

A production-grade **Task Manager REST API** built with **Spring Boot**, featuring a comprehensive **CI/CD pipeline** using **GitHub Actions** with integrated **security scanning**, **quality gates**, and **containerization**.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Application Features](#application-features)
3. [Tech Stack](#tech-stack)
4. [Getting Started](#getting-started)
5. [CI/CD Pipeline Explanation](#cicd-pipeline-explanation)
6. [Why Each Stage Exists](#why-each-stage-exists)
7. [Security & DevSecOps](#security--devsecops)
8. [Secrets Configuration](#secrets-configuration)
9. [API Documentation](#api-documentation)

---

## Project Overview

This project demonstrates a complete **DevOps CI/CD workflow** implementing:

- **Continuous Integration** - Automated builds, testing, and security scanning on every push
- **Continuous Deployment** - Automated deployment with integration testing and DAST
- **DevSecOps** - Shift-left security with SAST, SCA, and container scanning
- **Containerization** - Docker-based deployment ensuring consistency across environments

### Problem Statement

Manual deployment processes lead to human errors, inconsistent environments, and security vulnerabilities reaching production. This project solves these issues by implementing a fully automated pipeline that ensures only **tested, secure, and validated code** is deployed.

---

## Application Features

| Feature | Description |
|---------|-------------|
| CRUD Operations | Create, Read, Update, Delete tasks |
| Task Filtering | Filter by completed/pending status |
| Search | Search tasks by keyword |
| Health Monitoring | `/health` endpoint for orchestration |
| Input Validation | Bean validation for data integrity |

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
| Security | CodeQL, OWASP Dependency Check, Trivy, ZAP |

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker (optional)

### Run Locally

```bash
# Clone the repository
git clone https://github.com/KoolDrip/task-manager-api.git
cd task-manager-api

# Build the application
mvn clean package

# Run the application
java -jar target/task-manager-api-1.0.0.jar

# Access the API
curl http://localhost:8080/health
```

### Run with Docker

```bash
# Build Docker image
docker build -t task-manager-api:latest .

# Run container
docker run -p 8080:8080 task-manager-api:latest

# Test
curl http://localhost:8080/health
```

---

## CI/CD Pipeline Explanation

### Pipeline Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         CI PIPELINE (ci.yml)                            │
│                     Triggered on: push to master                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐             │
│  │Checkout │ → │ Linting │ → │  SAST   │ → │   SCA   │             │
│  │ & Setup │    │Checkstyle│   │ CodeQL  │    │OWASP DC │             │
│  └─────────┘    └─────────┘    └─────────┘    └─────────┘             │
│       │                                             │                   │
│       ▼                                             ▼                   │
│  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐             │
│  │  Unit   │ → │  Build  │ → │ Docker  │ → │  Image  │             │
│  │  Tests  │    │   JAR   │    │  Build  │    │  Scan   │             │
│  └─────────┘    └─────────┘    └─────────┘    └─────────┘             │
│                                                     │                   │
│                                                     ▼                   │
│                              ┌─────────┐    ┌─────────┐                │
│                              │ Runtime │ → │  Push   │                │
│                              │  Test   │    │DockerHub│                │
│                              └─────────┘    └─────────┘                │
├─────────────────────────────────────────────────────────────────────────┤
│                         CD PIPELINE (cd.yml)                            │
│                    Triggered after: CI success                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐             │
│  │ Deploy  │ → │  Integ  │ → │  DAST   │ → │  Perf   │             │
│  │Container│    │  Tests  │    │OWASP ZAP│    │  Tests  │             │
│  └─────────┘    └─────────┘    └─────────┘    └─────────┘             │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### CI Pipeline Stages

| Stage | Tool | Duration |
|-------|------|----------|
| 1. Checkout & Setup | actions/checkout, setup-java | ~30s |
| 2. Linting | Maven Checkstyle | ~45s |
| 3. SAST | GitHub CodeQL | ~2-3min |
| 4. SCA | OWASP Dependency Check | ~1-2min |
| 5. Unit Tests | JUnit 5 + JaCoCo | ~1min |
| 6. Build | Maven package | ~1min |
| 7. Docker Build | docker/build-push-action | ~2min |
| 8. Image Scan | Aqua Trivy | ~1min |
| 9. Runtime Test | curl smoke tests | ~1min |
| 10. Registry Push | DockerHub | ~30s |

### CD Pipeline Stages

| Stage | Tool | Purpose |
|-------|------|---------|
| 1. Deploy | Docker | Run container from DockerHub |
| 2. Integration Tests | curl | End-to-end API testing |
| 3. DAST | OWASP ZAP | Dynamic security scanning |
| 4. Performance Tests | Apache Bench | Load testing |

---

## Why Each Stage Exists

Understanding **why** each stage exists is critical for DevOps reasoning:

### 1. Checkout & Setup
**Why:** Retrieves source code and establishes a consistent build environment (Java 17). Without this, builds would be inconsistent across different runners.

### 2. Linting (Checkstyle)
**Why:** Enforces coding standards automatically.
**Risk Mitigated:** Technical debt, inconsistent code style, maintainability issues.
**Shift-Left Benefit:** Catches style issues before code review, saving reviewer time.

### 3. SAST - Static Application Security Testing (CodeQL)
**Why:** Analyzes source code for security vulnerabilities without executing it.
**Risk Mitigated:** OWASP Top 10 vulnerabilities:
- SQL Injection
- Cross-Site Scripting (XSS)
- Insecure Deserialization
- Security Misconfigurations

**Shift-Left Benefit:** Finds vulnerabilities at code-write time, not in production.

### 4. SCA - Software Composition Analysis (OWASP Dependency Check)
**Why:** Scans third-party dependencies for known vulnerabilities (CVEs).
**Risk Mitigated:** Supply chain attacks, vulnerable libraries.
**Example:** Log4Shell (CVE-2021-44228) would be detected here.

### 5. Unit Tests
**Why:** Validates business logic works correctly.
**Risk Mitigated:** Regressions, broken functionality.
**Fail-Fast:** If tests fail, pipeline stops immediately - no point building a broken app.

### 6. Build
**Why:** Compiles code and packages into deployable JAR artifact.
**Risk Mitigated:** Compilation errors, missing dependencies.

### 7. Docker Build
**Why:** Creates immutable container image ensuring consistency.
**Risk Mitigated:** "Works on my machine" problem.
**Best Practice:** Multi-stage build reduces image size and attack surface.

### 8. Image Scan (Trivy)
**Why:** Scans container for OS and library vulnerabilities.
**Risk Mitigated:** Vulnerable base images, outdated packages in container.
**Difference from SCA:** SCA scans app dependencies; Trivy scans entire container (OS + runtime).

### 9. Runtime Test (Smoke Test)
**Why:** Validates the container actually starts and responds.
**Risk Mitigated:** Container crashes, misconfigured entrypoints.
**Critical:** A container can build successfully but fail to run.

### 10. Registry Push
**Why:** Publishes trusted, validated image to DockerHub.
**Risk Mitigated:** Only images that pass ALL checks reach the registry.
**Enables:** Downstream CD pipeline can pull trusted image.

### 11. DAST - Dynamic Application Security Testing (OWASP ZAP)
**Why:** Tests the running application for security vulnerabilities.
**Risk Mitigated:** Runtime security issues not detectable by static analysis:
- Missing security headers
- Information disclosure
- Authentication/Authorization issues

---

## Security & DevSecOps

### Shift-Left Security Model

```
Traditional:  Code → Build → Test → Deploy → Security Scan (Too Late!)
                                                    ↑
                                              Vulnerabilities found
                                              in production = costly

Shift-Left:   Code → Security Scan → Build → Test → Deploy (Early Detection!)
                          ↑
                    Vulnerabilities found
                    at development = cheap to fix
```

### Security Tools Integrated

| Tool | Type | What It Detects | Stage |
|------|------|-----------------|-------|
| Checkstyle | Quality | Code style violations | CI |
| CodeQL | SAST | Code vulnerabilities | CI |
| OWASP Dependency Check | SCA | Vulnerable dependencies | CI |
| Trivy | Container Scan | OS/library vulnerabilities | CI |
| OWASP ZAP | DAST | Runtime vulnerabilities | CD |

### Security Findings Location

All security findings are surfaced in the **GitHub Security tab**:
- CodeQL results → Security → Code scanning alerts
- Trivy results → Security → Code scanning alerts (via SARIF)

---

## Secrets Configuration

### Required GitHub Secrets

| Secret Name | Purpose | How to Get |
|-------------|---------|------------|
| `DOCKERHUB_USERNAME` | DockerHub login | Your DockerHub username |
| `DOCKERHUB_TOKEN` | DockerHub authentication | DockerHub → Account Settings → Security → Access Tokens |

### How to Configure

1. Go to your GitHub repository
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add each secret:

```
Name: DOCKERHUB_USERNAME
Value: your-dockerhub-username

Name: DOCKERHUB_TOKEN
Value: dckr_pat_xxxxxxxxxxxx
```

### Getting DockerHub Token

1. Log in to [DockerHub](https://hub.docker.com)
2. Click your profile → **Account Settings**
3. Go to **Security** → **Access Tokens**
4. Click **New Access Token**
5. Name: `github-actions`
6. Permissions: **Read & Write**
7. Copy the token (shown only once!)

### Security Best Practices

- **NEVER** hardcode secrets in code or YAML files
- **NEVER** commit `.env` files with secrets
- **ALWAYS** use GitHub Secrets for sensitive values
- **ROTATE** tokens periodically

---

## API Documentation

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Welcome message |
| GET | `/health` | Health check (for container orchestration) |
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

```bash
# Health check
curl http://localhost:8080/health

# Create a task
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "Learn DevOps", "description": "Complete CI/CD project"}'

# Get all tasks
curl http://localhost:8080/api/tasks

# Update a task
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Learn DevOps", "description": "Project completed!", "completed": true}'

# Delete a task
curl -X DELETE http://localhost:8080/api/tasks/1
```

---

## Project Structure

```
task-manager-api/
├── .github/
│   └── workflows/
│       ├── ci.yml              # CI Pipeline (11 stages)
│       └── cd.yml              # CD Pipeline (4 stages)
├── src/
│   ├── main/
│   │   ├── java/com/taskmanager/
│   │   │   ├── controller/     # REST Controllers
│   │   │   ├── model/          # Entity Classes
│   │   │   ├── repository/     # Data Access
│   │   │   └── service/        # Business Logic
│   │   └── resources/
│   │       └── application.properties
│   └── test/                   # Unit Tests
├── k8s/                        # Kubernetes Manifests (optional)
├── Dockerfile                  # Multi-stage Docker build
├── pom.xml                     # Maven config + plugins
├── checkstyle.xml             # Code quality rules
└── README.md                  # This file
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

# Run checkstyle
mvn checkstyle:check
```

---

## Troubleshooting

### Pipeline Fails on Linting
```bash
# Check violations locally
mvn checkstyle:check
# Fix formatting issues before pushing
```

### Pipeline Fails on Unit Tests
```bash
# Run tests locally to see failures
mvn test
```

### Docker Build Fails
```bash
# Ensure JAR is built first
mvn clean package -DskipTests
docker build -t task-manager-api .
```

### DockerHub Push Fails
- Verify `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN` secrets are set
- Ensure token has Read & Write permissions
- Check if repository exists on DockerHub

---

## License

This project is created for educational purposes as part of the DevOps CI/CD assessment.

---

## Author

**Jenish**
DevOps Engineering Student
GitHub: [@KoolDrip](https://github.com/KoolDrip)
