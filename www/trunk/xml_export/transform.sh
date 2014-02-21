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
Usage: $0 JEDIT-BACKUP-XXXX DEST_DIR/

    JEDIT-BACKUP-XXXX: direct export from SourceForge JSON export (uncompressed)

    DEST_DIR  : directory to put index.html and subdirs per tracker in it
"

if [ $# != 2 ]; then
	echo "$usage"
	exit -1
fi

jar=~/.jedit/jars/saxon9he.jar
mydir=`dirname $0`
jackson=$mydir/jackson-core-2.3.1.jar
source=$1
stylesheet=$mydir/transform.xsl
destdir=$2
output=$2/index.html

if [ ! -f "$jar" ]; then
	echo "saxonhe.jar is necessary
install SaxonPlugin in jEdit and/or modify the 'jar' variable in this script"
	exit -1
fi

if [ ! -f "$jackson" ]; then
	echo "jackson-core-xxx.jar is necessary
Download it from maven (http://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/)"
	exit -1
fi

if [ ! -d "$source" ]; then
	echo "Source $source is not a directory"
	exit -1
fi

if [ -f "$destdir" ]; then
	echo "Destination $destdir exists and is not a directory"
	exit -1
fi

if [ ! -f "$mydir/org/jedit/JSonXMLReader.class" ]; then
	(cd $mydir && javac -cp "$jackson" org/jedit/JSonXMLReader.java)
fi

echo Preparing...
mkdir -p "$destdir"
for i in "$mydir/style.css" \
	 "$mydir/jquery-latest.js" \
	 "$mydir/jquery.dataTables.js" \
	 "$mydir/jquery.dataTables.htmlnum.js" \
	 "$mydir/jquery.dataTables.css" ; do
	
	cp -v $i "$destdir"
done
echo "Signature: 8a477f597d28d172789f06886806bc55
# This file is a cache directory tag created by (application name).
# For information about cache directory tags, see:
#http://www.brynosaurus.com/cachedir/" > "$destdir/CACHEDIR.TAG"

# timestamp based on archive contents filename.
# There is no export metadata in the zip
exportts=`basename $source`
exportts=`echo $exportts |  sed 's/jedit-backup-\(....\)-\(..\)-\(..\)-\(..\)\(..\)\(..\)/\1-\2-\3 - \4:\5:\6z/'`

# using XInclude to keep the transform.xsl simple
# I first used separate xml files but the transform was then full of document(@link)/
# It could also be feasible to transform json to XML on the fly (using the -x saxon flag).
# This is the reason why I implemented the conversion as an XMLReader in the first place,
# but I've actually not tried it, because it was more convenient to see the XML source when
# tweaking the transform.
echo "<document xmlns:xi='http://www.w3.org/2001/XInclude'>
		<export_details><time>$exportts</time></export_details>
		<trackers>
" > "$source/trackers.xml"
for i in $source/*.json; do
	j=`basename "$i"`
	j=${j%.json}.xml
	echo "			<xi:include href=\"$j\" />" >> "$source/trackers.xml"
done
echo "		</trackers>
</document>" >> "$source/trackers.xml"

for i in $source/*.json; do
	j=${i%.json}.xml
	echo "$i --> $j"
	java -cp "$jackson:."  org.jedit.JSonXMLReader "$i" > "$j"
done

echo Running transformation...
java -jar "$jar" "-s:$source/trackers.xml" "-xsl:$stylesheet" "-o:$output" "-xi:on"
echo "Done !"
