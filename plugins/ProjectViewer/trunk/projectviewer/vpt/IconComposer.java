/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package projectviewer.vpt;

//{{{ Imports
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.GUIUtilities;
//}}}


/**
 *	Create decorated icons for project nodes
 *
 *	@author		Stefan Kost
 *	@version	$Id$
 */
public final class IconComposer {

	//{{{ Constants
	final static int file_state_normal=0;
	final static int file_state_changed=1;
	final static int file_state_readonly=2;

	final static int vc_state_none=0;

	final static int msg_state_none=0;
	//}}}

	//{{{ Attributes
	private final static Icon[][][][] cache= new Icon[2][1][1][1];

	private final static Image file_state_changed_img=((ImageIcon)GUIUtilities.loadIcon("File.png")).getImage();
	private final static Image file_state_readonly_img=((ImageIcon)GUIUtilities.loadIcon("File.png")).getImage();
	//}}}

	//{{{ Public methods
	//{{{ composeIcon(Icon, int, int, int, int) method
	static Icon composeIcon(Icon baseIcon,int file_state, int vc_state, int msg_state, int unused) {
		Icon res=baseIcon;
		try {
			if(cache[file_state][vc_state][msg_state][0]==null) {
				Image baseImage=((ImageIcon)baseIcon).getImage();
				Image tl=null;
				switch(file_state) {
					case IconComposer.file_state_changed: tl=IconComposer.file_state_changed_img;break;
					case IconComposer.file_state_readonly: tl=IconComposer.file_state_readonly_img;break;
				}
				Image tr=null;
				Image bl=null;
				Image br=null;
				Image compositeImage=composeImages(baseImage,tl,tr,bl,br);
				cache[file_state][vc_state][msg_state][0]=new ImageIcon(compositeImage);
			}
			res=cache[file_state][vc_state][msg_state][0];
		}
		catch(ArrayIndexOutOfBoundsException ex) {
			Log.log(Log.WARNING, null, ex);
		}
		return(res);
	} //}}}

	//{{{ composeImages(Image, Image, Image, Image, Image) method
	private static Image composeImages(Image base, Image tl, Image tr, Image bl, Image br) {
		// copy base image
		if(tl!=null) { composeImages(base,tl, 1, 1); }
		if(tr!=null) { composeImages(base,tl,-1, 1); }
		if(bl!=null) { composeImages(base,tl, 1,-1); }
		if(br!=null) { composeImages(base,tl,-1,-1); }
		return(base);
	}
	//}}}

	//{{{ composeImages(Image, Image, int, int)
	private static Image composeImages(Image base, Image deco, int px, int py) {
		// overlay base image with deco image
		return(base);
	}//}}}
}

