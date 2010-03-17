<?xml version="1.0" encoding="ISO-8859-1" ?>
<!-- this XSLT 2.0 stylesheet demonstrates that the output URI is correctly
	 set, since the output-document.txt is next to the intended output.
	 see bug #752074
	 input : test_data/simple/source.xml
     output: output-document.txt, next to the file designated as output 
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:template match="/">
		<xsl:result-document method="text" href="output-document.txt">
		<xsl:text>Hello world !</xsl:text>
		</xsl:result-document>
	</xsl:template>
	
</xsl:stylesheet>
