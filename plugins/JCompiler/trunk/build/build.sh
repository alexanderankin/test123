#!/bin/sh

# -----------------------------------------------------------
# The targets are the different build scripts.
# The default "dist" is suggested
# and does not require any external packages
# 
# "core"           target builds Turbine core classes
# "naming"         target builds Turbine core + naming classes
# "clean"          target #oves bin directory
# "dist"           target builds "core" + jar file
#
# -----------------------------------------------------------
TARGET=${1}
# TARGET=javadoc
# TARGET=core
# TARGET=naming
# TARGET=clean
# TARGET=dist

#-------------------------------------------------------------------
# Define the paths to each of the packages
# If you use the naming target then NAMING must be defined.
#-------------------------------------------------------------------
VILLAGE=/projects/servlets/village/classes
ECS=/projects/servlets/ecs/classes
JAVAMAIL=/classes/javamail/mail.jar
JAF=/classes/jaf/activation.jar
JSDK=/classes/JSDK2.0/lib/jsdk.jar
NAMING=
#--------------------------------------------
# No need to edit anything past here
#--------------------------------------------
if test -z "${TARGET}"; then 
TARGET=dist
fi

CP=${CLASSPATH}:${JSDK}:${JAVAMAIL}:${JAF}:${VILLAGE}:${ECS}:${NAMING}:ant.jar:projectx-tr2.jar

BUILDFILE=build.xml

java -classpath ${CP} org.apache.tools.ant.Main -buildfile ${BUILDFILE} ${TARGET}
