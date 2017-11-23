/* 
 *            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *                    Version 2, December 2004
 * 
 * Everyone is permitted to copy and distribute verbatim or modified
 * copies of this license document, and changing it is allowed as long
 * as the name is changed.
 * 
 *            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 * 
 *  0. You just DO WHAT THE FUCK YOU WANT TO.
 *  
 */
package sidekick.java;

// Imports
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;

import java.util.*;
import javax.swing.*;

import sidekick.java.classloader.AntClassLoader;
import sidekick.java.node.*;
import sidekick.java.options.*;
import sidekick.java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.buffer.JEditBuffer;

import sidekick.SideKickParsedData;

/**
 * Finds 2 kinds of completions, word completions and method/field completions,
 * also known as "dot" completions.
 * Word completions complete a partial word, for example, typing "String.val"
 * would show a popup containing "valueOf".  Method/field completions are
 * activated when the last character in the word is a dot, for example, typing
 * "String." would list all methods and fields in the String class.
 *
 * TODO: completion inside anonymous inner classes doesn't work.  For example,
 * button.addActionListener(
 *     new ActionListener() {
 *         public void actionPerformed( ActionEvent ae ) {
 *             ae.      // nothing happens here
 *         }
 *     }
 *
 * );                                                              
 *
 * Code completion no longer works inside of comments
 * If a word-break character wasn't found, the completion could potentially mix
 * comments with tokens, which occasionally caused errors
 *
 * Valid tokens: NULL, OPERATOR, FUNCTION, DIGIT
 */
public class JavaCompletionFinder {
	
	private JavaSideKickParsedData data = null;
	private CUNode rootNode = null;
	private View view = null;
	private int caret = 0;

	public JavaCompletion complete( EditPane editPane, int caret ) {
		this.caret = caret;
		Buffer buffer = editPane.getBuffer();
		this.view = editPane.getView();
		JavaParser parser = new JavaParser();
		
		SideKickParsedData skpd = null;
		if ( jEdit.getBooleanProperty("sidekick.java.parseOnComplete") ) {
			skpd = parser.parse(buffer, null);
			rootNode = (CUNode) buffer.getProperty("javasidekick.compilationUnit");
		}
		else {
			skpd = SideKickParsedData.getParsedData(view);
			if ( skpd == null ) {
				skpd = parser.parse(buffer, null);
			}

			rootNode = (CUNode) buffer.getProperty("javasidekick.compilationUnit");
			
		}

		SideKickParsedData.setParsedData( view, skpd );
		if ( skpd instanceof JavaSideKickParsedData ) {
			data = ( JavaSideKickParsedData ) skpd;
		}
		else {
			GUIUtilities.error(view, "sidekick.java.msg.bufferNotParsed", null);
			return null;
		}

		// get the word just before the caret.  It might be a partial word, that's okay.
		String word = getWordAtCursor( editPane.getBuffer() );
		if ( word == null || word.length() == 0 ) {
			return null;
		}

		/*
		 * initial completion goals:
		 * 1. partial word: get matching fields and methods in the class
		 * 2. words ending with dot: get matching fields and methods in the
		 * 	class for the type represented by the word.
		 * 3. words ending with "(": get constructors
		 * 4. class name: get packages
		 */
		JavaCompletion completion = getPossibleCompletions( word );
		return completion ;
	}
	
	private String getWordAtCursor( Buffer buffer ) {
		if ( caret <= 0 )
			return "";
		if ( data == null )
			return null;
		
		// get the text in the current asset just before the cursor
		TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );
		if ( tn == null ) {
			return null;
		}
		
		int start = tn.getStart().getOffset();
		if ( caret - start < 0 ) {
			return "";
		}

		// Check tokens; this prevents using commented code and keywords such as "new" in completion
		DefaultTokenHandler handler = new DefaultTokenHandler();
		int lastLine = -1;
		int closed = 0;
		for (int i = caret-1; i>=start; i--) {
			int line = buffer.getLineOfOffset(i);
			if (line != lastLine) {
				handler.init();
				buffer.markTokens(line, handler);
				lastLine = line;
			}
			int offset = i-buffer.getLineStartOffset(line);
			String lineText = buffer.getLineText(line);
			if (offset == lineText.length()) offset--;
			if (offset < 0) continue;
			Token t = TextUtilities.getTokenAtOffset(handler.getTokens(), offset);
			if (t.id != Token.NULL && t.id != Token.DIGIT && t.id != Token.FUNCTION &&
					t.id != Token.LITERAL1 && t.id != Token.LITERAL2 && t.id != Token.LITERAL3 &&
					t.id != Token.LITERAL4 && t.id != Token.KEYWORD1 && t.id != Token.KEYWORD2 &&
					t.id != Token.KEYWORD3 && t.id != Token.KEYWORD4) {
				// This stops at an open parenthese
				if (t.id == Token.OPERATOR) {            // NOPMD
					char op = lineText.charAt(offset);
					if (op == ')')
						closed++;
					else {
						// Stop at an open parenthese, but not if it's the last character
						// Otherwise constructor completion breaks
						if (i == (caret-1)) closed++;
						closed--;
						if (closed < 0) {
							start = i+1;
							break;
						}
					}
				}
			}
			
			// Skip over parentheses
			if (buffer.getText(i, 1).equals(")")) {
				i = TextUtilities.findMatchingBracket(buffer, line, offset);
				continue;
			}
		}

		String text = buffer.getText( start, caret - start );
		if ( text == null || text.length() == 0 ) {
			return null;
		}
		Mode mode = buffer.getMode();       // TODO: check for java mode?
		String word_break_chars = ( String ) mode.getProperty( "wordBreakChars" );
		if ( word_break_chars == null ) {
			word_break_chars = "";
		}
		word_break_chars += "!;{}";        // NOPMD

