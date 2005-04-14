package sidekick.perl;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import sidekick.*;

/**
 * PerlAsset: extends sidekick.Asset, provides the nodes for a perl structure tree
 *
 * @author     Martin Raspe
 * @created    March 3, 2005
 * @modified   $Date$ by $Author$
 * @version    $Revision$
 */
public class PerlAsset extends Asset {
	private String shortDesc;
	private String longDesc;
	private Icon _icon;
	private int _lineNo;
	private int _type;

	public PerlAsset() {
		super("PerlAsset");
		}

	public PerlAsset(String name, int lineNo, Position start) {
		super(name);
		this.start = start;
		this.end = start;
		this.shortDesc = name;
		this.longDesc = "Line: " + lineNo;
		_lineNo = lineNo;
		}

	public int getLineNo() {
		return _lineNo;
		}
	
	public Icon getIcon() {
		return _icon;
		}
	
	public void setIcon(ImageIcon icon) {
		_icon = icon;
		}
	
	public int get_type() {
		return _type;
		}

	public String getShortString() {
		return shortDesc;
		}

	public String getLongString() {
		return longDesc;
		}

	public void setEnd(Position end) {
		this.end = end;
		}

	public void setShort(String shortDesc) {
		this.shortDesc = shortDesc;
		}

	public void setLong(String longDesc) {
		this.longDesc = longDesc;
		}

	public String toString() {
		return shortDesc + " " + longDesc;
		}

	
}
