/*
 * ChunkCache.java - Intermediate layer between token lists from a TokenMarker
 * and what you see on screen
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2002 Slava Pestov
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

package org.gjt.sp.jedit.textarea;

//{{{ Imports
import javax.swing.text.*;
import java.util.ArrayList;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;
//}}}

/**
 * A "chunk" is a run of text with a specified font style and color. This class
 * contains various static methods for manipulating chunks and chunk lists. It
 * also has a number of package-private instance methods used internally by the
 * text area for painting text.
 *
 * @author Slava Pestov
 * @version $Id$
 */
class ChunkCache
{
	//{{{ ChunkCache constructor
	ChunkCache(JEditTextArea textArea)
	{
		this.textArea = textArea;
		out = new ArrayList();
		noWrap = new NoWrapTokenHandler();
		softWrap = new SoftWrapTokenHandler();
	} //}}}

	//{{{ getMaxHorizontalScrollWidth() method
	int getMaxHorizontalScrollWidth()
	{
		int max = 0;
		for(int i = 0; i < lineInfo.length; i++)
		{
			LineInfo info = lineInfo[i];
			if(info.chunksValid && info.width > max)
				max = info.width;
		}
		return max;
	} //}}}

	//{{{ getScreenLineOfOffset() method
	int getScreenLineOfOffset(int line, int offset)
	{
		if(line < textArea.getFirstPhysicalLine())
		{
			return -1;
		}
		else if(line > textArea.getLastPhysicalLine())
		{
			return -1;
		}
		else if(!textArea.softWrap)
		{
			return textArea.physicalToVirtual(line)
				- textArea.getFirstLine();
		}
		else
		{
			int screenLine;

			if(line == lastScreenLineP)
			{
				LineInfo last = lineInfo[lastScreenLine];

				if(offset >= last.offset
					&& offset < last.offset + last.length)
				{
					updateChunksUpTo(lastScreenLine);
					return lastScreenLine;
				}
			}

			screenLine = -1;

			// Find the screen line containing this offset
			for(int i = 0; i < lineInfo.length; i++)
			{
				updateChunksUpTo(i);

				LineInfo info = getLineInfo(i);
				if(info.physicalLine > line)
				{
					// line is invisible?
					if(i == 0)
						screenLine = 0;
					else
						screenLine = i - 1;
					break;
				}
				else if(info.physicalLine == line)
				{
					if(offset >= info.offset
						&& offset < info.offset + info.length)
					{
						screenLine = i;
						break;
					}
				}
			}

			if(screenLine == -1)
				return -1;
			else
			{
				lastScreenLineP = line;
				lastScreenLine = screenLine;

				return screenLine;
			}
		}
	} //}}}

	//{{{ recalculateVisibleLines() method
	void recalculateVisibleLines()
	{
		lineInfo = new LineInfo[textArea.getVisibleLines() + 1];
		for(int i = 0; i < lineInfo.length; i++)
			lineInfo[i] = new LineInfo();

		lastScreenLine = lastScreenLineP = -1;
	} //}}}

	//{{{ setFirstLine() method
	void setFirstLine(int firstLine)
	{
		if(textArea.softWrap || Math.abs(firstLine - this.firstLine) >= lineInfo.length)
		{
			for(int i = 0; i < lineInfo.length; i++)
			{
				lineInfo[i].chunksValid = false;
			}
		}
		else if(firstLine > this.firstLine)
		{
			System.arraycopy(lineInfo,firstLine - this.firstLine,
				lineInfo,0,lineInfo.length - firstLine
				+ this.firstLine);

			for(int i = lineInfo.length - firstLine
				+ this.firstLine; i < lineInfo.length; i++)
			{
				lineInfo[i] = new LineInfo();
			}
		}
		else if(this.firstLine > firstLine)
		{
			System.arraycopy(lineInfo,0,lineInfo,this.firstLine - firstLine,
				lineInfo.length - this.firstLine + firstLine);

			for(int i = 0; i < this.firstLine - firstLine; i++)
			{
				lineInfo[i] = new LineInfo();
			}
		}

		lastScreenLine = lastScreenLineP = -1;
		this.firstLine = firstLine;
	} //}}}

