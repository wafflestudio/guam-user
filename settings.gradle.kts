rootProject.name = "user"

include(
    "app:api",
    "app:notification-consumer",
    "client",
    "model",
    "service",
    "infra"
)

project(":client").projectDir = file("app/client")
project(":model").projectDir = file("app/model")
