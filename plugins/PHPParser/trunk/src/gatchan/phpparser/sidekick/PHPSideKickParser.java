/*
 * PHPSideKickParser.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2011 Matthieu Casanova
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
package gatchan.phpparser.sidekick;

//{{{ Imports
import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import gatchan.phpparser.PHPErrorSource;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.ParseException;
import gatchan.phpparser.parser.ParsingAbortedError;
import gatchan.phpparser.project.PHPProjectChangedMessage;
import gatchan.phpparser.project.Project;
import gatchan.phpparser.project.ProjectManager;
import gatchan.phpparser.project.itemfinder.PHPItem;
import net.sourceforge.phpdt.internal.compiler.ast.*;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;
import sidekick.IAsset;
import sidekick.SideKickCompletion;
import sidekick.SideKickParsedData;
import sidekick.SideKickParser;

import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
//}}}

/**
 * My sidekick implementation of the sidekick parser.
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public class PHPSideKickParser extends SideKickParser
{
	/**
	 * the php parser.
	 */
	private PHPParser parser;

	/**
	 * the project manager.
	 */
	private final ProjectManager projectManager;

	/**
	 * the error source.
	 */
	private final PHPErrorSource phpErrorSource = new PHPErrorSource();
	public static final String PHPDOCUMENT_PROPERTY = "PHPDocument";

	//{{{ PHPSideKickParser constructor
	/**
	 * Instantiate the PHPSideKickParser.
	 *
	 * @param name the name of the parser
	 */
	public PHPSideKickParser(String name)
	{
		super(name);
		projectManager = ProjectManager.getInstance();
	} //}}}

	//{{{ parse() methods
	/**
	 * Parses the given text and returns a tree model. This method is called by the sidekick plugin
	 *
	 * @param buffer      The buffer to parse.
	 * @param errorSource An error source to add errors to.
	 * @return A new instance of the <code>SideKickParsedData</code> class.
	 */
	@Override
	public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource)
	{
		phpErrorSource.setErrorSource(errorSource);
		String path = buffer.getPath();
		Project project = projectManager.getProject();
		if (project != null) project.clearSourceFile(path);
		if (parser != null && !parser.isStopped())
		{
			Log.log(Log.ERROR, PHPSideKickParser.class, "The parser had not been stopped before asking a new parse !");
			stop();
		}
		parser = new PHPParser();
		parser.setPath(path);
		try
		{
			errorSource.removeFileErrors(path);
			parser.addParserListener(phpErrorSource);
			try
			{
				parser.parse(buffer.getText(0, buffer.getLength()));
			}
			catch (ParsingAbortedError parsingAbortedError)
			{
				Log.log(Log.MESSAGE, PHPSideKickParser.class, "The parser was aborted");
				return null;
			}
			PHPDocument phpDocument = parser.getPHPDocument();
			updateProject(phpDocument, path);
			buffer.setProperty(PHPDOCUMENT_PROPERTY, phpDocument);
			parser = null;
			SideKickParsedData data = new SideKickParsedData(buffer.getName());

			buildChildNodes(data.root, phpDocument, buffer);
			EditBus.sendAsync(new PHPProjectChangedMessage(this, projectManager.getProject(), PHPProjectChangedMessage.UPDATED));
			return data;
		}
		catch (ParseException e)
		{
			parser = null;
			Log.log(Log.ERROR, this, e);
			errorSource.addError(ErrorSource.ERROR,
					     path,
					     e.currentToken.beginLine - 1,
					     e.currentToken.beginColumn,
					     e.currentToken.endColumn,
					     "Unhandled error please report the bug (with the trace in the activity log");
		}
		catch (Throwable t)
		{
			parser = null;
			Log.log(Log.ERROR, this, t);
		}
		return null;
	}

	public void parse(String path, Reader reader)
	{
		PHPParser parser = new PHPParser();
		parser.setPath(path);
		try
		{
			parser.parse(reader);
		}
		catch (ParsingAbortedError parsingAbortedError)
		{
			Log.log(Log.MESSAGE, PHPSideKickParser.class, "The parser was aborted");
		}
		catch (ParseException e)
		{
			Log.log(Log.MESSAGE, PHPSideKickParser.class, e.getMessage());
		}
		PHPDocument phpDocument = parser.getPHPDocument();
		updateProject(phpDocument, path);
	} //}}}

	//{{{ stop() method
	@Override
	public void stop()
	{
		if (parser != null)
		{
			Log.log(Log.MESSAGE, PHPSideKickParser.class, "Stopping parser");
			parser.stop();
		}
	} //}}}

	//{{{ buildChildNodes() method
	private void buildChildNodes(DefaultMutableTreeNode parent,
				     Outlineable outlineable,
				     Buffer buffer)
	{
		for (int i = 0; i < outlineable.size(); i++)
		{
			Outlineable o = outlineable.get(i);
			buildNode(parent, o, buffer);
		}
	} //}}}

	//{{{ buildNode() method
	private void buildNode(DefaultMutableTreeNode parent, Outlineable sourceNode, Buffer buffer)
	{
		AstNode astNode = (AstNode) sourceNode;
		Position startPosition = buffer.createPosition(buffer.getLineStartOffset(astNode.getBeginLine() - 1) + astNode.getBeginColumn());
		System.out.println("ASTNODE="+astNode+ ' ' +astNode.getEndLine());
		Position endPosition = buffer.createPosition(buffer.getLineStartOffset(astNode.getEndLine() - 1) + astNode.getEndColumn());
		IAsset asset = (IAsset) astNode;
		asset.setStart(startPosition);
		asset.setEnd(endPosition);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(sourceNode, true);
		buildChildNodes(node, sourceNode, buffer);
		parent.add(node);
	} //}}}

	//{{{ supportsCompletion() method
	@Override
	public boolean supportsCompletion()
	{
		return true;
	} //}}}

	//{{{ complete() method
	@Override
	public SideKickCompletion complete(EditPane editPane, int caret)
	{
		Buffer buffer = editPane.getBuffer();
		PHPDocument phpDocument = (PHPDocument) buffer.getProperty(PHPDOCUMENT_PROPERTY);
		return super.complete(editPane, caret);
	} //}}}


	//{{{ complete() method
	public SideKickCompletion complete0(EditPane editPane, int caret)
	{
		Log.log(Log.DEBUG, this, "Requesting sidekick complete");
		Buffer buffer = editPane.getBuffer();
		PHPDocument phpDocument = (PHPDocument) buffer.getProperty(PHPDOCUMENT_PROPERTY);
		if (phpDocument == null)
		{
			Log.log(Log.DEBUG, this, "No php document for this buffer");
			return null;
		}
		JEditTextArea textArea = editPane.getTextArea();
		int caretLine = textArea.getCaretLine();
		int caretInLine = caret - buffer.getLineStartOffset(caretLine);
		if (caretInLine == 0) return null;

		String line = buffer.getLineText(caretLine);
		int wordStart = TextUtilities.findWordStart(line, caretInLine - 1, "");
		String currentWord = line.substring(wordStart, caretInLine);

		Project project = projectManager.getProject();

		//Static class access
		PHPSideKickCompletion phpSideKickCompletion;

		if (project != null)
		{
			phpSideKickCompletion = completeStaticClassAccess(currentWord, line, wordStart, project);
			if (phpSideKickCompletion != null) return phpSideKickCompletion;
		}

		ClassDeclaration currentClass = classDeclarationAtOffset(editPane.getView(), caret);


		String lastWord2 = getPreviousWord(caret, buffer);

		AstNode statementAt = phpDocument.getNodeAt(caretLine + 1, caretInLine);
		if (statementAt != null)
		{
			Log.log(Log.DEBUG, this, "Statement at caret " + statementAt);
			AstNode expression = statementAt.subNodeAt(caretLine + 1, caretInLine);
			if (expression != null)
			{
				Log.log(Log.DEBUG, this, "Expression at caret " + expression);
			}
		}
		MethodDeclaration currentMethod;
		if (currentClass == null)
		{
			//We are not inside a class
			currentMethod = phpDocument.methodAtOffset(caretLine, caretInLine);
		}
		else
		{
			//We are inside a class
			currentMethod = currentClass.insideWichMethodIsThisOffset(caretLine, caretInLine);


			if (currentMethod == null)
			{
				//We are inside a class but not inside a method
				if (lastWord2.endsWith(";") || lastWord2.endsWith("{") || lastWord2.endsWith("}"))
				{
					phpSideKickCompletion = new PHPSideKickCompletion(currentWord, lastWord2);
					phpSideKickCompletion.addItem("var", currentWord);
					phpSideKickCompletion.addItem("function", currentWord);
					return phpSideKickCompletion;
				}
			}
		}

		String lastWord = null;
		if (wordStart != 0)
		{
			int previousWordStart = TextUtilities.findWordStart(line, wordStart - 1, "$_->");
			lastWord = line.substring(previousWordStart, wordStart);
		}

		if ("::".equals(currentWord) || "::".equals(lastWord))
		{
			String className = lastWord2.substring(0, lastWord2.indexOf("::"));
			ClassHeader classHeader;
			if ("self".equals(className))
			{
				if (currentClass == null)
					return null;
				classHeader = currentClass.getClassHeader();
			}
			else
				classHeader = getClassHeader(className, phpDocument);

			// if the current word is :: there is no starting word. If it isn't it means that there is a starting word and
			// the previous word is ::
			String completed = "::".equals(currentWord) ? "" : currentWord;

			phpSideKickCompletion = completeClassMembers(classHeader, completed, lastWord + "::");
			if (phpSideKickCompletion != null) return phpSideKickCompletion;
		}

		//We are inside a method of a class we can get the assigned variables in code
		if (lastWord != null &&
		    lastWord.charAt(0) == '$' &&
		    ("->".equals(currentWord) || lastWord.endsWith("->")) &&
		    currentMethod != null)
		{
			int classAccessIndex = lastWord.indexOf("->");
			String variableName;
			if (classAccessIndex == -1)
			{
				variableName = lastWord.substring(1);
			}
			else
			{
				variableName = lastWord.substring(1, classAccessIndex);
			}
			VariableUsage variableUsage = currentMethod.getAssignedVariableInCode(variableName,
											      caretLine,
											      caretInLine);
			String className = variableUsage == null ? null : variableUsage.getType().getClassName();
			if (className != null)
			{
				ClassHeader classHeader = getClassHeader(className, phpDocument);

				if (classHeader != null)
				{
					String completed = "->".equals(currentWord) ? "" : currentWord;
					phpSideKickCompletion = completeClassMembers(classHeader, completed, lastWord);
					/*phpSideKickCompletion = new PHPSideKickCompletion(currentWord, lastWord);
						  List methodsHeaders = classHeader.getMethodsHeaders();
						  for (int j = 0; j < methodsHeaders.size(); j++) {
						    phpSideKickCompletion.addItem(methodsHeaders.get(j), "");
						  }
						  List fields = classHeader.getFields();
						  for (int j = 0; j < fields.size(); j++) {
						    phpSideKickCompletion.addItem(fields.get(j), "");
						  } */
					return phpSideKickCompletion;
				}
			}
		}
		else if ("$this->".equals(lastWord) || ("->".equals(currentWord) && "$this".equals(lastWord)))
		{
			Log.log(Log.DEBUG, this, "Completing $this->");
			if ("->".equals(currentWord))
			{
				phpSideKickCompletion = completeClassMembers(currentClass.getClassHeader(), "", "$this->");
			}
			else
			{
				phpSideKickCompletion = completeClassMembers(
					currentClass.getClassHeader(),
					currentWord,
					"$this->");
			}
			return phpSideKickCompletion;
		}

		// Log.log(Log.DEBUG, PHPSideKickParser.class, "Word found : '"+lastWord2+"'");
		if ("new".equals(lastWord2) || "extends".equals(lastWord2))
		{
			Log.log(Log.DEBUG, this, "Completing class name");
			return buildClassNameList(currentWord.trim(), lastWord2, phpDocument);
		}

		return null;
	} //}}}

	//{{{ classDeclarationAtOffset() method
	private static ClassDeclaration classDeclarationAtOffset(View view, int caret)
	{
		SideKickParsedData data = SideKickParsedData.getParsedData(view);
		if (data == null)
		{
			return null;
		}
		int pos = caret;
		IAsset oldAsset = null;
		ClassDeclaration classDeclaration;
		while (true)
		{
			IAsset asset = data.getAssetAtOffset(pos);

			if (asset == null)
			{
				return null;
			}
			while (oldAsset == asset)
			{
				asset = data.getAssetAtOffset(pos--);
				if (asset == null)
					return null;
			}
			oldAsset = asset;
			pos = asset.getStart().getOffset() - 1;
			if (asset instanceof ClassDeclaration)
			{
				classDeclaration = (ClassDeclaration) asset;
				break;
			}
		}
		return classDeclaration;
	} //}}}

	//{{{ getClassHeader() method
	private ClassHeader getClassHeader(String className, Outlineable phpDocument)
	{
		Project project = projectManager.getProject();
		ClassHeader classHeader = null;
		if (project != null)
		{
			classHeader = project.getClass(className);
		}
		if (classHeader == null)
		{
			for (int j = 0; j < phpDocument.size(); j++)
			{
				Outlineable outlineable = phpDocument.get(j);
				if (outlineable instanceof ClassDeclaration && outlineable.getName().equals(className))
				{
					classHeader = ((ClassDeclaration) outlineable).getClassHeader();
				}
			}
		}
		return classHeader;
	} //}}}

	//{{{ completeStaticClassAccess() method
	private static PHPSideKickCompletion completeStaticClassAccess(String currentWord,
								       String line,
								       int wordStart,
								       Project project)
	{
		PHPSideKickCompletion phpSideKickCompletion = null;
		if ("::".equals(currentWord))
		{
			int previousWordStart = TextUtilities.findWordStart(line, wordStart - 1, "_");
			String classAccessed = line.substring(previousWordStart, wordStart);
			ClassHeader classHeader = project.getClass(classAccessed);
			phpSideKickCompletion = completeClassMembers(classHeader, "", classAccessed + "::");
		}
		else if (wordStart > 2 && (line.charAt(wordStart - 1) == ':' || line.charAt(wordStart - 2) == ':'))
		{
			int previousWordStart = TextUtilities.findWordStart(line, wordStart - 3, "_");
			String classAccessed = line.substring(previousWordStart, wordStart - 2);
			ClassHeader classHeader = project.getClass(classAccessed);
			phpSideKickCompletion = completeClassMembers(classHeader, currentWord, classAccessed + "::");
		}
		return phpSideKickCompletion;
	} //}}}

	//{{{ getPreviousWord() method
	/**
	 * Returns the previous word in a buffer.
	 *
	 * @param caret  the caret position
	 * @param buffer the buffer
	 * @return the previous word or ""
	 */
	private static String getPreviousWord(int caret, JEditBuffer buffer)
	{
		int i;
		for (i = caret - 1; i > 0; i--)
		{
			char c = buffer.getText(i, 1).charAt(0);
			if (!Character.isWhitespace(c)) break;
		}
		int j;
		for (j = i - 1; j > 0; j--)
		{
			char c = buffer.getText(j, 1).charAt(0);
			if (Character.isWhitespace(c)) break;
		}
		return buffer.getText(j + 1, i - j);
	} //}}}

	//{{{ buildClassNameList() method
	/**
	 * Build the completion list to follow a 'new'. It will contains classes name
	 *
	 * @param startName
	 * @param lastWord
	 * @param phpDocument
	 * @return a completion list
	 */
	private SideKickCompletion buildClassNameList(String startName,
						      String lastWord,
						      Outlineable phpDocument)
	{
		PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(startName, lastWord);
		Project project = projectManager.getProject();
		if (project != null)
		{
			Map<String, Object> classes = project.getClasses();
			Collection<Object> collection = classes.values();
			for (Object o : collection)
			{
				if (o instanceof PHPItem)
				{
					phpSideKickCompletion.addItem(o, startName);
				}
				else
				{
					Iterable<PHPItem> item = (Iterable<PHPItem>) o;
					for (PHPItem phpItem : item)
					{
						phpSideKickCompletion.addItem(phpItem, startName);
					}
				}
			}
		}

		for (int i = 0; i < phpDocument.size(); i++)
		{
			Outlineable outlineable = phpDocument.get(i);
			if (outlineable instanceof ClassDeclaration)
			{
				phpSideKickCompletion.addItem(outlineable, startName);
			}
		}
		Log.log(Log.DEBUG, this, "Items in list : " +
			phpSideKickCompletion.getItemsCount());
		return phpSideKickCompletion;
	} //}}}

	//{{{ updateProject() method
	private void updateProject(Outlineable phpDocument, String path)
	{
		Project project = projectManager.getProject();
		if (project != null)
		{
			if (project.acceptFile(path))
			{
				for (int i = 0; i < phpDocument.size(); i++)
				{
					Outlineable o = phpDocument.get(i);
					if (o.getItemType() == PHPItem.CLASS)
					{
						ClassDeclaration classDeclaration = (ClassDeclaration) o;
						project.addClass(classDeclaration.getClassHeader());
					}
					else if (o.getItemType() == PHPItem.METHOD)
					{
						MethodDeclaration methodDeclaration = (MethodDeclaration) o;
						project.addMethod(methodDeclaration.getMethodHeader());
					}
					else if (o.getItemType() == PHPItem.INTERFACE)
					{
						project.addInterface((InterfaceDeclaration) o);
					}
				}
			}
		}
	} //}}}

	//{{{ completeClassMembers() methods
	/**
	 * Build the completion list to follow a '$this->'. It will contains fields and methods
	 *
	 * @param classHeader   classHeader
	 * @param completedWord the current word
	 * @param lastWord      the previous word
	 * @return a completion list or null if we aren't in a class
	 */
	private static PHPSideKickCompletion completeClassMembers(ClassHeader classHeader,
								  String completedWord,
								  String lastWord)
	{
		if (classHeader == null) return null;
		PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(completedWord, lastWord);
		completeClassMembers(classHeader, phpSideKickCompletion, completedWord);
		Log.log(Log.DEBUG, PHPSideKickParser.class, "Items in list : " + phpSideKickCompletion.getItemsCount());
		return phpSideKickCompletion;
	}

	private static void completeClassMembers(ClassHeader classHeader,
						 PHPSideKickCompletion phpSideKickCompletion,
						 String currentWord)
	{
		List<MethodHeader> methods = classHeader.getMethodsHeaders();
		List<FieldDeclaration> fields = classHeader.getFields();
		phpSideKickCompletion.addOutlineableList(methods, currentWord);
		phpSideKickCompletion.addOutlineableList(fields, currentWord);
		String superClassName = classHeader.getSuperClassName();
		Project project = ProjectManager.getInstance().getProject();
		if (project != null)
		{
			if (superClassName != null)
			{
				ClassHeader superClassHeader = project.getClass(superClassName);
				if (superClassHeader == null)
				{
					Log.log(Log.DEBUG, PHPSideKickParser.class, "Unknown superclass " + superClassHeader);
				}
				else
				{
					completeClassMembers(superClassHeader, phpSideKickCompletion, currentWord);
				}
			}
			List<String> interfaceNames = classHeader.getInterfaceNames();
			if (interfaceNames != null)
			{
				for (String interfaceName : interfaceNames)
				{
					InterfaceDeclaration anInterface = project.getInterface(interfaceName);
					if (anInterface == null)
					{
						Log.log(Log.DEBUG, PHPSideKickParser.class, "Unknown interface " + anInterface);
					}
					else
					{
						completeInterfaceMembers(anInterface, phpSideKickCompletion, currentWord);
					}
				}
			}
		}
	} //}}}

	//{{{ completeInterfaceMembers() method
	private static void completeInterfaceMembers(InterfaceDeclaration interfaceDeclaration,
						     PHPSideKickCompletion phpSideKickCompletion,
						     String currentWord)
	{
		List<MethodHeader> methodsHeaders = interfaceDeclaration.getMethodsHeaders();
		phpSideKickCompletion.addOutlineableList(methodsHeaders, currentWord);

		Project project = ProjectManager.getInstance().getProject();
		if (project != null)
		{
			List<String> superInterfaces = interfaceDeclaration.getSuperInterfaces();
			if (superInterfaces != null)
			{
				for (String superInterface : superInterfaces)
				{
					InterfaceDeclaration anInterface = project.getInterface(superInterface);
					if (anInterface == null)
					{
						Log.log(Log.DEBUG, PHPSideKickParser.class, "Unknown interface " + anInterface);
					}
					else
					{
						completeInterfaceMembers(anInterface, phpSideKickCompletion, currentWord);
					}
				}
			}
		}
	} //}}}
}
