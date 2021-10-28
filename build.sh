#!/bin/sh
mkdir -p out/production/platformer
mkdir -p out/artifacts/platformer_jar
cp -r res out/production/platformer
javac -cp "lib/ArgParser.jar;lib/java-json.jar" --source-path src -d out/production/platformer src/com/platformer/Main.java
cd lib
mkdir exploded
cd exploded
jar -xf ../ArgParser.jar
jar -xf ../java-json.jar
cd ../..
jar -cmvf src/META-INF/MANIFEST.MF out/artifacts/platformer_jar/platformer.jar -C out/production/platformer . -C ./../../../lib/exploded .
rm -r lib/exploded