dependencies {
    implementation(project(":model"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.rootProject.group}"
            artifactId = project.name
            version = "${project.rootProject.version}"

            from(components["java"])
        }
    }
}
