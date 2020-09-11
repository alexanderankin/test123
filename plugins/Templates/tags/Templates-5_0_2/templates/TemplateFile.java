/*
 *  TemplateFile.java - Represents a file within the templates
 *  directory hierarchy.
 *  Copyright (C) 1999, 2008 Steve Jakob
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package templates;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.tree.TreeNode;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
/**
 * A TemplateFile is similar to a java.io.File object as it acts as a reference
 * to a template file, but it also contains information describing the template
 * (eg. a label to be used on the Templates menu).
 *
 * @author   Steve Jakob
 */
public class TemplateFile implements TreeNode, Comparable
{
	private final static String labelRE =
			"(\\s*##\\s*)(TEMPLATE)(\\s*=\\s*)(\\S+.*)";
	protected String label;
	protected File templateFile;
	// private static RE ctpragmaLabelFilter = null;
	private static Pattern templateLabelPattern;
	private TemplateDir parent;

	//Constructors
	public TemplateFile(TemplateDir parent, File templateFile)
	{
		super();
		this.parent = parent;
		this.templateFile = templateFile;
		this.label = templateFile.getName();
		createREs();
		if (!this.isDirectory())
		{
			String s = null;
			try
			{
				s = readTemplateLabel(this.getBufferedReader());
			}
			catch (Exception e)
			{
				Log.log(Log.ERROR, this, jEdit.getProperty(
						"plugin.TemplatesPlugin.error.template-label")
						 + templateFile.getName());
				Log.log(Log.ERROR, this, e);
			}
			if (s != null)
			{
				label = s;
			}
		}
	}

	//Accessors & Mutators
	public String getLabel()
	{
		return label;
	}

	public void setLabel(String labelVal)
	{
		label = labelVal;
	}
	
	/**
	 * Returns the "fully qualified" label, that is, a hierarchical label
	 * which includes the label of this <code>TemplateFile</code>'s parent.
	 */
	public String getFQLabel()
	{
		if (parent == null)
			return "";
		if ("".equals(parent.getFQLabel()))
			return this.getLabel();
		return parent.getFQLabel() + "/" + this.getLabel();
	}

	public String getPath()
	{
		return templateFile.getPath();
	}

	/**
	 * Determine the relative path of the file from the templates directory, given
	 * the file's absolute path.
	 *
	 * @return   The relativePath value
	 */
	public String getRelativePath()
	{
		String absolutePath = templateFile.getPath();
		if (absolutePath.startsWith(TemplatesPlugin.getTemplateDir()))
		{
			return absolutePath.substring(TemplatesPlugin.getTemplateDir().length());
		}
		return absolutePath;
	}

	//Implementors
	public boolean isDirectory()
	{
		return false;
	}


	/**
	 * Convenience method to create a BufferedReader to the template file.
	 *
	 * @return                           A BufferedReader object corresponding to
	 *      the underlying file.
	 * @exception FileNotFoundException  Thrown if the template file is not found
	 */
	public BufferedReader getBufferedReader() throws FileNotFoundException
	{
		return new BufferedReader(new FileReader(this.templateFile));
	}

	private static String readTemplateLabel(BufferedReader in) throws IOException
	{
		String templateLabel = null;
		try
		{
			String line;
			if ((line = in.readLine()) != null)
			{
				Matcher m = templateLabelPattern.matcher(line);
				// REMatch labelMatch = templateLabelMatcher.getMatch(line);
				if (m.matches())
				{
					// templateLabel = labelMatch.toString(4);
					templateLabel = m.group(4);
				}
			}
		}
		catch (IOException e)
		{
			throw e;
		}// In case of problems, throw the exception to the caller
		finally
		{// but close the file, also.
			try
			{
				in.close();
			}
			catch (IOException ioe)
			{}
		}
		return templateLabel;
	}

	/**
	 * Creates a RE to parse template labels. Each directive is composed of 4
	 * parts:<P>
	 *
	 *
	 * <LI> Velcocity comments token ("##")
	 * <LI> the directive type ("TEMPLATE")
	 * <LI> an equals ("=") sign
	 * <LI> the value to assign for the template label
	 */
	private void createREs()
	{
		try
		{
			templateLabelPattern = Pattern.compile(labelRE, Pattern.CASE_INSENSITIVE);
			// ctpragmaLabelFilter = new RE(labelRE, RE.REG_ICASE);
		}
		catch (PatternSyntaxException pe)
		{
			// This won't happen if the programmer did his job right.
			Log.log(Log.ERROR, TemplateFile.this, "PatternSyntaxException in label RE: " +
				labelRE);
		}
		catch (IllegalArgumentException iae)
		{
			// This won't happen if the programmer did his job right.
			Log.log(Log.ERROR, TemplateFile.this, "IllegalArgumentException in label RE: " +
				labelRE);
		}
	}

	public int compareTo(Object o)
	{
		TemplateFile t2 = (TemplateFile) o;
		return this.toString().compareToIgnoreCase(t2.toString());
	}

	public String toString()
	{
		return label;
	}

	//
	// The next seven methods satisfy the TreeNode interface requirements.
	//
	public Enumeration children()
	{
		return null;
	}

	public boolean getAllowsChildren()
	{
		return false;
	}

	public TreeNode getChildAt(int index)
	{
		return null;
	}

	public int getChildCount()
	{
		return 0;
	}

	public int getIndex(TreeNode child)
	{
		return -1;
	}

	public TreeNode getParent()
	{
		return parent;
	}

	public boolean isLeaf()
	{
		return true;
	}

}

