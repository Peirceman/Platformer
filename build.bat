@echo off
mkdir out\production\platformer
mkdir out\artifacts\platformer_jar
xcopy /s /e /y res out\production\platformer\res
javac -cp "lib\ArgParser.jar;lib\java-json.jar" --source-path src -d out\production\platformer src\com\platformer\Main.java
mkdir lib\exploded
cd lib\exploded
jar -xf ../ArgParser.jar
jar -xf ../java-json.jar
cd ../..
copy my.lev out\artifacts\platformer_jar
jar -cmf src\META-INF\MANIFEST.MF out\artifacts\platformer_jar\platformer.jar -C out\production\platformer\ . -C lib\exploded .
rmdir /s /q lib\exploded