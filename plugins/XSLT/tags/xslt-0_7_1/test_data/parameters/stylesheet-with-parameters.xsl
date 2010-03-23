<!--      
     input : any
     output: Hello $p !
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="text"/>

	<xsl:param name="p"/>
	<xsl:param name="q"/>
	
	<xsl:template match="/">
		<xsl:text>Hello </xsl:text><xsl:value-of select="concat($p,' ',$q)"/>
	</xsl:template>
	
</xsl:stylesheet>
