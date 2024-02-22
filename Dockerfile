FROM public.ecr.aws/t9v6d8o9/ubuntu-20.04:latest as build
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk openjdk-17-jre maven && \
    apt-get clean
ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH
ENV PATH /usr/bin/mvn:$PATH
RUN java -version
RUN mvn -version
WORKDIR /app
COPY . /app
RUN sh ./mvnw clean package

FROM public.ecr.aws/t9v6d8o9/openjdk-17:latest
WORKDIR /app
COPY . /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]