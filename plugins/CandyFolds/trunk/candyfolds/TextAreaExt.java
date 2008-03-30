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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.geom.Line2D;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.swing.text.Segment;
import candyfolds.config.ModeConfig;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

final class TextAreaExt
	extends TextAreaExtension {

	private static final class FontMetricsInfo {
		private FontMetrics fontMetrics;
		int spaceWidth;
		int lineHeight;
		Stroke barStroke;

		void setFontMetrics(FontMetrics fontMetrics) {
			if(this.fontMetrics==fontMetrics)
				return;
			this.fontMetrics=fontMetrics;
			spaceWidth = fontMetrics.charWidth(' ');
			lineHeight=fontMetrics.getHeight();
			barStroke=new BasicStroke(spaceWidth/2.3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		}

	}

	final CandyFoldsPlugin foldsPlugin;
	final EditPane editPane;
	private final TextAreaPainter painter;

	private final LineInfo lineInfo=new LineInfo(this);
	private final LineInfo firstInRangeLineInfo=new LineInfo(this);

	private final FontMetricsInfo fontMetricsInfo=new FontMetricsInfo();
	private ModeConfig modeConfig;
	private String modeConfigName; // use it separated from modeConfig because modeConfig can be null!
	private final Line2D.Float line2D=new Line2D.Float();
	final Segment segment=new Segment();

	TextAreaExt(CandyFoldsPlugin foldsPlugin, EditPane editPane) {
		this.foldsPlugin=foldsPlugin;
		this.editPane=editPane;
		this.painter=editPane.getTextArea().getPainter();
		painter.addExtension(this);
	}

	void clearModeConfig() {
		modeConfigName=null;
		modeConfig=null;
	}

	private void setModeConfig(Buffer buffer) {
		if(modeConfigName!=buffer.getMode().getName()) {
			modeConfigName=buffer.getMode().getName();
			modeConfig=foldsPlugin.getConfig().getModeConfig(modeConfigName);
			//Log.log(Log.NOTICE, this, "selected modeConfig for mode "+modeConfigName+": "+modeConfig);
		}
	}

	ModeConfig getModeConfig() {
		return modeConfig;
	}

	void remove() {
		painter.removeExtension(this);
	}

	private void drawLine(LineInfo lineInfo, Graphics2D g, int y) {
		if(lineInfo.indents.size()<=1)
			return;
		fontMetricsInfo.setFontMetrics(painter.getFontMetrics());
		Stroke gStroke=g.getStroke();
		g.setStroke(fontMetricsInfo.barStroke);
		//float x;
		int x;
		int indent;
		int horizontalOffset=editPane.getTextArea().getHorizontalOffset();
		for (int i =lineInfo.indents.size();--i>=1;) { // ignore the first. Iteration from outer to inner fold. the first is right above the first char of the line.
			indent =lineInfo.indents.get(i);
			if(indent==0)
				continue;
			g.setColor(lineInfo.foldConfigs.get(i).getColor());
			x= (int)((indent+0.4f )* fontMetricsInfo.spaceWidth+horizontalOffset);//(indent+0.4f) * fontMetricsInfo.spaceWidth;
			//line2D.setLine(x, y, x, y+fontMetricsInfo.lineHeight);
			//g.draw(line2D); this is slooow compared to drawLine (at least in my comp)!
			g.drawLine(x, y,x, y+fontMetricsInfo.lineHeight);
		}
		g.setStroke(gStroke);
	}

	@Override
	public void paintValidLine(Graphics2D g,
	    int screenLine,
	    int physicalLine,
	    int start,
	    int end,
	    int y) {
		//lineInfo.eval(editPane.getBuffer(), physicalLine);
		//drawLine(lineInfo, g, y);
	}

	@Override
	public synchronized void paintScreenLineRange(Graphics2D g,
	    int firstLine,
	    int lastLine,
	    int[] physicalLines,
	    int[] start,
	    int[] end,
	    int y,
	    int lineHeight) {
		//Log.log(Log.NOTICE, this, "painting range: firstLine="+firstLine+", lastLine=" + lastLine + ", y=" + y+"lineHeight="+lineHeight);
		Buffer buffer = editPane.getBuffer();
		setModeConfig(buffer);
		if(!getModeConfig().getEnabled())
			return;

		for(int i = 0; i < physicalLines.length; i++) {
			int screenLine = i + firstLine;
			if(physicalLines[i]==-1)
				continue;
			//Log.log(Log.NOTICE, this, "physicalLine="+physicalLines[i]+", start="+start[i]+", end="+end[i]+", lineHeight="+lineHeight);
			if(i==0) {
				firstInRangeLineInfo.eval(buffer, physicalLines[i]);
				drawLine(firstInRangeLineInfo, g, y);
			} else {
				lineInfo.eval(buffer, physicalLines[i], firstInRangeLineInfo);
				drawLine(lineInfo, g, y);
			}
			y += lineHeight;
		}
		//Log.log(Log.NOTICE, this, "done painting range");
	}
}
