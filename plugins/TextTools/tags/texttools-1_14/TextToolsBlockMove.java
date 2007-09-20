/*
 * Block_Move.java - a Java class for the jEdit text editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Rudi Widmann
 * Rudi.Widmann@web.de
 * http://eHome.compuserve.de/rudwid531/index.html
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
 *
 * Notes on use:
 *
 * Checked for jEdit 4.0 API
 *
 */

//{{{ Imports
import java.util.*;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;
//}}}

public class TextToolsBlockMove
{
	public static final int MOVE_LEFT = 1;
	public static final int MOVE_RIGHT = 2;
	public static final int MOVE_UP = 3;
	public static final int MOVE_DOWN = 4;
	public static boolean debug=false;
	
	//{{{ setRectSelection() method
	/**
	 * setRectSelection() method
	 * sets a rectangular selection
	 * adapted form Selection.Rect constructor
	 */
	public static void setRectSelection(JEditTextArea ta, Buffer buffer, int startLine, int startColumn, int endLine, int endColumn)
	{
		if (debug)
			Log.log(Log.DEBUG, BeanShell.class,"Block_Move.33: startLine = "+startLine+", startColumn = "+startColumn+", endLine = "+endLine+", endColumn = "+endColumn);
		// cut start/end to allowed sizes
		if (endLine >= buffer.getLineCount())
			endLine = buffer.getLineCount()-1;
		if (endLine < startLine)
			startLine = endLine;
		int[] width = new int[1];
		int extraStartVirt = 0;
		int extraEndVirt = 0;
		int selStartCol = Math.min(startColumn, endColumn);
		
		int startOffset = buffer.getOffsetOfVirtualColumn(startLine, selStartCol, width);  // left offset
		if (startOffset == -1)
		{
			// check if start is virtual: maybe lineend ?
			int vsler = buffer.getVirtualWidth(startLine, buffer.getLineEndOffset(startLine)-buffer.getLineStartOffset(startLine));
			if (debug) Log.log(Log.DEBUG, BeanShell.class,"Block_Move.45: vsler = "+vsler);
			if (startColumn >= vsler) // start is really virtual
				extraStartVirt = endColumn - width[0];
			startOffset = buffer.getLineEndOffset(startLine) - 1;
		}
		else
			startOffset += buffer.getLineStartOffset(startLine);
		if (debug)
			Log.log(Log.DEBUG, BeanShell.class,"Set_Rect_Selection.17: width = "+width[0]+", startOffset = "+startOffset+", extraStartVirt = "+extraStartVirt);
	
		// if start virtual, find endOffset at left side
		int selEndCol = extraStartVirt > 0 ? selStartCol : Math.max(startColumn, endColumn);
		int endOffset;
		try
		{
			endOffset = buffer.getOffsetOfVirtualColumn(endLine, selEndCol, width);
		}
		catch(java.lang.ArrayIndexOutOfBoundsException e)
		{
			// Selection exceeds end-of-buffer
			endOffset = buffer.getLength();
			if (debug)
				Log.log(Log.DEBUG, BeanShell.class,"Block_Move.66: endOffset = "+endOffset);
		}

		if (endOffset == -1)
		{
			extraEndVirt = selEndCol - width[0];
			endOffset = buffer.getLineEndOffset(endLine) - 1;
		}
		else
			endOffset += buffer.getLineStartOffset(endLine);
		
		if (debug)
			Log.log(Log.DEBUG, BeanShell.class,"Set_Rect_Selection.28: width = "+width[0]+", endOffset = "+endOffset+", extraEndVirt = "+extraEndVirt);
		
		// ta.selectNone();
		//if (extraStartVirt <= extraEndVirt)
		if (extraStartVirt == 0)
			ta.resizeSelection(startOffset, endOffset, extraEndVirt, true);
		else
			ta.resizeSelection(endOffset, startOffset, extraStartVirt, true);
	} //}}}
	
	//{{{ blockMove() method
	
