# Use JRE instead of JDK for smaller runtime image
FROM eclipse-temurin:23-jre-alpine

# Add curl for health checks
RUN apk add --no-cache curl

# Add non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# Copy the pre-built JAR file
COPY target/app.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:6996/health || exit 1

EXPOSE 6996

# Use exec form for better signal handling
ENTRYPOINT ["java", "-jar", "app.jar"]