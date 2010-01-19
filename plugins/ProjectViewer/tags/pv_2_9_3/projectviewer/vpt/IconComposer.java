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
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.util.Log;

import errorlist.ErrorSource;

import projectviewer.config.ProjectViewerConfig;
import projectviewer.config.VersionControlService;
//}}}

/**
 *	Create decorated icons for VPT nodes
 *
 *	@author		Stefan Kost
 *	@version	$Id$
 */
public final class IconComposer {

	//{{{ Private constants
	private final static int FILE_STATE_NORMAL		= 0;
	private final static int FILE_STATE_CHANGED		= 1;
	private final static int FILE_STATE_READONLY	= 2;
	private final static int FILE_STATE_NOT_FOUND	= 3;

	private final static int MSG_STATE_NONE			= 0;
	private final static int MSG_STATE_MESSAGES		= 1;
	private final static int MSG_STATE_ERRORS		= 2;

	private final static Icon FILE_STATE_CHANGED_IMG =
		new ImageIcon(IconComposer.class.getResource("/projectviewer/images/file_state_changed.png"));
	private final static Icon FILE_STATE_READONLY_IMG =
		new ImageIcon(IconComposer.class.getResource("/projectviewer/images/file_state_readonly.png"));
	private final static Icon FILE_STATE_NOT_FOUND_IMG =
		new ImageIcon(IconComposer.class.getResource("/projectviewer/images/file_state_not_found.png"));
	private final static Icon MSG_STATE_MESSAGES_IMG =
		new ImageIcon(IconComposer.class.getResource("/projectviewer/images/msg_state_messages.png"));
	private final static Icon MSG_STATE_ERRORS_IMG =
		new ImageIcon(IconComposer.class.getResource("/projectviewer/images/msg_state_errors.png"));
	//}}}

	private final VersionControlService vcservice;

	/**
	 * Constructs a new icon composer that will use the given version
	 * control service for vc-related status.
	 *
	 * @param	vcservice	Version control service instance, or null.
	 */
	IconComposer(VersionControlService vcservice)
	{
		this.vcservice = vcservice;
	}


	/**
	 * Composes a new icon by overlaying status icons on top of the
	 * given base icon. Currently, three status icons are supported:
	 *
	 * <ul>
	 *   <li>top left: version control status, if configured for the
	 *                 project.</li>
	 *
	 *   <li>bottom left: file status (read only, dirty, etc).</li>
	 *
	 *   <li>bottom right: error list status (if ErrorList is available).</li>
	 * </ul>
	 *
	 * @param	node		Node to query.
	 * @param	baseIcon	Icon where to overlay status icons.
	 *
	 * @return A new icon with the overlayed status icons.
	 */
	public Icon composeIcon(VPTNode node,
							Icon baseIcon)
	{
		int msg_state = MSG_STATE_NONE;
		int file_state = FILE_STATE_NORMAL;
		if (node.isFile()) {
			VPTFile f = (VPTFile) node;
			VFSFile vfsf = f.getFile(true);
			file_state = (vfsf != null) ? getFileState(vfsf, f.getURL())
			                            : FILE_STATE_NOT_FOUND;
			if (ProjectViewerConfig.getInstance().isErrorListAvailable()) {
				msg_state = Helper.getMessageState(f.getURL());
			}
		}

		Icon tr = null; // unused

		Icon tl = null; // vc_state
		if (vcservice != null) {
			int vcstate = vcservice.getNodeState(node);
			if (vcstate != VersionControlService.VC_STATUS_NORMAL) {
				tl = vcservice.getIcon(vcstate);
			}
		}

		Icon bl = null; // file_state
		switch(file_state) {
			case FILE_STATE_CHANGED:
				bl = FILE_STATE_CHANGED_IMG;
				break;

			case FILE_STATE_READONLY:
				bl = FILE_STATE_READONLY_IMG;
				break;

			case FILE_STATE_NOT_FOUND:
				bl = FILE_STATE_NOT_FOUND_IMG;
				break;
		}

		Icon br = null; // msg_state
		switch(msg_state) {
			case MSG_STATE_MESSAGES:
				br = MSG_STATE_MESSAGES_IMG;
				break;
			case MSG_STATE_ERRORS:
				br = MSG_STATE_ERRORS_IMG;
				break;
		}

		return composeIcons(baseIcon, tl, tr, bl, br);
	}


