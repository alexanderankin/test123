package common.gui.util;

import javax.swing.*;
import java.awt.*;

/**
 * Provides easier creation of GridBagConstraints
 *
 * @author    mace
 * @created   October 22, 2002
 */
public class ConstraintFactory {
	public final static int NONE = GridBagConstraints.NONE;
	public final static int BOTH = GridBagConstraints.BOTH;
	public final static int HORIZONTAL = GridBagConstraints.HORIZONTAL;
	public final static int H = HORIZONTAL;
	public final static int VERTICAL = GridBagConstraints.VERTICAL;
	public final static int V = VERTICAL;
	public final static int RELATIVE = GridBagConstraints.RELATIVE;
	public final static int REMAINDER = GridBagConstraints.REMAINDER;
	public final static int CENTER = GridBagConstraints.CENTER;
	public final static int N = GridBagConstraints.NORTH;
	public final static int E = GridBagConstraints.EAST;
	public final static int S = GridBagConstraints.SOUTH;
	public final static int W = GridBagConstraints.WEST;
	public final static int NW = GridBagConstraints.NORTHWEST;
	public final static int NE = GridBagConstraints.NORTHEAST;
	public final static int SW = GridBagConstraints.SOUTHWEST;
	public final static int SE = GridBagConstraints.SOUTHEAST;

	protected int DEFAULT_WEIGHTX = 100;
	protected int DEFAULT_WEIGHTY = 100;
	protected int DEFAULT_ANCHOR = NW;
	protected int DEFAULT_FILL = BOTH;
	protected Insets DEFAULT_INSETS = new Insets(0, 0, 0, 0);
	protected int DEFAULT_IPADX = 0;
	protected int DEFAULT_IPADY = 0;

	protected int LAST_X = 0;
	protected int LAST_Y = 0;
	protected int LAST_WIDTH = 0;
	protected int LAST_HEIGHT = 0;

	public ConstraintFactory() { }

	private GridBagConstraints buildConstraints() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = DEFAULT_WEIGHTX;
		constraints.weighty = DEFAULT_WEIGHTY;
		constraints.anchor = DEFAULT_ANCHOR;
		constraints.fill = DEFAULT_FILL;
		constraints.insets = DEFAULT_INSETS;
		constraints.ipadx = DEFAULT_IPADX;
		constraints.ipady = DEFAULT_IPADY;
		return constraints;
	}

	/**
	 * Build basic constraints. weights default to 100, anchor defaults to NW, fill
	 * defaults to BOTH. If a negative value is given, the last non-negative value
	 * will be re-used
	 *
	 * @param x       an int specifying the object's x grid position
	 * @param y       an int specifying the object's y grid position
	 * @param width   an int specifying the object's width
	 * @param height  an int specifying the object's height
	 * @return        a GridBagConstraints object
	 */
	public GridBagConstraints buildConstraints
			(int x, int y, int width, int height) {
		GridBagConstraints constraints = buildConstraints();
		if (x < 0) {
			constraints.gridx = LAST_X;
		} else {
			constraints.gridx = LAST_X = x;
		}
		if (y < 0) {
			constraints.gridx = LAST_Y;
		} else {
			constraints.gridy = LAST_Y = y;
		}
		if (width < 0) {
			constraints.gridwidth = LAST_WIDTH;
		} else {
			constraints.gridwidth = LAST_WIDTH = width;
		}
		if (height < 0) {
			constraints.gridheight = LAST_HEIGHT;
		} else {
			constraints.gridheight = LAST_HEIGHT = height;
		}
		return constraints;
	}

	/**
	 * Builds basic constraints + anchor and fill constraints. Weights default to
	 * 100
	 *
	 * @param x       an int specifying the object's x grid position
	 * @param y       an int specifying the object's y grid position
	 * @param width   an int specifying the object's width
	 * @param height  an int specifying the object's height
	 * @param anchor  an int specifying the position to place the object
	 * @param fill    an int specifying which directions to fill
	 * @return        a GridBagConstraints object
	 */
	public GridBagConstraints buildConstraints
			(int x, int y, int width, int height, int anchor, int fill) {
		GridBagConstraints constraints = buildConstraints(x, y, width, height);
		constraints.anchor = anchor;
		constraints.fill = fill;
		return constraints;
	}

	/**
	 * Builds basic constraints + anchor,fill, and weights.
	 *
	 * @param x        an int specifying the object's x grid position
	 * @param y        an int specifying the object's y grid position
	 * @param width    an int specifying the object's width
	 * @param height   an int specifying the object's height
	 * @param anchor   an int specifying the position to place the object
	 * @param fill     an int specifying which directions to fill
	 * @param xweight  an int specifying the object's x weight
	 * @param yweight  an int specifying the object's y weight
	 * @return         a GridBagConstraints object
	 */
	public GridBagConstraints buildConstraints
			(int x, int y, int width, int height, int anchor, int fill, int xweight,
			int yweight) {
		GridBagConstraints constraints
				 = buildConstraints(x, y, width, height, anchor, fill);
		constraints.weightx = xweight;
		constraints.weighty = yweight;
		return constraints;
	}

	/**
	 * Builds basic constraints + anchor,fill, weights, and insets.
	 *
	 * @param x        an int specifying the object's x grid position
	 * @param y        an int specifying the object's y grid position
	 * @param width    an int specifying the object's width
	 * @param height   an int specifying the object's height
	 * @param anchor   an int specifying the position to place the object
	 * @param fill     an int specifying which directions to fill
	 * @param weightx  an int specifying the object's x weight
	 * @param weighty  an int specifying the object's y weight
	 * @param i        <TT>Insets</TT> to use for this object
	 * @return         a GridBagConstraints object
	 */
	public GridBagConstraints buildConstraints
			(int x, int y, int width, int height, int anchor, int fill, int weightx,
			int weighty, Insets i) {
		GridBagConstraints constraints
				 = buildConstraints(x, y, width, height, anchor, fill, weightx, weighty);
		constraints.insets = i;
		return constraints;
	}

	public void setDefaultAnchor(int i) {
		DEFAULT_ANCHOR = i;
	}

	public void setDefaultFill(int i) {
		DEFAULT_FILL = i;
	}

	public void setDefaultWeights(int x, int y) {
		setDefaultWeightX(x);
		setDefaultWeightY(y);
	}

	public void setDefaultWeightX(int i) {
		DEFAULT_WEIGHTX = i;
	}

	public void setDefaultWeightY(int i) {
		DEFAULT_WEIGHTY = i;
	}

	public void setDefaultInsets(Insets i) {
		DEFAULT_INSETS = i;
	}

	public void setDefaultInternalPaddingX(int i) {
		DEFAULT_IPADX = i;
	}

	public void setDefaultInternalPaddingY(int i) {
		DEFAULT_IPADY = i;
	}

	public void setDefaultInternalPadding(int i) {
		setDefaultInternalPaddingX(i);
		setDefaultInternalPaddingY(i);
	}
}

