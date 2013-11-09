<?xml version="1.0" ?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

    <xsl:import href="partial.xsl"/>
    
    <xsl:output method="text"/>
    
    <xsl:template match="/hello">
    <xsl:text>this is a greeting: </xsl:text><xsl:call-template name="hello"/>
    </xsl:template>
</xsl:stylesheet>
