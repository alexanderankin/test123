#!/usr/bin/sh
# astyle.sh
#
# (c) 2001 Dirk Moebius

java -classpath "$CLASSPATH:../lib/astyle.jar" astyle.AStyle $*
