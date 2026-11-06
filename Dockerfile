# =========================================================
# ETAPA 1: compilar la aplicación (necesita el JDK completo)
# =========================================================
FROM eclipse-temurin:25-jdk AS compilacion
WORKDIR /app

# Primero, SOLO lo necesario para descargar las dependencias.
# Si el pom.xml no cambia, Docker reutiliza esta capa y no las vuelve a descargar
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

# Después, el código (lo que cambia en cada versión)
COPY src ./src
RUN ./mvnw package -DskipTests -B

# =========================================================
# ETAPA 2: separar el JAR en capas
# =========================================================
FROM eclipse-temurin:25-jdk AS capas
WORKDIR /capas
COPY --from=compilacion /app/target/*.jar app.jar
# Herramienta de Spring Boot que extrae el JAR en sus capas
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extraido

# =========================================================
# ETAPA 3: la imagen final (solo el JRE, sin herramientas de compilación)
# =========================================================
FROM eclipse-temurin:25-jre
# Un usuario sin privilegios: si alguien comprometiera la aplicación, no sería root
RUN useradd --system --uid 1001 spring
USER spring
WORKDIR /app

# Las capas, de la que menos cambia a la que más cambia
COPY --from=capas /capas/extraido/dependencies/ ./
COPY --from=capas /capas/extraido/spring-boot-loader/ ./
COPY --from=capas /capas/extraido/snapshot-dependencies/ ./
COPY --from=capas /capas/extraido/application/ ./

EXPOSE 8080
# MaxRAMPercentage: la JVM usará como máximo el 75 % de la memoria del contenedor
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]