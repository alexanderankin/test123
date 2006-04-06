#!/bin/bash

dist='SuperAbbrevs_jedit4.3pre3.dist'

cd ..
rm -rf $dist 
cp -r SuperAbbrevs_jedit4.2 $dist
cd $dist

for file in $(find . -name '*.java') ; do 
	echo "process: " $file
	mv $file $file.temp
	sed -f convert.sed $file.temp > $file 
	rm $file.temp
done 

mv build.xml build.xml.temp
sed s/'jedit4.2'/'jedit4.3pre3'/g build.xml.temp > build.xml

ant clean 