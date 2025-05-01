 # Step 1: Integrating Student Management Service (SMS) into Spring Cloud Project
As the first step in transforming our standalone Student Management Service (SMS) into a microservice-based architecture using Spring Cloud, I successfully added the SMS module into the existing microservices project structure.
## Tasks Completed:
Moved SMS into the Microservices Workspace:
The sms module was copied into the microservices parent project directory to ensure it follows the shared build and configuration structure.
Modified pom.xml to Add Spring Cloud Dependencies:
Added the required dependencies in the sms service’s pom.xml:
<!-- Spring Cloud Dependencies -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>

## Enabled Service Discovery and Config Client:
Annotated the main application class with:
## @EnableDiscoveryClient
## @SpringBootApplication

## Updated application.properties:

Replaced local properties with:
Added sms-dev properties in weather-config-repo


Moved Properties to Config Server( weather-config repo aka properties)

The original properties (like server.port, datasource, etc.) were moved from local application.properties into the centralized Config Server repo, under student-service.yml.

Verified Service Registration and Externalized Config:

On running, the sms service:

Registers itself with Eureka Discovery Server

Loads its properties from Spring Cloud Config Server

Screeshots:
![image](https://github.com/user-attachments/assets/14b060a1-faa1-4923-bcdb-c4cf337ff403)
![image](https://github.com/user-attachments/assets/e5c141b1-edd5-4606-8d65-830b87c9d962)


2. Built a Search Service with Ribbon, RestTemplate, and CompletableFuture
I created a search microservice that connects to other services using Ribbon (client-side load balancing).

I configured a @LoadBalanced RestTemplate bean so that I could use service names like http://sms and http://details instead of hardcoded IPs or ports.

Inside the SearchService, I used CompletableFuture.supplyAsync() to parallelize service calls:

✅ One call to the SMS service to get a list of students via /api/students

✅ One call to the Details service via /details/port using:

java
Copy
Edit
restTemplate.getForObject("http://details/port", String.class);
I created a wrapper class GeneralResponse to standardize the API output with:

code (e.g., 200)

timestamp

data (merged result)

I combined the results of both service calls using CompletableFuture.allOf(...)

The final response from /search returns a JSON containing:

Student data from SMS

Port info from Details

ScreenShot:

![image](https://github.com/user-attachments/assets/e200bce4-899a-4b4d-a2a5-783c315d212a)


3. Integrated Hystrix for Circuit Breaking in the Search Service
I added Hystrix to my Search microservice to make it resilient to failures in downstream services.

I updated the pom.xml to include:

xml
Copy
Edit
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

Screenshot:
![image](https://github.com/user-attachments/assets/8912f558-b80a-41b9-be3b-5890631da14a)
