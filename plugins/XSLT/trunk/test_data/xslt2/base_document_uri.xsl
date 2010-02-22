<?xml version="1.0" encoding="ISO-8859-1" ?>
<!--
	 input : test_data/simple/source.xml
     output: Untitled x
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:output method="text"/>
	
	<xsl:template match="/">
		document:<xsl:value-of select="document-uri(/)"/>
		base-uri:<xsl:value-of select="base-uri(/)"/>
		stylesheet:<xsl:value-of select="document-uri(document(''))"/>
	</xsl:template>
	
</xsl:stylesheet>
