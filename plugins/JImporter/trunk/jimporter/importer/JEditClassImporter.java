/*
 *  JEditClassImporter.java - Plugin for add java imports to the top of a java file.
 *  Copyright (C) 2002 Matthew Flower (MattFlower@yahoo.com)
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
package jimporter.importer;

import gnu.regexp.*;
import javax.swing.JOptionPane;
import jimporter.options.AutoSearchAtPointOption;
import jimporter.options.AutoImportOnOneMatchOption;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;

/**
 * This class is the interface between jEdit and the importing utilities. It is
 * the only class that should know about the jEdit classes.
 *
 * @author  Matthew Flower
 */
public class JEditClassImporter
{
	/**
	 * Delegates to #importClassAtPoint with an empty #Word
	 *
	 * @param view The current jEdit view
	 * @see #importClassAtPoint
	 */
	public static void importClass(View view)
	{
		Operation importOperation = new ImportOperation(view, new Word(view.getTextArea().getCaretPosition());

		importOperation.execute();
	}

	/**
	 * This method executes a 2-step procedure: identify the class FQN to import, and insert the import statement
	 * in the buffer if it has not already been imported.  Here are the steps:
	 *
	 * 1) Grab the word at the caret, as delimited according to java.lang.Character.isJavaIdentifierStart()
	 * and java.lang.Character.isJavaIdentifierPart().  If this word is an identifier, try to find a
	 * matching class in the classpath.  The user may have set the option to automatically import when
	 * only one match is found; this method executes according to that option.  When auto-import is
	 * not enabled or not possible, popup a dialog asking the user to specify the class fQN.
	 *
	 * 2) Look for a match in the current import list.  If there is one, do nothing; otherwise append the import
	 * list with an import stateent of the class FQN.  In either case, check the user's auto-sort option and sort
	 * the imports if it is enabled.
	 *
	 * @param view The current jEdit view
	 */
	public static void importClassAtPoint(View view)
	{
		Operation importOperation = new ImportOperation(view);

		importOperation.execute();
	}

	/**
	 * Identical to #importClassAtPoint, but uses <code>classToSearchFor</code> instead of the word at the caret.
	 *
	 * @param view The current jEdit view
	 * @param classToSearchFor The unqualified classname of the class to import
	 * @see #importClassAtPoint
	 */
	public static void importClass(View view, String classToSearchFor)
	{
		Operation importOperation = new ImportOperation(view, new Word(classToSearchFor));

		importOperation.execute();
	}

	/**
	 * Insert the fully qualified name of a class that the user selects into the
	 * current buffer at the current caret position.  When auto-import is enabled
	 * and possible, auto-import based on the word at the caret.
	 *
	 * @param currentView A <code>View</code> value used to determine what we
	 * are going to import and where we are going to import it into.
	 * @see #importClassAtPoint
	 */
	public static void insertClassAtPoint(View view)
	{
		Operation qualifyOperation = new QualifyOperation(view);

		qualifyOperation.execute();
	}

	protected static abstract class Operation
	{
		protected View view;
		protected Buffer buffer;
		protected JEditTextArea textArea;
		protected Word word;

		protected String fqImportClassname;

		public Operation(View view)
		{
			this.view = view;
			this.buffer = this.view.getBuffer();
			this.textArea = this.view.getTextArea();
			this.word = this.getWordAtPoint();
		}

		public Operation(View view, Word word)
		{
			this.view = view;
			this.buffer = this.view.getBuffer();
			this.textArea = this.view.getTextArea();
			this.word = word;
		}

		protected abstract boolean executeOperation();

		protected boolean execute()
		{
			this.getImportClass();

			boolean result = this.executeOperation();

			this.textArea.requestFocus();

			return result;
		}

		protected void getImportClass()
		{
			JavaImportClassForm importClassForm = new JavaImportClassForm(this.view, this.word.text);
			importClassForm.setLocationRelativeTo(this.view);

			if ((new AutoSearchAtPointOption().state()) && !this.word.isEmpty()) {
				importClassForm.generateImportModel(this.word.text);
			}

			if (! ((new AutoImportOnOneMatchOption().state()) && (importClassForm.getMatchCount() == 1))) {
				importClassForm.show();
			}

			this.fqImportClassname = importClassForm.getImportedClass();
		}

		protected Word getWordAtPoint()
		{
			int lineNumber = this.textArea.getCaretLine();
			String lineText = this.textArea.getLineText(lineNumber);
			int lineOffset = this.textArea.getLineStartOffset(lineNumber);

			int wordStart = (this.textArea.getCaretPosition() - lineOffset);
			int wordEnd = wordStart;

			while (true)
			{
				if ((wordStart == 0) || !Character.isJavaIdentifierPart(lineText.charAt(wordStart-1)))
				{
					break;
				}

				wordStart--;
			}

			while (true)
			{
				if ((wordEnd == lineText.length()) || !Character.isJavaIdentifierPart(lineText.charAt(wordEnd)))
				{
					break;
				}

				wordEnd++;
			}

			Word currentWord = new Word(lineOffset + wordStart);

			if ((wordStart != wordEnd) && Character.isJavaIdentifierStart(lineText.charAt(wordStart)))
			{
				currentWord.text = lineText.substring(wordStart, wordEnd);
			}

			return currentWord;
		}
	}

	protected static class ImportOperation extends Operation
	{
		public ImportOperation(View view)
		{
			super(view);
		}

		public ImportOperation(View view, Word word)
		{
			super(view, word);
		}

		protected boolean executeOperation()
		{
			String importText = ("import " + super.fqImportClassname + ";\n");

			ClassImporter classImporter = ClassImporterFactory.getInstance(super.buffer.getMode().getName());
			classImporter.setSourceBuffer(super.buffer);
			classImporter.setImportClass(importText);

			return classImporter.addImportToBuffer();
		}
	}

	protected static class QualifyOperation extends Operation
	{
		public QualifyOperation(View view)
		{
			super(view);
		}

		public QualifyOperation(View view, Word word)
		{
			super(view, word);
		}

		protected boolean executeOperation()
		{
			if ((!super.word.isEmpty()) && ((super.word.offset == 0) || (super.textArea.getText().charAt(super.word.offset-1) != '.')))
			{
				super.buffer.remove(super.word.offset, super.word.text.length());
			}

			super.buffer.insert(super.word.offset, super.fqImportClassname);

			return true;
		}
	}

	protected static class Word
	{
		protected String text;
		protected int offset;

		// a word that is not in the current buffer
		public Word(String text)
		{
			this(text, -1);
		}

		public Word(int offset)
		{
			this("", offset);
		}

		public Word(String text, int offset)
		{
			this.text = text;
			this.offset = offset;
		}

		public boolean isEmpty()
		{
			return (this.text.length() == 0);
		}
	}
}
