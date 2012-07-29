<?xml version="1.0" ?>
<!-- :mode=xsl:tabSize=4:indentSize=4:folding=explicit: -->
<!-- 
    a transformation to strip SF XML export from sensitive date (work in progress).
    It currently keeps only content of tracker items and metadata about the export.
    TODO: the tracker items history seems to contain IP addresses. Strip them.
    
    Copyright © 2012 - Eric Le Lay <kerik-sf@users.sf.net>
    
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
-->
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
    version="2.0">

    <xsl:template match="/document">
    	<xsl:copy>
    	<!-- skip any other section -->
    	<xsl:apply-templates select="export_details"/>
    	<xsl:apply-templates select="trackers"/>
    	</xsl:copy>
    </xsl:template>
	
    <xsl:template match="element()">
	  <xsl:copy>
		<xsl:apply-templates select="@*,node()"/>
	   </xsl:copy>
	</xsl:template>
	
	<xsl:template match="attribute()|text()|comment()|processing-instruction()">
	  <xsl:copy/>
	</xsl:template>
	
	<!-- remove ip addresses -->
	<xsl:template match="history_entry[field_name='IP']"/>

</xsl:stylesheet>
