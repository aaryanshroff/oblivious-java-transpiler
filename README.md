# Oblivious Java Transpiler

To build it, you will need to download and unpack the latest (or recent) version of Maven (https://maven.apache.org/download.cgi)
and put the `mvn` command on your path.
Then, you will need to install a Java 1.8 (or higher) JDK (not JRE!), and make sure you can run `java` from the command line.
Now you can run `mvn clean install` and Maven will compile your project,
an put the results it in two jar files in the `target` directory.
If you like to run from the command line,
execute `java -jar target/oblivious-java-transpiler-1.0-SNAPSHOT-shaded.jar`.
