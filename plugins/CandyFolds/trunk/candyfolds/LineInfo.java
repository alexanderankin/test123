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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.Segment;

import candyfolds.config.ModeConfig;
import candyfolds.config.StripConfig;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;

final class LineInfo {
	static final Logger L=Logger.getLogger(LineInfo.class.getName());
	//static { L.setLevel(Level.ALL); }

	private final TextAreaExt textAreaExt;
	private int line;
	private final IndentationInfo lineIndentationInfo=new IndentationInfo();
	private final List<IndentationInfo> indentationInfos=new LinkedList<IndentationInfo>();
	private final List<Integer> lines=new LinkedList<Integer>(); // lines where the indentationInfos belong (1:1).
	private final List<StripConfig> stripConfigs=new LinkedList<StripConfig>();

	static class IndentationInfo{
		private int indent;
		private int spaceCount;
		private int tabCount;

		private IndentationInfo(){}

		private IndentationInfo(int indent, TextAreaExt textAreaExt, int line){
			setIndent(indent);
			countSpacesAndTabs(textAreaExt, line);
		}

		private void clear(){
			indent=spaceCount=tabCount=0;
		}

		private void setIndent(int indent){
			this.indent=indent;
		}

		int getIndent() {
			return indent;
		}

		void countSpacesAndTabs(TextAreaExt textAreaExt, int line){
			Segment seg = textAreaExt.segment;
			Buffer buffer=textAreaExt.getBuffer();
			buffer.getLineText(line,seg);
		loop:
			for(int i = 0; i < seg.count; i++)
			{
				char c = seg.array[seg.offset + i];
				switch(c)
				{
				case ' ':
					spaceCount++;
					break;
				case '\t':
					tabCount++;
					break;
				default:
					break loop;
				}
			}
		}

		int evalXOffset(int spaceWidth, int tabSpaceWidth, int tabSize){
			return (int)(0.4f*spaceWidth+spaceCount*spaceWidth+tabCount*tabSize*tabSpaceWidth);
		}
	}

	LineInfo(TextAreaExt textAreaExt) {
		this.textAreaExt=textAreaExt;
	}

	int getLine(int i){
		return lines.get(i);
	}

	int getIndentationInfosSize(){
		return indentationInfos.size();
	}

	IndentationInfo getIndentationInfo(int index){
		return indentationInfos.get(index);
	}

	StripConfig getStripConfig(int index){
		return stripConfigs.get(index);
	}

	private void clear() {
		line=0;
		lineIndentationInfo.clear();
		indentationInfos.clear();
		lines.clear();
		stripConfigs.clear();
	}

	void eval(TextAreaExt textAreaExt, final int line){
		eval(textAreaExt, line, null);
	}

	void eval(TextAreaExt textAreaExt, final int line, LineInfo upLineInfo){
		clear();
		Buffer buffer=textAreaExt.getBuffer();
		this.line=line;
		int upLine= upLineInfo==null? 0: upLineInfo.line+1;

		int calcLine;
		int lineIndent;
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
		lineIndentationInfo.setIndent(lineIndent);
		lineIndentationInfo.countSpacesAndTabs(textAreaExt, line);

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
			indentationInfos.add(new IndentationInfo(indent, textAreaExt, calcLine));
			calcLine=addStripConfig(buffer, calcLine, indent);
			lines.add(calcLine);
			if(lastCaughtIndent==0) // optimization
				break;
		}

		if(upLineInfo!=null
			 && lastCaughtIndent>0 // optimization
			){
			for(int i=0; i<upLineInfo.indentationInfos.size(); i++){
				IndentationInfo indentationInfo=upLineInfo.getIndentationInfo(i);
				indent=indentationInfo.indent;
				if(indent>=lastCaughtIndent ||
					 indent>lineIndent)
					continue;
				lastCaughtIndent=indent;
				//L.fine("line="+line+", adding upLineInfo indent="+indent);
				indentationInfos.add(indentationInfo);
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