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
import org.gjt.sp.util.Log;

import sidekick.enhanced.SourceParser;
import sidekick.SideKickParsedData;

import java.io.*;
import java.util.ArrayList;
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
    Pattern pClassFunction	= Pattern.compile("^((\\w+\\.)*[A-Z][\\w]*)\\s*=\\s*function");
    Pattern pMethodFunction	= Pattern.compile("^([\\w+\\.]+\\w+)\\.([\\w]+)\\s*=\\s*function");
    Pattern pPrototypeFunction	= Pattern.compile("^([\\w+\\.]+\\w+)\\.prototype\\.([\\w]+)\\s*=\\s*function");

    Pattern pAssignedFunction	= Pattern.compile("^(\\w+)\\s*=\\s*function");
    Pattern pVarFunction	= Pattern.compile("^var\\s+(\\w+)\\s*=\\s*function");
    Pattern pSimpleFunction	= Pattern.compile("^function\\s+(\\w+)");

//  var my.test.namespace.MyClass.Object = {...
//  my.test.namespace.MyClass.Object = {...
//  method_name : function ...

    Pattern pObject		= Pattern.compile("^((\\w+\\.)*[A-Z][\\w]*)\\s*=[^\\{]*\\{");
    Pattern pVarObject		= Pattern.compile("^var\\s+((\\w+\\.)*[A-Z][\\w]*)\\s*=[^\\{]*\\{");
    Pattern pObjectMethod	= Pattern.compile("^(\\w+)\\s*:\\s*function");

/**	 * Constructs a new Parser object
	 *
	 */
	public JavaScriptParser() {
		super("javascript");
		LINE_COMMENT	= "//";
		COMMENT		= "Comments";
		MAIN		= "(Window)";
		USE		= "import";
	}

/**	 * Parses the given text and returns a tree model.
	 *
	 * @param buffer The buffer to parse.
	 * @param errorSource An error source to add errors to.
	 */

	 protected void parseBuffer(Buffer buffer, DefaultErrorSource errorSource) {
        setStartLine( 0 );
        parseBuffer( buffer, buffer.getText( 0, buffer.getLength() ), errorSource );
     }

     public SideKickParsedData parse( Buffer buffer, String text, DefaultErrorSource errorSource ) {
		data = new SideKickParsedData(buffer.getName());
		packages = new PackageMap(new PackageComparator());
		commentList = new ArrayList();
        parseBuffer(buffer, text, errorSource);
		completePackageAsset(_end, _lastLineNumber);
		Log.log(Log.DEBUG, this, "parsing completed");
		buildTrees();
		Log.log(Log.DEBUG, this, "tree built");
		return data;
     }

     protected void parseBuffer(Buffer buffer, String text, DefaultErrorSource errorSource) {
		String line;
		String name;
		String pkgname;
		String[] names;
		Stack funcstack = new Stack();
		Stack pkgstack = new Stack();
        //if (startLine == 0) {
            pkgstack.push(MAIN);
        //}
		boolean in_comment = false;
		int buflen = buffer.getLength();
		int _tmp;
		for (int lineNo = startLine; lineNo < startLine + getLineCount(text); lineNo++) {
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
				addPackageAsset(name, lineNo, _start);
				addAsset(SUB_KEY, name, "(constructor)", lineNo, _start);
				continue;
				}
			name = find(line, pObject, 1);
			if (name == null)
				name = find(line, pVarObject, 1);
			// var Object = {
			if (name != null) {
				if (! pkgstack.empty()) pkgstack.pop();
				pkgstack.push(name);
				addPackageAsset(name, lineNo, _start);
				addLineAsset(SUB_KEY, name, "(assignment)", lineNo, _start, _end);
				continue;
				}
			// check class methods
			names = find2(line, pPrototypeFunction);
			if (names == null)
				names = find2(line, pMethodFunction);
			if (names != null) {
				pkgname = names[0];
				if (pkgstack.empty()) {
					pkgstack.push(pkgname);
					addPackageAsset(pkgname, lineNo, _start);
					}
				else if (! pkgname.equals((String) pkgstack.peek())) {
					pkgstack.pop();
					pkgstack.push(pkgname);
					addPackageAsset(pkgname, lineNo, _start);
					}
				if (! funcstack.empty()) {
					funcstack.pop();
					completeAsset(_start);
					}
				funcstack.push(names[1]);
				addAsset(SUB_KEY, names[0], names[1], lineNo, _start);
				continue;
				}
			// check functions
			name = find(line, pAssignedFunction, 1);
			if (name == null)
				name = find(line, pVarFunction, 1);
			if (name == null)
				name = find(line, pObjectMethod, 1);
			if (name == null)
				name = find(line, pSimpleFunction, 1);
			if (name != null) {
				if (! funcstack.empty()) {
					funcstack.pop();
					completeAsset(_start);
					}
				funcstack.push(name);
				addAsset(SUB_KEY, (String) pkgstack.peek(), name, lineNo, _start);
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

    private int getLineCount( CharSequence text ) {
        if ( text == null )
            return 0;
        BufferedReader br = new BufferedReader( new StringReader( text.toString() ) );
        String line = null;
        int count = 0;
        try {
            while ( true ) {
                line = br.readLine();
                if ( line == null )
                    break;
                ++count;
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return count;
    }
}