	//{{{ invalidateAll() method
	void invalidateAll()
	{
		for(int i = 0; i < lineInfo.length; i++)
		{
			lineInfo[i].chunksValid = false;
		}

		lastScreenLine = lastScreenLineP = -1;
	} //}}}

	//{{{ invalidateChunksFrom() method
	void invalidateChunksFrom(int screenLine)
	{
		for(int i = screenLine; i < lineInfo.length; i++)
		{
			lineInfo[i].chunksValid = false;
		}

		lastScreenLine = lastScreenLineP = -1;
	} //}}}

	//{{{ invalidateChunksFromPhys() method
	void invalidateChunksFromPhys(int physicalLine)
	{
		for(int i = 0; i < lineInfo.length; i++)
		{
			if(lineInfo[i].physicalLine >= physicalLine)
			{
				invalidateChunksFrom(i);
				break;
			}
		}
	} //}}}

	//{{{ lineToChunkList() method
	void lineToChunkList(int physicalLine, ArrayList out)
	{
		if(textArea.softWrap)
		{
			TextAreaPainter painter = textArea.getPainter();
			Buffer buffer = textArea.getBuffer();

			buffer.getLineText(physicalLine,textArea.lineSegment);
			softWrap.init(textArea.lineSegment,painter.getStyles(),
				painter.getFontRenderContext(),
				painter,out,textArea.wrapMargin);
			buffer.markTokens(physicalLine,softWrap);
		}
		else
		{
			Chunk chunks = lineToChunkList(physicalLine);
			if(chunks != null)
				out.add(chunks);
		}
	} //}}}

	//{{{ lineToChunkList() method
	Chunk lineToChunkList(int physicalLine)
	{
		TextAreaPainter painter = textArea.getPainter();
		Buffer buffer = textArea.getBuffer();

		buffer.getLineText(physicalLine,textArea.lineSegment);
		noWrap.init(textArea.lineSegment,painter.getStyles(),
			painter.getFontRenderContext(),
			painter);
		buffer.markTokens(physicalLine,noWrap);
		return noWrap.getChunks();
	} //}}}

	//{{{ updateChunksUpTo() method
	void updateChunksUpTo(int lastScreenLine)
	{
		if(!textArea.softWrap)
			return;

		if(lineInfo[lastScreenLine].chunksValid)
			return;

		int firstScreenLine = 0;

		for(int i = lastScreenLine; i >= 0; i--)
		{
			if(lineInfo[i].chunksValid)
			{
				firstScreenLine = i + 1;
				break;
			}
		}

		int physicalLine;

		if(firstScreenLine == 0)
		{
			physicalLine = textArea.getFirstPhysicalLine();
		}
		else
		{
			int prevPhysLine = lineInfo[
				firstScreenLine - 1]
				.physicalLine;
			if(prevPhysLine == -1)
				physicalLine = -1;
			else
			{
				physicalLine = textArea
					.getFoldVisibilityManager()
					.getNextVisibleLine(prevPhysLine);
			}
		}

		// Note that we rely on the fact that when a physical line is
		// invalidated, all screen lines/subregions of that line are
		// invalidated as well. See below comment for code that tries
		// to uphold this assumption.

		out.clear();

		int offset = 0;
		int length = 0;

		for(int i = firstScreenLine; i <= lastScreenLine; i++)
		{
			LineInfo info = lineInfo[i];

			Chunk chunks;

			if(out.size() == 0)
			{
				if(physicalLine != -1 && i != firstScreenLine)
				{
					physicalLine = textArea.getFoldVisibilityManager()
						.getNextVisibleLine(physicalLine);
				}

				if(physicalLine == -1)
				{
					info.chunks = null;
					info.chunksValid = true;
					info.physicalLine = -1;
					continue;
				}

				lineToChunkList(physicalLine,out);

				info.firstSubregion = true;

				if(out.size() == 0)
				{
					chunks = null;
					offset = 0;
					length = 1;
				}
				else
				{
					chunks = (Chunk)out.get(0);
					out.remove(0);
					offset = 0;
					if(out.size() != 0)
						length = ((Chunk)out.get(0)).offset - offset;
					else
						length = textArea.getLineLength(physicalLine) - offset + 1;
				}
			}
			else
			{
				info.firstSubregion = false;

				chunks = (Chunk)out.get(0);
				out.remove(0);
				offset = chunks.offset;
				if(out.size() != 0)
					length = ((Chunk)out.get(0)).offset - offset;
				else
					length = textArea.getLineLength(physicalLine) - offset + 1;
			}

			boolean lastSubregion = (out.size() == 0);

			if(i == lastScreenLine
				&& lastScreenLine != lineInfo.length - 1)
			{
				/* If this line has become longer or shorter
				 * (in which case the new physical line number
				 * is different from the cached one) we need to:
				 * - continue updating past the last line
				 * - advise the text area to repaint
				 * On the other hand, if the line wraps beyond
				 * lastScreenLine, we need to keep updating the
				 * chunk list to ensure proper alignment of
				 * invalidation flags (see start of method) */
				if(info.physicalLine != physicalLine
					|| info.lastSubregion != lastSubregion)
				{
					lastScreenLine++;
					needFullRepaint = true;
				}
				else if(out.size() != 0)
					lastScreenLine++;
			}

			info.physicalLine = physicalLine;
			info.lastSubregion = lastSubregion;
			info.offset = offset;
			info.length = length;
			info.chunks = chunks;
			info.chunksValid = true;
		}
	} //}}}

