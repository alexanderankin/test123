<?xml version="1.0" ?>
<!--
  this stylesheet fails at runtime because it creates an element named 'hello to the world'.
  It is not detected at compilation by Xalan.
  
  input: test_data/simple/source.xml
  output: empty document and a warning
-->
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

    <xsl:output method="text"/>
    
    <xsl:template match="/hello">
    <xsl:element name="to the {.}">
    </xsl:element>
    </xsl:template>
</xsl:stylesheet>
