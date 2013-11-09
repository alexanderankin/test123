<?xml version="1.0" encoding="UTF-8" ?>
<!-- This XSLT template demonstrates that the encoding of the output is
     taken into account, since we write an accented character in non 
     platform default encoding and will be able to load it afterward.
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:param name="encoding"/>

	<xsl:output method="xml" encoding="UTF-8"/>
	<xsl:template match="/">
		<test>réussi !</test>
	</xsl:template>
	
</xsl:stylesheet>