	//{{{ getLineInfo() method
	LineInfo getLineInfo(int screenLine)
	{
		LineInfo info = lineInfo[screenLine];

		if(textArea.softWrap)
		{
			if(!info.chunksValid)
				Log.log(Log.ERROR,this,"Not up-to-date: " + screenLine);
			return info;
		}
		else
		{
			if(!info.chunksValid)
			{
				int virtLine = screenLine + firstLine;
				if(virtLine >= textArea.getVirtualLineCount())
				{
					info.chunks = null;
					info.chunksValid = true;
					info.physicalLine = -1;
				}
				else
				{
					info.physicalLine = textArea.getFoldVisibilityManager()
						.virtualToPhysical(virtLine);


					info.chunks = lineToChunkList(info.physicalLine);
					info.chunksValid = true;

					info.firstSubregion = true;
					info.lastSubregion = true;
					info.offset = 0;
					info.length = textArea.getLineLength(info.physicalLine) + 1;
				}
			}

			return info;
		}
	} //}}}

	//{{{ getLineInfosForPhysicalLine() method
	public LineInfo[] getLineInfosForPhysicalLine(int physicalLine)
	{
		out.clear();
		lineToChunkList(physicalLine,out);

		if(out.size() == 0)
			out.add(null);

		LineInfo[] returnValue = new LineInfo[out.size()];

		for(int i = 0; i < out.size(); i++)
		{
			Chunk chunks = (Chunk)out.get(i);
			LineInfo info = new LineInfo();
			info.physicalLine = physicalLine;
			if(i == 0)
			{
				info.firstSubregion = true;
				info.offset = 0;
			}
			else
				info.offset = chunks.offset;

			if(i == out.size() - 1)
			{
				info.lastSubregion = true;
				info.length = textArea.getLineLength(physicalLine)
					- info.offset + 1;
			}
			else
			{
				info.length = ((Chunk)out.get(i + 1)).offset
					- info.offset;
			}

			info.chunksValid = true;
			info.chunks = chunks;

			returnValue[i] = info;
		}

		return returnValue;
	} //}}}

	//{{{ needFullRepaint() method
	/**
	 * The needFullRepaint variable becomes true when the number of screen
	 * lines in a physical line changes.
	 */
	boolean needFullRepaint()
	{
		boolean retVal = needFullRepaint;
		needFullRepaint = false;
		return retVal;
	} //}}}

	//{{{ Private members
	private JEditTextArea textArea;
	private int firstLine;
	private LineInfo[] lineInfo;
	private ArrayList out;

	private int lastScreenLineP;
	private int lastScreenLine;

	private boolean needFullRepaint;

	private NoWrapTokenHandler noWrap;
	private SoftWrapTokenHandler softWrap;
	//}}}

	//{{{ LineInfo class
	static class LineInfo
	{
		int physicalLine;
		int offset;
		int length;
		boolean firstSubregion;
		boolean lastSubregion;
		boolean chunksValid;
		Chunk chunks;
		int width;
	} //}}}
}
