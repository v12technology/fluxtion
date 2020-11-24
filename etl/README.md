## Building a springboot jar

The springboot jar needs patching with classes from Fluxtion runtime jars. The dynamic
compiler cannot locate classes that are in the BOOT-INF\lib\*.jar. The classes are 
patched into the spring boot fat jar after building.

step 1 - run mvn package
step 2 - on windows run springbootfix\updateBootJar.bat
step 3 execute the fat jar :)

