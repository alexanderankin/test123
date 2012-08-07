<?xml version="1.0" encoding="utf-8" ?>
<!-- :mode=xsl:tabSize=4:indentSize=4:folding=explicit: -->
<!-- 
    a transformation to produce a bunch of HTML files from SF XML export (only the trackers section is required).
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
    xmlns:local="urn:local:functions"
    version="2.0">

    <xsl:output method="html" indent="yes"/>
    
    <!-- utility: be able to put double quotes in computed values: -->
    <xsl:variable name="quote" select='""""' />
    
    <!-- {{{ functions -->
    
    <!-- {{{ local:include_status(current)
       modify this function to include also Closed tickets -->
	<xsl:variable name="status_to_include" select="('Open','Pending')" as="xs:string*"/>
    <xsl:function name="local:include_status" as="xs:string*">
        <xsl:param name="current" as="node()"/>
        <xsl:sequence select="$current/ancestor-or-self::tracker/statuses/status[name = $status_to_include]/id"/>
    </xsl:function><!-- }}} -->
    
    <!-- {{{ local:status_name(current,status_id) -->
    <xsl:function name="local:status_name" as="xs:string">
        <xsl:param name="current" as="node()"/>
        <xsl:param name="id" as="xs:string"/>
        <xsl:value-of select="$current/ancestor-or-self::tracker/statuses/status[id = $id]/name"/>
    </xsl:function><!-- }}} -->

    <!-- {{{ local:format_date(date_in_seconds) -->
    <xsl:function name="local:format_date" as="xs:string">
        <xsl:param name="date" as="xs:string"/>
        <xsl:value-of select="format-dateTime(
            xs:dateTime('1970-01-01T00:00:00Z') + xs:integer($date) * xs:dayTimeDuration('PT1S'),
            '[Y0001]-[M01]-[D01] - [H01]:[m01]:[s01][Zn]', 'en', 'AD', 'US')"/>
    </xsl:function><!-- }}} -->
    
    <!-- {{{ local:submitter(tracker_item)
         sometime the submitter field is empty (e.g. 2929554).
         I suppose it's when tracker item is submitted by anonymous.
      -->
    <xsl:function name="local:submitter" as="xs:string">
        <xsl:param name="tracker_item" as="node()"/>
        <xsl:value-of select="($tracker_item/submitter/text(),'Anonymous')[1]"/>
    </xsl:function><!-- }}} -->
    
    <!-- }}} -->
    
    <!-- {{{ root template -->
    <xsl:template match="/">
		<xsl:apply-templates select="/document/trackers"/>
    </xsl:template>
    
    <xsl:template match="trackers">
		<xsl:message>Constructing main index.html</xsl:message>
        <html>
            <head>
            	<title>Export from SF @ <xsl:value-of select="local:format_date(../export_details/time)"/> GLOBAL INDEX</title>
                <xsl:call-template name="css-js"/>
            </head>
            <body>
                <h1>Export from SF @ <xsl:value-of select="local:format_date(../export_details/time)"/></h1>
    	
                <xsl:call-template name="foreword"/>

                <a name="top"/>
                <h2>Contents</h2>
                <ul>
                    <xsl:for-each select="tracker">
                        <li>
                            <a href="#tracker_{tracker_id}">
                                <xsl:value-of select="name"/>
                            </a>
                        </li>                           
                    </xsl:for-each>
                </ul>
                <xsl:apply-templates select="tracker"/>
                
                <a id="goto-top" href="#top">(top)</a>
            </body>
        </html>
    	
        <xsl:for-each select="tracker">

            <!-- index for this tracker only (in its directory) -->
            <xsl:message>exporting <xsl:value-of select="name"/></xsl:message>
            <xsl:apply-templates select="." mode="single"/>

            <!-- details of all items in this tracker (1 per page) -->
            <xsl:variable name="itms" as="node()*">
            <xsl:for-each select="tracker_items/tracker_item[status_id = local:include_status(current())]">
            <xsl:sort select="status_id"/>
            <xsl:sort select="id"/>
            <xsl:copy-of select="."/>
            </xsl:for-each>
            </xsl:variable>
            
            <xsl:for-each select="tracker_items/tracker_item[status_id = local:include_status(current())]">
            <xsl:sort select="status_id"/>
            <xsl:sort select="id"/>
                <xsl:variable name="pos" select="position()"/>
                <xsl:variable name="cnt" select="last()"/>
                <xsl:apply-templates select="." mode="details">
                    <xsl:with-param name="pos" select="$pos"/>
                    <xsl:with-param name="cnt" select="$cnt"/>
                    <xsl:with-param name="prev" select="$itms[$pos - 1]"/>
                    <xsl:with-param name="next" select="$itms[$pos + 1]"/>
                </xsl:apply-templates>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template><!-- }}} -->
    
    <!-- {{{ template for tracker in global index -->
    <xsl:template match="tracker">
    	
        <a name="tracker_{tracker_id}"></a>
        <h2>
            <xsl:value-of select="name"/>
        </h2>
        <a href="{name}/index.html">Details...</a>
        <p>
            <xsl:value-of select="description"/>
        </p>
	
        <xsl:apply-templates select="statuses" />
        
        <xsl:apply-templates select="tracker_items">
        <xsl:with-param name="item-directory" select="concat(name,'/')"/>
        </xsl:apply-templates>

    </xsl:template><!-- }}} -->
    
    <!-- {{{ template for standalone tracker index (mode=single) -->
    <xsl:template match="tracker" mode="single">
            <!-- one index for this tracker -->
            <xsl:result-document href="{name}/index.html" method="html" indent="yes" encoding="utf-8">
            <html>
                <head>
                <title>Tracker <xsl:value-of select="name"/></title>
                <xsl:call-template name="css-js"><xsl:with-param name="path-to-root" select="'../'"/></xsl:call-template>
                </head>
                <body>
                <!-- {{{ navigation -->
                <xsl:for-each select="preceding-sibling::tracker[1]">
                <a style="float:left" href="../{name}/index.html" title="Go to {name} index">Previous</a>
                </xsl:for-each>
                <xsl:for-each select="following-sibling::tracker[1]">
                <a style="float:right" href="../{name}/index.html" title="Go to {name} index">Next</a>
                </xsl:for-each>
                <a style="margin:0 30% 0 30%; text-align:center; display:block" href="../index.html" title="index of all trackers">Global index</a><!-- fixme: how to get the base output url ? -->
                <a id="online" href="{url}">See it online !</a><!-- }}} -->

                <h1><xsl:value-of select="name"/> <span class="subtitle"> - <xsl:value-of select="description"/></span></h1>
            
                <xsl:call-template name="foreword"/>

                <xsl:apply-templates select="statuses" />
                
                <xsl:apply-templates select="tracker_items">
                <xsl:with-param name="item-directory" select="''"/>
                </xsl:apply-templates>

                </body>
            </html>
            </xsl:result-document>

    </xsl:template><!-- }}} -->

    <!-- {{{ template for status counts -->
    <xsl:template match="statuses">
    	<xsl:variable name="tableid" select="generate-id()"/>
	<script>
	$(document).ready(function(){
	$("#<xsl:value-of select="$tableid"/>").tablesorter({ sortList: [[0,0]] });
	});
	</script>
        <table id="{$tableid}">
            <thead>
                <th>Status</th>
                <th>Count</th>
            </thead>
            <tbody>
                <xsl:for-each select="status">
                    <tr>
                        <td>
                            <xsl:value-of select="name"/>
                        </td>
                        <td>
                            <xsl:value-of select="count(ancestor::tracker/tracker_items/tracker_item[status_id = current()/id])"/>
                        </td>
                    </tr>
                </xsl:for-each>
            </tbody>
        </table>
	</xsl:template><!-- }}} -->

	<!-- {{{ template for table of tickets -->
    <xsl:template match="tracker_items">
        <xsl:param name="item-directory" as="xs:string"/>
        
        <xsl:variable name="fields" as="node()*">
            <field name="id" title="Id" sort="integer"/>
            <field name="summary" title="Title"/>
            <field name="status_id" title="Status"/>
            <field name="submit_date" title="Submitted"/>
            <field name="submitter" title="By"/>
            <field name="assignee" title="Assigned to"/>
        </xsl:variable>

    	<xsl:variable name="tableid" select="generate-id()"/>
	<script>
	$(document).ready(function(){
	$("#<xsl:value-of select="$tableid"/>").tablesorter({ sortList: [[2,0],[0,0]], headers:{ 
	<xsl:for-each select="$fields">
		<xsl:value-of select="concat((position() - 1),': {sorter:',$quote, (@sort,'text')[1], $quote,'}')"/>
		<xsl:if test="position() &lt; last()">, </xsl:if>
	</xsl:for-each>
	<xsl:text>} });</xsl:text>
	});
	</script>
        <table id="{$tableid}">
            <thead>
                <title>List of Tracker items</title>
                <tr>
                    <xsl:for-each select="$fields">
                        <th>
                            <xsl:value-of select="@title"/>
                        </th>
                    </xsl:for-each>
                </tr>
            </thead>
            <tbody>
                <xsl:for-each select="tracker_item[status_id = local:include_status(current())]">
                    <xsl:sort select="status_id"/>
                    <xsl:sort select="id"/>
                    <tr>
                        <th>
                            <a href="{$item-directory}{id}.html" title="details..."><xsl:value-of select="id"/></a>
                        </th>
                        <td>
                            <xsl:value-of select="summary"/>
                        </td>
                        <td>
                            <xsl:value-of select="local:status_name(.,status_id)"/>
                        </td>
                        <td>
                            <xsl:value-of select="local:format_date(submit_date)"/>
                        </td>
                        <td>
                            <xsl:value-of select="local:submitter(.)"/>
                        </td>
                        <td>
                            <xsl:value-of select="assignee"/>
                        </td>
                    </tr>
                </xsl:for-each>
            </tbody>
            <tfoot>
                <tr>
                    <xsl:for-each select="$fields">
                        <th>
                            <xsl:value-of select="@title"/>
                        </th>
                    </xsl:for-each>
                </tr>
            </tfoot>
        </table>

    </xsl:template><!-- }}} -->

    <!-- {{{ template for standalone tracker_item (mode=details) -->
    <xsl:template match="tracker_item" mode="details">
        <xsl:param name="pos" as="xs:integer"/>
        <xsl:param name="cnt" as="xs:integer"/>
        <xsl:param name="prev" as="node()*"/>
        <xsl:param name="next" as="node()*"/>
        
        <xsl:result-document href="{ancestor::tracker/name}/{id}.html" method="html" indent="yes" encoding="utf-8">
            <html>
                <head>
                <xsl:call-template name="css-js"><xsl:with-param name="path-to-root" select="'../'"/></xsl:call-template>
                </head>
                <body>
                    <!-- {{{ navigation -->
                    <xsl:for-each select="$prev">
                    <a style="float:left" href="{id}.html" title="{summary}">Previous</a>
                    </xsl:for-each>
                    <xsl:for-each select="$next">
                    <a style="float:right" href="{id}.html" title="{summary}">Next</a>
                    </xsl:for-each>
                    <a style="margin:0 30% 0 30%; text-align:center; display:block" href="index.html" title="index of tracker">Tracker index</a><!-- fixme: how to get the base output url ? -->
                    <a id="online" href="{url}">See it online !</a>
                    <!-- }}} -->
                    
                    <h1>
                        (<xsl:value-of select="$pos"/>/<xsl:value-of select="$cnt"/>) <xsl:value-of select="id"/> - <xsl:value-of select="summary"/>
                    </h1>
    	
                    <p id="details"><xsl:for-each select="details"><xsl:call-template name="format-text"/></xsl:for-each></p>
                    <!-- {{{ data-->
                    <table id="data">
                        <tr>
                            <th>Submitted</th>
                            <td>
                                <xsl:value-of select="local:submitter(.)"/> - <xsl:value-of select="local:format_date(submit_date)"/>
                            </td>
                            <th>Assigned</th>
                            <td>
                                <xsl:value-of select="assignee"/>
                            </td>
                        </tr>
                        <tr>
                            <th>Priority</th>
                            <td>
                                <xsl:value-of select="priority"/>
                            </td>
                            <th>Category</th>
                            <td>
                                <xsl:value-of select="(ancestor::tracker/categories/category[id=current()/category_id]/category_name,'None')[1]"/>
                            </td>
                        </tr>
                        <tr>
                            <th>Status</th>
                            <td>
                                <xsl:value-of select="ancestor::tracker/statuses/status[id=current()/status_id]/name"/>
                            </td>
                            <th>Group</th>
                            <td>
                                <xsl:value-of select="(ancestor::tracker/groups/group[id=current()/group_id]/group_name,'None')[1]"/>
                            </td>
                        </tr>
                        <tr>
                            <th>Resolution</th>
                            <td>
                                <xsl:value-of select="ancestor::tracker/resolutions/resolution[id=current()/resolution_id]/name"/>
                            </td>
                            <th>Visibility</th>
                            <td>
                                <xsl:value-of select="if(isprivate/@yes) then 'Yes' else 'No'"/>
                            </td>
                        </tr>
                    </table><!-- }}} -->
                    
                    <!-- {{{ comments -->
                    <h2>Comments</h2>
                    <table id="comments" class="timestamped">
                    <xsl:for-each select="followups/followup">
                    <xsl:sort select="date"/>
                    <tr><th><xsl:value-of select="local:format_date(date)"/><br/><xsl:value-of select="submitter"/></th>
                        <td><xsl:for-each select="details"><xsl:call-template name="format-text"/></xsl:for-each></td>
                    </tr>
                    </xsl:for-each>
                    </table>
                    <!-- }}} -->
                    
                    <!-- {{{ attachments -->
                    <h2>Attachments</h2>
                    <table id="attachments" class="timestamped">
                    <xsl:for-each select="attachments/attachment">
                    <xsl:sort select="date"/>
                    <tr><th><xsl:value-of select="local:format_date(date)"/><br/><xsl:value-of select="submitter"/></th>
                        <td><a href="{url}"><xsl:value-of select="filename"/></a><br/>
                            <p style="font-size:80%"><xsl:value-of select="description"/></p></td>
                    </tr>
                    </xsl:for-each>
                    </table>
                    <!-- }}} -->
                </body>
            </html>
        </xsl:result-document>

    </xsl:template><!-- }}} -->
    
    <!-- {{{ named templates -->
    <!-- {{{ format-text template -->
    <xsl:template name="format-text">
        <xsl:analyze-string select="." regex="\n">
      <xsl:matching-substring>
        <br/>
      </xsl:matching-substring>
      <xsl:non-matching-substring>
        <xsl:value-of select="."/>
      </xsl:non-matching-substring>
    </xsl:analyze-string>
    </xsl:template><!-- }}} -->
    
    <!-- {{{ css-js(path-to-root) template
        common links to css and scripts
      -->
    <xsl:template name="css-js">
    	<xsl:param name="path-to-root" select="''"/>

	<link rel="stylesheet" type="text/css" href="{$path-to-root}style.css"/>
	<script src="{$path-to-root}jquery-latest.js"/>
	<script src="{$path-to-root}jquery.metadata.js"/>
	<script  src="{$path-to-root}jquery.tablesorter.js"/>

    </xsl:template><!-- }}} -->
    
    <!-- {{{ foreword template
      warning that it's not live
      -->
    <xsl:template name="foreword">
		<p class="descr">This list reflects the state from <xsl:value-of select="local:format_date(ancestor-or-self::trackers/../export_details/time)"/>.<br/>
		It is not the live jEdit trackers. For the live trackers, got to <a href="http://sourceforge.net/tracker/?group_id=588">the jEdit project on Sourceforge</a>.
		<br/>
		It doesn't contain every tickets, but only those in the state <xsl:value-of select="string-join($status_to_include,', ')"/>.
		</p>
    </xsl:template>
    <!-- }}} -->
    <!-- }}} -->
</xsl:stylesheet>
