# Eligibility Service with Hazelcast Distributed Caching

A production-ready Spring Boot microservice demonstrating distributed caching with Hazelcast, designed for Kubernetes deployment with comprehensive monitoring and observability.

## Features

- **Java 21** with **Spring Boot 3.3.4**
- **Hazelcast 5.4.0** for distributed in-memory caching
- **Kubernetes-native** with Hazelcast cluster discovery
- **Production-ready** error handling and logging
- **Observability** with Prometheus metrics and health checks
- **Cloud-native** deployment with Docker, Kubernetes, and Helm
- **CI/CD** with GitHub Actions

## Architecture

### Technology Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| Java | 21 | JDK language version |
| Spring Boot | 3.3.4 | Web framework with caching |
| Hazelcast | 5.4.0 | Distributed in-memory data grid |
| Gradle | 8.10.2 | Build automation |
| Docker | Multi-stage | Containerization |
| Kubernetes | 1.x | Container orchestration |
| Helm | 3.x | Kubernetes package management |
| Prometheus | via Micrometer | Metrics and monitoring |

### Application Components

- **Controller Layer**: REST API endpoints (`/api/eligibility/{pnr}`)
- **Service Layer**: Business logic with eligibility rules
- **Model Layer**: Domain objects (EligibilityResponse, EligibilityStatus)
- **Exception Handling**: Global exception handler with custom error responses
- **Caching**: Hazelcast distributed cache with 60s TTL

## API Endpoints

### Check Eligibility
```bash
GET /api/eligibility/{pnr}
```

**Parameters:**
- `pnr` (path): 6-character alphanumeric PNR (Passenger Name Record)

**Response:**
```json
{
  "pnr": "ABC123",
  "eligible": true,
  "status": "APPROVED",
  "reason": "All eligibility criteria met",
  "computedAt": "2025-11-13T10:30:00Z",
  "fromCache": false
}
```

**Eligibility Status Values:**
- `APPROVED`: Eligibility approved
- `DENIED`: Eligibility denied (PNR starts with 'D')
- `PENDING_REVIEW`: Requires manual review (PNR starts with 'P')
- `INSUFFICIENT_DATA`: Not enough data for determination
- `EXPIRED`: PNR has expired (starts with 'X')
- `INVALID`: Invalid PNR format

### Health Check
```bash
GET /actuator/health
GET /actuator/health/liveness
GET /actuator/health/readiness
```

### Metrics
```bash
GET /actuator/prometheus
GET /actuator/metrics
```

## Quick Start

### Prerequisites
- Java 21
- Gradle 8.10+
- Docker (for containerized deployment)
- Kubernetes cluster (for K8s deployment)

### Local Development

#### Run with Gradle
```bash
./gradlew bootRun
```

#### Test the API
```bash
# Check eligibility (first call - computes in 500ms)
curl http://localhost:8083/api/eligibility/ABC123

# Check again (cached - instant response)
curl http://localhost:8083/api/eligibility/ABC123

# Test denied PNR
curl http://localhost:8083/api/eligibility/DENIED

# Test expired PNR
curl http://localhost:8083/api/eligibility/X12345

# Health check
curl http://localhost:8083/actuator/health

# Prometheus metrics
curl http://localhost:8083/actuator/prometheus
```

### Run with Docker Compose

```bash
docker-compose up -d
```

This starts:
- Eligibility service on port 8083
- Hazelcast Management Center on port 8080

Access Management Center at http://localhost:8080

### Build and Test

```bash
# Run tests
./gradlew test

# Build JAR
./gradlew build

# Build Docker image
docker build -t ghcr.io/akhilsplendid/eligibility:latest .

# Run container
docker run -p 8083:8083 ghcr.io/akhilsplendid/eligibility:latest
```

## Kubernetes Deployment

### Using kubectl

```bash
# Apply Kubernetes manifests
kubectl apply -f k8s/deployment.yaml

# Check deployment status
kubectl get pods -l app=eligibility
kubectl get svc eligibility

# View logs
kubectl logs -f deployment/eligibility

# Port forward to access locally
kubectl port-forward svc/eligibility 8083:80
```

### Using Helm

```bash
# Install the Helm chart
helm install eligibility ./helm/eligibility

# Install with custom values
helm install eligibility ./helm/eligibility \
  --set replicaCount=3 \
  --set image.tag=v1.0.0 \
  --set monitoring.enabled=true

# Upgrade release
helm upgrade eligibility ./helm/eligibility

# Uninstall
helm uninstall eligibility
```

### Helm Configuration

Key values you can customize in `helm/eligibility/values.yaml`:

```yaml
image:
  repository: ghcr.io/akhilsplendid/eligibility
  tag: latest
  pullPolicy: IfNotPresent

replicaCount: 1

service:
  port: 80
  targetPort: 8083

monitoring:
  enabled: true        # Enable Prometheus ServiceMonitor
  interval: 30s

hazelcast:
  enabled: true
  port: 5701
```

## Hazelcast Configuration

### Cluster Discovery

The service uses Hazelcast Kubernetes discovery for automatic cluster formation:

- **Namespace**: `default` (configurable in hazelcast.yaml)
- **Service Name**: `eligibility-hazelcast`
- **Port**: `5701`

### Cache Settings

- **Name**: `eligibility`
- **TTL**: 60 seconds
- **Backup Count**: 1 (one backup copy per entry)
- **Eviction Policy**: LRU (Least Recently Used)
- **Max Size**: 10,000 entries per node

### Scaling

To scale the service and form a Hazelcast cluster:

```bash
# Scale to 3 replicas
kubectl scale deployment eligibility --replicas=3

# Or with Helm
helm upgrade eligibility ./helm/eligibility --set replicaCount=3
```

All instances will automatically discover each other and form a distributed cache cluster.

## Monitoring and Observability

### Prometheus Metrics

The service exposes metrics at `/actuator/prometheus`:

- JVM metrics (memory, threads, GC)
- HTTP request metrics
- Cache hit/miss ratios
- Hazelcast cluster metrics
- Custom application metrics

### Logs

Logs are configured with:
- **Console output**: Structured logging to stdout
- **File output**: `logs/eligibility.log` (in container)
- **Log levels**:
  - Application: DEBUG
  - Spring Cache: DEBUG
  - Hazelcast: INFO
  - Root: INFO

### Health Probes

Kubernetes readiness and liveness probes are configured:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8083
  initialDelaySeconds: 30
  periodSeconds: 10

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8083
  initialDelaySeconds: 10
  periodSeconds: 5
```

## CI/CD Pipeline

GitHub Actions workflow (`.github/workflows/ci.yml`) automatically:

1. Runs tests on every push and PR
2. Builds Docker image
3. Pushes to GitHub Container Registry (GHCR)
4. Tags with branch name, git SHA, and `latest`

### Container Registry

Images are published to: `ghcr.io/akhilsplendid/eligibility`

## Development

### Project Structure

```
eligibility/
├── src/main/java/com/example/eligibility/
│   ├── Application.java           # Spring Boot entry point
│   ├── api/
│   │   └── EligibilityController.java  # REST endpoints
│   ├── service/
│   │   └── EligibilityService.java     # Business logic
│   ├── model/
│   │   ├── EligibilityResponse.java    # Response DTO
│   │   └── EligibilityStatus.java      # Status enum
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java # Error handling
│   │   ├── EligibilityException.java
│   │   ├── InvalidPnrException.java
│   │   └── ErrorResponse.java
│   └── config/
│       └── CorsConfig.java             # CORS configuration
├── src/main/resources/
│   ├── application.yml                 # Spring configuration
│   └── hazelcast.yaml                  # Hazelcast configuration
└── src/test/java/
    └── HazelcastClientIntegrationTest.java
```

### Adding New Business Rules

Edit `EligibilityService.java` to add custom eligibility logic:

```java
private EligibilityResponse determineEligibility(String pnr) {
    // Add your business rules here
    if (yourCondition) {
        return new EligibilityResponse(pnr, false,
            EligibilityStatus.DENIED, "Your reason");
    }
    // ...
}
```

## Troubleshooting

### Cache Not Working

1. Check Hazelcast logs: `kubectl logs -f deployment/eligibility | grep Hazelcast`
2. Verify cluster members: Look for "Members {size:X}" in logs
3. Test cache: Make same request twice, second should be instant

### Pods Not Forming Cluster

1. Verify service exists: `kubectl get svc eligibility-hazelcast`
2. Check RBAC permissions for Kubernetes discovery
3. Ensure namespace matches in `hazelcast.yaml`

### High Memory Usage

1. Adjust cache size in `hazelcast.yaml`:
   ```yaml
   eviction:
     size: 5000  # Reduce from 10000
   ```
2. Reduce TTL to expire entries faster
3. Set resource limits in Helm values

## License

This is a demonstration project for showcasing Spring Boot with Hazelcast distributed caching.

## Contributing

This project demonstrates best practices for:
- Distributed caching with Hazelcast
- Kubernetes-native microservices
- Observability and monitoring
- Cloud-native deployment patterns

Feel free to use this as a template for your own services!
