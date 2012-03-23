/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package superabbrevs.zencoding.html;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

import junit.framework.TestCase;
import org.gjt.sp.util.IOUtilities;
import superabbrevs.zencoding.ZenParser;

/**
 * @author Matthieu Casanova
 */
public class HTMLZenParserTester extends TestCase
{
	private ZenSerializer serializer;
	public void testXML()
	{
		Properties props = getProps();
		serializer = new MLSerializer("xml", props);
		doTest("#name.b", "<div id=\"name\" class=\"b\">$1</div>");
		doTest(".a#b", "<div id=\"b\" class=\"a\">$1</div>");
		doTest("div#name", "<div id=\"name\">$1</div>");
		doTest("div.name", "<div class=\"name\">$1</div>");
		doTest("div.one.two", "<div class=\"one two\">$1</div>");
		doTest("div#name.one.two", "<div id=\"name\" class=\"one two\">$1</div>");
		doTest("head>link", "<head>\n" + "\t<link rel=\"stylesheet\" href=\"$1\">$2</link>\n" + "</head>");
		doTest("table>tr>td", "<table>\n" +
				      "\t<tr>\n" +
				      "\t\t<td>$1</td>\n" +
				      "\t</tr>\n" + "</table>");
		doTest("ul#name>li.item", "<ul id=\"name\">\n" +
					  "\t<li class=\"item\">$1</li>\n" +
					  "</ul>");
		doTest("p.a+p", "<p class=\"a\">$1</p>\n" + "<p>$2</p>");
		doTest("p[title]", "<p title=\"$1\">$2</p>");
		doTest("td[colspan=2]", "<td colspan=\"2\">$1</td>");
		doTest("span[title=\"Hello\" rel]", "<span title=\"Hello\" rel=\"$1\">$2</span>");
		doTest("p*3", "<p>$1</p>\n" +
			      "<p>$2</p>\n" +
			      "<p>$3</p>");
		doTest("ul#name>li.item*3", "<ul id=\"name\">\n" +
					    "\t<li class=\"item\">$1</li>\n" +
					    "\t<li class=\"item\">$2</li>\n" +
					    "\t<li class=\"item\">$3</li>\n" +
					    "</ul>");
		doTest("p.name-$$$$*3", "<p class=\"name-0001\">$1</p>\n" +
					"<p class=\"name-0002\">$2</p>\n" +
					"<p class=\"name-0003\">$3</p>");
		doTest("div#header.some.classes+ul>li", "<div id=\"header\" class=\"some classes\">$1</div>\n" +
							"<ul>\n" + "\t<li>$2</li>\n" +
							"</ul>");
		doTest("div#name>p.one+p.two", "<div id=\"name\">\n" +
					       "\t<p class=\"one\">$1</p>\n" +
					       "\t<p class=\"two\">$2</p>\n" +
					       "</div>");
		doTest("select>option#item-$*3", "<select id=\"$1\" name=\"$2\">\n"
						 + "\t<option id=\"item-1\" value=\"$3\">$4</option>\n"
						 + "\t<option id=\"item-2\" value=\"$5\">$6</option>\n"
						 + "\t<option id=\"item-3\" value=\"$7\">$8</option>\n" + "</select>");
		doTest("div#page>div.logo+ul#navigation>li*5>a", "<div id=\"page\">\n"
								 + "\t<div class=\"logo\">$1</div>\n"
								 + "\t<ul id=\"navigation\">\n" + "\t\t<li>\n"
								 + "\t\t\t<a href=\"$2\">$3</a>\n" + "\t\t</li>\n"
								 + "\t\t<li>\n" + "\t\t\t<a href=\"$4\">$5</a>\n"
								 + "\t\t</li>\n" + "\t\t<li>\n"
								 + "\t\t\t<a href=\"$6\">$7</a>\n" + "\t\t</li>\n"
								 + "\t\t<li>\n" + "\t\t\t<a href=\"$8\">$9</a>\n"
								 + "\t\t</li>\n" + "\t\t<li>\n"
								 + "\t\t\t<a href=\"$10\">$11</a>\n" + "\t\t</li>\n"
								 + "\t</ul>\n" + "</div>");
		doTest("p{here}", "<p>here</p>");
		doTest("p>{here}", "<p>here</p>");
		doTest("p>{Click }+a{here}+{ to continue}", "<p>Click <a href=\"$1\">here</a> to continue</p>");
	}

