#!/bin/sh
mkdir -p out/production/platformer
mkdir -p out/artifacts/platformer_jar
cp -r res out/production/platformer
javac -cp "lib/ArgParser.jar;lib/java-json.jar" --source-path src -d out/production/platformer src/com/platformer/Main.java
mkdir lib/exploded
cd lib/exploded
jar -xf ../ArgParser.jar
jar -xf ../java-json.jar
cd ../..
cp my.lev out/artifacts/platformer_jar
jar -cmvf src/META-INF/MANIFEST.MF out/artifacts/platformer_jar/platformer.jar -C out/production/platformer . -C lib/exploded .
rm -r lib/exploded