#! /bin/bash
cvschangelogbuilder.pl -output=listdeltabydate -m=plugins/ShortcutDisplay -d=`cat CVS/Root` -d=/tmp/cvsdiff > changes.txt
