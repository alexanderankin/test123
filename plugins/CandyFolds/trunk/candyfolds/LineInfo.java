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

import candyfolds.config.StripConfig;
import candyfolds.config.ModeConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Segment;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;

final class LineInfo {
	static final Logger L=Logger.getLogger(LineInfo.class.getName());
	//static { L.setLevel(Level.ALL); }

	private final TextAreaExt textAreaExt;
	private int line;
	private int lineIndent;
	private final List<Integer> indents=new ArrayList<Integer>();
	private final List<Integer> lines=new ArrayList<Integer>(); // lines where the indents belong (1:1).
	private final List<StripConfig> stripConfigs=new ArrayList<StripConfig>();

	LineInfo(TextAreaExt textAreaExt) {
		this.textAreaExt=textAreaExt;
	}

	/*
	int getLine(){
		return line;
	}
	*/
	
	int getLine(int i){
		return lines.get(i);
	}

	int getIndentsSize(){
		return indents.size();
	}

	int getIndent(int indentIndex){
		return indents.get(indentIndex);
	}

	StripConfig getStripConfig(int indentIndex){
		return stripConfigs.get(indentIndex);
	}

	private void clear() {
		line=lineIndent=0;
		indents.clear();
		lines.clear();
		stripConfigs.clear();
	}

	void eval(Buffer buffer, final int line){
		eval(buffer, line, null);
	}

	void eval(Buffer buffer, final int line, LineInfo upLineInfo){
		clear();
		this.line=line;
		int upLine= upLineInfo==null? 0: upLineInfo.line+1;
		
		int calcLine;
		if(isEmptySegment(buffer, line)){
			int firstIndentDown=-1, firstIndentDownLine=line;
			for(int i=line+1, lineCount=buffer.getLineCount(); i<lineCount; i++){ // TODO: optimize for performance on large files with a lot of empty lines...
				if(isEmptySegment(buffer, i))
					continue;
				firstIndentDown=buffer.getCurrentIndentForLine(i, null);
				firstIndentDownLine=i;
				break;
			}
			if(firstIndentDown!=-1){
				calcLine=firstIndentDownLine;
				lineIndent=firstIndentDown;
			}else{
				calcLine=line;
				lineIndent=0;
			}
		}
		else{
			calcLine=line;
			lineIndent=buffer.getCurrentIndentForLine(line, null);
		}

		int indent, lastCaughtIndent=Integer.MAX_VALUE;
		for(; calcLine>=upLine; calcLine--) {
			indent=buffer.getCurrentIndentForLine(calcLine, null);
			if(indent>=lastCaughtIndent ||
				 indent>lineIndent)
				continue;
			if(isEmptySegment(buffer, calcLine))
				continue;
			lastCaughtIndent=indent;
			//L.fine("adding indent="+indent+" on line="+calcLine);
			indents.add(indent);
			calcLine=addStripConfig(buffer, calcLine, indent);
			lines.add(calcLine);
			if(lastCaughtIndent==0) // optimization
				break;
		}

		if(upLineInfo!=null
			 && lastCaughtIndent>0 // optimization
			){
			for(int i=0; i<upLineInfo.indents.size(); i++){
				indent=upLineInfo.indents.get(i);
				if(indent>=lastCaughtIndent ||
					 indent>lineIndent)
					continue;
				lastCaughtIndent=indent;
				//L.fine("line="+line+", adding upLineInfo indent="+indent);
				indents.add(indent);
				lines.add(upLineInfo.lines.get(i));
				stripConfigs.add(upLineInfo.stripConfigs.get(i));
			}
		}
	}

	private boolean isEmptySegment(Buffer buffer, int line){
		buffer.getLineText(line, textAreaExt.segment);
		return isEmptySegment(textAreaExt.segment);
	}

	private static boolean isEmptySegment(Segment segment){
		for(int i=segment.length(); --i>=0; )
			if(!Character.isWhitespace(segment.charAt(i)))
				return false;
		return true;
	}

	private int addStripConfig(Buffer buffer, int line, int lineIndent){
		line=prepareSegmentForStripConfig(buffer, line, lineIndent);
		StripConfig stripConfig=textAreaExt.getModeConfig().evalStripConfig(textAreaExt.segment);
		stripConfigs.add(stripConfig);
		return line;
	}

	private int prepareSegmentForStripConfig(final Buffer buffer, final int line, final int lineIndent){
		final Segment segment=textAreaExt.segment;
		ModeConfig modeConfig=textAreaExt.getModeConfig();
		if(modeConfig.getUseIgnoreLineRegex()){
			for( int prevLine=line; prevLine>=0; prevLine-- ){ // TODO: use a limit instead of 0?
				buffer.getLineText(prevLine, segment);
				int segmentContentLength=evalSegmentContentLength(segment);
				if(segmentContentLength==0)
					break;
				int prevLineIndent=buffer.getCurrentIndentForLine(prevLine, null);
				if(prevLineIndent<lineIndent)
					break;
				if(prevLineIndent>lineIndent)
					continue;
				// -> here: lineIndent==prevLineIndent
				if(modeConfig.ignoreLineRegex.matches(segment))
					continue;
				// -> the segment is ready on the desired prevLine:
				return prevLine;
			}
		}
		buffer.getLineText(line, segment);
		return line;
	}

	private static int evalSegmentContentLength(Segment segment){
		int contentLength=0;
		for(int i=segment.length(); --i>=0; )
			if(!Character.isWhitespace(segment.charAt(i)))
				contentLength++;
		return contentLength;
	}
}