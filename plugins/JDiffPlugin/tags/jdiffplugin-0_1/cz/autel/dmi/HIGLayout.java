/*
 * HIGLayout.java - HIGLayout layout manager
 * Copyright (C) 1999 Daniel Michalik
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. 
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package cz.autel.dmi;

import java.awt.*;
import java.util.*;

/**
 * Layout manager based on idea of design grid. For description please see tutorial included
 * in download bundle.
 * @see cz.autel.dmi.HIGConstraints
 * @version 	0.95 12/17/1999
 * @author 	Daniel Michalik (email: dmi@autel.cz)
 */
public class HIGLayout implements LayoutManager2, java.io.Serializable {
	
	/* since we number rows and columns from 1, size of these arrays must be nummber columns + 1*/
	private int []colWidths;
	private int []rowHeights;
	private int colCount;
	private int rowCount;
	
	private Vector []colComponents;
	private Vector []rowComponents;

	private int []widenWeights;
	private int []heightenWeights;
	private int widenWeightsSum = 0;
	private int heightenWeightsSum = 0;
		
	private Hashtable components = new Hashtable();
	
	/**
	 * Construct a new layout object. Length of passed arrays define number of columns and
	 * number of rows. Each width or height can be less then 0, equal 0 or greater then 0.
	 * Passed arrays defines design grid - sizes and dependences between columns and rows. 
	 * For details see tutorial.
	 * @param widths array of column widths.
	 * @param heights array of row heights.
	 */
	public HIGLayout(int []widths, int []heights) {
		colCount = widths.length;
		rowCount = heights.length;
		
		colWidths = new int[colCount + 1];
		System.arraycopy(widths, 0, colWidths, 1, colCount);
		rowHeights = new int[rowCount + 1];
		System.arraycopy(heights, 0, rowHeights, 1, rowCount);

		widenWeights = new int[colCount +1];
		heightenWeights = new int[rowCount +1];		

		colComponents = new Vector[colCount + 1];
		rowComponents = new Vector[rowCount + 1];
	}
		
	/**
	 * Sets weight of specified column. Weight determines distribution
	 * of difference when resizing.
	 * @param col index of column. Index must be > 0.
	 */
	public void setColumnWeight(int col, int weight) {
		if(col > colCount) {
			throw new RuntimeException("Column index cannot be greater then "+colCount+".");	
		}
		widenWeights[col] = weight;
		widenWeightsSum = 0;
		for(int i=1; i<=colCount; i++)	widenWeightsSum += widenWeights[i];
	}

	/**
	 * Sets weight of specified row. Weight determines distribution
	 * of difference when resizing.
	 * @param row index of row. Index must be > 0.
	 */
	public void setRowWeight(int row, int weight) {
		if(row > rowCount) {
			throw new RuntimeException("Column index cannot be greater then "+rowCount+".");	
		}
		heightenWeights[row] = weight;
		heightenWeightsSum = 0;
		for(int i=1; i<=rowCount; i++)	heightenWeightsSum += heightenWeights[i];
	}
	
   /**   
     * @deprecated  replaced by <code>addLayoutComponent(Component, Object)</code>. Throws 
	 * <EM>UnsupportedOperationException</EM>.
     */
	public void addLayoutComponent(String name, Component comp) {
		throw new UnsupportedOperationException();
	}

    /**
     * Removes the specified component from the layout.
     * @param comp the component to be removed
     */
	 public void removeLayoutComponent(Component comp) {
		 synchronized (comp.getTreeLock()) {
			HIGConstraints c = (HIGConstraints) components.remove(comp);
			if(c == null) return;
			colComponents[c.x].removeElement(comp);
			rowComponents[c.y].removeElement(comp);			 			 
		 }

	}


