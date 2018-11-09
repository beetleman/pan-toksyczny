FROM java:8-alpine

COPY target/uberjar/pan-toksyczny.jar /pan-toksyczny/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/pan-toksyczny/app.jar"]
