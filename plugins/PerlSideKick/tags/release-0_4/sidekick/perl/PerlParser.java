/*
 * PerlParser.java
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
package sidekick.perl;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import java.util.regex.Pattern;

import errorlist.*;
import sidekick.*;

import sidekick.SourceParser;

/**
 * PerlParser: parses source and builds a sidekick structure tree
 * Parsers are based on regular expressions and will therefore 
 * not able to correctly parse irregular source 
 *
 * @author     Martin Raspe
 * @created    Oct 15, 2005
 * @modified   $Date$ by $Author$
 * @version    $Revision$
 */
public class PerlParser extends SourceParser {
/**	 * Constructs a new SourceParser object
	 *
	 * @param name See sidekick.SidekickParser.
	 */
	 Pattern pPodHead	= Pattern.compile("^=head1\\s+(.*)");
	 Pattern pPodDirective	= Pattern.compile("^=(\\w+)");
	 Pattern pPackageSub	= Pattern.compile("^sub\\s+([\\w:]+)::(\\w+)");
	 Pattern pSub		= Pattern.compile("^sub\\s+(\\w+)");
	 Pattern pUse		= Pattern.compile("^(use|require)\\s+([\\w:.]+)");
	 Pattern pPackage	= Pattern.compile("^package\\s+([\\w:]+)");

	 public PerlParser() {
		 super("perl", PerlSideKickPlugin.class);
		 LINE_COMMENT	= "#";
		 COMMENT	= "POD";
		 MAIN		= "main";
		 USE		= "use";
	 }

	 protected void parseBuffer(Buffer buffer, DefaultErrorSource errorSource) {
		boolean _inComment = false;
		String _line;
		String _name;
		String[] _names;
		String _package = MAIN;
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
			// POD: end
			if (_inComment && _line.startsWith("=cut")) { 
				_inComment = false;
				completeAsset(_end);
				continue;
				}
			// POD: headline 1
			_name = find(_line, pPodHead, 1);
			if (_name != null) {
				_inComment = true;
				addCommentAsset(_name, _lineNo, _start);
				continue;
				}
			if (_inComment) continue;
			// POD: any other directive
			_name = find(_line, pPodDirective, 1); 
			if (_name != null) {
				_inComment = true;
				continue;
				}
			// sub (fully qualified with package)
			_names = find2(_line, pPackageSub);
			if (_names != null) {
				addAsset("_sub", _names[0], _names[1], _lineNo, _start);
				continue;
				}
			// sub (simple)
			_name = find(_line, pSub, 1);
			if (_name != null) {
				addAsset("_sub", _package, _name, _lineNo, _start);
				continue;
				}
			// use/require
			_name = find(_line, pUse, 2);
			if (_name != null) {
				addLineAsset("_use", _package, _name, _lineNo, _start, _end);
				continue;
				}
			// explicit package
			_name = find(_line, pPackage, 1);
			if (_name != null) {
				// remember the new package name
				_package = _name;
				addPackageAsset(_name, _lineNo, _start);
				continue;
				}
			}
	 }

}
