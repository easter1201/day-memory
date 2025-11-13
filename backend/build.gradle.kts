plugins {
	java
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.daymemory"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot Starters
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// PostgreSQL
	runtimeOnly("org.postgresql:postgresql")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.12.3")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

	// Springdoc OpenAPI (Swagger)
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

	// Jackson Java 8 Date/Time
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// DevTools
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// Test Dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
	archiveFileName.set("${project.name}.jar")
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	// 개발 환경 변수 설정
	environment("SPRING_PROFILES_ACTIVE", "dev")
	environment("DB_URL", "jdbc:postgresql://localhost:54320/daymemory")
	environment("DB_USERNAME", "postgres")
	environment("DB_PASSWORD", "postgres")
	environment("JWT_SECRET", "your-secret-key-must-be-at-least-256-bits-long-for-HS256-algorithm-development-only")
	// AI API 설정 (Gemini)
	environment("AI_PROVIDER", "gemini")
	environment("AI_API_KEY", "AIzaSyAnBXOppRPYbiiwmOihxnX8PDuXka9gTF0")
	environment("AI_MODEL", "gemini-2.5-flash")
}
