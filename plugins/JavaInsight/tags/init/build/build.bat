@echo off

REM -----------------------------------------------------------
REM The targets are the different build scripts.
REM The default "core" is suggested
REM and does not require any external packages
REM 
REM "core"           target builds Turbine core classes
REM "naming"         target builds Turbine core + naming classes
REM "clean"          target removes bin directory
REM "dist"           target builds "core" + jar file
REM
REM -----------------------------------------------------------
set TARGET=%1%
REM set TARGET=javadoc
REM set TARGET=core
REM set TARGET=naming
REM set TARGET=clean
REM set TARGET=dist

REM -------------------------------------------------------------------
REM Define the paths to each of the packages.
REM If you use the naming target then NAMING must be defined.
REM -------------------------------------------------------------------
set JAVAMAIL=C:\classes\javamail-1.1.2\mail.jar
set JAF=C:\classes\jaf-1.0.1\activation.jar
set JSDK=C:\classes\jsdk2.0\lib\jsdk.jar
set VILLAGE=e:\projects\village\classes
set ECS=e:\projects\ecs\classes
set NAMING=

REM --------------------------------------------
REM No need to edit anything past here
REM --------------------------------------------

if "%TARGET%" == "" goto setdist
goto final

:setdist
set TARGET=core
goto final

:final
echo Now building %TARGET%...

set CP=%JSDK%;%JAVAMAIL%;%JAF%;%VILLAGE%;%ECS%;%NAMING%;ant.jar;projectx-tr2.jar;javac.jar

set BUILDFILE=build.xml

echo "Classpath: %CP%"

java -classpath %CP% org.apache.tools.ant.Main -buildfile %BUILDFILE% %TARGET%
