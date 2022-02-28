#!/bin/sh

set -e

if [ ! -e out ];then
	mkdir -p out/production/platformer
	mkdir -p out/artifacts/platformer_jar
fi

javac -p lib --source-path src -d out/production/platformer src/com/platformer/Main.java src/module-info.java
jar -c -f out/artifacts/platformer_jar/platformer.jar -e com.platformer.Main -C . res -C out/production/platformer .

if [ -z ${1+x} ];then
	exit
fi

if [ "$1" = "package" ];then
	shift
	jpackage "$@" --license-file LICENSE --resource-dir res -n platformer -p out/artifacts/platformer_jar:lib -m platformer/com.platformer.Main
	exit
fi

echo unknown arg $1 >&2
