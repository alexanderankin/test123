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

import candyfolds.config.ModeConfig;
import candyfolds.config.StripConfig;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.geom.Line2D;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.Set;
import javax.swing.text.Segment;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.BufferListener;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;

final class TextAreaExt
	extends TextAreaExtension {
	static final Logger L=Logger.getLogger(TextAreaExt.class.getName());
	static { L.setLevel(Level.ALL); }

	final CandyFoldsPlugin foldsPlugin;
	final EditPane editPane;
	private final TextAreaPainter painter;

	private LineInfo previousLineInfo=new LineInfo(this);
	private LineInfo currentLineInfo=new LineInfo(this);
	//private final LineInfo toolTipLineInfo=new LineInfo(this);

	private static final int jEditMajorVersion=Integer.parseInt(jEdit.getBuild().substring(0,2));
	private final IndentMetrics indentMetrics=new IndentMetrics();
	private final class IndentMetrics {

		private FontMetrics fontMetrics;
		int spaceWidth;
		int tabSpaceWidth;
		int lineHeight;
		BasicStroke barStroke;
		Stroke thinBarStroke;

		void forceNextUpdate(){
			fontMetrics=null;
		}

		void update() {
			FontMetrics fontMetrics=painter.getFontMetrics();
			if(this.fontMetrics==fontMetrics)
				return;
			this.fontMetrics=fontMetrics;
			spaceWidth = fontMetrics.charWidth(' ');
			if(jEditMajorVersion<5)
				tabSpaceWidth=spaceWidth;
			else{
				//v taken from org.gjt.sp.jedit.textarea.TextArea. ToDo: request a public method getCharWidth on this class to avoid code duplication?
				String tabSpaceCharWidthSample = " 0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
				tabSpaceWidth = (int)Math.round(
													painter.getFont().getStringBounds(tabSpaceCharWidthSample, painter.getFontRenderContext())
													.getWidth()
													/ tabSpaceCharWidthSample.length());
				//^
				// v weird, this other method does not get always the same result as the one above:
				//tabSpaceWidth = (int)Math.round(fontMetrics.charsWidth(tabSpaceCharWidthSample, 0, tabSpaceCharWidthSample.length)
				//							/tabSpaceCharWidthSample.length);
				//^
			}
			lineHeight=fontMetrics.getHeight();
			barStroke=new BasicStroke(spaceWidth/2.3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
			if(modeConfig.getUseThinStripesPixelSize())
				thinBarStroke=new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
			else{
				float factor=modeConfig.getDrawThinStripesFactor();
				float thinBarStrokeWidth=barStroke.getLineWidth()*factor;
				if(thinBarStrokeWidth<1)
					thinBarStrokeWidth=1;
				thinBarStroke=new BasicStroke( thinBarStrokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
			}
		}
	}

	private ModeConfig modeConfig;
	private String modeConfigName; // use it separated from modeConfig because modeConfig can be null!
	private final Line2D.Float line2D=new Line2D.Float();
	final Segment segment=new Segment();
	private final Rectangle rect=new Rectangle();

	TextAreaExt(CandyFoldsPlugin foldsPlugin, EditPane editPane) {
		this.foldsPlugin=foldsPlugin;
		this.editPane=editPane;
		this.painter=editPane.getTextArea().getPainter();
		painter.addExtension(this);
	}

	void clearModeConfig() {
		modeConfigName=null;
		modeConfig=null;
		indentMetrics.forceNextUpdate();
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

	@Override
	public void paintValidLine(Graphics2D g,
			int screenLine,
			int physicalLine,
			int start,
			int end,
			int y) {
	}

	@Override
	public synchronized void paintScreenLineRange(Graphics2D g,
			int firstLine,
			int lastLine,
			int[] physicalLines,
			int[] start,
			int[] end,
			int y,
			int lineHeight){
		//Log.log(Log.NOTICE, this, "painting range: firstLine="+firstLine+", lastLine=" + lastLine + ", y=" + y+"lineHeight="+lineHeight);
		//long nanoTime=System.nanoTime();
		Buffer buffer = getBuffer();
		setModeConfig(buffer);
		if(!getModeConfig().getEnabled())
			return;

		for(int i = 0; i < physicalLines.length; i++) {
			int line=physicalLines[i];
			if(line==-1)
				continue;
			if(i==0)
				currentLineInfo.eval(this, line);
			else
				currentLineInfo.eval(this, line, previousLineInfo);
			drawLineIndents(buffer, currentLineInfo, g, y);
			LineInfo swap=previousLineInfo;
			previousLineInfo=currentLineInfo;
			currentLineInfo=swap;
			y += lineHeight;
		}
		//Log.log(Log.NOTICE, this, "done painting range");
		//L.fine("new painting time="+(System.nanoTime()-nanoTime));
	}

	Buffer getBuffer(){
		return editPane.getBuffer();
	}

	private void drawLineIndents(Buffer buffer, LineInfo lineInfo, Graphics2D g, int y) {

		indentMetrics.update();
		/*
		FontMetrics fontMetrics=painter.getFontMetrics();
		fontMetricsInfo.setFontMetrics(fontMetrics);
		*/

		int x;
		int indent;
		int horizontalOffset=editPane.getTextArea().getHorizontalOffset();

		int tabSize=buffer.getTabSize();

		Stroke gStroke=g.getStroke();
		g.setStroke(getModeConfig().getDrawThinStripes()? indentMetrics.thinBarStroke: indentMetrics.barStroke);
		for (int i =lineInfo.getIndentationInfosSize();--i>=1;) {// ATTENTION: the first is omitted
			LineInfo.IndentationInfo indentationInfo=lineInfo.getIndentationInfo(i);
			indent =indentationInfo.getIndent();
			if(indent==0 && !modeConfig.getShowStripOn0Indent())
				continue;
			//Log.log(Log.NOTICE, this, "painting indent="+indent);
			/*
			StripConfig stripConfig=lineInfo.getStripConfig(i);
			Stroke stroke=stripConfig.fontMetricsInfo.getStroke(fontMetrics);
			if(stroke==null)
			 continue;
			g.setStroke(stroke);
			Color color=stripConfig.getColor();
			*/
			Color color=lineInfo.getStripConfig(i).getColor();
			if(color.getAlpha()==0)
				continue;
			g.setColor(color);

			x=horizontalOffset+indentationInfo.evalXOffset(indentMetrics.spaceWidth, indentMetrics.tabSpaceWidth, tabSize);
			g.drawLine(x, y,x, y+indentMetrics.lineHeight+painter.getLineExtraSpacing());
		}
		g.setStroke(gStroke);
	}

	// DISABLED: not really useful?
	/*
	@Override
	public synchronized String getToolTipText(int x, int y) {
	 JEditTextArea ta=editPane.getTextArea();
	 Buffer buffer = editPane.getBuffer();

	 int offset = ta.xyToOffset(x, y, false);
	 if ((offset == -1) || (offset >= buffer.getLength())) {
	  return null;
	 }

	 int physicalLine = ta.getLineOfOffset(offset);
	 toolTipLineInfo.eval(buffer, physicalLine);

	 fontMetricsInfo.setFontMetrics(painter.getFontMetrics());
	 rect.y=ta.offsetToXY(physicalLine, 0).y;
	 rect.width=2*fontMetricsInfo.spaceWidth;
	 rect.height=fontMetricsInfo.lineHeight;
	 int indent;
	 int horizontalOffset=ta.getHorizontalOffset();
	 for (int i =toolTipLineInfo.getIndentsSize();--i>=1;) { // ignore the first. Iteration from outer to inner fold. the first (i=0) is right over the first char of the line.
	  indent =toolTipLineInfo.getIndent(i);
	  if(indent==0 && !getModeConfig().getShowStripOn0Indent())
	   continue;
	  rect.x= (int)(indent* fontMetricsInfo.spaceWidth+horizontalOffset);
	  if(rect.contains(x, y)){
	   //int foldLevel=toolTipLineInfo.foldLevels.get(i);
	   String stripConfigName=toolTipLineInfo.getStripConfig(i).getName();
	   int line=toolTipLineInfo.getLine(i);
	   buffer.getLineText(line, segment);
	   return "<html><b>"+(line+1)+"</b>  <u>"+stripConfigName+"</u>: "+segment.toString().trim()+"</html>";
	  }
	 }
	 return null;
}
	*/
}