/*
 * HIGConstraints.java - HIGLayout layout manager
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

/**
 * Defines constraints for each component added to container with HIGLayout layout manager.
 * Defines components target area: x, y (column, row) of top left corner, width (number of columns or
 * absolute size in pixels), height (number of rows or absolute size in pixels), anchors string,
 * vertical and horizontal corrections. Usually You will use one instance of this class because
 * HIGLayout copies passed constraint object when adds component to container. 
 * <P>
 * Most of methods returns <em>this</em> object so Your can chain more calls into single
 * line of code.</P> 
 * @version 	0.95 12/17/1999
 * @author 	Daniel Michalik (email: dmi@autel.cz)
 */
public class HIGConstraints implements java.io.Serializable {
	private static final String defaultAnchor = "lrtb";
	int	x;
	int y;
	int w = 1;	/* when >0, means width in cells; < 0, means fixed width in pixels */
	int h = 1;
	int xCorrection = 0;
	int yCorrection = 0;
	int wCorrection = 0;
	int hCorrection = 0;
	String anchor;
	
	HIGConstraints(HIGConstraints c) {
		x = c.x;
		y = c.y;
		w = c.w;
		h = c.h;
		xCorrection = c.xCorrection;
		yCorrection = c.yCorrection;
		wCorrection = c.wCorrection;
		hCorrection = c.hCorrection;
		anchor = c.anchor;
	}
	public HIGConstraints() {
		anchor = defaultAnchor;
	}
	
	/**
	 * Set horizontal correction until changed or cleared. When layout manager
	 * takes component's preferred size it will add to it passed width correction;
	 * after positioning it will change position about position correction.
	 * @param xCorr correction of horizontal position, in pixels. Can be negative.
	 * @param wCorr correction of width, in pixels. Can be negative.
	 * @see #clearCorrection
	 * @return this
	 */
	public HIGConstraints setHCorrection(int xCorr, int wCorr) {
		xCorrection = xCorr;
		wCorrection = wCorr;
		return this;
	}
	/**
	 * Set vertical correction until changed or cleared. When layout manager
	 * takes component's preferred size it will add to it passed height correction;
	 * after positioning it will change position about position correction.
	 * @param yCorr correction of vertical position, in pixels. Can be negative.
	 * @param hCorr correction of height, in pixels. Can be negative.
	 * @see #clearCorrection
	 * @return this
	 */
	public HIGConstraints setVCorrection(int yCorr, int hCorr) {
		yCorrection = yCorr;
		hCorrection = hCorr;
		return this;
	}

	/**
	 * Clears all corrections.
	 * @see #setHCorrection
	 * @see #setVCorrection
	 * @return this
	 */
	public HIGConstraints clearCorrection() {
		xCorrection = 0;
		yCorrection = 0;
		wCorrection = 0;
		hCorrection = 0;		
		return this;
	}

	/**
	 * Increases current row index by one. Preserves all previous settings.
	 * @return this
	 */
	public HIGConstraints nextRow() {
		y++;
		return this;
	}
	/**
	 * Increases current row index by two. Preserves all previous settings.
	 * @return this
	 */
	public HIGConstraints next2Row() {
		y += 2;
		return this;
	}
	
	/**
	 * Increases current column index by one. Preserves all previous settings.
	 * @return this
	 */
	public HIGConstraints nextCol() {
		x++;
		return this;
	}
	/**
	 * Increases current column index by two. Preserves all previous settings.
	 * @return this
	 */
	public HIGConstraints next2Col() {
		x += 2;
		return this;
	}
	
	/**
	 * Sets column index. Preserves row index, sets anchor to "rltb", width and height to 1.
	 * @param x column index
 	 * @return this
	 */
	public HIGConstraints x(int x) {
		this.x = x;
		anchor = "rltb";
		this.w = 1;
		this.h = 1;
		return this;
	}
	
	/**
	 * Sets column index. Preserves row index, sets anchor to "rltb", width and height to 1.
	 * @param c column index
 	 * @return this
	 */
	public HIGConstraints c(int c) {
		this.x = c;
		anchor = "rltb";
		this.w = 1;
		this.h = 1;
		return this;
	}

	/**
	 * Sets column index and anchors. Preserves row index, sets width and height to 1.
	 * @param x column index
	 * @param anchors anchors string (of letters 'l','r','t','b')
 	 * @return this
	 */
	public HIGConstraints x(int x, String anchors) {
		this.x = x;
		anchor = anchors;
		this.w = 1;
		this.h = 1;
		return this;
	}
	/**
	 * Sets column index and anchors. Preserves row index, sets width and height to 1.
	 * @param c column index
	 * @param anchors anchors string (of letters 'l','r','t','b')
 	 * @return this
	 */
	public HIGConstraints c(int c, String anchors) {
		this.x = c;
		anchor = anchors;
		this.w = 1;
		this.h = 1;
		return this;
	}

