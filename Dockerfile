FROM amazoncorretto:21-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG DEPENDENCY=build/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
copy ./configs/config.properties ./configs/config.properties
ENTRYPOINT ["java","-cp","app:app/lib/*","com.multirkh.chimhahaclone.ChimhahaCloneApplication"]