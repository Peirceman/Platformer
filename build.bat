mkdir out\production\platformer
mkdir out\artifacts\platformer_jar
xcopy /s /e res out\production\platformer\res
javac -cp "lib\ArgParser.jar;lib\java-json.jar" --source-path src -d out\production\platformer src\com\platformer\Main.java
jar -cmvf src\META-INF\MANIFEST.MF out\artifacts\platformer_jar\platformer.jar -C 