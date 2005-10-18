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
import org.gjt.sp.util.*;


import java.util.Stack;
import java.util.regex.Pattern;

import errorlist.*;
import sidekick.SourceParser;
import sidekick.perl.PerlSideKickPlugin;

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

	Pattern pClassFunction		= Pattern.compile("^([A-Z][\\w]+)\\s*=\\s*function");
	Pattern pMethodFunction		= Pattern.compile("^([\\w]+)\\.([\\w]+)\\s*=\\s*function");
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
		MAIN		= "/";
		USE		= "import";
	}

/**	 * Parses the given text and returns a tree model.
	 *
	 * @param buffer The buffer to parse.
	 * @param errorSource An error source to add errors to.
	 */

	 protected void parseBuffer(Buffer buffer, DefaultErrorSource errorSource) {
		String _line;
		String _name;
		String[] _names;
		Stack _fnc = new Stack();
		Stack _pkg = new Stack();
		_pkg.push(MAIN);
		boolean _inComment = false;
		int buflen = buffer.getLength();
		int _tmp;
		for (int _lineNo = 0; _lineNo < buffer.getLineCount(); _lineNo++) {
			_start = buffer.createPosition(buffer.getLineStartOffset(_lineNo));
			_tmp = buffer.getLineEndOffset(_lineNo);
			if (_tmp > buflen) _tmp = buflen;
			_end = buffer.createPosition(_tmp);
			_line = buffer.getLineText(_lineNo).trim();
			// line comment or empty line
			if (_line.indexOf(LINE_COMMENT) == 0 || _line.length() == 0) continue;
			// block comment: end
			if (_inComment && _line.indexOf("*/") != -1 ) { 
				_inComment = false;
				completeAsset(_end);
				}
			if (_inComment) continue;
			// Class = function()
			_name = find(_line, pClassFunction, 1);
			if (_name != null) {
				if (! _pkg.empty()) _pkg.pop();
				_pkg.push(_name);
				addAsset("_sub", _name, "Class", _lineNo, _start);
				continue;
				}
			// instance.method = function()
			_names = find2(_line, pMethodFunction);
			if (_names != null) {
				if (! _pkg.empty()) _pkg.pop();
				if (! _fnc.empty()) {
					_fnc.pop();
					completeAsset(_start);
					}
				_pkg.push(_names[0]);
				_fnc.push(_names[1]);
				addAsset("_sub", _names[0], _names[1], _lineNo, _start);
				continue;
				}
			// vname = function()
			_name = find(_line, pAssignedFunction, 1);
			if (_name != null) {
				if (! _fnc.empty()) {
					_fnc.pop();
					completeAsset(_start);
					}
				_fnc.push(_name);
				addLineAsset("_sub", (String) _pkg.peek(), _name, _lineNo, _start, _end);
				continue;
				}
			// var vname = function()
			_name = find(_line, pVarFunction, 1);
			if (_name != null) {
				if (! _fnc.empty()) {
					_fnc.pop();
					completeAsset(_start);
					}
				_fnc.push(_name);
				addLineAsset("_sub", (String) _pkg.peek(), _name, _lineNo, _start, _end);
				continue;
				}
			// function fname()
			_name = find(_line, pSimpleFunction, 1);
			if (_name != null) {
				if (! _fnc.empty()) {
					_fnc.pop();
					completeAsset(_start);
					}
				_fnc.push(_name);
				addLineAsset("_sub", (String) _pkg.peek(), _name, _lineNo, _start, _end);
				continue;
				}
			// block comment: start
			if (! _inComment && _line.indexOf("/*") != -1 ) { 
				completeAsset(_end);
				if (! _fnc.empty()) _name = (String) _fnc.peek();
				if (_name == null)  _name = (String) _pkg.peek();
				_inComment = true;
				addCommentAsset(_name, _lineNo, _start);
				}
			}
	 }
}
