# ❤️ orders-in-kotlin

Backend API that I always use to learn new stuff, produced using Kotlin

# 🏃‍♂️ How to run

## 🛳 Using docker

Running it with docker is just as running any other image. Building the docker image is agnostic to how the host is configured.

```bs
docker build ./ -t "orders-api"
docker run -d -p 8080:8080 --name orders-api orders-api
```

## 🌱 Using gradlew

1. Make sure JDK 11 is installed
2. Make sure Gradle 6.8.3 is installed
3. Run `./gradlew bootRun` in your terminal

## 🔄 Testing the API

Test an endpoint by:

```bs
curl --header "Content-Type: application/json" \
    --request POST \
    --data '{ "price": 59.99 }' \
    http://localhost:8080/products
```

You should see a response similar to this:

```json
{ "productId": "60afca44654a620577ea0c28" }
```

# 🧐 Logs

This application is configured to use structured logs with the following setup:

- SLF4J, as an abstraction layer for logging
- Logback, as a logging framework
- Elastic stack, for log ingestion and logging frontend

To spin up Elastic Servers

```bs
cd logstash
docker-compose up -d
```
