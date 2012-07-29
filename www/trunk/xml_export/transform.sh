#!/bin/bash
# transform the XML export into a bunch of HTML files for offline browsing
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
Usage: $0 SOURCE.XML DEST_DIR

    SOURCE.XML: either direct export from SourceForge XML export (uncompressed)
                or filtered.xml produced from filter.sh

    DEST_DIR  : directory to put index.html and subdirs per tracker in
"

if [ $# != 2 ]; then
	echo "$usage"
	exit -1
fi

jar=~/.jedit/jars/saxon9he.jar
mydir=`dirname $0`
source=$1
stylesheet=$mydir/transform.xsl
destdir=$2
output=$2/index.html

if [ ! -f "$jar" ]; then
	echo "saxonhe.jar is necessary
install SaxonPlugin in jEdit and/or modify the 'jar' variable in this script"
	exit -1
fi

echo Preparing...
mkdir -p "$destdir"
for i in "$mydir/style.css" \
	 "$mydir/jquery-latest.js" \
	 "$mydir/jquery.metadata.js" \
	 "$mydir/jquery.tablesorter.js" ; do
	
	cp -v $i "$destdir"
done
echo "Signature: 8a477f597d28d172789f06886806bc55
# This file is a cache directory tag created by (application name).
# For information about cache directory tags, see:
#http://www.brynosaurus.com/cachedir/" > "$destdir/CACHEDIR.TAG"


echo Running transformation...
java -jar "$jar" "-s:$source" "-xsl:$stylesheet" "-o:$output"
echo "Done !"
