# java 21 환경 os 선택
FROM eclipse-temurin:21-jdk

# 빌드 시 생성된 jar 파일의 위치 지정
ARG JAR_FILE=build/libs/*.jar

# jar 파일을 컨테이너 내부로 복사 (이름은 app.jar로 변경)
COPY ${JAR_FILE} app.jar

# 컨테이너 실행 시 자바 실행 명령 (app.jar 실행)
ENTRYPOINT ["java", "-jar", "/app.jar"]