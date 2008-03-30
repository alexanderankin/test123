/* % [{
% (C) Copyright 2008 Nicolas Carranza and individual contributors.
% See the CandyFolds-copyright.txt file in the CandyFolds distribution for a full
% listing of individual contributors.
%
% This file is part of CandyFolds.
%
% CandyFolds is free software: you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation, either version 3 of the License,
% or (at your option) any later version.
%
% CandyFolds is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License
% along with CandyFolds.  If not, see <http://www.gnu.org/licenses/>.
% }] */
package candyfolds;

import java.util.ArrayList;
import java.util.List;
import candyfolds.config.FoldConfig;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;

final class LineInfo {
	private final TextAreaExt textAreaExt;

	private int line;

	final List<Integer> indents=new ArrayList<Integer>();
	final List<Integer> lines=new ArrayList<Integer>(); // lines where the indents belong (1:1).
	final List<Integer> foldLevels=new ArrayList<Integer>();
	final List<FoldConfig> foldConfigs=new ArrayList<FoldConfig>();

	LineInfo(TextAreaExt textAreaExt) {
		this.textAreaExt=textAreaExt;
	}

	private void clear() {
		indents.clear();
		lines.clear();
		foldLevels.clear();
		foldConfigs.clear();
	}

	void eval(Buffer buffer,
	          int line) {
		eval(buffer, line, null);
	}

	private void copyFrom(LineInfo lineInfo) {
		clear();
		line=lineInfo.line;
		indents.addAll(lineInfo.indents);
		lines.addAll(lineInfo.lines);
		foldLevels.addAll(lineInfo.foldLevels);
		foldConfigs.addAll(lineInfo.foldConfigs);
	}

	void eval(Buffer buffer,
	          int line,
	          LineInfo stopLineInfo) {
		//long startTime=System.nanoTime();
		if(stopLineInfo==null) {
			eval(buffer, line, 0);
			//Log.log(Log.NOTICE, this, "line evaluation time: "+(System.nanoTime()-startTime)+" NO "+line);
			return ;
		}
		if(stopLineInfo.line==line) {
			copyFrom(stopLineInfo);
			//Log.log(Log.NOTICE, this, "No need to eval "+line);
			return ;
		}
		if(stopLineInfo.line>line)
			throw new AssertionError("stopLineInfo was "+stopLineInfo+", and requested for "+line);
		eval(buffer, line, stopLineInfo.line+1);
		append(stopLineInfo);
		//Log.log(Log.NOTICE, this, "line evaluation time: "+(System.nanoTime()-startTime)+" O "+line);
	}

	private void eval(Buffer buffer,
	    int line,
	    int stopLine) {
		clear();
		this.line=line;
		int indent;
		int foldLevel, lastCatchedFoldLevel=Integer.MAX_VALUE, lastCatchedIndent=0;
		for(int i=line; i>=stopLine; i--) {
			foldLevel=buffer.getFoldLevel(i);
			if(foldLevel< lastCatchedFoldLevel) {
				indent=foldLevel==0? 0: buffer.getCurrentIndentForLine(i, null);
				//v check if this is an indented second (or third, or...) line of a fold... like a funcion with its parameters indented:
				if(foldLevel!=0)
					for(int j=i-1, upperLineIndent; j>=stopLine && buffer.getFoldLevel(j)==foldLevel; j--) {
						upperLineIndent=buffer.getCurrentIndentForLine(j, null); // optim? this is a second call
						if(indent!=0 && upperLineIndent==0) // optim
							break;
						if( indent==0 || (upperLineIndent!=0 && upperLineIndent<indent) ) {//indent==0 fixes when fold are made on empty lines
							indent=upperLineIndent;
							i=j;
						}
					}
				//^
				lastCatchedFoldLevel=foldLevel;
				if(lastCatchedIndent>0 && indent>lastCatchedIndent) { // los indent siempre van disminuyendo si es que no se trataba de un indent en 0 que era una linea vacia
					continue;
				}
				lastCatchedIndent=indent;
				indents.add(indent);
				lines.add(i);
				foldLevels.add(foldLevel);
			}
			if(foldLevel<=1) // change this to zero if you want the top level too... put this as pref?
				break;
		}
		removeRepeated();
		selectFoldConfigs(buffer);
	}

	private void removeRepeated() {
		int indent;
		for(int i=0, size=indents.size(); i<size;) {
			indent=indents.get(i);
			if(/*indent==0 ||*/ // se dejan pues puede ser el primero. el primero se ignora. (pensar en caso de lineas vacias dentro de fold)
			  (i+1<size && indent==indents.get(i+1)) ) {
				indents.remove(i);
				lines.remove(i+1); // remove the next line info
				foldLevels.remove(i+1);
				// foldConfigs.remove(i+1); has not been fulled!
				size--;
			} else
				i++;
		}
	}

	private void selectFoldConfigs(Buffer buffer) {
		for(int i=0, size=indents.size(); i<size; i++ )
			selectFoldConfig(buffer, lines.get(i), i);
	}

	private void selectFoldConfig(Buffer buffer, int line, int lineIndex) {
		FoldConfig foldConfig;
		if(indents.get(lineIndex)==0) {
			foldConfig=null;
		} else {
			buffer.getLineText(line, textAreaExt.segment);
			foldConfig=textAreaExt.getModeConfig().evalFoldConfig(textAreaExt.segment);
		}
		foldConfigs.add(lineIndex, foldConfig);
	}


	private void append(LineInfo extLineInfo) {
		int lastFoldLevel=foldLevels.get(foldLevels.size()-1); // I assume that I have always at least one element
		int prevIndent=indents.get(indents.size()-1);
		for(int i=0, size=extLineInfo.foldLevels.size(); i<size; i++) {
			int extFoldLevel=extLineInfo.foldLevels.get(i);
			if(extFoldLevel>=lastFoldLevel)
				continue;
			int extIndent=extLineInfo.indents.get(i);
			if(extIndent==0 || extIndent==prevIndent) // extIndent==prevIndent may be true only in the first iteration
				continue;
			prevIndent=extIndent;
			indents.add(extIndent);
			lines.add(extLineInfo.lines.get(i));
			foldLevels.add(extFoldLevel);
			foldConfigs.add(extLineInfo.foldConfigs.get(i));
		}
	}
}
