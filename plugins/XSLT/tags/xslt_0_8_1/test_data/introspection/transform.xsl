<?xml version="1.0" ?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

    <xsl:output method="text"/>
    
    <xsl:template match="/describe">
    <xsl:text>What have we got here ?&#10;</xsl:text>
    <xsl:apply-templates/>
    <xsl:text>That's all !</xsl:text>
    </xsl:template>
    
    <xsl:template match="count">
    <xsl:value-of select="count(document('')//*[local-name()=current()/@name])"/>
    </xsl:template>
    
</xsl:stylesheet>