	private void solveCycles(int g[], int lengths[]) {
		/* TODO: handle cycles of length 1*/
		int path[] = new int[g.length];
		int stackptr = 0;
		
		/* marks of visited vertices. 0 - not visited, 1 - visited, 2 - visited and set final value */		
		byte visited[] = new byte[g.length]; 
		for(int i = g.length - 1; i> 0; i--) {
			if((g[i] < 0) && (visited[i]==0)) {
				int current = i;
				
				/* find cycle or path with cycle */
				stackptr = 0;
				int maxLength = 0;
				int last;
				do {
					maxLength = (lengths[current] > maxLength) ? lengths[current] : maxLength;
					path[stackptr++] = current;
					visited[current] = 1;
					last = current;
					current = -g[current];
				} while ( (current > 0) && (visited[current]==0) );
				
				if(current <= 0) { /* there is no cycle, only end of path */
					maxLength = lengths[last];
				} else if(current == 0) {
					maxLength = lengths[last];	
				} else if(visited[current] == 1) { /* cycle, max. cannot lie outside the cycle, find it */
					int start = current;
					maxLength = 0;
					do {
						maxLength = (lengths[current] > maxLength) ? lengths[current] : maxLength;
						current = -g[current];							
					} while( start != current );
				} else if(visited[current] == 2) { /* this vertice already has final value */
					maxLength = lengths[current];
				} else {
					throw new RuntimeException("This should not happen.");	
				}
				while(stackptr > 0) {
					lengths[path[--stackptr]] = maxLength;
					visited[path[stackptr]] = 2;
				}
			}
		}
	}

	private int[] calcMinWidths() {
		int []widths = new int[colCount + 1];
		for(int i=1; i <= colCount ; i++) {
			if(colWidths[i] > 0) {
				widths[i] = colWidths[i];			
			} else {
				Vector iComps = colComponents[i];
				int maxWidth = 0;
				if(iComps != null) {
					for(int j = iComps.size() - 1; j >= 0; j--) {
						Component c = (Component)iComps.elementAt(j);
						Dimension d = c.getMinimumSize();
						HIGConstraints constr = (HIGConstraints) components.get(c);
						if(constr.w < 0) d.width = -constr.w;
						else d.width += constr.wCorrection;
						maxWidth = (d.width > maxWidth) ? d.width : maxWidth;
					}
				}
				widths[i] = maxWidth;
			}
		}
		solveCycles(colWidths, widths);
		
		return widths;
	}

	private int[] calcMinHeights() {
		int []heights = new int[rowCount + 1];
		for(int i=1; i <= rowCount ; i++) {
			if(rowHeights[i] > 0) {
				heights[i] = rowHeights[i];			
			} else {
				int maxHeight = 0;
				Vector iComps = rowComponents[i];
				if(iComps != null) {
					for(int j = iComps.size() - 1; j >= 0; j--) {
						Component c = (Component)iComps.elementAt(j);
						Dimension d = c.getMinimumSize();
						HIGConstraints constr = (HIGConstraints) components.get(c);
						if(constr.h < 0) d.height = -constr.h;
						else d.height += constr.hCorrection;
						maxHeight = (d.height > maxHeight) ? d.height : maxHeight;
					}
				}
				heights[i] = maxHeight;
			}
		}
		solveCycles(rowHeights, heights);
		
		return heights;
	}
	
	private int[] calcPreferredWidths() {
		int []widths = new int[colCount + 1];
		for(int i=1; i <= colCount ; i++) {
			if(colWidths[i] > 0) {
				widths[i] = colWidths[i];			
			} else {
				int maxWidth = 0;
				Vector iComps = colComponents[i];
				if(iComps != null) {
					for(int j = iComps.size() - 1; j >= 0; j--) {
						Component c = (Component)iComps.elementAt(j);
						Dimension d = c.getPreferredSize();
						HIGConstraints constr = (HIGConstraints) components.get(c);
						if(constr.w < 0) d.width = -constr.w;
						else d.width += constr.wCorrection;
						maxWidth = (d.width > maxWidth) ? d.width : maxWidth;
					}
				}
				widths[i] = maxWidth;
			}
		}
		solveCycles(colWidths, widths);
		
		return widths;
	}

