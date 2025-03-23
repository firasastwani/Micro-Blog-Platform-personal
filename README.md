Start the MySQL docker container and get the mysql prompt.

Create the database and the user table using the given database_setup.sql file. This is needed only if this is the first time running the web app.

After the initial database setup, you can run the starter code as it is. The starter code can demo the app features with the static mock data it already has. You can use this to get an idea of how features should work. Note that the project code stays in your local machine. Not in the docker container.

Navigate to the directory with the pom.xml using the terminal in your local machine and run the following command:
On unix like machines:
mvn spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port=8081'
On windows command line:
mvn spring-boot:run -D"spring-boot.run.arguments=--server.port=8081"
On windows power shell:
mvn spring-boot:run --% -Dspring-boot.run.arguments="--server.port=8081"

Open the browser and navigate to the following URL:
http://localhost:8081/

Harley Guan: Implemented final version of hashtag search and comment, implemented prototype of post in homepage.
