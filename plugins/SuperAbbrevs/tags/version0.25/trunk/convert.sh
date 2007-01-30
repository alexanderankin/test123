#!/bin/bash

dist='SuperAbbrevs_jedit4.3pre3.dist'

rm -rf ../$dist
mkdir ../$dist
cp -r . ../$dist
cd ../$dist

for file in $(find . -name '*.java') ; do 
	echo "process: " $file
	mv $file $file.temp
	sed -f convert.sed $file.temp > $file 
	rm $file.temp
done 

mv build.xml build.xml.temp
sed s/'jedit4.2'/'jedit4.3pre3'/g build.xml.temp > build.xml
rm build.xml.temp

mv SuperAbbrevs.props SuperAbbrevs.props.temp
sed "s/plugin\.SuperAbbrevsPlugin\.depend\.0=.*/plugin.SuperAbbrevsPlugin.depend.0=jedit 04.03.03.00/" SuperAbbrevs.props.temp > SuperAbbrevs.props 
rm SuperAbbrevs.props.temp

ant clean 