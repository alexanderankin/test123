package xml.completion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.types.EnumeratedAttribute;

/**
 * Creates a jEdit XML plugin XML-DTD file for Ant from the currently known
 * tasks.
 * 
 * 
 * @ant.task category="xml"
 */
public class AntCompleteTask extends Task
{

	private static final String BOOLEAN = "(true|false|on|off|yes|no)";

	private String TASKS;

	private String TYPES;

	private Hashtable visited = new Hashtable();

	private File output;

	/**
	 * The output file.
	 * 
	 * @param output
	 *                the output file
	 */
	public void setOutput(File output)
	{
		this.output = output;
	}

	/**
	 * Build the antcomplete XML-DTD.
	 * 
	 * @exception BuildException
	 *                    if the XML-DTD cannot be written.
	 */
	public void execute() throws BuildException
	{

		if (output == null)
		{
			throw new BuildException("output attribute is required", getLocation());
		}

		PrintWriter out = null;
		try
		{
			try
			{
				out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
					output), "UTF8"));
			}
			catch (UnsupportedEncodingException ue)
			{
				/*
				 * Plain impossible with UTF8, see
				 * http://java.sun.com/products/jdk/1.2/docs/guide/internat/encoding.doc.html
				 * 
				 * fallback to platform specific anyway.
				 */
				out = new PrintWriter(new FileWriter(output));
			}

			TASKS = getList(getProject().getTaskDefinitions().keys());
			TYPES = getList(getProject().getDataTypeDefinitions().keys());

			out.println("<element-list>");
			out.println("");

			printProjectDecl(out);

			printTargetDecl(out);

			Enumeration types = getProject().getDataTypeDefinitions().keys();
			while (types.hasMoreElements())
			{
				String typeName = (String) types.nextElement();
				printElementDecl(out, typeName, (Class) getProject()
					.getDataTypeDefinitions().get(typeName));
			}

			Enumeration tasks = getProject().getTaskDefinitions().keys();
			while (tasks.hasMoreElements())
			{
				String taskName = (String) tasks.nextElement();
				printElementDecl(out, taskName, (Class) getProject()
					.getTaskDefinitions().get(taskName));
			}

			out.println("");
			out.print("</element-list>");

		}
		catch (IOException ioe)
		{
			throw new BuildException("Error writing " + output.getAbsolutePath(), ioe,
				getLocation());
		}
		finally
		{
			if (out != null)
			{
				out.close();
			}
			visited.clear();
		}
	}

	/**
	 * Returns a | seperated listing of the elements in e
	 * 
	 * @param e
	 *                the Enumeration
	 * @return the listing as a String
	 */
	private String getList(Enumeration e)
	{
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		while (e.hasMoreElements())
		{
			if (!first)
			{
				sb.append('|');
			}
			else
			{
				first = false;
			}
			sb.append(e.nextElement().toString());
		}
		return sb.toString();
	}

	/**
	 * Prints the definition for the target element.
	 * 
	 * @param out
	 *                the PrintWriter to use
	 * @param name
	 *                the value for the name attribute
	 * @param content
	 *                the value for the content attribute, if null then
	 *                EMPTY is printed
	 */
	private void printElementOpen(PrintWriter out, String name, String content)
	{
		if (null == content)
		{
			content = "EMPTY";
		}
		else
		{
			content = '(' + content + ')';
		}
		out.print("<element name=\"");
		out.print(name);
		out.println('\"');
		out.print("content=\"");
		out.print(content);
		out.println("\">");
	}

	/**
	 * Prints the &lt;/element>.
	 * 
	 * @param out
	 *                the PrintWriter to use
	 */
	private void printElementClose(PrintWriter out)
	{
		out.println("</element>");
	}

	/**
	 * Prints the definition for the target element.
	 * 
	 * @param out
	 *                the PrintWriter to use
	 * @param name
	 *                the value for the name attribute
	 * @param type
	 *                the value for the type attribute
	 * @param required
	 *                whether this is a required attribute
	 */
	private void printAttribute(PrintWriter out, String name, String type, boolean required)
	{
		out.print("<attribute name=\"");
		out.print(name);
		out.print("\" type=\"");
		out.print(type);
		out.print("\" ");
		if (required)
			out.print("required=\"true\" ");
		out.println("/>");
	}

	/**
	 * Prints the definition for the target element.
	 * 
	 * @param out
	 *                the PrintWriter to use
	 */
	private void printProjectDecl(PrintWriter out)
	{
		visited.put("project", "");
		printElementOpen(out, "project", "target|" + TYPES + '|' + TASKS);
		printAttribute(out, "basedir", "CDATA", false);
		printAttribute(out, "default", "CDATA", true);
		printAttribute(out, "name", "CDATA", false);
		printElementClose(out);
	}

	/**
	 * Prints the definition for the target element.
	 * 
	 * @param out
	 *                the PrintWriter to use
	 */
	private void printTargetDecl(PrintWriter out)
	{
		visited.put("target", "");
		printElementOpen(out, "target", TYPES + '|' + TASKS);
		printAttribute(out, "id", "ID", false);
		printAttribute(out, "name", "CDATA", true);
		printAttribute(out, "if", "CDATA", false);
		printAttribute(out, "unless", "CDATA", false);
		printAttribute(out, "depends", "CDATA", false);
		printAttribute(out, "description", "CDATA", false);
		printElementClose(out);
	}

	/**
	 * Print the definition for a given element.
	 * 
	 * @param out
	 *                the PrintWriter to use
	 * @param name
	 *                the element name
	 * @param element
	 *                the Class of the element
	 */
	private void printElementDecl(PrintWriter out, String name, Class element)
		throws BuildException
	{

		if (visited.containsKey(name))
		{
			return;
		}
		visited.put(name, "");

		if (org.apache.tools.ant.types.Reference.class.equals(element))
		{
			printElementOpen(out, name, null);
			printAttribute(out, "id", "ID", false);
			printAttribute(out, "refid", "IDREF", false);
			printElementClose(out);
			return;
		}

		IntrospectionHelper ih = null;
		try
		{
			ih = IntrospectionHelper.getHelper(element);
		}
		catch (Throwable t)
		{
			/*
			 * XXX - failed to load the class properly.
			 * 
			 * should we print a warning here?
			 */
			return;
		}

		Vector v = new Vector();
		if (ih.supportsCharacters())
		{
			v.addElement("");
		}

		if (TaskContainer.class.isAssignableFrom(element))
		{
			v.addElement(TASKS);
		}

		Enumeration e = ih.getNestedElements();
		while (e.hasMoreElements())
		{
			v.addElement(e.nextElement());
		}

		StringBuffer sb = null;
		String content = null;
		if (!v.isEmpty())
		{
			sb = new StringBuffer();
			final int count = v.size();
			for (int i = 0; i < count; i++)
			{
				if (i != 0)
				{
					sb.append("|");
				}
				sb.append(v.elementAt(i));
			}
			content = sb.toString();
		}
		printElementOpen(out, name, content);

		printAttribute(out, "id", "ID", false);

		e = ih.getAttributes();
		while (e.hasMoreElements())
		{
			String attrName = (String) e.nextElement();
			if ("id".equals(attrName))
			{
				continue;
			}

			String attrType = "CDATA";
			Class type = ih.getAttributeType(attrName);
			if (type.equals(java.lang.Boolean.class)
				|| type.equals(java.lang.Boolean.TYPE))
			{
				attrType = BOOLEAN;
			}
			else if (Reference.class.isAssignableFrom(type))
			{
				attrType = "IDREF";
			}
			else if (EnumeratedAttribute.class.isAssignableFrom(type))
			{
				try
				{
					EnumeratedAttribute ea = (EnumeratedAttribute) type
						.newInstance();
					String[] values = ea.getValues();
					if (values != null && values.length != 0
						&& areNmtokens(values))
					{
						sb = new StringBuffer("(");
						for (int i = 0; i < values.length; i++)
						{
							if (i != 0)
							{
								sb.append("|");
							}
							sb.append(values[i]);
						}
						sb.append(")");
					}
				}
				catch (InstantiationException ie)
				{
					attrType = "CDATA";
				}
				catch (IllegalAccessException ie)
				{
					attrType = "CDATA";
				}
			}
			printAttribute(out, attrName, attrType, false);
		}

		printElementClose(out);

		final int count = v.size();
		for (int i = 0; i < count; i++)
		{
			String nestedName = (String) v.elementAt(i);
			if (!"".equals(nestedName) && !TASKS.equals(nestedName)
				&& !TYPES.equals(nestedName))
			{
				printElementDecl(out, nestedName, ih.getElementType(nestedName));
			}
		}
	}

	/**
	 * Does this String match the XML-NMTOKEN production?
	 * 
	 * @param s
	 *                the string to test
	 * @return true if the string matches the XML-NMTOKEN
	 */
	protected boolean isNmtoken(String s)
	{
		final int length = s.length();
		for (int i = 0; i < length; i++)
		{
			char c = s.charAt(i);
			// XXX - we are committing CombiningChar and Extender
			// here
			if (!Character.isLetterOrDigit(c) && c != '.' && c != '-' && c != '_'
				&& c != ':')
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Do the Strings all match the XML-NMTOKEN production?
	 * 
	 * <p>
	 * Otherwise they are not suitable as an enumerated attribute, for
	 * example.
	 * </p>
	 * 
	 * @param s
	 *                the array of string to test
	 * @return true if all the strings in the array math XML-NMTOKEN
	 */
	protected boolean areNmtokens(String[] s)
	{
		for (int i = 0; i < s.length; i++)
		{
			if (!isNmtoken(s[i]))
			{
				return false;
			}
		}
		return true;
	}

}
