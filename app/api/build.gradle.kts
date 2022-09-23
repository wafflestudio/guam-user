plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":service"))
    implementation(project(":model"))

    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.11")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.11")

    implementation("com.github.wafflestudio.kotlin-lib:slack-notification-spring-boot-starter:0.0.1")
    implementation("com.github.wafflestudio.kotlin-lib:simple-corouter:corouter-SNAPSHOT")
}
