/******************************************************************************
 *                                     __                                     *
 *                              <-----/@@\----->                              *
 *                             <-< <  \\//  > >->                             *
 *                               <-<-\ __ /->->                               *
 *                               Data /  \ Crow                               *
 *                                   ^    ^                                   *
 *                              info@datacrow.net                             *
 *                                                                            *
 *                       This file is part of Data Crow.                      *
 *       Data Crow is free software; you can redistribute it and/or           *
 *        modify it under the terms of the GNU General Public                 *
 *       License as published by the Free Software Foundation; either         *
 *              version 3 of the License, or any later version.               *
 *                                                                            *
 *        Data Crow is distributed in the hope that it will be useful,        *
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *           MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.             *
 *           See the GNU General Public License for more details.             *
 *                                                                            *
 *        You should have received a copy of the GNU General Public           *
 *  License along with this program. If not, see http://www.gnu.org/licenses  *
 *                                                                            *
 ******************************************************************************/

package launcher;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.gjt.sp.jedit.ActionContext;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.menu.EnhancedMenu;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextArea;

public class LauncherUtils {

	public static final String LABEL_SUFFIX = ".label";

	public static final JMenuItem SEPARATOR = new JMenuItem();


	//{{{ getFileExtension(String) : String
	/**
	 *	Returns the file's extension, or the file name if no extension can be
	 *	recognized.
	 *
	 *@param  filename
	 *@return	   The fileExtension value
	 */
	public static String getFileExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex == -1 || dotIndex == fileName.length() - 1)
			return fileName;
		return fileName.substring(dotIndex + 1);
	} //}}}

	public static void runCmd(String[] cmds) throws Exception {
		(new LauncherSwingWorker(cmds)).execute(); 
	}

	public static void runCmd(String cmd) throws Exception {
		(new LauncherSwingWorker(cmd)).execute(); 
	}

	@SuppressWarnings("unchecked")
	public static boolean isArrayOrCollection(Object resource) {
		return resource != null && (
				resource instanceof Collection ||
				resource.getClass().isArray() );
	}
	
	@SuppressWarnings("unchecked")
	public static Object getOnlyObjectFrom(Object resource) {
		if (resource == null)
			return null;
		Object firstAndOnly = null;
		if (resource.getClass().isArray() &&
				Array.getLength(resource) == 1) {
			firstAndOnly = Array.get(resource, 0);
		} else if (resource instanceof Collection) {
			Collection collection = (Collection)resource;
			if (collection.size() == 1)
				firstAndOnly = collection.iterator().next();
		} else {
			firstAndOnly = resource;
		}
		return firstAndOnly;
	}

	public static Buffer resolveToBuffer(Object resource) {
		try {
			resource = getOnlyObjectFrom(resource);
			if (resource == null)
				return null;
	    	Buffer buffer = null;
	    	if (resource instanceof Buffer) {
				buffer = (Buffer)resource;
			} else if (resource instanceof File) {
				File file = (File)resource;
				buffer = jEdit._getBuffer(file.getCanonicalPath());
			} else if (resource instanceof JEditTextArea) {
				JEditTextArea textArea = (JEditTextArea) resource;
				View view = textArea == null ? jEdit.getFirstView() : textArea.getView();
				buffer = view.getBuffer();
			} else {
				File file = resolveToFile(resource);
				if (file != null)
					buffer = jEdit._getBuffer(file.getCanonicalPath());
			}
	    	return buffer;
		} catch (Exception exp) {
			return null;
		}
	}
	
	public static File resolveToFile(Object resource) {
		try {
			resource = getOnlyObjectFrom(resource);
			if (resource == null)
				return null;
	    	File file = null;
	    	if (resource instanceof File) {
				file = (File)resource;
			} else if (resource instanceof URI) {
				file = new File((URI)resource);
			} else if (resource instanceof URL) {
				file = new File(((URL)resource).toURI());
			} else if (resource instanceof Buffer) {
				Buffer buffer = (Buffer)resource;
				file = buffer.isUntitled() ? null : new File(buffer.getPath());
			} else if (resource instanceof JEditTextArea) {
				JEditTextArea textArea = (JEditTextArea) resource;
				View view = textArea == null ? jEdit.getFirstView() : textArea.getView();
				Buffer buffer = view.getBuffer();
				file = buffer.isUntitled() ? null : new File(buffer.getPath());
			} else if (resource instanceof VFSFile) {
				VFSFile vfsFile = (VFSFile)resource;
				if (VFSHelper.isLocal(vfsFile))
					file = new File(vfsFile.getPath());
			} else if (resource instanceof String) {
				String path = resource.toString();
				if (VFSHelper.isLocal(path))
					file = new File(path);
			}
			return file.exists() ? file : null;
		} catch (Exception exp) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static File[] resolveToFileArray(Object resource) {
		if (resource == null)
			return null;
		if (resource instanceof File[])
			return (File[])resource;
		File[] files = null;
		if (resource instanceof Collection) {
			Collection collection = (Collection)resource;
			if (collection.isEmpty())
				return null;
			files = new File[collection.size()];
			int i = 0;
			for(Object res : collection) {
				File file = resolveToFile(res);
				if (file == null)
					return null;
				files[i++] = file;
			}
		} else if (resource.getClass().isArray()) {
			int size = Array.getLength(resource);
			if (size == 0)
				return null;
			files = new File[size];
			for (int i = 0; i < size; i++) {
				Object res = Array.get(resource, i);
				File file = resolveToFile(res);
				if (file == null)
					return null;
				files[i] = file;
			}
		} else {
			File file = resolveToFile(resource);
			if (file != null) {
				files = new File[1];
				files[0] = file;
			}
		}
		return files;
	}

	public static URI resolveToURI(Object resource) {
		try {
			resource = getOnlyObjectFrom(resource);
			if (resource == null)
				return null;
			URI uri = null;
			if (resource instanceof URI) {
				uri = (URI)resource;
			} else if (resource instanceof URL) {
				uri = ((URL)resource).toURI();
			} else if (resource instanceof File) {
				uri = ((File)resource).toURI();
			} else if (resource instanceof VFSFile) {
				VFSFile vfsFile = (VFSFile)resource;
				if (VFSHelper.isLocal(vfsFile))
					uri = new File(vfsFile.getPath()).toURI();
				else if (VFSHelper.isURL(vfsFile))
					uri = new URL(vfsFile.getPath()).toURI();
			} else if (resource instanceof Buffer) {
				Buffer buffer = (Buffer)resource;
				uri = buffer.isUntitled() ? null : new File(buffer.getPath()).toURI();
			} else if (resource instanceof JEditTextArea) {
				JEditTextArea textArea = (JEditTextArea) resource;
				View view = textArea == null ? jEdit.getFirstView() : textArea.getView();
				Buffer buffer = view.getBuffer();
				uri = buffer.isUntitled() ? null : (new File(buffer.getPath())).toURI();
			} else if (resource instanceof String) {
				String path = resource.toString();
				if (VFSHelper.isLocal(path))
					uri = new File(path).toURI();
				else if (VFSHelper.isURL(path))
					uri = new URL(path).toURI();
				else
					uri = new URI(path);
			}
			return uri;
		} catch (Exception exp) {
			return null;
		}
	}
    
	public static Object resolveToFileOrURIOrString(Object resource) {
		try {
			resource = getOnlyObjectFrom(resource);
			if (resource == null)
				return null;
			Object resolvedResource = null;
			if (resource instanceof String ||
					resource instanceof File ||
					resource instanceof URI) {
				resolvedResource = resource;
			} else if (resource instanceof URL) {
				resolvedResource = ((URL)resource).toURI();
			} else {
				resolvedResource = resolveToFile(resource);
			}
			return resolvedResource;
		} catch (Exception exp) {
			return null;
		}
	}
    
	public static CharSequence resolveToCharSequence(Object resource) {
		try {
			resource = getOnlyObjectFrom(resource);
			if (resource == null)
				return null;
			CharSequence resolvedResource = null;
			if (resource instanceof CharSequence) {
				resolvedResource = (CharSequence)resource;
			} else if (resource instanceof TextArea) {
				TextArea textArea = (TextArea) resource;
				resolvedResource = textArea.getSelectedText();
				if (resolvedResource == null)
					resolvedResource = textArea.getText();
			}
			return resolvedResource;
		} catch (Exception exp) {
			return null;
		}
	}
    
	public static CharSequence resolveToSelectedText(Object resource) {
		try {
			resource = getOnlyObjectFrom(resource);
			if (resource == null)
				return null;
			CharSequence resolvedResource = null;
			if (resource instanceof CharSequence) {
				resolvedResource = (CharSequence)resource;
			} else if (resource instanceof TextArea) {
				TextArea textArea = (TextArea) resource;
				resolvedResource = textArea.getSelectedText();
			}
			return resolvedResource;
		} catch (Exception exp) {
			return null;
		}
	}
    
	public static JMenuItem buildMenuItemFor(EditAction action,
			ActionContext context) {
		JMenuItem item = null;
		String actionLabelProp = action.getName() + LABEL_SUFFIX;
		String actionLabel = jEdit.getProperty(actionLabelProp);
		boolean tempLabelNeeded = actionLabel == null || actionLabel.trim().length() == 0;
		if (tempLabelNeeded) {
			String label = action.getLabel();
			if (label==null || label.trim().length() == 0)
				label = "NO LABEL FOR " + action.getName(); 
			jEdit.setTemporaryProperty(actionLabelProp, label);
		}
		item = GUIUtilities.loadMenuItem(context, action.getName(), true);
		if (tempLabelNeeded)
			jEdit.resetProperty(actionLabelProp);
		return item;
	}

	public static JMenuItem[] buildMenuItemsWith(
					String submenuName, String submenuLabel,
					Set<EditAction> level1Actions, 
					Set<EditAction> level2Actions,
					Map<EditAction,ActionContext> actionContexts) {
		// Build the list of 1st and 2nd level actions
		JMenuItem[] items = new JMenuItem[level2Actions.size() == 0 ?
		                                		level1Actions.size() :
		                                		level1Actions.size() + 1];
		// Build 1st level menu items
		int i = 0;
		for (EditAction action: level1Actions) {
			ActionContext context = actionContexts.get(action);
			items[i++] = buildMenuItemFor(action, context);
			//Log.log(Log.DEBUG, LauncherUtils.class, "1stLevelMenuItem=" + action.getLabel());
		}
		// Build submenu for 2nd level menu items
		if (level2Actions.isEmpty())
			return items;
		items[i] = new EnhancedMenu(submenuName,submenuLabel);
		for (EditAction action: level2Actions) {
			ActionContext context = actionContexts.get(action);
			items[i].add(buildMenuItemFor(action, context));
			//Log.log(Log.DEBUG, LauncherUtils.class, "2ndLevelMenuItem=" + action.getLabel());
		}
		return items;
	}

	public static void addItemsToMenu(JMenu menu, JMenuItem[] items) {
		if (menu == null)
			return;
		if (items != null && items.length>0) {
			for (JMenuItem item: items) {
				if (item == SEPARATOR)
					menu.addSeparator();
				else
					menu.add(item);
			}
		}
	}

}
