#!/bin/bash
# remove sensitive information from the XML export (some may remain)
# the goal is to produce something you could give to anybody
# contrary to the main export.xml which contains cleartext emails.
#
# Copyright © 2012 - Eric Le Lay <kerik-sf@users.sf.net>
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or any later version.
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
#/

usage="
Usage: $0 EXPORT.XML DEST_DIR

    EXPORT.XML: direct export from SourceForge 'XML export' (uncompressed)

    DEST_DIR  : directory to put filtered.xml in
"

if [ $# != 2 ]; then
	echo "$usage"
	exit -1
fi

jar=~/.jedit/jars/saxon9he.jar
mydir=`dirname $0`
source=$1
stylesheet=$mydir/only_trackers.xsl
destdir=$2
output=$2/filtered.xml


if [ ! -f "$jar" ]; then
	echo "saxonhe.jar is necessary
install SaxonPlugin in jEdit and/or modify the 'jar' variable in this script"
	exit -1
fi

echo Preparing...
mkdir -p $destdir

echo Running transformation...
java -jar "$jar" "-s:$source" "-xsl:$stylesheet" "-o:$output"
echo "Done !"
