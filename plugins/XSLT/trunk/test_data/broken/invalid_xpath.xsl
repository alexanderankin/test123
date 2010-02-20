<?xml version="1.0" ?>
<!-- this stylesheet is broken in a way not captured by a schema :
     it uses the @atts-within-att within an attribute (match=@att).
     Hopefuly, it is detected during stylesheet compilation
  -->
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

    <xsl:output method="text"/><xsl:template match="/hello/@att">
    <xsl:text>hello </xsl:text><xsl:value-of select="@att-within-att"/>
    </xsl:template>
</xsl:stylesheet>
