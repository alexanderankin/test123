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
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.gjt.sp.util.Log;
//}}}


/**
 *	Create decorated icons for VPT nodes
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
	final static int msg_state_messages=1;
	//}}}

	//{{{ Attributes
	private final static Icon[][][][][] cache= new Icon[2][1][1][3][2];

	private final static Icon file_state_changed_img=
		new ImageIcon(IconComposer.class.getResource("/images/file_state_changed.png"));
	private final static Icon file_state_readonly_img=
		new ImageIcon(IconComposer.class.getResource("/images/file_state_readonly.png"));
	private final static Icon msg_state_messages_img=
		new ImageIcon(IconComposer.class.getResource("/images/msg_state_messages.png"));
	//}}}

	//{{{ Public methods
	//{{{ composeIcon(Icon, int, int, int, int) method
	static Icon composeIcon(Icon baseIcon,int base_state,int vc_state, int unused, int file_state,int msg_state) {
		Icon res=baseIcon;
		try {
			if(cache[base_state][vc_state][0][file_state][msg_state]==null) {
				Icon tl=null;
				Icon tr=null;
				Icon bl=null;
				switch(file_state) {
					case IconComposer.file_state_changed: bl=IconComposer.file_state_changed_img;break;
					case IconComposer.file_state_readonly: bl=IconComposer.file_state_readonly_img;break;
				}
				Icon br=null;
				switch(msg_state) {
					case IconComposer.msg_state_messages: br=IconComposer.msg_state_messages_img;break;
				}
				cache[base_state][vc_state][0][file_state][msg_state]=composeIcons(baseIcon,tl,tr,bl,br);
			}
			res=cache[base_state][vc_state][0][file_state][msg_state];
		}
		catch(ArrayIndexOutOfBoundsException ex) {
			Log.log(Log.WARNING, null, ex);
		}
		return(res);
	} //}}}

	//{{{ composeImages(Icon, Icon, Icon, Icon, Icon) method
	private static Icon composeIcons(Icon baseIcon, Icon tl, Icon tr, Icon bl, Icon br) {
		// copy base image
		Icon compositeIcon = baseIcon;
		int baseWidth=compositeIcon.getIconWidth();
		int baseHeight=compositeIcon.getIconHeight();

		//Log.log(Log.DEBUG, null, "baseSize :["+baseWidth+"x"+baseHeight+"]");

		if(tl!=null) {
			compositeIcon=composeIcons(compositeIcon,tl,0,0);
		}
		if(tr!=null) {
			int decoWidth=tr.getIconWidth();
			compositeIcon=composeIcons(compositeIcon,tr,baseWidth-decoWidth,0);
		}
		if(bl!=null) {
			int decoHeight=bl.getIconHeight();
			compositeIcon=composeIcons(compositeIcon,bl,0,baseHeight-decoHeight);
		}
		if(br!=null) {
			int decoWidth=br.getIconWidth();
			int decoHeight=br.getIconHeight();
			compositeIcon=composeIcons(compositeIcon,br,baseWidth-decoWidth,baseHeight-decoHeight);
		}
		return(compositeIcon);
	}
	//}}}

	//{{{ composeIcons(Icon, Icon, int, int)
	private static Icon composeIcons(Icon baseIcon, Icon decoIcon, int px, int py) {
		Image baseImage=((ImageIcon)baseIcon).getImage();
		int baseWidth=baseIcon.getIconWidth();
		int baseHeight=baseIcon.getIconHeight();
		int [] base = new int[baseWidth*baseHeight];
		PixelGrabber basePG = new PixelGrabber(baseImage, 0, 0, baseWidth, baseHeight, base, 0, baseWidth);
		try { basePG.grabPixels(); } catch (Exception ie1) { }

		Image decoImage=((ImageIcon)decoIcon).getImage();
		int decoWidth=decoIcon.getIconWidth();
		int decoHeight=decoIcon.getIconHeight();
		int [] deco = new int[decoWidth*decoHeight];
		PixelGrabber decoPG = new PixelGrabber(decoImage, 0, 0, decoWidth, decoHeight, deco, 0, decoWidth);
		try { decoPG.grabPixels(); } catch (Exception ie1) { }

		//Log.log(Log.DEBUG, null, "baseSize :["+baseWidth+"x"+baseHeight+"]");
		//Log.log(Log.DEBUG, null, "decoSize :["+decoWidth+"x"+decoHeight+"]");

		// overlay base icon with deco icon
		int baseIx,decoIx;
		int p,bb,bg,br,ba,db,dg,dr,da,r,g,b,a;
		double w;
		for(int y = 0; y < decoHeight; y++) {
			for(int x = 0; x < decoWidth; x++) {
				decoIx = y * decoWidth + x;
				baseIx = (py+y) * baseWidth +(px+x);

				// read pixels
				p=base[baseIx];
				bb =  p        & 0x00ff;
				bg = (p >>  8) & 0x00ff;
				br = (p >> 16) & 0x00ff;
				ba = (p >> 24) & 0x00ff;
				p=deco[decoIx];
				db =  p        & 0x00ff;
				dg = (p >>  8) & 0x00ff;
				dr = (p >> 16) & 0x00ff;
				da = (p >> 24) & 0x00ff;
				// combining the pixels
				w = (da / 255.0);
				r = ((int)(br + dr * w)) >> 1;
				r = (r < 0)?(0):((r>255)?(255):(r));
				g = ((int)(bg + dg * w)) >> 1;
				g = (g < 0)?(0):((g>255)?(255):(g));
				b = ((int)(bb + db * w)) >> 1;
				b =  (b < 0)?(0):((b>255)?(255):(b));
				a = ((int)(ba + da * w)) >> 1;
				a =  (a < 0)?(0):((a>255)?(255):(a));

				p = (((((a << 8) + (r & 0x0ff)) << 8) + (g & 0x0ff)) << 8) + (b & 0x0ff);
				// save the pixel
				base[baseIx] = p;
			}
		}

		ColorModel cm=ColorModel.getRGBdefault();
		MemoryImageSource mis=new MemoryImageSource(baseWidth, baseHeight, cm, base, 0, baseWidth);
		Image compositeImage = Toolkit.getDefaultToolkit().createImage(mis);

		return(new ImageIcon(compositeImage));
	}//}}}

	//}}}
}

