#  Student Management Microservices Architecture with Spring Cloud

This project demonstrates how a standalone **Student Management Service (SMS)** was transformed into a **Spring Cloud microservices architecture**. It integrates service discovery, config server, circuit breaking, monitoring, logging, and interactive API documentation.

---

##  Step 1: Integrating SMS into Microservices Workspace

✅ **Moved SMS into microservices workspace**  
✅ **Modified `pom.xml`** to include:
```xml
<!-- Spring Cloud Dependencies -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

✅ **Enabled Discovery and Config Client**
```java
@EnableDiscoveryClient
@SpringBootApplication
```

✅ **Externalized Configuration** to [Weather Config Repo](#):  
- Moved `application.properties` to `student-service.yml` in Git-backed config server  
- Properties are loaded via Spring Cloud Config Server

✅ **Verified**:
- SMS registers with **Eureka Discovery Server**
- Properties are loaded remotely

📸 _Screenshots_:
![Eureka](https://github.com/user-attachments/assets/14b060a1-faa1-4923-bcdb-c4cf337ff403)
![Config Server](https://github.com/user-attachments/assets/e5c141b1-edd5-4606-8d65-830b87c9d962)

---

##  Step 2: Created Search Service with Ribbon, RestTemplate & CompletableFuture

✅ **Ribbon** for client-side load balancing  
✅ **@LoadBalanced RestTemplate** used for service-to-service calls  
✅ **Parallel service calls** using `CompletableFuture.supplyAsync()`

### Services Called:
- `http://sms/api/students`
- `http://details/port`

✅ **GeneralResponse Wrapper** standardizes output

📸 _Screenshot_:  
![Search Service](https://github.com/user-attachments/assets/e200bce4-899a-4b4d-a2a5-783c315d212a)

---

##  Step 3: Circuit Breaker with Hystrix

✅ Added Hystrix Dependency:
```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

✅ Enabled Circuit Breaker:
```java
@EnableCircuitBreaker
```

✅ **@HystrixCommand** on service calls  
✅ **Fallbacks** implemented:
- Empty student list when SMS is down
- `"Unknown"` when Details service is down

📸 _Screenshot_:  
![Hystrix](https://github.com/user-attachments/assets/8912f558-b80a-41b9-be3b-5890631da14a)

---

##  Step 4: Encrypted Credentials with Keystore

✅ Created keystore using:
```bash
keytool -genkeypair -alias config-server-key -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore config-server.p12 -validity 3650
```

✅ Configured `application.properties`:
```properties
encrypt.key-store.location=classpath:config-server.p12
encrypt.key-store.password=changeme
encrypt.key-store.alias=config-server-key
encrypt.key-store.secret=changeme
spring.cloud.config.server.encrypt.enabled=true
```

⚠️ Encountered **500 Internal Server Error** when calling `/encrypt`  
🔐 But understood how to use POST `/encrypt` to generate encrypted values

📸 _Screenshot_:  
![Keystore Setup](https://github.com/user-attachments/assets/495c66ab-6741-4737-a347-ae18f00b7180)

---

##  Step 5: Monitoring with Prometheus and Grafana

✅ Added Prometheus Dependencies:
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

✅ Enabled Metrics in `application.properties`:
```properties
management.endpoints.web.exposure.include=*
management.metrics.export.prometheus.enabled=true
```

✅ Created `prometheus.yml`:
```yaml
global:
  scrape_interval: 5s

scrape_configs:
  - job_name: 'sms'
    static_configs: [{ targets: ['localhost:8081'] }]
  - job_name: 'search'
    static_configs: [{ targets: ['localhost:8300'] }]
  - job_name: 'gateway'
    static_configs: [{ targets: ['localhost:8200'] }]
```

✅ Grafana Dashboard Panels:
- JVM Memory
- CPU Usage
- HTTP Request Rate
- Service Health

📸 _Screenshots_:  
![Prometheus](https://github.com/user-attachments/assets/5a8712b8-a142-4e30-91cb-8ee8c76b4ce9)
![Grafana](https://github.com/user-attachments/assets/ab0f2626-b7a0-4833-88d3-2cbb0eb4772a)

---

## 📚 Step 6: API Documentation with Swagger (OpenAPI 3)

✅ Added Swagger Dependency:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.7.0</version>
</dependency>
```

✅ Access API Docs:  
[🔗 Swagger UI for SMS](http://localhost:8081/swagger-ui/index.html)

📸 _Screenshot_:  
![Swagger UI](https://github.com/user-attachments/assets/290e1f84-7321-4141-87b7-005156bb932c)

---

## 📦 Step 7: Centralized Logging with Splunk

✅ Logs written to:
```properties
logging.file.name=logs/sms.log
```

✅ Splunk Universal Forwarder Config:
**inputs.conf**
```ini
[monitor://C:\\path\\to\\logs]
disabled = false
index = main
sourcetype = springboot
```
**outputs.conf**
```ini
[tcpout]
defaultGroup = default-autolb-group

[tcpout:default-autolb-group]
server = prd-p-xxxx.splunkcloud.com:9997
[tcpout-server://prd-p-xxxx.splunkcloud.com:9997]
```

⚠️ Note: Splunk Cloud **trial plans do not allow TCP (9997)** by default.

📸 _Screenshot_:  
![Splunk Log Centralization](https://github.com/user-attachments/assets/c25189f0-4af4-41b3-9a6e-4e51b09f1675)

---



---


