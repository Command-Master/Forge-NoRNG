norng.jar: NoRandom.java Endpoint.java
	/usr/lib/jvm/java-8-openjdk-amd64/bin/javac -XDignore.symbol.file=true *.java
	jar cmf manifest.txt norng.jar *.class
