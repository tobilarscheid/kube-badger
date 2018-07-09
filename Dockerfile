FROM openjdk:8-jdk-alpine3.7 as builder
COPY . .
RUN ./gradlew shadowJar

FROM openjdk:8-jre-alpine3.7
COPY --from=builder build/libs/kube-badger-all.jar /application/kube-badger-all.jar
CMD ["java", "-cp", "/application/kube-badger-all.jar", "com.larscheid.Server"]