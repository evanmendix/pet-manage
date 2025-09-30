# 階段 1：使用 Gradle 建構 Ktor 應用程式
# 使用標準 JDK 映像而非 Alpine，避免相容性問題
FROM gradle:8.13-jdk21 AS builder

# 設定工作目錄
WORKDIR /home/gradle/project

# 複製整個專案（排除 .dockerignore 中的檔案）
COPY . .

# 確保 gradlew 有執行權限（處理 Windows 換行符號問題）
RUN dos2unix ./gradlew 2>/dev/null || sed -i 's/\r$//' ./gradlew || true && \
    chmod +x ./gradlew

# 建構 Shadow JAR
# 使用 gradle wrapper 而非系統 gradle
RUN ./gradlew :backend:shadowJar --no-daemon --stacktrace

# 階段 2：建立最終的輕量映像
# 使用 glibc 版的 Eclipse Temurin JRE（避免 Netty tcnative 在 musl/Alpine 上的 SIGSEGV）
FROM eclipse-temurin:21-jre

# 設定工作目錄
WORKDIR /app

# 從 builder 階段複製建構的 fat JAR。
# JAR 檔案通常由 Shadow 外掛程式命名為 'backend-all.jar'
COPY --from=builder /home/gradle/project/backend/build/libs/backend-all.jar ./application.jar

# 暴露應用程式運行時使用的連接埠
EXPOSE 5070

# 運行應用程式的指令
ENTRYPOINT ["java", "-jar", "application.jar"]