	public static void blockMove(View view, JEditTextArea textArea, Buffer buffer, int direction)
	{
		if (textArea.getSelectionCount() == 0) 
			view.getStatus().setMessageAndClear(jEdit.getProperty("view.status.textTools.selection-required"));
		else 
		{
			int oldIndent = buffer.getIndentSize();
			// set indent temporarily to "1"
			buffer.setIntegerProperty("indentSize",1);
			Selection[] sels = textArea.getSelection();
			boolean overwrite = false;
			boolean cont=true;
			buffer.beginCompoundEdit();
			
			for (int i=sels.length-1; i>=0 && cont; i--)
			{
				Selection currSel = sels[i];
				//		moveSelection(currSel,-1,-1);
				//		return;
				//boolean rect = currSel instanceof Selection.Rect;
				int currStartLine = currSel.getStartLine();
				int currEndLine = currSel.getEndLine();
				if (currSel instanceof Selection.Range)
				{
					// write line numbers of selection into array
					if (currSel.getEnd() == textArea.getLineStartOffset(currEndLine))
						currEndLine--;
					int[] selLines = new int[currEndLine-currStartLine+1];
					for (int lc=currStartLine; lc<=currEndLine; lc++)
						selLines[lc-currStartLine] = lc;
					if (direction == MOVE_RIGHT)
						buffer.shiftIndentRight(selLines);
					else if (direction == MOVE_LEFT)
						buffer.shiftIndentLeft(selLines);
				}
				else
				{
					/***************************************************
					 * handle rectangular selection
					****************************************************/
					boolean skipSelection = false;
					boolean moveExceedsBuffer = false;
					int vssr = ((Selection.Rect)currSel).getStartColumn(buffer);  //virtualStartSelectionRow
					int vesr = ((Selection.Rect)currSel).getEndColumn(buffer);    //virtualEndSelectionRow
					int selWidth = vesr - vssr;  // width of selection
					if (debug) Log.log(Log.DEBUG, BeanShell.class,"Block_Move.121: selWidth = "+selWidth);
					switch (direction) {
						case MOVE_UP:
							if (currStartLine == 0 || vssr == vesr)
								skipSelection = true;
							else
								// note: sso/eso[currStartLine] not setup
								currStartLine--;
							break;
						case MOVE_DOWN:
							if (vssr == vesr)
								skipSelection = true;
							else 
							{
								// note: sso/eso[currEndLine] not setup
								currEndLine++;
								if (currEndLine >= buffer.getLineCount()) 
								{
									moveExceedsBuffer = true;
									currEndLine = buffer.getLineCount();
								}
							}
							// instead of end-of-buffer calc, insert 2 newlines
							// buffer.insert(buffer.getLength(), "\n\n"); 
							// doesn't work because selection changes
							break;
						case MOVE_LEFT:
							if (vssr == 0)
								skipSelection = true;
							break;
					}
					
					if (!skipSelection) 
					{
						// memorize start/end selection offset, because selection changes during execution  
						int[] sso = new int[currEndLine-currStartLine+1];
						int[] eso = new int[currEndLine-currStartLine+1];
							for (int lin=currStartLine, j=0; lin<=currEndLine; lin++, j++)
							{
								if (lin<currEndLine || direction != MOVE_DOWN)
								{
									sso[j] = currSel.getStart(buffer,lin);
									eso[j] = currSel.getEnd(buffer,lin);
									if (debug)
										Log.log(Log.DEBUG, BeanShell.class,"Block_Move.148: sso[j] = "+sso[j]+", eso[j] = "+eso[j]);
							}
						}
						String lastLine = "";
						String fillLeft;
						String fillRight;
						int lastWidth=0;
						
						if (debug)
							Log.log(Log.DEBUG, BeanShell.class,"Block_Move_Left.118: vssr = "+vssr+", vesr = "+vesr);
						
						for (int lin=currEndLine; lin>=currStartLine; lin--) 
						{
							if (debug) Log.log(Log.DEBUG, BeanShell.class,"Block_Move.157: lin = "+lin);
							int slo = 0;  //startLineOffset
							int elo = 0;    //endLineOffset
							int velr = 0;     //virtualEndLineRow
							int rsso = 0;
							int reso = 0;
							if (!moveExceedsBuffer)
							{
								slo = buffer.getLineStartOffset(lin);  //startLineOffset
								elo = buffer.getLineEndOffset(lin);    //endLineOffset
								velr = buffer.getVirtualWidth(lin,elo-slo-1)+1;  //virtualEndLineRow
								//velr = buffer.getVirtualWidth(lin,elo-slo);  //virtualEndLineRow
								rsso = sso[lin-currStartLine];
								reso = eso[lin-currStartLine];
							}
							/*******************************************************************
							 * start buffer manipulation
							 *******************************************************************/
							switch (direction) 
							{
								case MOVE_UP:
									/*******************************************************************
									 * save last line
									 *******************************************************************/
									String insLine = lastLine;
									int insWidth = lastWidth;
									if (vssr  < velr) {
										lastLine = buffer.getText(rsso, reso-rsso);
										if (vesr  < velr) 
											lastWidth = vesr - vssr;
										else
											lastWidth = buffer.getVirtualWidth(lin,reso-slo) - vssr;
									}
									else
									{
										lastLine = "";
										lastWidth = 0;
									}
									if (debug)
										Log.log(Log.DEBUG, BeanShell.class,"Block_Move.163: lastLine = "+lastLine+", lastWidth = "+lastWidth);
									/*******************************************************************
									 * insert line
									 *******************************************************************/
									if (debug)
										Log.log(Log.DEBUG, BeanShell.class,"Block_Move.167: insLine = "+insLine+", insWidth = "+insWidth);
									
									// insert only if start not virtual or anything to insert
									if (vssr  < velr || insWidth > 0)
									{
										int leftTabOffset=0;  //additional offset if tab in target
										// construct insertion: fillLeft + insLine + fillRight
										int fillNbrWs = 0;
										if (vssr >= velr) 
										{
											fillNbrWs = vssr - buffer.getVirtualWidth(lin,reso-slo);
										}
										else
										{
											if (lin == currStartLine) 
											{
												// check if there is a tab before the selection: expand it to spaces
												if (vssr>0 && buffer.getText(rsso-1,1).charAt(0) == '\t') 
												{
													leftTabOffset = 1;
													int vslsr1 = buffer.getVirtualWidth(lin,rsso-slo-1);  //virtual(StartSelection-1)Row
													fillNbrWs = vssr - vslsr1;
												}
												// check if there is a tab as last char of the selection: expand it to spaces
												if (vesr<velr && buffer.getText(reso-1,1).charAt(0) == '\t') 
												{
													//decrease insWidth to enforce more filling
													if (debug)
														Log.log(Log.DEBUG, BeanShell.class,"Block_Move.238: insWidth = "+insWidth);
													insWidth -= buffer.getVirtualWidth(lin,reso-slo) - vesr;
													if (debug)
														Log.log(Log.DEBUG, BeanShell.class,"Block_Move.240: insWidth = "+insWidth+", buffer.getVirtualWidth(lin,reso-slo) = "+buffer.getVirtualWidth(lin,reso-slo)+", vesr = "+vesr);
												}
											}
										}
										if (fillNbrWs > 0)
											fillLeft = StandardUtilities.createWhiteSpace(fillNbrWs, 0);
										else
											fillLeft = "";
										if (insWidth < selWidth)
											fillRight = StandardUtilities.createWhiteSpace(selWidth-insWidth, 0);
										else
											fillRight = "";
										if (debug)
											Log.log(Log.DEBUG, BeanShell.class,"Block_Move.181: fillLeft = "+fillLeft+", fillRight = "+fillRight+", selWidth = "+selWidth);
										// do modifications
										buffer.remove(rsso-leftTabOffset,reso-rsso+leftTabOffset);
										buffer.insert(rsso-leftTabOffset, fillLeft+insLine+fillRight);
									}
									break;
								case MOVE_DOWN:
									/*******************************************************************
									* detect previous line: no collision with currLastLine, because previous searched
									*******************************************************************/
									if (lin == currStartLine)
									{
										lastLine = "";
										lastWidth = 0;
									}
									else
									{
										// previousVirtualEndLineRow
										if (debug)
											Log.log(Log.DEBUG, BeanShell.class,"Block_Move.233: buffer.getLineEndOffset(lin-1) = "+buffer.getLineEndOffset(lin-1)+", buffer.getLineStartOffset(lin-1) = "+buffer.getLineStartOffset(lin-1));
										
										int pvelr = buffer.getVirtualWidth(lin-1,
										buffer.getLineEndOffset(lin-1)-1-buffer.getLineStartOffset(lin-1))+1;     
										
										if (debug)
											Log.log(Log.DEBUG, BeanShell.class,"Block_Move.236");
										
										int prsso = sso[lin-1-currStartLine];
										int preso = eso[lin-1-currStartLine];
										
										if (debug)
											Log.log(Log.DEBUG, BeanShell.class,"Block_Move.222: pvelr = "+pvelr+", prsso = "+prsso+", preso = "+preso);
										
										if (vssr  < pvelr) {
											lastLine = buffer.getText(prsso, preso-prsso);
											
											if (debug)
												Log.log(Log.DEBUG, BeanShell.class,"Block_Move.226: lastLine = "+lastLine);
											
											if (vesr  < pvelr) 
												lastWidth = vesr - vssr;
											else
												lastWidth = buffer.getVirtualWidth(lin-1,preso-buffer.getLineStartOffset(lin-1)) - vssr;
											
											if (debug)
												Log.log(Log.DEBUG, BeanShell.class,"Block_Move.231: lastWidth = "+lastWidth);
										}
										else 
										{
											lastLine = "";
											lastWidth = 0;
										}
									}
									if (debug)
										Log.log(Log.DEBUG, BeanShell.class,"Block_Move.280: lastLine = "+lastLine+", lastWidth = "+lastWidth);
									/*******************************************************************
									 * insert line
									 *******************************************************************/
									if (moveExceedsBuffer) 
									{
										rsso = buffer.getLength();
										//fillLeft = "\n"+MiscUtilities.createWhiteSpace(vssr, 0);
										fillLeft = System.getProperty("line.separator")+
										StandardUtilities.createWhiteSpace(vssr, 0);
										if (lastWidth > 0)
											buffer.insert(rsso, fillLeft+lastLine);
										moveExceedsBuffer = false;  // for further lines
									}
									else 
									{
										if (lin == currEndLine) 
										{
											// rsso and reso not setup, because there is no selection
											// note: buffer.getOffsetOfVirtualColumn returns the column, not the offset
											int[] width = new int[1];
											rsso = buffer.getOffsetOfVirtualColumn(lin, vssr, width);
											if (rsso == -1)
												rsso = elo-1; // vssr virtual: assign end-of-line offset
											else
												rsso += slo;
											reso = buffer.getOffsetOfVirtualColumn(lin, vesr, width);
											if (reso == -1)
												reso = elo-1; // vesr virtual: assign end-of-line offset
											else
												reso += slo;
											
											if (debug)
												Log.log(Log.DEBUG, BeanShell.class,"Block_Move.301: rsso = "+rsso+", reso = "+reso+", elo = "+elo);
										}
										// insert only if start not virtual or anything to insert
										if (vssr  < velr || lastWidth > 0) 
										{
											int leftTabOffset=0;  //additional offset if tab in target
											// construct insertion: fillLeft + insLine + fillRight
											int fillNbrWs = 0;
											if (vssr >= velr) 
											{
												fillNbrWs = vssr - buffer.getVirtualWidth(lin,reso-slo);
											}
											else 
											{
												if (lin == currEndLine) 
												{
													// check if there is a tab before the selection: expand it to spaces
													if (vssr>0 && buffer.getText(rsso-1,1).charAt(0) == '\t') 
													{
														leftTabOffset = 1;
														int vslsr1 = buffer.getVirtualWidth(lin,rsso-slo-1);  //virtual(StartSelection-1)Row
														fillNbrWs = vssr - vslsr1;
													}
													// check if there is a tab as last char of the selection: expand it to spaces
													if (vesr<velr && buffer.getText(reso-1,1).charAt(0) == '\t') 
													{
														//decrease insWidth to enforce more filling
														if (debug)
															Log.log(Log.DEBUG, BeanShell.class,"Block_Move.238: lastWidth = "+lastWidth);
														
														lastWidth -= buffer.getVirtualWidth(lin,reso-slo) - vesr;
														
														if (debug)
															Log.log(Log.DEBUG, BeanShell.class,"Block_Move.240: lastWidth = "+lastWidth+", buffer.getVirtualWidth(lin,reso-slo) = "+buffer.getVirtualWidth(lin,reso-slo)+", vesr = "+vesr);
													}
												}
											}
											if (fillNbrWs > 0)
												fillLeft = StandardUtilities.createWhiteSpace(fillNbrWs, 0);
											else
												fillLeft = "";
											if (lastWidth < selWidth)
												fillRight = StandardUtilities.createWhiteSpace(selWidth-lastWidth, 0);
											else
												fillRight = "";
											
											if (debug)
												Log.log(Log.DEBUG, BeanShell.class,"Block_Move.181: fillLeft = "+fillLeft+", fillRight = "+fillRight+", selWidth = "+selWidth);
											
											// do modifications
											buffer.remove(rsso-leftTabOffset,reso-rsso+leftTabOffset);
											buffer.insert(rsso-leftTabOffset, fillLeft+lastLine+fillRight);
										}
									}
									break;
								case MOVE_RIGHT:
									//if (direction == MOVE_RIGHT) {
									/*******************************************************************
									 * move right
									 *******************************************************************/
									/*******************************************************************
									 * move right border 
									 *******************************************************************/
									// skip if endSelection is at eol
									// note: velr is always v(lastChar)+1
									//if (vsliner + 1 < velr) {
									if (vesr + 1 < velr) 
									{
										// check overwrite
										if (!overwrite && !Character.isWhitespace(buffer.getText(reso,1).charAt(0))) 
										{
											if (debug) 
												Log.log(Log.DEBUG, BeanShell.class,"Block_Move_Right.52: buffer.getText(reso,1).charAt(0) = "+buffer.getText(reso,1).charAt(0));
											/*
											if (JOptionPane.showConfirmDialog(view, "overwrite right column ?",
											"Block move right",JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) 
												cont = false;
											else 
												*/
											overwrite = true;
										}
										
										int insNbrWs=0;
										
										if (buffer.getText(reso,1).charAt(0) == '\t') 
										{
											insNbrWs = buffer.getVirtualWidth(lin,reso-slo+1) - vesr - 1;  // if a tab is removed, add virtual difference
										}
										
										buffer.remove(reso,1);
										if (insNbrWs > 0) 
										{
											if (insNbrWs == 1)
												buffer.insert(reso," ");
											else
												buffer.insert(reso,StandardUtilities.createWhiteSpace(insNbrWs, 0));
										}
									}
									/*******************************************************************
									 * move left border : only, if visual selection not in virtual space
									 *******************************************************************/
									if (vssr < velr) 
									{
										buffer.insert(rsso," ");
										
										if (debug) 
											Log.log(Log.DEBUG, BeanShell.class,"Block_Move_Right.239.insert at: rsso = "+rsso);
									}
									break;
								case MOVE_LEFT:
									if (vssr > 0) {
										/*******************************************************************
										 * move left
										 *******************************************************************/
										/*******************************************************************
										 * move right border : only, if visual selection not in virtual space
										 *******************************************************************/
										//if (vsliner +1 < velr) {
										if (vesr + 1 < velr) 
										{
											buffer.insert(reso," ");
											if (debug) Log.log(Log.DEBUG, BeanShell.class,"Block_Move_Left.257.insert at: rsso = "+rsso);
										}
										// skip if startSelection remains in virtuel
										//if (vslsr + 1 < velr) {
										if (vssr < velr) 
										{
											/*******************************************************************
											 * move left border 
											 *******************************************************************/
											// check overwrite
											if (!overwrite && !Character.isWhitespace(buffer.getText(rsso-1,1).charAt(0))) 
											{
												if (debug)
													Log.log(Log.DEBUG, BeanShell.class,"Block_Move_Left.52: buffer.getText(reso,1).charAt(0) = "+buffer.getText(reso,1).charAt(0));
												/*
												if (JOptionPane.showConfirmDialog(view, "overwrite left column ?",
												"Block move right",JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) 
													cont = false;
												else 
													*/
												overwrite = true;
											}
											int insNbrWs=0;
											
											if (buffer.getText(rsso-1,1).charAt(0) == '\t') 
											{
												insNbrWs = vssr - buffer.getVirtualWidth(lin,rsso-slo-1) - 1;  // if a tab is removed, add virtual difference
											}
											
											if (insNbrWs > 0) 
											{
												buffer.remove(rsso-1,1);
												buffer.insert(rsso-1,StandardUtilities.createWhiteSpace(insNbrWs, 0));
											}
											else
												buffer.remove(rsso-1,1);
										}
									}  // end move left
									break;
							}
						}  // end selection loop
						if (sels.length == 1)
							textArea.selectNone();
						else 
						{
							// redefine current selection to avoid relicts
							textArea.removeFromSelection(currSel);
							//new Selection.Range(currSel.getStart(), currSel.getEnd());
						}
						switch (direction) {
							case MOVE_UP:
								setRectSelection(textArea, buffer, currStartLine, vssr, currEndLine-1, vesr);
								break;
							case MOVE_DOWN:
								setRectSelection(textArea, buffer, currStartLine+1, vssr, currEndLine, vesr);
								break;
							case MOVE_RIGHT: 
								setRectSelection(textArea, buffer, currStartLine, vssr+1, currEndLine, vesr+1);
								break;
							case MOVE_LEFT: 
								if (vssr>0)
									setRectSelection(textArea, buffer, currStartLine, vssr-1, currEndLine, vesr-1);
								break;
						}
					}
				}
			}
			buffer.endCompoundEdit();
			buffer.setIntegerProperty("indentSize",oldIndent);
			/* this code is only relevant if the code runs as bsh macro
			if (!cont) {
				buffer.endCompoundEdit();
				buffer.undo(textArea);
				buffer.beginCompoundEdit();
			}
			*/
		}
	} //}}}
}

