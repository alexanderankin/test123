<?xml version="1.0"?>

<!--
    Stylesheet to generated an table of contents (TOC) for an XHTML file.

    A new XHTML document is created starting with the generated TOC. The
    contents of the orignal XHTML file follows the TOC.

    The TOC is created by looking for H1, H2, H3, H4, H5, and H6 display headings
    tags and adding a TOC entry for each one. The TOC entries are linked to the
    original display heading and are indented according to the display heading
    level (i.e. H3 is indented more than H2).

    Anchors are added before the the display headings in the original XHTML
    file. These are used as the targets of the links in the TOC. The format of
    the names of the targets is: A<id>. Where A is the literial character A and
    <id> is the position() of the display heading in the DOM (each node has a unique
    position in the DOM).

    Limitations:
        &nbsp; entities in the input XHTML file are not supported.

    $Revision: 1.1 $
    $Date: 2001-09-09 15:04:14 $
    $Author: cswilly $
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html" />

  <xsl:template match="/">
    <HTML>
      <HEAD>
        <TITLE><xsl:apply-templates mode="title" /></TITLE>
      </HEAD>
      <BODY>
        <H1><xsl:apply-templates mode="title" /></H1>
        <H2>Table of Contents</H2>
        <xsl:apply-templates mode="toc" />
        <HR/>
        <xsl:apply-templates mode="body"  />
      </BODY>
    </HTML>
  </xsl:template>


  <!--
        title Mode

                Only use the title element.
  -->
  <xsl:template mode="title" match="*/text()"  />
  <xsl:template mode="title" match="TITLE | title" >
    <xsl:value-of select="." />
  </xsl:template>


  <!--
        toc Mode
  -->

  <xsl:template mode="toc" match="*/text()"  />

  <xsl:template mode="toc" match="H1 | h1"  >
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputLink" />
      <BR/>
  </xsl:template>

  <xsl:template mode="toc" match="H2 | h2" >
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputLink" />
      <BR/>
  </xsl:template>

  <xsl:template mode="toc" match="H3 | h3" >
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputLink" />
      <BR/>
  </xsl:template>

  <xsl:template mode="toc" match="H4 | h4" >
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputLink" />
      <BR/>
  </xsl:template>

  <xsl:template mode="toc" match="H5 | h5" >
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputLink" />
      <BR/>
  </xsl:template>

  <xsl:template mode="toc" match="H6 | h6" >
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputBaseIndent" />
      <xsl:call-template name="outputLink" />
      <BR/>
  </xsl:template>


  <!--
        body Mode
  -->

  <!--xsl:template mode="body" match="*" >
    <xsl:text disable-output-escaping="yes" >&lt;</xsl:text><xsl:value-of select="name(.)" /> <xsl:text disable-output-escaping="yes" >&gt;</xsl:text>
    <xsl:apply-templates mode="body"/>
    <xsl:text disable-output-escaping="yes" >&lt;/</xsl:text><xsl:value-of select="name(.)" /> <xsl:text disable-output-escaping="yes" >&gt;</xsl:text>
  </xsl:template-->

  <!--
        Default body mode template.

        For tags which do not have matching templates (after this one), outputs
        the tags, including their attributes, as is. That is, what goes in, is
        what goes out.
  -->
  <xsl:template mode="body" match="*" >
    <!-- Output the tag -->
    <xsl:element name="{name(.)}">
      <!-- Output the tag's attributes -->
      <xsl:for-each select="@*">
        <xsl:attribute name="{name(.)}"><xsl:value-of select="." /></xsl:attribute>
      </xsl:for-each>
      <!-- Output inner tags -->
      <xsl:apply-templates mode="body"/>
    </xsl:element>
  </xsl:template>

  <xsl:template mode="body" match="HTML | html" >
    <!-- Ignore the HTML tag, but output its inner tags -->
    <xsl:apply-templates mode="body"/>
  </xsl:template>

  <xsl:template mode="body" match="HEADER | header" />
  <!-- Ignore the HEADER tag -->

  <xsl:template mode="body" match="BODY | body" >
    <xsl:apply-templates mode="body"/>
  </xsl:template>

  <xsl:template mode="body" match="H1 | H2 | H3 | H4 | H5 | H6 |
                                   h1 | h2 | h3 | h4 | h5 | h6" >
    <xsl:call-template name="outputAnchor" />
    <xsl:element name="{name(.)}">
    <xsl:value-of select="." />
    </xsl:element>
  </xsl:template>

  <!--xsl:template mode="body" match="H2" >
    <xsl:call-template name="outputAnchor" />
    <H2><xsl:value-of select="." /></H2>
  </xsl:template-->


  <!-- Output Templates -->

  <xsl:template name="outputAnchor">
    <xsl:element name="A">
      <xsl:attribute name="NAME">a<xsl:number value="position()"/> </xsl:attribute>
    </xsl:element>
  </xsl:template>

  <xsl:template name="outputBaseIndent" >
    <xsl:text disable-output-escaping="yes" >&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
  </xsl:template>

  <xsl:template name="outputLink">
    <xsl:element name="A">
      <xsl:attribute name="HREF">#a<xsl:number value="position()"/> </xsl:attribute>
      <xsl:value-of select="." />
    </xsl:element>
    <xsl:text>
    </xsl:text>
  </xsl:template>

</xsl:stylesheet>
