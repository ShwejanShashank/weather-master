 # Step 1: Integrating Student Management Service (SMS) into Spring Cloud Project
As the first step in transforming our standalone Student Management Service (SMS) into a microservice-based architecture using Spring Cloud, I successfully added the SMS module into the existing microservices project structure.
## Tasks Completed:
1.Moved SMS into the Microservices Workspace:
2.The sms module was copied into the microservices parent project directory to ensure it follows the shared build and configuration structure.
3.Modified pom.xml to Add Spring Cloud Dependencies:
4.Added the required dependencies in the sms service’s pom.xml:
<!-- Spring Cloud Dependencies -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>

## 5.Enabled Service Discovery and Config Client:
## 6.Annotated the main application class with:
## @EnableDiscoveryClient
## @SpringBootApplication

## 7.Updated application.properties:

Replaced local properties with:
Added sms-dev properties in weather-config-repo
Moved Properties to Config Server( weather-config repo aka properties)
The original properties (like server.port, datasource, etc.) were moved from local application.properties into the centralized Config Server repo, under student-service.yml.
Verified Service Registration and Externalized Config:
On running, the sms service:
Registers itself with Eureka Discovery Server
Loads its properties from Spring Cloud Config Server

## Screeshots:
![image](https://github.com/user-attachments/assets/14b060a1-faa1-4923-bcdb-c4cf337ff403)
![image](https://github.com/user-attachments/assets/e5c141b1-edd5-4606-8d65-830b87c9d962)


# Step2. Built a Search Service with Ribbon, RestTemplate, and CompletableFuture
I created a search microservice that connects to other services using Ribbon (client-side load balancing).

I configured a @LoadBalanced RestTemplate bean so that I could use service names like http://sms and http://details instead of hardcoded IPs or ports.

Inside the SearchService, I used CompletableFuture.supplyAsync() to parallelize service calls:

## One call to the SMS service to get a list of students via /api/students
## One call to the Details service via /details/port using:

restTemplate.getForObject("http://details/port", String.class);
I created a wrapper class GeneralResponse to standardize the API output with:
code (e.g., 200)
timestamp
data (merged result)

I combined the results of both service calls using CompletableFuture.allOf(...)
The final response from /search returns a JSON containing:

Student data from SMS
Port info from Details

## ScreenShot:
![image](https://github.com/user-attachments/assets/e200bce4-899a-4b4d-a2a5-783c315d212a)


## Step3. Integrated Hystrix for Circuit Breaking in the Search Service
*I added Hystrix to my Search microservice to make it resilient to failures in downstream services.
*I updated the pom.xml to include:
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
I enabled Hystrix globally by adding @EnableCircuitBreaker in SearchApplication.java.
In the SearchService, I applied @HystrixCommand to the asynchronous methods that call:

the SMS service (/api/students)
the Details service (/details/port)

I defined fallback methods to ensure the service responds gracefully even when another service is down:

If SMS is unavailable, it returns an empty list of students.
If Details is unavailable, it returns "Unknown" for the port.

## Screenshot:
![image](https://github.com/user-attachments/assets/8912f558-b80a-41b9-be3b-5890631da14a)


# Step4. Secured Sensitive Properties Using a Custom Keystore and Encryption
I created my own Java Keystore (.keystore) using the keytool command to securely handle sensitive credentials like DB username and password.

 command I used:
 ### keytool -genkeypair -alias config-server-key -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore config-server.p12 -validity 3650
I placed the keystore file in the config-server/src/main/resources directory and configured it in the application.properties of the Config Server:

encrypt.key-store.location=classpath:config-server.keystore
encrypt.key-store.password=changeme
encrypt.key-store.alias=config-server-key
encrypt.key-store.secret=changeme
spring.cloud.config.server.encrypt.enabled=true
I restarted the Config Server and used Postman to make a POST request to /encrypt with plain values like root. I received back encrypted strings like:
I got 500 Internal Error, I couldnt find the issue but know the process how to hide the username and passowrd after i receive response from postman.

![image](https://github.com/user-attachments/assets/495c66ab-6741-4737-a347-ae18f00b7180)
![image](https://github.com/user-attachments/assets/0710eb6e-3d5f-41c1-9be9-296f709429a8)

# Step5. Monitored My Microservices Using Prometheus and Grafana
I integrated Prometheus and Grafana to monitor my services (SMS, Search, Gateway, Details) in real time.
In each service, I added these dependencies in pom.xml:

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
I configured the following properties in each service to expose metrics:

management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
I confirmed that each service was exposing metrics at:

http://localhost:{port}/actuator/prometheus
I downloaded and ran Prometheus, and created a prometheus.yml config like:

global:
  scrape_interval: 5s

scrape_configs:
  - job_name: 'sms'
    static_configs:
      - targets: ['localhost:8081']
  - job_name: 'search'
    static_configs:
      - targets: ['localhost:8300']
  - job_name: 'gateway'
    static_configs:
      - targets: ['localhost:8200']
I verified in Prometheus UI (http://localhost:9090) that all services were being scraped successfully.

## Then I installed Grafana, added Prometheus as a data source, and built a custom dashboard with panels for:

✅ JVM memory usage
✅ HTTP request rate
✅ CPU usage
✅ Service health (UP/DOWN)


My dashboard now gives me full visibility into the health and performance of my microservices.

## Screenshots:

![image](https://github.com/user-attachments/assets/5a8712b8-a142-4e30-91cb-8ee8c76b4ce9)
![image](https://github.com/user-attachments/assets/da622b45-bb17-4ceb-9e93-8650016f00ad)
![image](https://github.com/user-attachments/assets/ab0f2626-b7a0-4833-88d3-2cbb0eb4772a)

# Step6. Documented My APIs Using Swagger (OpenAPI 3)
I integrated Swagger UI (Springdoc OpenAPI) into all my microservices to generate interactive REST API documentation.
For the time being i have done for only sms service , I added this dependency to the pom.xml:

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.7.0</version> <!-- Compatible with Spring Boot 2.7.x -->
</dependency>


For example, my /api/students and /port endpoints were automatically visible.
I accessed the documentation in browser via:
http://localhost:8081/swagger-ui/index.html   # SMS service
The Swagger UI:

✅ Displayed all my endpoints grouped by controller
✅ Showed request/response schemas with parameters
✅ Allowed me to test my APIs directly from the browser without using Postman

![image](https://github.com/user-attachments/assets/290e1f84-7321-4141-87b7-005156bb932c)

# Step 7. Centralized Logging with Splunk Universal Forwarder and Splunk Cloud
I configured log centralization using Splunk to collect logs from my microservices (SMS).
I created a logs/ folder in my project root, and configured each service to write logs to its own file:

# In application.properties
logging.file.name=logs/sms.log   # For SMS

I installed Splunk Universal Forwarder on my local machine and configured it to monitor this log directory.

## In inputs.conf:
[monitor://C:\\Users\\snistal\\Desktop\\Antra_Training\\weather-master\\logs]
disabled = false
index = main
sourcetype = springboot
## In outputs.conf:
[tcpout]
defaultGroup = default-autolb-group
[tcpout:default-autolb-group]
server = prd-p-xxxx.splunkcloud.com:9997
[tcpout-server://prd-p-xxxx.splunkcloud.com:9997]
🔒 I realized that Splunk Cloud trial does not expose TCP (9997) inputs by default.

Screenshot:
![image](https://github.com/user-attachments/assets/c25189f0-4af4-41b3-9a6e-4e51b09f1675)