	private int[] calcPreferredHeights() {
		int []heights = new int[rowCount + 1];
		for(int i=1; i <= rowCount ; i++) {
			if(rowHeights[i] > 0) {
				heights[i] = rowHeights[i];			
			} else {
				Vector iComps = rowComponents[i];
				int maxHeight = 0;
				if(iComps != null) {
					for(int j = iComps.size() - 1; j >= 0; j--) {
						Component c = (Component)iComps.elementAt(j);
						Dimension d = c.getPreferredSize();
						HIGConstraints constr = (HIGConstraints) components.get(c);
						if(constr.h < 0) d.height = -constr.h;
						else d.height += constr.hCorrection;
						maxHeight = (d.height > maxHeight) ? d.height : maxHeight;
					}
				}
				heights[i] = maxHeight;
			}
		}
		solveCycles(rowHeights, heights);
		
		return heights;
	}
	
	private void distributeSizeDifference(int desiredLength, int []lengths, int []minLengths, 
					int []weights, int weightSum) {
		int preferred = 0;
		int newLength;
		for(int i=lengths.length-1; i>0; i--)	preferred += lengths[i];

		double unit = ((double)(desiredLength - preferred)) / (double)weightSum;
		
		for(int i=lengths.length-1; i>0; i--) {
			newLength = lengths[i] + (int) (unit * (double) weights[i]);
			lengths[i] = (newLength > minLengths[i]) ? newLength : minLengths[i];
		}
	}
	
    /**
     * Calculates the preferred size dimensions for the specified 
     * container given the components in the specified parent container.
     * @param parent the component to be laid out
     *  
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			int []prefColWidths = calcPreferredWidths();
			int []prefRowHeights = calcPreferredHeights();
			int w = 0;
			int h = 0;
			for(int i=1; i<= colCount; i++) w+= prefColWidths[i];
			for(int i=1; i<= rowCount; i++) h+= prefRowHeights[i];
			return new Dimension(w,h);
		}	
	
	}

    public Dimension minimumLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			int []minColWidths = calcMinWidths();
			int []minRowHeights = calcMinHeights();
			int w = 0;
			int h = 0;
			for(int i=1; i<= colCount; i++) w+= minColWidths[i];
			for(int i=1; i<= rowCount; i++) h+= minRowHeights[i];
			return new Dimension(w,h);
		}	
	}

	/**
	 * returns array of x-coordinates of columns. First coordinate is stored in x[1]
	 */
	int []	getColumnsX(int targetWidth) {
			int []prefColWidths = calcPreferredWidths();
			int []minColWidths = calcMinWidths();
			
			distributeSizeDifference(targetWidth, prefColWidths, minColWidths, widenWeights, widenWeightsSum);
			int x[] = new int[colCount + 2];
			x[1] = 0;

			for(int i=2; i<= colCount+1; i++) x[i] = x[i-1] + prefColWidths[i-1];
			return x;
	}
	
	/**
	 * returns array of y-coordinates of rows. First coordinate is stored in y[1]
	 */
	int []  getRowsY(int targetHeight) {
			int []prefRowHeights = calcPreferredHeights();
			int []minRowHeights = calcMinHeights();
			
			distributeSizeDifference(targetHeight, prefRowHeights, minRowHeights, heightenWeights, heightenWeightsSum);
			int y[] = new int[rowCount + 2];
			y[1] = 0;

			for(int i=2; i<= rowCount+1; i++) y[i] = y[i-1] + prefRowHeights[i-1];
			return y;
	}
	
