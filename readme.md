To build the jar:

mvn clean compile assembly:single



To start:

java -Dlog4j.configurationFile=./conf/log4j2.xml -jar mbusMaster-1.0-SNAPSHOT-jar-with-dependencies.jar