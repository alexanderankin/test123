<?xml version="1.0" ?>
<!-- toy transformation extracting the title of the actions in the XSLT plugin
	 input: actions.xml
	 output: actions.html
  -->
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0">

    <xsl:output method="html"/>
    
    <xsl:variable name="props" select="unparsed-text(resolve-uri('../../XSLT.props'))"/>
    
    <xsl:template match="/">
    <html>
    <body>
    <h1>XSLT's actions</h1>
    
    <ul>
    <xsl:apply-templates/>
    </ul>
    </body>
    </html>
    </xsl:template>
    
    <xsl:template match="ACTION">
    <xsl:variable name="label-prop" select="concat(replace(@NAME,'\.','\\.'),'\.label')"/>
    <xsl:variable name="label-regex" select="concat('^',$label-prop,'=(.+)$')"/>
    <xsl:variable name="label">
    <xsl:analyze-string select="$props" regex="{$label-regex}" flags="m">
    <xsl:matching-substring>
    <xsl:value-of select="regex-group(1)"/>
    </xsl:matching-substring>
    </xsl:analyze-string>
    </xsl:variable>
    <li>
    <xsl:choose>
    <xsl:when test="not($label = '')">
    <xsl:value-of select="concat($label,' (',@NAME,')')"/>
    </xsl:when>
    <xsl:otherwise>
    <em><xsl:value-of select="@NAME"/></em>
    </xsl:otherwise>
    </xsl:choose>
    </li><xsl:text>&#10;</xsl:text>
    </xsl:template>
</xsl:stylesheet>
