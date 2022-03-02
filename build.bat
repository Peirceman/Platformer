@echo off

if not exist out (
	mkdir out\production\platformer
	mkdir out\artifacts\platformer_jar
)

javac -p lib --source-path src -d out\production\platformer src\com\platformer\Main.java src\module-info.java | exit
jar -c -f out\artifacts\platformer_jar\platformer.jar -e com.platformer.Main -C . res -C out\production\platformer .

if "%1" == "" (
	exit /b
)

setlocal EnableDelayedExpansion
set first_arg=%1
set args=""
shift
:parse
	if "%~1" neq "" (
		set args="!args:~1,-1! %~1"
		shift
		goto :parse
	)
	set args=!args:~2,-1!

if %first_arg% == package (
	jpackage !args! --win-shortcut-prompt --win-menu --win-dir-chooser --app-version %date:~-2%.%date:~-7,2%.%date:~-10,2%%time:~0,2%%time:~3,1% --license-file LICENSE --resource-dir res -n platformer -p out\artifacts\platformer_jar;lib -m platformer/com.platformer.Main
	exit /b
)

>&2 echo unknown arg %first_arg%

endlocal