    public void layoutContainer(Container target) {
		synchronized (target.getTreeLock()) {
			Dimension size = target.getSize();
			int x[] = getColumnsX(size.width);
			int y[] = getRowsY(size.height);
			Component comps[] = target.getComponents();
			for(int i = comps.length -1; i>=0; i--) {
				Component comp = comps[i];
				HIGConstraints c = (HIGConstraints) components.get(comp);
				/* first we centre component into cell */
				Dimension d = comp.getPreferredSize();
				int cellw;
				int cellh;
				if(c.w < 0) {
					d.width = -c.w;
					cellw = x[c.x+1] - x[c.x];
				} else {
					d.width += c.wCorrection;
					cellw = x[c.x+c.w] - x[c.x];
				}
				if(c.h < 0) {
					d.height = -c.h;
					cellh = y[c.y+1] - y[c.y];
				} else {
					d.height += c.hCorrection;
					cellh = y[c.y+c.h] - y[c.y];
				}				
				
				Dimension dMax = comp.getMaximumSize();

				boolean allowXSize = true;
				boolean allowYSize = true;
				/* I had intend to ensure that maximumSize is respected, but Swing components returns stupid maximumSize */
				/*
				if(cellw > dMax.width) {
					d.width = dMax.width;
					allowXSize = false;
				}
				if(cellh > dMax.height) {
					d.height = dMax.height;
					allowYSize = false;
				} */
				float dw = ((float)(cellw - d.width)) / 2.0f;
				float dh = ((float)(cellh - d.height)) / 2.0f;
				float compx = (float)x[c.x] + dw;
				float compy = (float)y[c.y] + dh;

				/* now anchor to cell borders */
				String anchor = c.anchor;
				boolean xSize = false;	/* first move, then change width (when opposite border) */
				boolean ySize = false;
				for(int j=anchor.length() - 1; j >= 0; j--) {
					if(anchor.charAt(j) == 'l') {
						compx = (float)x[c.x];
						if(xSize && allowXSize) d.width = cellw;						
						xSize = true;
					} else if(anchor.charAt(j) == 'r') {
						if(xSize && allowXSize) d.width = cellw;
						else compx += dw;
						xSize = true;
					} else if(anchor.charAt(j) == 't') {
						compy = (float)y[c.y];
						if(ySize && allowYSize) d.height = cellh;
						ySize = true;
					} else if(anchor.charAt(j) == 'b') {
						if(ySize && allowYSize) d.height = cellh;
						else compy += dh;
						ySize = true;
					} else {
						throw new RuntimeException("Wrong character in anchor.");	
					}
				}

				comp.setBounds((int)compx+c.xCorrection, (int)compy+c.yCorrection, d.width, d.height);
			}
		}
	}

	// LayoutManager2
    /**
     * Adds the specified component to the layout, using the HIGConstraints
     * constraint object. Constraints object is copied so passed instance
	 * can be safely modifed.
     * @param comp the component to be added
     * @param HIGConstraints object determining where/how the component is added to the layout.
	 * @see cz.autel.dmi.HIGConstraints
     */
    public void addLayoutComponent(Component comp, Object constraints) {
		synchronized (comp.getTreeLock()) {
			HIGConstraints constr = (HIGConstraints) constraints;
			if(constr.x > colCount) {
				throw new RuntimeException("Column index in constraint object cannot be greater then "+colCount+".");	
			}
			if(constr.x + constr.w - 1 > colCount) {
				throw new RuntimeException("Width in constraint object cannot be greater then "
					+(colCount - constr.x + 1)+".");	
			}
			if(constr.y > rowCount) {
				throw new RuntimeException("Row index in constraint object cannot be greater then "+rowCount+".");	
			}
			if(constr.y + constr.h - 1 > rowCount) {
				throw new RuntimeException("Height in constraint object cannot be greater then "
					+(rowCount - constr.y + 1)+".");	
			}
			
			/* if comp. occupies one column (row), we insert it to list for this column (row) */
			if(constr.w == 1) {
				if(colComponents[constr.x] == null) {
					colComponents[constr.x] = new Vector();
				}
				colComponents[constr.x].addElement(comp);
			}
			if(constr.h == 1) {
				if(rowComponents[constr.y] == null) {
					rowComponents[constr.y] = new Vector();
				}
				rowComponents[constr.y].addElement(comp);
			}
			components.put(comp, new HIGConstraints(constr));
		}
		
	}

    /** 
     * Returns the maximum size of this component.
     * @see java.awt.Component#getMinimumSize()
     * @see java.awt.Component#getPreferredSize()
     * @see LayoutManager
     */
    public Dimension maximumLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);			
		}	
	}

    /**
     * Returns 0.
     */
    public float getLayoutAlignmentX(Container target) {
		return 0f;
	}

    /**
     * Returns 0.
     */
    public float getLayoutAlignmentY(Container target) {
		return 0f;
	}

    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.
     */
    public void invalidateLayout(Container target) {
	}
}

class UnsupportedOperationException extends RuntimeException {}