	private Icon composeIcons(Icon baseIcon,
							  Icon tl,
							  Icon tr,
							  Icon bl,
							  Icon br)
	{
		// copy base image
		int baseWidth = baseIcon.getIconWidth();
		int baseHeight = baseIcon.getIconHeight();

		if (tl != null) {
			baseIcon = composeIcons(baseIcon, tl, 0, 0);
		}
		if (tr != null) {
			baseIcon = composeIcons(baseIcon, tr, baseWidth - tr.getIconWidth(), 0);
		}
		if (bl != null) {
			baseIcon = composeIcons(baseIcon, bl, 0, baseHeight - bl.getIconHeight());
		}
		if (br != null) {
			baseIcon = composeIcons(baseIcon, br,
				baseWidth - br.getIconWidth(), baseHeight  - br.getIconHeight());
		}

		return baseIcon;
	}


	private Icon composeIcons(Icon baseIcon,
							  Icon decoIcon,
							  int px,
							  int py)
	{
		Image baseImage;
		if (baseIcon instanceof ImageIcon) {
			baseImage = ((ImageIcon)baseIcon).getImage();
		} else {
			// not an image icon. do this ugly thing.
			baseImage = new BufferedImage(baseIcon.getIconWidth(),
										  baseIcon.getIconHeight(),
										  BufferedImage.TYPE_INT_ARGB);
			Graphics g = baseImage.getGraphics();
			baseIcon.paintIcon(null, g, 0, 0);
			g.dispose();
		}
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

		int baseIx,decoIx;
		int p,bb,bg,br,ba,db,dg,dr,da,r,g,b,a;
		double bw,dw;
		for (int y = 0; y < decoHeight; y++) {
			for (int x = 0; x < decoWidth; x++) {
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
				/*
				dw = (da / 255.0);
				r = ((int)(br + dr * dw)) >> 1;
				r = (r < 0)?(0):((r>255)?(255):(r));
				g = ((int)(bg + dg * dw)) >> 1;
				g = (g < 0)?(0):((g>255)?(255):(g));
				b = ((int)(bb + db * dw)) >> 1;
				b =  (b < 0)?(0):((b>255)?(255):(b));
				a = ((int)(ba + da * dw)) >> 1;
				a =  (a < 0)?(0):((a>255)?(255):(a));
				*/
				dw = (da / 255.0);bw = 1.0 - dw;
				r = (int)(br * bw + dr * dw);
				r = (r < 0)?(0):((r>255)?(255):(r));
				g = (int)(bg * bw + dg * dw);
				g = (g < 0)?(0):((g>255)?(255):(g));
				b = (int)(bb * bw + db * dw);
				b =  (b < 0)?(0):((b>255)?(255):(b));
				a = (int)(ba * bw + da * dw);
				a =  (a < 0)?(0):((a>255)?(255):(a));

				p = (((((a << 8) + (r & 0x0ff)) << 8) + (g & 0x0ff)) << 8) + (b & 0x0ff);
				// save the pixel
				base[baseIx] = p;
			}
		}

		ColorModel cm = ColorModel.getRGBdefault();
		MemoryImageSource mis =
			new MemoryImageSource(baseWidth, baseHeight, cm, base, 0, baseWidth);
		Image compositeImage = Toolkit.getDefaultToolkit().createImage(mis);

		return (new ImageIcon(compositeImage));
	}


	private int getFileState(VFSFile f,
							 String path)
	{
		if (f != null && !f.isReadable())
			return FILE_STATE_NOT_FOUND;
		Buffer buffer = jEdit.getBuffer(path);
		int file_state = IconComposer.FILE_STATE_NORMAL;
		if (buffer != null) {
			if (buffer.isDirty()) {
				return FILE_STATE_CHANGED;
			} else if (!buffer.isEditable()) {
				return FILE_STATE_READONLY;
			}
		} else if (!f.isWriteable()) {
			return FILE_STATE_READONLY;
		}
		return FILE_STATE_NORMAL;
	}


	//{{{ -class _Helper_
	/**
	 *	Class to hold references to classes that may not be available, so this
	 *	class can behave well when called from a BeanShell script.
	 */
	private static class Helper {

		//{{{ +_getMessageState(String)_ : int
		public static int getMessageState(String path) {
			int msg_state = IconComposer.MSG_STATE_NONE;
			ErrorSource[] sources = ErrorSource.getErrorSources();
			for(int i = 0; i < sources.length; i++) {
				if (sources[i].getFileErrorCount(path) > 0) {
					msg_state = IconComposer.MSG_STATE_MESSAGES;
					ErrorSource.Error[] errors = sources[i].getAllErrors();
					for(int j=0; j < errors.length; j++) {
						if(errors[j].getErrorType() == ErrorSource.ERROR
								&& errors[j].getFilePath().equals(path)) {
							msg_state = IconComposer.MSG_STATE_ERRORS;
							break;
						}
					}
					break;
				}
			}
			return msg_state;
		} //}}}

	} //}}}

}

