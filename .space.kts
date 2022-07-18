job("Build and publish") {
    container(displayName = "Gradle build", image = "openjdk:17") {
        kotlinScript { api ->
            api.gradlew("build")
            api.gradlew("publish")
        }
    }
}