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
import java.io.File;
import java.util.HashMap;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import errorlist.ErrorListPlugin;
import errorlist.ErrorSource;

import projectviewer.config.ProjectViewerConfig;
//}}}

/**
 *	Create decorated icons for VPT nodes
 *
 *	@author		Stefan Kost
 *	@version	$Id$
 */
public final class IconComposer {

	//{{{ Constants
	public final static int FILE_STATE_NORMAL		= 0;
	public final static int FILE_STATE_CHANGED		= 1;
	public final static int FILE_STATE_READONLY		= 2;
	public final static int FILE_STATE_NOT_FOUND	= 3;

	public final static int VC_STATE_NONE			= 0;
	public final static int VC_STATE_LOCAL_MOD		= 1;
	public final static int VC_STATE_LOCAL_ADD		= 2;
	public final static int VC_STATE_LOCAL_RM		= 3;
	public final static int VC_STATE_NEED_UPDATE	= 4;
	public final static int VC_STATE_CONFLICT		= 5;

	public final static int MSG_STATE_NONE			= 0;
	public final static int MSG_STATE_MESSAGES		= 1;
	public final static int MSG_STATE_ERRORS		= 2;
	//}}}

	//{{{ Attributes
	private final static HashMap iconCache = new HashMap();

	private final static Icon FILE_STATE_CHANGED_IMG =
		new ImageIcon(IconComposer.class.getResource("/images/file_state_changed.png"));
	private final static Icon FILE_STATE_READONLY_IMG =
		new ImageIcon(IconComposer.class.getResource("/images/file_state_readonly.png"));
	private final static Icon FILE_STATE_NOT_FOUND_IMG =
		new ImageIcon(IconComposer.class.getResource("/images/file_state_not_found.png"));
	private final static Icon MSG_STATE_MESSAGES_IMG =
		new ImageIcon(IconComposer.class.getResource("/images/msg_state_messages.png"));
	private final static Icon MSG_STATE_ERRORS_IMG =
		new ImageIcon(IconComposer.class.getResource("/images/msg_state_errors.png"));

	private static VCProvider vcProvider = null;
	//}}}

	//{{{ Public methods

	//{{{ +_setVersionControlProvider(VCProvider)_ : void
	public static void setVersionControlProvider(VCProvider vc) {
		iconCache.clear();
		vcProvider = vc;
	} //}}}

	//{{{ +_composeIcon(File, String, Icon)_ : Icon
	public static Icon composeIcon(File f, String path, Icon baseIcon) {
		Icon[][][][] cache = getIconCache(baseIcon);

		int msg_state = MSG_STATE_NONE;
		if (ProjectViewerConfig.getInstance().isErrorListAvailable()) {
			msg_state = Helper.getMessageState(path);
		}

		int file_state = getFileState(f, path);
		int vc_state = VC_STATE_NONE;
		if (vcProvider != null) {
			vc_state = vcProvider.getFileState(f, path);
		}

		try {
			if(cache[vc_state][0][file_state][msg_state] == null) {
				Icon tl = null; // vc_state
				Icon tr = null; // unused
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
				if (vcProvider != null)
					tl = vcProvider.getIcon(vc_state);
				cache[vc_state][0][file_state][msg_state] =
					composeIcons(baseIcon, tl, tr, bl, br);
			}
			baseIcon = cache[vc_state][0][file_state][msg_state];
		} catch(ArrayIndexOutOfBoundsException ex) {
			Log.log(Log.WARNING, null, ex);
		}
		return baseIcon;
	} //}}}

	//}}}

	//{{{ Private methods

	//{{{ -_composeIcons(Icon, Icon, Icon, Icon, Icon)_ : Icon
	private static Icon composeIcons(Icon baseIcon, Icon tl, Icon tr, Icon bl, Icon br) {
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
	} //}}}

	//{{{ -_composeIcons(Icon, Icon, int, int)_ : Icon
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
		double bw,dw;
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
	} //}}}

	//{{{ -_getIconCache(Icon)_ : Icon[][][][][]
	private static Icon[][][][] getIconCache(Icon icon) {
		Icon[][][][] cache = (Icon[][][][]) iconCache.get(icon);
		if (cache == null) {
			if (vcProvider == null)
				cache = new Icon[1][1][4][3];
			else
				cache = new Icon[6][1][4][3];
			iconCache.put(icon, cache);
		}
		return cache;
	} //}}}

	//{{{ -_getFileState(File, String)_ : int
	private static int getFileState(File f, String path) {
		if (f != null && !f.exists())
			return FILE_STATE_NOT_FOUND;
		Buffer buffer = jEdit.getBuffer(path);
		int file_state = IconComposer.FILE_STATE_NORMAL;
		if (buffer != null) {
			if(buffer.isDirty()) {
				return FILE_STATE_CHANGED;
			} else if (!buffer.isEditable()) {
				return FILE_STATE_READONLY;
			}
		}
		return FILE_STATE_NORMAL;
	} //}}}

	//}}}

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
						if(errors[j].getErrorType() == ErrorSource.ERROR) {
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

	//{{{ +class _VCProvider_
	/**
	 *	Version control plugins that want to provide file status info to PV
	 *	should implement this interface.
	 *
	 *	@since	PV 2.1.0
	 */
	public static interface VCProvider {

		//{{{ +*getFileState(File, String)* : int
		/**
		 *	This method should return one of the VC_STATE possible values
		 *	for the given file. The file argument may be null, in which
		 *	case the path should be used to identify the file (this will
		 *	be the case, for example, with files from a VFS).
		 *
		 *	<p>No range checking is performed on the returned values, so
		 *	make sure that the value is one of the defined constants.</p>
		 *
		 *	@param	f		The file, if it's a local file.
		 *	@param	path	The path to the file (absolute path if local, VFS
		 *					URL otherwise).
		 */
		public int getFileState(File f, String path); //}}}

		//{{{ +*getIcon(int)* : Icon
		/**
		 *	This should return the icon to be used to represent the requested
		 *	state.
		 *
		 *	@param	state	One of the defined VC_STATE_* constants.
		 */
		public Icon getIcon(int state); //}}}

	} //}}}

}

