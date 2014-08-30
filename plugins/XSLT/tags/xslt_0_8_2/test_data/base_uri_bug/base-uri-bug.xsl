<!-- this stylesheet was submitted as plugin bug #1747005
     I edited it to replace the <xsl:attribute select="base-uri(/)"/> with
     a direct attribute definition.
     The base-uri function is not defined for XSL 1.0 so an error should be
     reported when using Xalan
     
     input : test_data/simple/source.xml
     output: nothing : should signal an exception
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output indent="yes" method="xml"/>

	<xsl:template match="/">
		<test href="{base-uri(/)}"/>
	</xsl:template>
	
</xsl:stylesheet>