	/**
	 * Sets column index, target area width and height. Preserves row index, sets anchors string to "lrtb".
	 * @param x column index
	 * @param w width, number of columns
	 * @param h height, number of rows
 	 * @return this
	 */
	public HIGConstraints xwh(int x, int w, int h) {
		this.x = x;
		anchor = "rltb";
		this.w = w;
		this.h = h;
		return this;
	}
	/**
	 * Sets column index, target area width and height. Preserves row index, sets anchors string to "lrtb".
	 * @param c column index
	 * @param w width, number of columns
	 * @param h height, number of rows
 	 * @return this
	 */
	public HIGConstraints cwh(int c, int w, int h) {
		this.x = c;
		anchor = "rltb";
		this.w = w;
		this.h = h;
		return this;
	}

	/**
	 * Sets column index, target area width, height and anchors. Preserver row index.
	 * @param x column index
	 * @param w width, number of columns
	 * @param h height, number of rows
	 * @param anchors anchors string (of letters 'l','r','t','b')
 	 * @return this
	 */
	public HIGConstraints xwh(int x, int w, int h, String anchors) {
		this.x = x;
		anchor = anchors;
		this.w = w;
		this.h = h;
		return this;
	}
	
	/**
	 * Sets column index, target area width, height and anchors. Preserver row index.
	 * @param c column index
	 * @param w width, number of columns
	 * @param h height, number of rows
	 * @param anchors anchors string (of letters 'l','r','t','b')
 	 * @return this
	 */
	public HIGConstraints cwh(int c, int w, int h, String anchors) {
		this.x = c;
		anchor = anchors;
		this.w = w;
		this.h = h;
		return this;
	}
	
	/**
	 * Sets row index. Preserves column index, sets anchor to "rltb", width and height to 1.
	 * @param y row index
 	 * @return this
	 */
	public HIGConstraints y(int y) {
		this.y = y;
		anchor = "rltb";
		this.w = 1;
		this.h = 1;
		return this;
	}
	
	/**
	 * Sets row index. Preserves column index, sets anchor to "rltb", width and height to 1.
	 * @param r row index
 	 * @return this
	 */
	public HIGConstraints r(int r) {
		this.y = y;
		anchor = "rltb";
		this.w = 1;
		this.h = 1;
		return this;
	}

	/**
	 * Sets row index and anchors. Preserves column index, width and height to 1.
	 * @param y row index
	 * @param anchors anchors string (of letters 'l','r','t','b')
 	 * @return this
	 */
	public HIGConstraints y(int y, String anchors) {
		this.y = y;
		anchor = anchors;
		this.w = 1;
		this.h = 1;
		return this;
	}

	/**
	 * Sets row index and anchors. Preserves column index, width and height to 1.
	 * @param r row index
	 * @param anchors anchors string (of letters 'l','r','t','b')
 	 * @return this
	 */
	public HIGConstraints r(int r, String anchors) {
		this.y = r;
		anchor = anchors;
		this.w = 1;
		this.h = 1;
		return this;
	}

	/**
	 * Sets row index, target area width and height. Preserves row index, sets anchors string to "lrtb".
	 * @param y row index
	 * @param w width, number of columns
	 * @param h height, number of rows
 	 * @return this
	 */
	public HIGConstraints ywh(int y, int w, int h) {
		this.y = y;
		anchor = "rltb";
		this.w = w;
		this.h = h;
		return this;
	}
	
	/**
	 * Sets row index, target area width and height. Preserves row index, sets anchors string to "lrtb".
	 * @param r row index
	 * @param w width, number of columns
	 * @param h height, number of rows
 	 * @return this
	 */
	public HIGConstraints rwh(int r, int w, int h) {
		this.y = r;
		anchor = "rltb";
		this.w = w;
		this.h = h;
		return this;
	}

	/**
	 * Sets row index, target area width, height and anchors. Preserver column index.
	 * @param y row index
	 * @param w width, number of columns
	 * @param h height, number of rows
	 * @param anchors anchors string (of letters 'l','r','t','b')
 	 * @return this
	 */
	public HIGConstraints ywh(int y, int w, int h, String anchors) {
		this.y = y;
		anchor = anchors;
		this.w = w;
		this.h = h;
		return this;
	}
	/**
	 * Sets row index, target area width, height and anchors. Preserver column index.
	 * @param r row index
	 * @param w width, number of columns
	 * @param h height, number of rows
	 * @param anchors anchors string (of letters 'l','r','t','b')
 	 * @return this
	 */
	public HIGConstraints rwh(int r, int w, int h, String anchors) {
		this.y = r;
		anchor = anchors;
		this.w = w;
		this.h = h;
		return this;
	}