	public void testHTML() throws IOException
	{
		Properties props = getProps();
		serializer = new MLSerializer("html", props);
		doTest("#name.b", "<div id=\"name\" class=\"b\">$1</div>");
		doTest(".a#b", "<div id=\"b\" class=\"a\">$1</div>");
		doTest("div#name", "<div id=\"name\">$1</div>");
		doTest("div.name", "<div class=\"name\">$1</div>");
		doTest("div.one.two", "<div class=\"one two\">$1</div>");
		doTest("div#name.one.two", "<div id=\"name\" class=\"one two\">$1</div>");
		doTest("head>link", "<head>\n" +
				    "\t<link rel=\"stylesheet\" href=\"$1\">$2</link>\n" +
				    "</head>");
		doTest("table>tr>td", "<table>\n" +
				      "\t<tr>\n" +
				      "\t\t<td>$1</td>\n" +
				      "\t</tr>\n" + "</table>");
		doTest("ul#name>li.item", "<ul id=\"name\">\n" +
					  "\t<li class=\"item\">$1</li>\n" +
					  "</ul>");
		doTest("p.a+p", "<p class=\"a\">$1</p>\n" + "<p>$2</p>");
		doTest("p[title]", "<p title=\"$1\">$2</p>");
		doTest("td[colspan=2]", "<td colspan=\"2\">$1</td>");
		doTest("span[title=\"Hello\" rel]", "<span title=\"Hello\" rel=\"$1\">$2</span>");
		doTest("p*3", "<p>$1</p>\n" +
			      "<p>$2</p>\n" +
			      "<p>$3</p>");
		doTest("ul#name>li.item*3", "<ul id=\"name\">\n" +
					    "\t<li class=\"item\">$1</li>\n" +
					    "\t<li class=\"item\">$2</li>\n" +
					    "\t<li class=\"item\">$3</li>\n" +
					    "</ul>");
		doTest("p.name-$$$$*3", "<p class=\"name-0001\">$1</p>\n" +
					"<p class=\"name-0002\">$2</p>\n" +
					"<p class=\"name-0003\">$3</p>");
		doTest("div#header.some.classes+ul>li", "<div id=\"header\" class=\"some classes\">$1</div>\n" +
							"<ul>\n" + "\t<li>$2</li>\n" +
							"</ul>");
		doTest("div#name>p.one+p.two", "<div id=\"name\">\n" +
					       "\t<p class=\"one\">$1</p>\n" +
					       "\t<p class=\"two\">$2</p>\n" +
					       "</div>");
		doTest("select>option#item-$*3", "<select id=\"$1\" name=\"$2\">\n" +
						 "\t<option id=\"item-1\" value=\"$3\">$4</option>\n"
						 + "\t<option id=\"item-2\" value=\"$5\">$6</option>\n"
						 + "\t<option id=\"item-3\" value=\"$7\">$8</option>\n" +
						 "</select>");
		doTest("div#page>div.logo+ul#navigation>li*5>a", "<div id=\"page\">\n" +
								 "\t<div class=\"logo\">$1</div>\n" +
								 "\t<ul id=\"navigation\">\n" +
								 "\t\t<li>\n" +
								 "\t\t\t<a href=\"$2\">$3</a>\n" +
								 "\t\t</li>\n" +
								 "\t\t<li>\n" +
								 "\t\t\t<a href=\"$4\">$5</a>\n" +
								 "\t\t</li>\n" +
								 "\t\t<li>\n" +
								 "\t\t\t<a href=\"$6\">$7</a>\n" +
								 "\t\t</li>\n" +
								 "\t\t<li>\n" +
								 "\t\t\t<a href=\"$8\">$9</a>\n" +
								 "\t\t</li>\n" +
								 "\t\t<li>\n" +
								 "\t\t\t<a href=\"$10\">$11</a>\n" +
								 "\t\t</li>\n" +
								 "\t</ul>\n" +
								 "</div>");
		doTest("p{here}", "<p>here</p>");
		doTest("p>{here}", "<p>here</p>");
		doTest("p>{Click }+a{here}+{ to continue}", "<p>Click <a href=\"$1\">here</a> to continue</p>");
		doTest("img", "<img alt=\"$1\" src=\"$2\">$3</img>");
	}

	private static Properties getProps()
	{
		Properties props = new Properties();
		Reader reader = null;
		try
		{
			reader = new FileReader("SuperAbbrevs.props");
			props.load(reader);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			IOUtilities.closeQuietly(reader);
		}
		return props;
	}

	private void doTest(String tested, String value)
	{
		ZenParser parser = new HTMLZenParser(new StringReader(tested));
		try
		{
			assertEquals(value, parser.parse(serializer));
		}
		catch (ParseException e)
		{
			fail(e.getMessage());
		}
	}
}
