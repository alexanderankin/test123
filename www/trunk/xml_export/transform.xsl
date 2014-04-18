<?xml version="1.0" encoding="utf-8" ?>
<!-- :mode=xsl:tabSize=4:indentSize=4:folding=explicit: -->
<!--
    a transformation to produce a bunch of HTML files from SF JSON export,
    xmlified by org/jedit/JSonXMLReader (in the same directory)
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
    xmlns:json="urn:json"
    version="2.0">

    <xsl:output method="html" indent="yes" encoding="utf-8"/>

    <!-- utility: be able to put double quotes in computed values: -->
    <xsl:variable name="quote" select='""""' />

    <!-- modify this variable to include also Closed tickets -->
	<xsl:variable name="status_to_include" select="('open','pending')" as="xs:string*"/>

	<!-- {{{ functions -->


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
        <xsl:value-of select="($tracker_item/reported_by/text(),'Anonymous')[1]"/>
    </xsl:function><!-- }}} -->

    <!-- {{{ local:filter-select(name, values)
         create a select with these values
      -->
    <xsl:function name="local:filter-select" as="node()*">
    	<xsl:param name="name" as="xs:string"/>
        <xsl:param name="values" as="xs:string*"/>
        <select name="name">
        	<!-- empty option to reset filter -->
			<option value=""></option>
        	<xsl:for-each select="$values">
        		<xsl:sort select="."/>
        		<option value="{.}"><xsl:value-of select="."/></option>
        	</xsl:for-each>
        </select>
    </xsl:function><!-- }}} -->
    <!-- {{{ local:filter-input(name)
         create an input with this name and title value
      -->
    <xsl:function name="local:filter-input" as="node()*">
    	<xsl:param name="name" as="xs:string"/>
    	<xsl:param name="size" as="xs:integer"/>
		<input type="text" name="{$name}" size="{$size}" class="search_init" />
    </xsl:function><!-- }}} -->
    <!-- {{{ local:status(status_and_resolution)
         return status from status_and_resolution
      -->
    <xsl:function name="local:status" as="xs:string">
    	<xsl:param name="status" as="xs:string"/>
		<xsl:choose>
			<xsl:when test="starts-with($status,'closed-')">closed</xsl:when>
			<xsl:when test="starts-with($status,'open-')">open</xsl:when>
			<xsl:when test="starts-with($status,'pending-')">pending</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$status"/>
			</xsl:otherwise>
		</xsl:choose>
    </xsl:function><!-- }}} -->
    <!-- {{{ local:resolution(status_and_resolution)
         return resolution from status_and_resolution
      -->
    <xsl:function name="local:resolution" as="xs:string">
    	<xsl:param name="status" as="xs:string"/>
		<xsl:choose>
			<xsl:when test="starts-with($status,'closed-')">
				<xsl:value-of select="substring-after($status,'closed-')"/>
			</xsl:when>
			<xsl:when test="starts-with($status,'open-')">
				<xsl:value-of select="substring-after($status,'open-')"/>
			</xsl:when>
			<xsl:when test="starts-with($status,'pending-')">
				<xsl:value-of select="substring-after($status,'pending-')"/>
			</xsl:when>
			<xsl:otherwise>None</xsl:otherwise>
		</xsl:choose>
    </xsl:function><!-- }}} -->
    <!-- {{{ local:fileSize(size_in_bytes)
         return nicely formatted size (in KB,MB,B)
      -->
    <xsl:function name="local:fileSize" as="xs:string">
    	<xsl:param name="s" as="xs:integer"/>
		<xsl:choose>
			<xsl:when test="$s &lt; 1000">
				<xsl:value-of select="concat($s,'B')"/>
			</xsl:when>
			<xsl:when test="$s &lt; 1000000">
				<xsl:value-of select="format-number($s div 1024,'0.0Kio')"/>
			</xsl:when>
			<xsl:when test="$s &lt; 1000000000">
				<xsl:value-of select="format-number(($s div 1024) div 1024,'0.0Mio')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat($s,'B')"/>
			</xsl:otherwise>
		</xsl:choose>
    </xsl:function><!-- }}} -->

    <!-- }}} -->


	<xsl:variable name="fields" as="node()*">
		<field name="id" title="Id"/>
		<field name="summary" title="Title"/>
		<field name="status_id" title="Status"/>
		<field name="submit_date" title="Submitted"/>
		<field name="submitter" title="By"/>
		<field name="assignee" title="Assigned to"/>
	</xsl:variable>


    <!-- {{{ root template -->
    <xsl:template match="/">
		<xsl:apply-templates select="/document/trackers"/>
    </xsl:template>

    <xsl:template match="trackers">
		<xsl:message>Constructing main index.html</xsl:message>
        <html>
            <head>
            	<title>Export from SF @ <xsl:value-of select="../export_details/time"/> GLOBAL INDEX</title>
                <xsl:call-template name="css-js"/>
            </head>
            <body>
                <h1>Export from SF @ <xsl:value-of select="../export_details/time"/></h1>

                <xsl:call-template name="foreword"/>


                <p class="descr">You can grab
                an archived version of all these pages <a href="Export.tgz">here</a>.</p>

                <a name="top"/>
                <h2>Individual trackers</h2>
                <ul>
                    <xsl:for-each select="json:obj">
                        <li>
                            <xsl:variable name="name" select="tracker_config/json:obj/options/json:obj/mount_label"/>
                            <a href="{$name}/index.html"><xsl:value-of select="$name"/></a>
                        </li>
                    </xsl:for-each>
                </ul>

				<h2>All trackers contents</h2>

				<p>A table listing all items for all trackers is <a href="table.html">available
				here</a>.</p>


				<xsl:variable name="statustableid" select="'statuses'"/>
				<script>
				$(document).ready(function(){
				$("#<xsl:value-of select="$statustableid"/>").dataTable({
						"bPaginate": false,
						"bLengthChange": false,
						"bFilter": false,
						"bSort": true,
						"bInfo": false,
						"bAutoWidth": true
        			});
				});
				</script>
				<table id="{$statustableid}" style="margin-top:2em; margin-bottom:2em">
					<thead>
						<th>Status</th>
						<xsl:for-each select="json:obj">
						<th><xsl:value-of select="tracker_config/json:obj/options/json:obj/mount_label"/></th>
						</xsl:for-each>
					</thead>
					<tbody>
						<xsl:variable name="self" select="." as="node()"/>
						<xsl:for-each select="distinct-values(for $i in (json:obj/open_status_names,
											 	json:obj/closed_status_names)
											 return (for $j in (tokenize($i,' ')) return local:status(normalize-space($j))))">
							<xsl:variable name="status" select="."/>
							<tr>
								<td>
									<xsl:value-of select="$status"/>
								</td>
								<xsl:for-each select="$self/json:obj">
								<td>
									<xsl:value-of select="count(tickets/json:arr/json:obj[local:status(status) = $status])"/>
								</td>
								</xsl:for-each>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>

				<hr style="color:gray; margin-top:2em"/>
				 <p style="color:#666666;">This content is generated statically
				 and uses the jQuery <a href="http://datatables.net/">datatables</a>
				 plugin for interactive display.</p>
            </body>
        </html>

        <xsl:apply-templates select="." mode="datatable"/>

        <xsl:for-each select="json:obj">

            <!-- index for this tracker only (in its directory) -->
            <xsl:message>exporting <xsl:value-of select="tracker_config/json:obj/options/json:obj/mount_label"/></xsl:message>
            <xsl:apply-templates select="." mode="single"/>

            <!-- details of all items in this tracker (1 per page) -->
            <xsl:variable name="itms" as="node()*">
            <xsl:for-each select="tickets/json:arr/json:obj[local:status(status) = $status_to_include]">
			<xsl:sort select="ticket_num" data-type="number"/>
            <json:obj>
            	<xsl:copy-of select="ticket_num"/>
            	<xsl:copy-of select="summary"/>
            </json:obj>
            </xsl:for-each>
            </xsl:variable>

            <xsl:for-each select="tickets/json:arr/json:obj[local:status(status) = $status_to_include]">
			<xsl:sort select="ticket_num" data-type="number"/>
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


    <!-- {{{ template for full datatable -->
    <xsl:template match="trackers" mode="datatable">
		<xsl:message>Constructing main data table</xsl:message>
		<xsl:result-document href="table.html" method="html" indent="yes" encoding="utf-8">
        <html>
            <head>
            	<title>Export from SF @ <xsl:value-of select="../export_details/time"/> GLOBAL INDEX</title>
                <xsl:call-template name="css-js"/>
            </head>
            <body>

            <!-- {{{ navigation -->
                <a style="margin:0 30% 0 30%; text-align:center; display:block" href="index.html" title="index of all trackers">Global index</a>
            <!-- }}} -->

                <h1>All of trackers data <span class="subtitle"> - in one big table</span></h1>

                <xsl:call-template name="foreword"/>


				<xsl:call-template name="datatable">
				<xsl:with-param name="item-directory" select="true()"/>
				<xsl:with-param name="display-tracker" select="true()"/>
				</xsl:call-template>

                <a id="goto-top" href="#top">(top)</a>
            </body>
        </html>
        </xsl:result-document>
    </xsl:template>
    <!-- }}} -->

    <!-- {{{ template for standalone tracker index (mode=single) -->
    <xsl:template match="trackers/json:obj" mode="single">
			<xsl:variable name="name" select="tracker_config/json:obj/options/json:obj/mount_label"/>
            <!-- one index for this tracker -->
            <xsl:result-document href="{$name}/index.html" method="html" indent="yes" encoding="utf-8">
            <html>
                <head>
                <title>Tracker <xsl:value-of select="$name"/></title>
                <xsl:call-template name="css-js"><xsl:with-param name="path-to-root" select="'../'"/></xsl:call-template>
                </head>
                <body>
                <!-- {{{ navigation -->
                <xsl:for-each select="preceding-sibling::json:obj[1]">
                <xsl:variable name="name" select="tracker_config/json:obj/options/json:obj/mount_label"/>
                <a style="float:left" href="../{$name}/index.html" title="Go to {$name} index">Previous</a>
                </xsl:for-each>
                <xsl:for-each select="following-sibling::json:obj[1]">
                <xsl:variable name="name" select="tracker_config/json:obj/options/json:obj/mount_label"/>
                <a style="float:right" href="../{$name}/index.html" title="Go to {$name} index">Next</a>
                </xsl:for-each>
                <a style="margin:0 30% 0 30%; text-align:center; display:block" href="../index.html" title="index of all trackers">Global index</a>
                <a id="online" href="http://sourceforge.net/p/jedit/{tracker_config/json:obj/options/json:obj/mount_point}">See it online !</a><!-- }}} -->

                <h1><xsl:value-of select="$name"/></h1>

                <xsl:call-template name="foreword"/>

                <a name="top"/>
                <xsl:call-template name="statuses" />


				<xsl:call-template name="datatable">
				<xsl:with-param name="item-directory" select="false()"/>
				<xsl:with-param name="display-tracker" select="false()"/>
				</xsl:call-template>

                <a id="goto-top" href="#top">(top)</a>

                </body>
            </html>
            </xsl:result-document>

    </xsl:template><!-- }}} -->

    <!-- {{{ template for status counts -->
    <xsl:template name="statuses">
    	<xsl:variable name="tableid" select="generate-id()"/>
	<script>
	$(document).ready(function(){
	$("#<xsl:value-of select="$tableid"/>").dataTable({
			"bPaginate": false,
			"bFilter": false,
			"bAutoWidth" : false
	});
	});
	</script>
        <table id="{$tableid}">
            <thead>
                <th>Status</th>
                <th>Count</th>
            </thead>
            <tbody>
            	<xsl:variable name="self" select="."/>
                <xsl:for-each select="distinct-values(
                						for $i in (open_status_names,closed_status_names)
                						return (for $j in (tokenize($i,' ')) return local:status(normalize-space($j))))">
                    <tr>
                        <td>
                            <xsl:value-of select="."/>
                        </td>
                        <td>
                            <xsl:value-of select="count($self/tickets/json:arr/json:obj[local:status(status) = current()])"/>
                        </td>
                    </tr>
                </xsl:for-each>
            </tbody>
        </table>
	</xsl:template><!-- }}} -->

	<!-- {{{ template for table of tickets -->
    <xsl:template name="datatable">
        <xsl:param name="item-directory" as="xs:boolean"/>
        <xsl:param name="display-tracker" as="xs:boolean"/>

		<xsl:variable name="tableid" select="'items'"/>
		<script type="text/javascript">
		$(document).ready(function(){
			var oTable = $('#<xsl:value-of select="$tableid"/>').dataTable( {
				"bPaginate": false,
				"bSortCellsTop": true,
				"oLanguage": {
					"sSearch": "Search all columns:"
				},
				"aoColumns": [
				<xsl:if test="$display-tracker">null,</xsl:if>
                { "sType": "num-html" },
                null,
                null,
                null,
                null,
                null
            ]
			} );

			$("thead tr.filter th").each( function(i) {
				$("input", this).bind('keyup', function (e) {
					var c;
					if ( e &amp;&amp; e.which )
					{
						c = e.which;
					}
					else
					{
						c = e.keyCode;
					}
					if( c == 27 ){
						$(this).val("");
					}
					oTable.fnFilter( this.value, i );
				} );
				$('select', this).change( function () {
					oTable.fnFilter( $(this).val(), i );
				} );
			});

		} );
		</script>
		<table id="{$tableid}">
			<thead>
				<title>List of Tracker items</title>
				<!-- 1st header row for sorters -->
				<tr>
					<xsl:if test="$display-tracker">
					<th>Tracker</th>
					</xsl:if>
					<xsl:for-each select="$fields">
					<th><xsl:value-of select="@title"/></th>
					</xsl:for-each>
				</tr>
				<!-- 2nd header row for filters -->
				<tr class="filter">
					<xsl:if test="$display-tracker">
					<th>
					<xsl:sequence select="local:filter-select('tracker',(json:obj/tracker_config/json:obj/options/json:obj/mount_label))"/>
					</th>
					</xsl:if>
					<th>
					<xsl:sequence select="local:filter-input('id',5)"/>
					</th>
					<th>
					<xsl:sequence select="local:filter-input('summary',30)"/>
					</th>
					<th>
					<xsl:sequence select="local:filter-select('status', $status_to_include)"/>
					</th>
					<th>
					<xsl:sequence select="local:filter-input('submit_date',10)"/>
					</th>
					<th>
					<xsl:sequence select="local:filter-select('submitter', (distinct-values(for $i in
						(self::json:obj,json:obj)/tickets/json:arr/json:obj[local:status(status) = $status_to_include]
						return local:submitter($i))))"/>
					</th>
					<th>
					<xsl:sequence select="local:filter-select('assigned', (distinct-values(for $i in
						(self::json:obj,json:obj)/tickets/json:arr/json:obj[local:status(status) = $status_to_include]
						return $i/assigned_to)))"/>
					</th>
				</tr>
			</thead>
			<tbody>
			<!-- always called on trackers or tracker -->
			<xsl:for-each select="(self::json:obj,json:obj)">
			<xsl:sort select="name"/>
				<xsl:variable name="dir" select="if($item-directory) then concat(tracker_config/json:obj/options/json:obj/mount_label,'/') else ''"/>

				<xsl:for-each select="tickets/json:arr/json:obj[local:status(status) = $status_to_include]">
				<xsl:sort select="ticket_num" data-type="number"/>
					<tr>
						<xsl:if test="$display-tracker">
						<td>
							<xsl:value-of select="ancestor::tickets/parent::json:obj/tracker_config/json:obj/options/json:obj/mount_label"/>
						</td>
						</xsl:if>
						<td>
							<a href="{$dir}{ticket_num}.html" title="details..."><xsl:value-of select="ticket_num"/></a>
						</td>
						<td>
							<xsl:value-of select="replace(summary,'[&#x007f;-&#x009f;]','?')"/>
						</td>
						<td>
							<xsl:value-of select="local:status(status)"/>
						</td>
						<td>
							<xsl:value-of select="created_date"/>
						</td>
						<td>
							<xsl:value-of select="local:submitter(.)"/>
						</td>
						<td>
							<xsl:value-of select="assigned_to"/>
						</td>
					</tr>
				</xsl:for-each>
			</xsl:for-each>
		</tbody>
		<!-- repeat column headers (not sortable) !-->
		<tfoot>
			<tr>
			<xsl:if test="$display-tracker">
			<th>Tracker</th>
			</xsl:if>
			<xsl:for-each select="$fields">
			<th><xsl:value-of select="@title"/></th>
			</xsl:for-each>
			</tr>
		</tfoot>
	</table>

    </xsl:template><!-- }}} -->

    <!-- {{{ template for standalone tracker_item (mode=details) -->
    <xsl:template match="json:obj" mode="details">
        <xsl:param name="pos" as="xs:integer"/>
        <xsl:param name="cnt" as="xs:integer"/>
        <xsl:param name="prev" as="node()*"/>
        <xsl:param name="next" as="node()*"/>

        <xsl:variable name="id" select="ticket_num"/>
        <xsl:result-document href="{ancestor::tickets/parent::json:obj/tracker_config/json:obj/options/json:obj/mount_label}/{$id}.html" method="html" indent="yes" encoding="utf-8">
            <html>
                <head>
                <xsl:call-template name="css-js"><xsl:with-param name="path-to-root" select="'../'"/></xsl:call-template>
                </head>
                <body>
                    <!-- {{{ navigation -->
                    <xsl:for-each select="$prev">
                    <a style="float:left" href="{ticket_num}.html" title="{ticket_num} - {replace(summary,'[&#x007f;-&#x009f;]','?')}">Previous</a>
                    </xsl:for-each>
                    <xsl:for-each select="$next">
                    <a style="float:right" href="{ticket_num}.html" title="{ticket_num} - {replace(summary,'[&#x007f;-&#x009f;]','?')}">Next</a>
                    </xsl:for-each>
                    <a style="margin:0 30% 0 30%; text-align:center; display:block" href="index.html" title="index of tracker">Tracker index</a>
                    <a id="online" href="http://sourceforge.net/p/jedit/{ancestor::tickets/parent::json:obj/tracker_config/json:obj/options/json:obj/mount_point}/{$id}">See it online !</a>
                    <!-- }}} -->

                    <h1>
                        (<xsl:value-of select="$pos"/>/<xsl:value-of select="$cnt"/>) <xsl:value-of select="ticket_num"/> - <xsl:value-of select="replace(summary,'[&#x007f;-&#x009f;]','?')"/>
                    </h1>

                    <p id="details"><xsl:for-each select="description"><xsl:call-template name="format-text"/></xsl:for-each></p>
                    <!-- {{{ data-->
                    <table id="data">
                        <tr>
                            <th>Submitted</th>
                            <td>
                                <xsl:value-of select="local:submitter(.)"/> - <xsl:value-of select="created_date"/>
                            </td>
                            <th>Assigned</th>
                            <td>
                                <xsl:value-of select="assigned_to"/>
                            </td>
                        </tr>
                        <tr>
                            <th>Priority</th>
                            <td>
                                <xsl:value-of select="custom_fields/json:obj/_priority"/>
                            </td>
                            <th>Labels</th>
                            <td>
                                <xsl:value-of select="(labels/json:arr,'None')[1]"/>
                            </td>
                        </tr>
                        <tr>
                            <th>Status</th>
                            <td>
                                <xsl:value-of select="local:status(status)"/>
                            </td>
                            <th>Group</th>
                            <td>
                                <xsl:value-of select="(custom_fields/json:obj/_milestone,'None')[1]"/>
                            </td>
                        </tr>
                        <tr>
                            <th>Resolution</th>
                            <td>
                                <xsl:value-of select="local:resolution(status)"/>
                            </td>
                            <!-- couldn't find any in the exports. I suspect they are not exported if they exist at all -->
                            <xsl:if test="private/json:true">
                            	<xsl:message terminate="yes">PRIVATE tracker item #<xsl:value-of select="$id"/></xsl:message>
                            </xsl:if>
                        </tr>
                    </table><!-- }}} -->

                    <!-- Attachments are now part of comments  -->

                    <!-- {{{ comments -->
                    <h2>Comments</h2>
                    <table id="comments" class="timestamped">
                    <xsl:for-each select="discussion_thread/json:obj/posts/json:arr/json:obj">
                    <xsl:sort select="timestamp"/>
                    <tr><th><xsl:value-of select="timestamp"/><br/><xsl:value-of select="author"/></th>
                        <td>
                        	<p><xsl:for-each select="text"><xsl:call-template name="format-text"/></xsl:for-each></p>
                        	<xsl:for-each select="attachments/json:arr/json:obj">
                        	<p><a href="{url}"><xsl:value-of select="tokenize(url,'/')[position() = last()]"></xsl:value-of></a> (<xsl:value-of select="local:fileSize(bytes)"/>)</p>
                        	</xsl:for-each>
                        </td>
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
        <xsl:analyze-string select="replace(.,'[&#x007f;-&#x009f;]','?')" regex="\n">
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

	<link rel="stylesheet" type="text/css" href="{$path-to-root}jquery.dataTables.css"/>
	<link rel="stylesheet" type="text/css" href="{$path-to-root}style.css"/>
	<script src="{$path-to-root}jquery-latest.js"/>
	<script  src="{$path-to-root}jquery.dataTables.js"/>
	<script  src="{$path-to-root}jquery.dataTables.htmlnum.js"/>

    </xsl:template><!-- }}} -->

    <!-- {{{ foreword template
      warning that it's not live
      -->
    <xsl:template name="foreword">
		<p class="descr">This list reflects the state from <xsl:value-of select="ancestor-or-self::trackers/../export_details/time"/>.<br/>
		It is not the live jEdit trackers. For the live trackers, go to <a href="http://sourceforge.net/p/jedit/_list/tickets">the jEdit project on Sourceforge</a>.
		<br/>
		It doesn't contain every tickets, but only those in the state <xsl:value-of select="string-join($status_to_include,', ')"/>.
		</p>
    </xsl:template>
    <!-- }}} -->
    <!-- }}} -->
</xsl:stylesheet>