		// remove line enders and tabs
		text = text.replaceAll( "[\\n\\r\\t]", "" ).trim();

		// read the text backwards until a word break character is found
		// It is possible that there is no word break character
		for ( int i = text.length() - 1; i >= 0; i-- ) {
			char c = text.charAt( i );
			if ( word_break_chars.indexOf( c ) > -1 ) {
				//Log.log( Log.DEBUG, this, "word break char is " + c );
				text = text.substring( i + 1 );
				// remove all spaces
				text = text.replaceAll( " ", "" );
				break;
			}
		}
		
		return text;
	}

	private JavaCompletion getPossibleCompletions( String word ) {
		org.gjt.sp.util.Log.log(org.gjt.sp.util.Log.DEBUG, this, "getting possible completions on: "+word);
		if ( word == null || word.length() == 0 )
			return null;

		// possibles:
		// cast
		// partialword
		// class.partialword
		// package.class.partialword
		// this.partialword
		// class.this.partialword
		// super.partialword
		// static field, like System.out
		// static method, like String.valueOf

		// check if cast
		// needs work.  This doesn't feel right, hand parsing a cast could
		// be difficult as there are several variations in the depth of
		// parens.

		char lastChar = word.charAt(word.length() - 1);
		if (lastChar == ')')
			return null;

		if (lastChar == '(') {
			// Constructors
			String _word = word.substring(0, word.length() - 1);
			Class c = null;
			if (_word.equals("super")) {
				TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );

				// find the first parent class containing this asset
				while ( tn.getOrdinal() != TigerNode.CLASS && tn.getParent() != null ) {
					tn = tn.getParent();
				}

				if ( tn.getOrdinal() == TigerNode.CLASS ) {
					c = getSuperclassForNode((ClassNode) tn);
				}
			} else {
				c = validateClassName(_word);
				if (c == null)
					c = getClassForType(_word, rootNode);
			}
			if (c != null) {
				return new JavaCompletion( view, _word, JavaCompletion.CONSTRUCTOR,
						getConstructorsForClass(c) );
			}
		}

		// check if "qualified", "qualified" means there is something.something
		boolean qualified = word.lastIndexOf( '.' ) > 0;
		if ( qualified ) {
			return getPossibleQualifiedCompletions( word );
		}
		else {
			return getPossibleNonQualifiedCompletions( word );
		}
	}

	public static ArrayList<String> tokenizeQual(String qualification) {
		JEditBuffer temp = new JEditBuffer();
		temp.insert(0, qualification);
		ArrayList<String> list = new ArrayList<String>();
		int i = 0, j = 0;
		
		while (true) {
			int dot = qualification.indexOf('.', j);
			int paren = qualification.indexOf('(', j);
			if (dot == -1) {
				list.add(qualification.substring(i));
				break;
			}
			if (paren != -1 && paren < dot) {
				j = TextUtilities.findMatchingBracket(temp, 0, paren);
				continue;
			}
			list.add(qualification.substring(i, dot));
			i = dot+1;
			j = i;
		}
		
		return list;
	}

	/**
	 * Get qualified completions for the given word.
	 * A qualified completion is one that contains a dot, which could be:
	 * - a reference using either "super" or "this"
	 * - a fully-qualified class name
	 * - accessing a variable or class field/method
	 */
	private JavaCompletion getPossibleQualifiedCompletions( String word ) {
		org.gjt.sp.util.Log.log(org.gjt.sp.util.Log.DEBUG, this, "getting qualified completions on: " + word);
		String qualification = word.substring( 0, word.lastIndexOf( '.' ) );

		if ( "super".equals( qualification ) ) {
			return getSuperCompletion( word.substring("super.".length()) );
		}

		if ( "this".equals( qualification ) ) {
			return getThisCompletion( word.substring("this.".length()) );
		}

		// Break it down into tokens to allow for completion off a return type
		// e.g. sb.append("hello").append("world")
		Class c = null;
		TigerNode parent = null;
		boolean static_only = false;

		// Get the class node that contains this asset
		parent = (TigerNode) data.getAssetAtOffset(caret);
		while (parent.getParent() != null && parent.getOrdinal() != TigerNode.CLASS)
			parent = parent.getParent();

		// Tokenize by qualifications
		// We create a temporary buffer object to make use of the findMatchingBracket() method
		// This lets us skip over argument parameters when breaking up the full qualification
		// into segments
		JEditBuffer temp = new JEditBuffer();

		// The mode must be set in order to correctly skip literals and comments
		temp.setMode("java");

		temp.insert(0, qualification);
		ArrayList<String> list = new ArrayList<String>();
		int i = 0, j = 0;
		while (true) {
			int dot = qualification.indexOf('.', j);
			int paren = qualification.indexOf('(', j);
			if (dot == -1) {
				list.add(qualification.substring(i));
				break;
			}
			if (paren != -1 && paren < dot) {
				j = TextUtilities.findMatchingBracket(temp, 0, paren);
				if (j == -1) {
					j = paren + 1;
					i = j;
				}
				continue;
			}
			list.add(qualification.substring(i, dot));
			i = dot+1;
			j = i;
		}

		// 'list' is a list of qualifications, separated by dots
		// Iterate through the list, with each condition:
		//
		//  - If a class has not been determined yet, try to determine a starting class
		//    (for example, System.out will start with the qualification 'System' which
		//    resolves to the class java.lang.System; a variable will resolve to its
		//    declared type; and if these fail, qualifications are joined together in order
		//    to determine a type, such as in the case of a fully-qualified class name)
		//
		//  - If a class has been found, resolve the qualification's field, method, or class
		//    and use the return type instead
		StringBuilder tokenBuilder = new StringBuilder();

		// if 'this' is the first token, pop it off.
		// it should be ignored
		if (list.get(0).equals("this"))
			list.remove(0);

		for (j = 0; j<list.size(); j++) {
			if (j>0)
				tokenBuilder.append('.');

			String newToken = list.get(j);
			if (newToken.equals("this")) {
				if (c != null) {
					// Switch from static methods to instance methods
					static_only = false;
					tokenBuilder.append("this");
					continue;
				} else {
					// If 'this' is not the first word,
					// and no class has been found yet,
					// do nothing. There is nothing to do.
					return null;
				}
			} else if (newToken.equals("class")) {
				// Use 'Class'
				try {
					c = Class.forName("java.lang.Class");
					static_only = false;
					break;
				} catch (ClassNotFoundException e) {
					org.gjt.sp.util.Log.log(org.gjt.sp.util.Log.ERROR, this, "Something went seriously fuckin' wrong...");
				}
			}

			if (c == null) {
				// if it ends with ']' and has '[' somewhere in it, make sure it's an array
				boolean array_access = false;
				int arrayIndexStart;
				if (newToken.endsWith("]") && (arrayIndexStart = newToken.indexOf('[')) != -1) {
					String arrayName = newToken.substring(0, arrayIndexStart);
					tokenBuilder.append(arrayName);
					array_access = true;
					array_access = true;
				}
				else tokenBuilder.append(newToken);

				// If it's a string literal, set it to String
				if (newToken.startsWith("\"") && newToken.endsWith("\"")) {
					c = validateClassName("java.lang.String");
					continue;
				}

				String token = tokenBuilder.toString();
				
				// Class?
				c = validateClassName(token);
				if (c == null) {
					//c = getClassForType(token, (CUNode) data.root.getUserObject());
					c = getClassForType(token, rootNode);
				}

				static_only = (c != null);
				if (c == null) {
					// Field?
					FieldNode fieldNode = getLocalVariable(token);
					if (fieldNode != null) {
						String type = fieldNode.getType();

						// check if we need to do array access
						if (array_access && type.endsWith("[]"))
							type = type.substring(0, type.length()-2);

						//c = getClassForType(type, (CUNode) data.root.getUserObject());
						c = getClassForType(type, rootNode);
					}

					if (c == null) {
						// Method?
						MethodNode methodNode = getLocalMethod(token);
						if (methodNode != null) {
							//c = getClassForType(methodNode.getReturnType().getName(), (CUNode) data.root.getUserObject());
							c = getClassForType(methodNode.getReturnType().getName(), rootNode);
						}

						if (c == null) {
							// Enum?
							EnumNode enumNode = getLocalEnum(token);
							if (enumNode != null)
								// ???: Not sure if this handles enums correctly...
								return new JavaCompletion( view, word, JavaCompletion.DOT, getMembersForEnum(enumNode));

							if (c == null) {
								// Class?
								ClassNode classNode = getLocalClass(token);
								if (classNode != null) {
									//c = getClassForType(classNode.getType(), (CUNode) data.root.getUserObject());
									c = getClassForType(classNode.getType(), rootNode);
								}

								if (c == null && parent.getOrdinal() == TigerNode.CLASS) {
									Class sc = getSuperclassForNode((ClassNode) parent);
									c = getClassForToken(sc, token);
								}

								if (c == null) {
									// Check for cast
									String castToken = newToken.trim();
									if (castToken.startsWith("(") && castToken.endsWith(")")) {
										int open = castToken.lastIndexOf('(');
										int close = castToken.indexOf(')');
										//c = getClassForType( castToken.substring(open+1, close), (CUNode) data.root.getUserObject());
										c = getClassForType( castToken.substring(open+1, close), rootNode);
									}
								}
							}
						}
					}
				}
			}
			else {
				String token = list.get(j);
				boolean found = false;

				// Fields
				Field[] fields = c.getFields();
				for (i = 0; i < fields.length; i++) {
					if (token.equals(fields[i].getName())) {
						c = fields[i].getType();
						found = true;
						static_only = false;
						break;
					}
				}

				if (!found) {
					// Methods
					Method[] methods = c.getMethods();
					for (i = 0; i < methods.length; i++) {
						if (token.startsWith(methods[i].getName() + "(")) {
							c = methods[i].getReturnType();
							found = true;
							static_only = false;
							break;
						}
					}

					if (!found) {
						// Subclasses
						Class[] classes = c.getClasses();
						for (i = 0; i < classes.length; i++) {
							if (token.equals(classes[i].getSimpleName())) {
								c = classes[i];
								found = true;
								static_only = true;
								break;
							}
						}

						if (!found) {
							c = null;
						}
					}
				}
			}
		}

		if (c == null) {
			// Might be inside a method call, like: while (tokenizer.<COMPLETION>
			int paren = qualification.lastIndexOf('(');
			if (paren != -1 && paren != qualification.length()-1 ) {
				String halfWord = qualification.substring(paren+1).trim();

				// Class?
				c = validateClassName(halfWord);
				if (c == null) {
					c = getClassForType(halfWord, rootNode);
				}

				static_only = (c != null);

				if (c == null) {
					// Field?
					FieldNode node = getLocalVariable(halfWord);

					if (node != null) {
						c = getClassForType(node.getType(), rootNode);
						if (c == null)
							return null;
					}
				}
			}
		}

		if (c != null) {
			// filter the members of the class by the part of the word
			// following the last dot.  The completion will replace this
			// part of a word
			String filter = word.substring( word.lastIndexOf( '.' ) + 1 );
			if ( filter != null && filter.length() == 0 )
				filter = null;
			if (filter != null && filter.endsWith("("))
				filter = filter.substring(0, filter.length()-1);

			List members = getMembersForClass( c, filter, static_only, true );
			if ( members != null && members.size() > 0 ) {
				if ( members.size() == 1 && members.get( 0 ).equals( word ) ) {
					return null;
				}
				return new JavaCompletion( view, word, JavaCompletion.DOT, members );
			}
		}

		// could have package.partialClass, e.g. javax.swing.tree.DefaultMu
		List<String> possibles = Locator.getInstance().getClasses(word);

		if ( possibles != null && possibles.size() > 0 ) {
			if ( possibles.size() == 1 && possibles.get( 0 ).equals( word ) ) {
				return null;
			}

			ArrayList candids = new ArrayList(possibles.size());
			for (int k = 0; k < possibles.size(); k++) {
				candids.add(new JavaCompletionCandidate(possibles.get(k),
							TigerLabeler.getClassIcon()));
			}

			Collections.sort(candids);
			return new JavaCompletion( view, word, JavaCompletion.DOT, candids );
		}

		return getLocalVariableCompletion( word );
	}

	/**
	 * Get non-qualified completions for the given word.
	 * Non-qualified completions are ones that don't have a dot, which could be
	 * - a variable, method, or class name
	 * - a constructor
	 */
	private JavaCompletion getPossibleNonQualifiedCompletions( String word ) {
		org.gjt.sp.util.Log.log(org.gjt.sp.util.Log.DEBUG, this, "getting non-qualified completions on: " + word);
		word = word.substring(word.lastIndexOf('(') + 1);
		
		// check if this word is a valid unqualified class name
		// if it is, then return all possible fully-qualified class names
		List<String> pkgs = Locator.getInstance().getClassName(word);
		if (pkgs != null && pkgs.size() > 0) {
			ArrayList pkgCandidates = new ArrayList(pkgs.size());
			for (int i = 0; i < pkgs.size(); i++) 
				pkgCandidates.add(new JavaCompletionCandidate(pkgs.get(i), TigerLabeler.getClassIcon()));

			return new JavaCompletion(view, word, JavaCompletion.PACKAGE, pkgCandidates);
		}
		
		// partialword
		// find all fields/variables declarations, methods, and classes in scope
		TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );
		Set<JavaCompletionCandidate> choices = new TreeSet<JavaCompletionCandidate>();
		while ( tn != null ) {
			// If we're in a method, check its parameters
			if (tn.getOrdinal() == TigerNode.CONSTRUCTOR || tn.getOrdinal() == TigerNode.METHOD) {
				List params = ( ( Parameterizable ) tn ).getFormalParams();
				if (params != null) {
                    for ( Iterator jt = params.iterator(); jt.hasNext(); ) {
                        Parameter param = ( Parameter ) jt.next();
                        if ( param.getName().startsWith( word ) ) {
                            choices.add( new JavaCompletionCandidate(
                                param.getName(),
                                TigerLabeler.getFieldIcon() ));
                        }
                    }
				}
			}
			
			List children = tn.getChildren();
			if ( children != null ) {
				for ( Iterator it = children.iterator(); it.hasNext(); ) {
					TigerNode child = (TigerNode) it.next();
					Icon icon = null;
					switch ( child.getOrdinal() ) {
						case TigerNode.CONSTRUCTOR:
						case TigerNode.METHOD:
							if (icon == null)
								icon = TigerLabeler.getMethodIcon();
						case TigerNode.FIELD:
						case TigerNode.VARIABLE:
							if (icon == null)
								icon = TigerLabeler.getFieldIcon();
						case TigerNode.CLASS:
						case TigerNode.ENUM:
						case TigerNode.INTERFACE:
							if (icon == null)
								icon = TigerLabeler.getClassIcon();
							
							if ( child.getName().startsWith( word ) ) {
								choices.add( new JavaCompletionCandidate(
									   child.getName(),
									   icon) );
							}
							break;
					}
				}
			}
			do {
				tn = tn.getParent();
			}
			while (tn != null && (tn.getOrdinal() == TigerNode.FIELD || tn.getOrdinal() == TigerNode.VARIABLE));
		}
		List list = new ArrayList( choices.size() );
		for ( JavaCompletionCandidate choice : choices ) {
			list.add(choice);
		}
		
		JavaCompletion jc = getSuperCompletion( word );
		if ( jc != null ) {
			// TODO: Convert this to JavaCompletionCandidate
			list.addAll( jc.getChoices() );
		}
		
		if ( list.size() > 0 ) {
			// don't show the completion popup if the only choice is an
			// exact match for the word
			if ( list.size() == 1 && word.equals( list.get( 0 ).toString() ) )
				return null;
			else {
				Collections.sort( list );
				return new JavaCompletion( view, word, list );
			}
		}
		return null;
	}

	/**
	 * Given a class node, return the first extends node, if any
	 * @param cn the class node
	 * @return an ExtendsNode object if one exists, otherwise null
	 */
	public ExtendsNode getExtendsNode(ClassNode cn) {
		for (int i = 0; i<cn.getChildCount(); i++) {
			TigerNode tn = cn.getChildAt(i);
			if (tn.getOrdinal() == TigerNode.EXTENDS)
				return (ExtendsNode) tn;
		}
		return null;
	}

	/**
	 * Returns the superclass Class object for the given class node
	 * @param cn the class node
	 * @return the Class object for cn's superclass; defaults to Object if none is found
	 */
	public Class getSuperclassForNode(ClassNode cn) {
		Class c = null;
		ExtendsNode superclass = getExtendsNode(cn);
		if (superclass != null) {
			//c = getClassForType(superclass.getName(), (CUNode) data.root.getUserObject());
			c = getClassForType(superclass.getName(), rootNode);
		}
		else {
			//c = getClassForType(cn.getName(), rootNode);
			// TODO: need a way to get the package for this classnode
			// NOTE: from Dale -- to get the package, recursively call ClassNode.getParent
			// until the parent is a CUNode, then call getPackage on the CUNode.
			// this call should *not* check for all classes with the same name
			try {
				c = Class.forName(cn.getName());
				if (c != null)
					return c.getSuperclass();
			}
			catch (ClassNotFoundException e) {        // NOPMD
				// ignore
			}
		}
		
		if (c == null) {
			try {
				c = Class.forName("java.lang.Object");
			}
			catch (Exception e) {
				// Something went seriously wrong
				e.printStackTrace();
			}
		}

		return c;
	}

	/**
	 * Returns the superclass' class node for the given node
	 * @param cn the class node
	 * @return the class node that the given node extends, if any
	 */
	public ClassNode getSuperclassNodeForNode(ClassNode cn) {
		ExtendsNode en = getExtendsNode(cn);
		if (en != null) {
			// TODO: support completion on classes extending another parsed class
			return null;
		}
		return null;
	}

	// returns a completion containing a list of fields and methods contained by
	// the super class
	private JavaCompletion getSuperCompletion( String word ) {
		// get the containing asset
		TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );

		// find the first parent class containing this asset
		while ( tn.getOrdinal() != TigerNode.CLASS ) {
			if ( tn.getParent() != null )
				tn = tn.getParent();
			else
				return null;    // shouldn't get here
		}
		
		// find the superclass of the enclosing class
		/*
		Class c = getClassForType( tn.getName(), ( CUNode ) data.root.getUserObject() );
		if ( c != null )
			c = c.getSuperclass();
		if ( c == null )
			return null;
		*/
		Class c = getSuperclassForNode((ClassNode) tn);

		// get the members (fields and methods) for the class node
		List m = getMembersForClass( c, word, false, false );
		if ( m == null || m.size() == 0 )
			return null;
		if ( m.size() == 1 && m.get( 0 ).toString().equals( word ) ) {
			return null;
		}

		for ( ListIterator it = m.listIterator(); it.hasNext(); ) {
			if ( !it.next().toString().startsWith( word ) ) {
				it.remove();
			}
		}


		return new JavaCompletion( view, word, JavaCompletion.DOT, m );
	}


	// returns a completion containing a list of fields and methods contained by
	// the enclosing class
	private JavaCompletion getThisCompletion( String word ) {
		// get the containing asset
		TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );

		// find the first parent class containing this asset
		while ( tn.getOrdinal() != TigerNode.CLASS ) {
			if ( tn.getParent() != null )
				tn = tn.getParent();
			else
				return null;    // shouldn't get here
		}

		Set members = new TreeSet();

		// get the members (fields and methods) for the class node
		List m = getMembersForClass( ( ClassNode ) tn, word );
		if ( m == null )
			return null;
		if ( m.size() == 1 && m.get( 0 ).equals( word ) ) {
			return null;
		}

		members.addAll(m);

		for (int i = 0; i<tn.getChildCount(); i++) {
			TigerNode child = tn.getChildAt(i);
			if (child.getOrdinal() == TigerNode.EXTENDS) {
				// Add members of the superclass
				//Class c = getClassForType(child.getName(), (CUNode) data.root.getUserObject());
				Class c = getClassForType(child.getName(), rootNode);
				if (c != null)
					members.addAll(getMembersForClass(c, word, false, false));
			}
		}

		ArrayList list = new ArrayList(members.size());
		list.addAll(members);
		Collections.sort(list);

		for ( ListIterator it = list.listIterator(); it.hasNext(); ) {
			if ( !it.next().toString().startsWith( word ) ) {
				it.remove();
			}
		}

		return new JavaCompletion( view, word, JavaCompletion.DOT, list );
	}



	// returns a completion containing a list of fields and methods contained contained by the type defined by the word,
	// for example, if the word is "my_word" and it is a String, return the fields and methods
	// for String.
	private JavaCompletion getLocalVariableCompletion( String word ) {
		String my_word = word.indexOf('.') > -1 ? word.substring(0, word.lastIndexOf('.')) : word;
		FieldNode lvn = getLocalVariable( my_word );
		if ( lvn == null ) {
			return null;
		}

		String name = lvn.getName();
		if ( name.startsWith( my_word ) ) {
			int insertionType = JavaCompletion.PARTIAL;
			if ( name.equals( my_word ) ) {
				insertionType = JavaCompletion.DOT;
			}
			String type = lvn.getType();
			//Class c = getClassForType( type, ( CUNode ) data.root.getUserObject() );
			Class c = getClassForType( type, rootNode );
			if ( c != null ) {
				List m = getMembersForClass( c );
				if ( m != null ) {
					return new JavaCompletion( view, word, insertionType, m );
				}
			}
		}
		return null;
	}


	private FieldNode getLocalVariable( String name ) {
		TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );
		while ( true ) {
			// check children of the node first
			List<TigerNode> children = tn.getChildren();
			if ( children != null ) {
				for (TigerNode child : children) {
				    if (child == null)
				        continue;
					if ( child instanceof FieldNode ) {     // LocalVariableNode is a subclass of FieldNode
						FieldNode lvn = ( FieldNode ) child;
						if (lvn.hasChildren()) {
                            for (TigerNode var : lvn.getChildren()) {
                                if (var instanceof VariableDeclarator) {
                                    VariableDeclarator vn = (VariableDeclarator) var;
                                    if (!vn.isPrimitive() && vn.getName().equals(name))
                                        return lvn;
                                }
                            }
						}
						/*
						if ( !lvn.isPrimitive() && lvn.getName().equals( name ) ) {
							return lvn;
						}
						*/
					}
				}
			}

			// check parameters to constructors and methods
			if ( tn.getOrdinal() == TigerNode.CONSTRUCTOR || tn.getOrdinal() == TigerNode.METHOD ) {
				List params = ( ( Parameterizable ) tn ).getFormalParams();
				if (params != null) {
                    for ( Iterator jt = params.iterator(); jt.hasNext(); ) {
                        Parameter param = ( Parameter ) jt.next();  // Parameter is a subclass of FieldNode
                        if ( param.getName().startsWith( name ) ) {
                            return param;
                        }
                    }
				}
			}

			// up the tree
			tn = tn.getParent();
			if ( tn == null ) {
				break;
			}
		}
		return null;
	}

	private EnumNode getLocalEnum( String name ) {
		TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );
		while ( tn != null ) {
			List<TigerNode> children = tn.getChildren();
			if (children != null) {
				for (TigerNode child : children) {
					if (child instanceof EnumNode && child.getName().equals(name)) {
                        return (EnumNode) child;
					}
				}
			}
			// up the tree
			tn = tn.getParent();
		}
		
		return null;
	}

	private MethodNode getLocalMethod( String name ) {
		TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );
		while ( tn != null ) {
			// check children of the node first
			List<TigerNode> children = tn.getChildren();
			if ( children != null ) {
				for (TigerNode child : children) {
					if ( child instanceof MethodNode && child.getName().equals(name) ) {
                        return (MethodNode) child;
					}
				}
			}
			// up the tree
			tn = tn.getParent();
		}

		return null;
	}

	private ClassNode getLocalClass( String name ) {
		TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );
		while ( true ) {
			// check children of the node first
			List<TigerNode> children = tn.getChildren();
			if ( children != null ) {
				for (TigerNode child : children) {
					if ( child instanceof ClassNode && child.getName().equals(name) ) {
                        return (ClassNode) child;
					}
				}
			}
			// up the tree
			tn = tn.getParent();
			if ( tn == null ) {
				break;
			}
		}
		return null;
	}

	/**
	 * Given a type, such as "String" or "Object", and a compilation unit,
	 * this method attempts to create an actual class of that type.
	 * @return a Class of the given type
	 */
	public Class getClassForType( String type, CUNode cu ) {
		return getClassForType( type, cu, null );
	}

	/**
	 * @param type name
	 * @param cu the top leve CU node
	 * @param filename the filename of the buffer
	 */
	public Class getClassForType( String type, CUNode cu, String filename ) {
		if (type.endsWith("[]")) {
			try {
				return Class.forName("sidekick.java.tools.Array");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		// check in same package
		String packageName;
		if (cu.getPackage() != null) {
            packageName = cu.getPackage().getName();
            if ( packageName != null ) {
                // check same package
                String className = ( packageName.length() > 0 ? packageName + "." : "" ) + type;
                Class c = validateClassName( className );
                if ( c != null ) {
                    return c;
                }
            }
		}

		// check imports
		List imports = cu.getImportNames();
		for ( Iterator it = imports.iterator(); it.hasNext(); ) {
			packageName = ( String ) it.next();
			if ( packageName != null ) {
				String className = packageName;
				// might have a fully qualified import
				if ( className.endsWith( type ) ) {
					Class c = validateClassName( className );
					if ( c != null ) {
						return c;
					}
				}
				else {
					// wildcard import, need to add . and type
					className = packageName + "." + type;
					try {
						Class c = validateClassName( className );
						if ( c != null ) {
							return c;
						}
					}
					catch ( Exception e ) {
						continue;
					}
				}
			}
		}

		// check in java.lang
		Class c = validateClassName( "java.lang."+type );
		if (c != null)
			return c;

		List<String> classNames = Locator.getInstance().getClassName( type );

		if (classNames == null || classNames.size() == 0)
			return null;
		else {
			if (classNames.size() > 1) {
				GUIUtilities.error(view, "options.sidekick.java.ambiguousClass",
						new String[] { type });
				return null;
			}
			else {
				return validateClassName( classNames.get(0) );
			}
		}
		// check jars in project classpath. These are the jars and/or directories
		// specified in the ProjectViewer "Classpath settings" option pane.
		/*
		if (PVHelper.getProject( editPane.getView() ) != null) {
			classNames = Locator.getInstance().getProjectClassName(
						PVHelper.getProject( editPane.getView() ), type);
			if (classNames != null && classNames.length > 1) {
				GUIUtilities.error(editPane.getView(), "options.sidekick.java.ambiguousClass",
					new String[] { type });
				return null;
			}
			else if (classNames != null && classNames.length > 0) {
				className = classNames[0];
				c = validateClassName( className );
				if (c != null) {
					return c;
				}
			}
		}
		// check jars in classpath.  These are the jars and/or directories specified
		// in System.getProperty("java.class.path").
		if ( c == null && PVHelper.useJavaClasspath( PVHelper.getProject( editPane.getView() ) ) ) {
			classNames = Locator.getInstance().getClassPathClassName( type );
			if (classNames != null && classNames.length > 1) {
				GUIUtilities.error(editPane.getView(), "options.sidekick.java.ambiguousClass",
					new String[] { type });
				return null;
			}
			else if (classNames != null && classNames.length > 0) {
				className = classNames[0];
				c = validateClassName( className );
				if (c != null) {
					return c;
				}
			}
		}

		// check Java runtime jars.  These are the jars specified in $JAVA_HOME/lib,
		// ext dirs, and endorsed dirs.
		if ( c == null ) {
			classNames = Locator.getInstance().getRuntimeClassName( type );
			if (classNames != null && classNames.length > 1) {
				GUIUtilities.error(editPane.getView(), "options.sidekick.java.ambiguousClass",
					new String[] { type });
				return null;
			}
			else if (classNames != null && classNames.length > 0) {
				className = classNames[0];
				c = validateClassName( className );
				if (c != null) {
					return c;
				}
			}
		}
		*/
		//return null;
	}

	/**
	 * Attempts to find the class in the current classloader.  If not found,
	 * attempts to find the class in the project classloader.  If not found,
	 * attempts to find the class in the build output directory.
	 *
	 * @param classname The name of the class to find.
	 * @return The class if found, null if not.
	 */
	private Class validateClassName( String classname ) {
		if ( classname == null ) {
			return null;
		}
		try {
			// check current classloader
			return Class.forName( classname );
		}
		catch ( ClassNotFoundException cnfe ) {     // NOPMD
			try {
				// check the project classloader
				AntClassLoader classloader = Locator.getInstance().getClassLoader();
				if ( classloader == null )
					throw new ClassNotFoundException();

				return classloader.forceLoadClass( classname );
			}
			catch ( Exception pcnfe ) {
				return null;
			}
		}
	}

	private Class getClassForToken(Class c, String token) {
		try {
			while (c != null) {
				Method[] methods = c.getDeclaredMethods();
				for (int i = 0; i<methods.length; i++) {
					// skip private methods
					if (Modifier.isPrivate(methods[i].getModifiers()))
						continue;

					int j = token.indexOf('(');
					if (j>0)
						token = token.substring(0, j).trim();

					if (methods[i].getName().equals(token))
						return methods[i].getReturnType();
				}

				Field[] fields = c.getDeclaredFields();
				for (int i = 0; i<fields.length; i++) {
					// skip private fields
					if (Modifier.isPrivate(fields[i].getModifiers()))
						continue;

					if (fields[i].getName().equals(token))
						return fields[i].getType();
				}

				// Climb up the inheritance tree
				c = c.getSuperclass();
			}
		}
		catch (Exception e) {
			return null;
		}
		catch (NoClassDefFoundError e) {
			GUIUtilities.error(jEdit.getActiveView(), "sidekick.java.msg.noClassDefFound",
					new String[] { e.getMessage() });
			return null;
		}
		return null;
	}

	// returns a list of TigerNodes that are immediate children of the given
	// class node
	private List getMembersForClass( ClassNode cn ) {
		return getMembersForClass( cn, null );
	}

	// returns a list of TigerNodes that are immediate children of the given
	// class node, filtered with the given filter.  A null filter returns all
	// children.
	private List getMembersForClass( ClassNode cn, String filter ) {
		if ( cn.getChildCount() == 0 )
			return null;

		Set members = new TreeSet();
		for ( Iterator it = cn.getChildren().iterator(); it.hasNext(); ) {
			TigerNode child = ( TigerNode ) it.next();
			if (filter != null && !child.getName().startsWith(filter))
				continue;

			switch ( child.getOrdinal() ) {     // NOPMD
				// field is a special case
				case TigerNode.FIELD:
					for (TigerNode var : child.getChildren()) {
						if (var.getOrdinal() == TigerNode.VARIABLE) {
							VariableDeclarator vn = (VariableDeclarator) var;
							members.add(new JavaCompletionCandidate(
										vn.getName()+" : "+child.getType(), TigerLabeler.getFieldIcon()));
						}
					}
					break;
				case TigerNode.ENUM:
					// FIXME
					members.add(new JavaCompletionCandidate(child.getName()+" : enum",
								TigerLabeler.getInnerClassIcon(false, true)));
					break;
				case TigerNode.METHOD:
					MethodNode mn = (MethodNode) child;
					String more = "(" + mn.getFormalParams(true, false, true, true) +
						") : " + mn.getReturnType().getType();
					members.add(new JavaCompletionCandidate(mn.getName()+more,
								TigerLabeler.getMethodIcon()));
					break;
				case TigerNode.CLASS:
					members.add(new JavaCompletionCandidate(child.getName(), TigerLabeler.getClassIcon()));
					break;
				case TigerNode.EXTENDS:
					//Class c = getClassForType(child.getName(), (CUNode) data.root.getUserObject() );
					Class c = getClassForType(child.getName(), rootNode );
					if (c == null)
						return null;
					members.addAll(getMembersForClass(c, filter));
			}
		}

		// Add members from Object
		try {
			Class c = Class.forName("java.lang.Object");
			members.addAll(getMembersForClass(c, filter));
		} catch (ClassNotFoundException e) {
			org.gjt.sp.util.Log.log(org.gjt.sp.util.Log.ERROR, this, "Whoops! java.lang.Object doesn't exist!");
		}

		ArrayList list = new ArrayList( members );
		Collections.sort( list );
		return list;
	}


	private List getMembersForClass( Class c ) {
		return getMembersForClass( c, null );
	}


	private List getMembersForClass( Class c, String filter ) {
		return getMembersForClass( c, filter, false, true );
	}

	private List getMembersForClass( Class c, String filter, boolean static_only, boolean public_only ) {
		if ( c == null )
			return null;

		Set list = new TreeSet();

		try {
			while (c != null) {
				Method[] methods = c.getDeclaredMethods();
				for ( int i = 0; i < methods.length; i++ ) {
					int modifiers = methods[ i ].getModifiers();
					// Is it static?
					if ( static_only != Modifier.isStatic( modifiers )) {
						continue;
					}
					// Is it public, or do we want protected?
					if ( Modifier.isPrivate(modifiers) || (!Modifier.isPublic(modifiers) && public_only) ) {
						continue;
					}
					if ( filter == null || methods[ i ].getName().startsWith( filter ) ) {
						Method method = methods[ i ];
						Class[] paramTypes = method.getParameterTypes();
						StringBuilder params = new StringBuilder("(");
						for (int j = 0; j < paramTypes.length; j++) {
							params.append( paramTypes[j].getSimpleName() );
							if (j < paramTypes.length - 1)
								params.append( ',' );
						}
						params.append( ')' );
						list.add( new JavaCompletionCandidate(
									method.getName() + params.toString() + " : " + method.getReturnType().getSimpleName(),
									TigerLabeler.getMethodIcon(), Modifier.isProtected(modifiers)) );
					}
				}
				Field[] fields = c.getDeclaredFields();
				for ( int i = 0; i < fields.length; i++ ) {
					int modifiers = fields[ i ].getModifiers();
					// Is it static?
					if ( static_only != Modifier.isStatic (modifiers )) {
						continue;
					}
					// Is it public, or do we want protected?
					if ( Modifier.isPrivate(modifiers) || (Modifier.isProtected(modifiers) && public_only) ) {
						continue;
					}
					if ( filter == null || fields[ i ].getName().startsWith( filter ) )
						list.add( new JavaCompletionCandidate(
									fields[ i ].getName() + " : " + fields[ i ].getType().getSimpleName(),
									TigerLabeler.getFieldIcon(), Modifier.isProtected(modifiers)) );
				}
				Class[] classes = c.getDeclaredClasses();
				for (int i = 0; i < classes.length; i++) {
					int modifiers = classes[ i ].getModifiers();
					// Is it static?
					if ( static_only != Modifier.isStatic (modifiers )) {
						continue;
					}
					// Is it public, or do we want protected?
					if ( Modifier.isPrivate(modifiers) || (Modifier.isProtected(modifiers) && public_only) ) {
						continue;
					}
					// Don't display the class if it's anonymous
					if ( classes[i].isAnonymousClass() ) {
						continue;
					}
					if (filter == null || classes[ i ].getName().startsWith( filter ) )
						list.add(new JavaCompletionCandidate(
									classes[ i ].getSimpleName(),
									TigerLabeler.getInnerClassIcon(c.isInterface(), c.isEnum()),
									Modifier.isProtected(modifiers)) );
				}
				// Climb up the inheritance tree
				c = c.getSuperclass();
			}
		}
		catch ( Exception e ) {
			return null;
		}
		catch ( NoClassDefFoundError ncdfe ) {
			// TODO: logging to the activity log is useless for the end user.  Need to
			// find a better way to let them know about this problem.
			view.getStatus().setMessage("Class not in classpath: " + ncdfe.getMessage());
			return null;
		}
		
		List members = new ArrayList( list );
		Collections.sort( members );
		return members;
	}

	private List getMembersForEnum(EnumNode enumNode) {
		Set list = new TreeSet();
		for (TigerNode node : enumNode.getChildren()) {
			list.add(new JavaCompletionCandidate(node.getName(), TigerLabeler.getFieldIcon()));
		}

		List members = new ArrayList(list);
		Collections.sort(members);
		return members;
	}

	private List getConstructorsForClass( Class c ) {
		Set list = new TreeSet();
		try {
			Constructor[] cons = c.getConstructors();
			for (int i = 0; i < cons.length; i++) {
				StringBuilder name = new StringBuilder();
				name.append(c.getSimpleName()).append('(');
				Class[] params = cons[i].getParameterTypes();
				for (int j = 0; j < params.length; j++) {
					name.append( params[j].getSimpleName() );
					if (j < params.length - 1)
						name.append( ',' );
				}
				name.append( ')' );
				list.add(new JavaCompletionCandidate(name.toString(), TigerLabeler.getConstructorIcon()));
			}
		}
		catch (Exception e) {
			return null;
		}
		List constructors = new ArrayList( list );
		Collections.sort( constructors );
		return constructors;
	}

	static class JavaCompletionCandidate implements Comparable {

		private String text;
		private Icon icon;
		private boolean isProtected;

		public JavaCompletionCandidate(String text, Icon icon) {
			this(text, icon, false);
		}

		public JavaCompletionCandidate(String text, Icon icon, boolean isProtected) {
			this.text = text;
			this.icon = icon;
			this.isProtected = isProtected;
		}

		public Icon getIcon() {
			return icon;
		}

		public String toString() {
			return text;
		}

		public boolean isProtected() {
			return isProtected;
		}

		/*
		 * Pass comparison to the string values. The icons are irrelevant.
		 */
		public int compareTo(Object ob) {
			try {
				JavaCompletionCandidate candid = (JavaCompletionCandidate) ob;
				return text.compareTo(candid.toString());
			} catch (ClassCastException e) {
				return 0;
			}
		}
		
		/*
		 * Override equals() to eliminate potential duplicates
		 */
		public boolean equals(Object ob) {
			try {
				JavaCompletionCandidate candid = (JavaCompletionCandidate) ob;
				return text.equals(candid.toString());
			} catch (ClassCastException e) {
				return false;
			}
		}

	}
}