	/**
	 * Sets row and column index. Sets anchor to "rltb", width and height to 1.
	 * @param x column index
	 * @param y row index
 	 * @return this
	 */
	public HIGConstraints xy(int x, int y) {
		this.x = x;
		this.y = y;
		anchor = "rltb";
		this.w = 1;
		this.h = 1;
		return this;
	}
	
	/**
	 * Sets row and column index. Sets anchor to "rltb", width and height to 1.
	 * @param r row index
	 * @param c column index
 	 * @return this
	 */
	public HIGConstraints rc(int r, int c) {
		this.x = c;
		this.y = r;
		anchor = "rltb";
		this.w = 1;
		this.h = 1;
		return this;
	}

	/**
	 * Sets row and column index and anchors. Sets width and height to 1.
	 * @param x column index
	 * @param y row index
	 * @param anchors anchors string (of letters 'l','r','t','b')
 	 * @return this
	 */
	public HIGConstraints xy(int x, int y, String anchors) {
		this.x = x;
		this.y = y;
		anchor = anchors;
		this.w = 1;
		this.h = 1;
		return this;
	}

	/**
	 * Sets row and column index and anchors. Sets width and height to 1.
	 * @param r row index
	 * @param c column index
	 * @param anchors anchors string (of letters 'l','r','t','b')
 	 * @return this
	 */
	public HIGConstraints rc(int r, int c, String anchors) {
		this.x = c;
		this.y = r;
		anchor = anchors;
		this.w = 1;
		this.h = 1;
		return this;
	}

	/**
	 * Sets row and column index, width and height. Sets anchors to "lrtb".
	 * @param x column index
	 * @param y row index
	 * @param w width, number of columns
	 * @param h height, number of rows
 	 * @return this
	 */
	public HIGConstraints xywh(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		anchor = "rltb";
		this.w = w;
		this.h = h;
		return this;
	}
	
	/**
	 * Sets row and column index, width and height. Sets anchors to "lrtb".
	 * @param r row index
	 * @param c column index
	 * @param w width, number of columns
	 * @param h height, number of rows
 	 * @return this
	 */
	public HIGConstraints rcwh(int r, int c, int w, int h) {
		this.x = c;
		this.y = r;
		anchor = "rltb";
		this.w = w;
		this.h = h;
		return this;
	}

	/**
	 * Sets row and column index, width, height and anchors.
	 * @param x column index
	 * @param y row index
	 * @param w width, number of columns
	 * @param h height, number of rows
	 * @param anchors anchors string (of letters 'l','r','t','b')
 	 * @return this
	 */
	public HIGConstraints xywh(int x, int y, int w, int h, String anchors) {
		this.x = x;
		this.y = y;
		anchor = anchors;
		this.w = w;
		this.h = h;
		return this;
	}

	/**
	 * Sets row and column index, width, height and anchors.
	 * @param r row index
	 * @param c column index
	 * @param w width, number of columns
	 * @param h height, number of rows
	 * @param anchors anchors string (of letters 'l','r','t','b')
 	 * @return this
	 */
	public HIGConstraints rcwh(int r, int c, int w, int h, String anchors) {
		this.x = c;
		this.y = r;
		anchor = anchors;
		this.w = w;
		this.h = h;
		return this;
	}
	
	/**
	 * Sets width to absolute size in pixels. Preserves all other settings.
 	 * @return this
	 */
	public HIGConstraints W(int w) {
		this.w = -w;	
		return this;
	}
	/**
	 * Sets width to absolute size in pixels. Preserves all other settings.
 	 * @return this
	 */
	public HIGConstraints H(int h) {
		this.h = -h;
		return this;
	}
	/**
	 * Sets anchors string. Preserves all other settings.
	 * @param anchors anchors string (of letters 'l','r','t','b')
 	 * @return this
	 */
	public HIGConstraints anchors(String anchors) {
		this.anchor = anchors;
		return this;
	}
	/**
	 * Returns current column index.
	 */
	public int x() { return x;}
	/**
	 * Returns current row index.
	 */
	public int y() { return y;}
	/**
	 * Returns current column index.
	 */
	public int c() { return x;}
	/**
	 * Returns current row index.
	 */
	public int r() { return y;}
}

