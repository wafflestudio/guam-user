dependencies {
    api("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework:spring-context")

    runtimeOnly("io.r2dbc:r2dbc-h2")
    runtimeOnly("io.r2dbc:r2dbc-pool")
    runtimeOnly("com.github.jasync-sql:jasync-r2dbc-mysql:2.0.8")

    implementation("org.springframework.cloud:spring-cloud-starter-vault-config:3.1.0")
    implementation("com.google.firebase:firebase-admin:7.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava")

    implementation(platform("software.amazon.awssdk:bom:2.15.0"))
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:netty-nio-client")
}
