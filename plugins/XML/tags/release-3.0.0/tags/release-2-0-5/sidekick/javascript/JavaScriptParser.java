/*
 * JavaScriptParser.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2005 by Martin Raspe
 * (hertzhaft@biblhertz.it)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package sidekick.javascript;

import org.gjt.sp.jedit.*;

import sidekick.enhanced.SourceParser;


import java.util.Stack;
import java.util.regex.Pattern;

import errorlist.*;

/**
 * JavaScriptParser: parses perl source and builds a sidekick structure tree
 * Parser is based on regular expressions and will therefore 
 * not able to correctly parse very irregular perl scripts 
 *
 * @author     Martin Raspe
 * @created    March 3, 2005
 * @modified   $Date$ by $Author$
 * @version    $Revision$
 */
public class JavaScriptParser extends SourceParser {

/*
    Changes suggested by David Padgett to make the parser 
    more compatible with namespace/package emulation in JavaScript
    
    my.test.namespace.MyClass = function ...
    my.test.namespace.MyClass.prototype.foobar = function ...
    my.test.namespace.MyClass.myStaticMethod = function ...
*/
    Pattern pClassFunction		= Pattern.compile("^((\\w+\\.)*[A-Z][\\w]*)\\s*=\\s*function");
    Pattern pMethodFunction		= Pattern.compile("^([\\w+\\.]+\\w+)\\.([\\w]+)\\s*=\\s*function");
    Pattern pPrototypeFunction	= Pattern.compile("^([\\w+\\.]+\\w+)\\.prototype\\.([\\w]+)\\s*=\\s*function");

//	Pattern pClassFunction		= Pattern.compile("^([A-Z][\\w]+)\\s*=\\s*function");
//	Pattern pMethodFunction		= Pattern.compile("^([\\w]+)\\.([\\w]+)\\s*=\\s*function");
//	Pattern pPrototypeFunction	= Pattern.compile("^([\\w]+)\\.prototype\\.([\\w]+)\\s*=\\s*function");

    Pattern pAssignedFunction	= Pattern.compile("^(\\w+)\\s*=\\s*function");
	Pattern pVarFunction		= Pattern.compile("^var\\s+(\\w+)\\s*=\\s*function");
	Pattern pSimpleFunction		= Pattern.compile("^function\\s+(\\w+)");

/**	 * Constructs a new Parser object
	 *
	 * @param name See sidekick.SidekickParser, sidekick.SourceParser
	 */
	public JavaScriptParser() {
		super("javascript");
		LINE_COMMENT	= "//";
		COMMENT		= "Comments";
		MAIN		= "(self)";
		USE		= "import";
	}

/**	 * Parses the given text and returns a tree model.
	 *
	 * @param buffer The buffer to parse.
	 * @param errorSource An error source to add errors to.
	 */

	 protected void parseBuffer(Buffer buffer, DefaultErrorSource errorSource) {
		String line;
		String name;
		String[] names;
		Stack funcstack = new Stack();
		Stack pkgstack = new Stack();
		pkgstack.push(MAIN);
		boolean in_comment = false;
		int buflen = buffer.getLength();
		int _tmp;
		for (int lineNo = 0; lineNo < buffer.getLineCount(); lineNo++) {
			_start = buffer.createPosition(buffer.getLineStartOffset(lineNo));
			_tmp = buffer.getLineEndOffset(lineNo);
			if (_tmp > buflen) _tmp = buflen;
			_end = buffer.createPosition(_tmp);
			line = buffer.getLineText(lineNo).trim();
			// line comment or empty line
			if (line.indexOf(LINE_COMMENT) == 0 || line.length() == 0) continue;
			// block comment: end
			if (in_comment && line.indexOf("*/") != -1 ) { 
				in_comment = false;
				completeAsset(_end);
				}
			if (in_comment) continue;
			// Class = function()
			name = find(line, pClassFunction, 1);
			if (name != null) {
				if (! pkgstack.empty()) pkgstack.pop();
				pkgstack.push(name);
				addAsset(SUB_KEY, name, "(constructor)", lineNo, _start);
				continue;
				}
			// instance.prototype.method = function()
			names = find2(line, pPrototypeFunction);
			if (names != null) {
				if (! pkgstack.empty()) pkgstack.pop();
				if (! funcstack.empty()) {
					funcstack.pop();
					completeAsset(_start);
					}
				pkgstack.push(names[0]);
				funcstack.push(names[1]);
				addAsset(SUB_KEY, names[0], names[1], lineNo, _start);
				continue;
				}
			// instance.method = function()
			names = find2(line, pMethodFunction);
			if (names != null) {
				if (! pkgstack.empty()) pkgstack.pop();
				if (! funcstack.empty()) {
					funcstack.pop();
					completeAsset(_start);
					}
				pkgstack.push(names[0]);
				funcstack.push(names[1]);
				addAsset(SUB_KEY, names[0], names[1], lineNo, _start);
				continue;
				}
			// vname = function()
			name = find(line, pAssignedFunction, 1);
			if (name != null) {
				if (! funcstack.empty()) {
					funcstack.pop();
					completeAsset(_start);
					}
				funcstack.push(name);
				addLineAsset(SUB_KEY, (String) pkgstack.peek(), name, lineNo, _start, _end);
				continue;
				}
			// var vname = function()
			name = find(line, pVarFunction, 1);
			if (name != null) {
				if (! funcstack.empty()) {
					funcstack.pop();
					completeAsset(_start);
					}
				funcstack.push(name);
				addLineAsset(SUB_KEY, (String) pkgstack.peek(), name, lineNo, _start, _end);
				continue;
				}
			// function fname()
			name = find(line, pSimpleFunction, 1);
			if (name != null) {
				if (! funcstack.empty()) {
					funcstack.pop();
					completeAsset(_start);
					}
				funcstack.push(name);
				addLineAsset(SUB_KEY, (String) pkgstack.peek(), name, lineNo, _start, _end);
				continue;
				}
			// block comment: start
			if (! in_comment && line.indexOf("/*") != -1 ) { 
				completeAsset(_end);
				if (! funcstack.empty()) name = (String) funcstack.peek();
				if (name == null)  name = (String) pkgstack.peek();
				if (name == null)  name = "(comment)";
				in_comment = true;
				addCommentAsset(name, lineNo, _start);
				}
			}
	 }
}
