/*
 * InfoViewerPlugin.java - an info viewer plugin for jEdit
 * Copyright (C) 1999 2000 Dirk Moebius
 * based on the original jEdit HelpViewer by Slava Pestov.
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


import infoviewer.InfoViewer;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.lang.reflect.*;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.msg.ViewURL;
import org.gjt.sp.util.Log;


public class InfoViewerPlugin extends EBPlugin {

	/** the shared InfoViewer instance */
	private static InfoViewer infoviewer = null;


	// begin EditPlugin implementation
	public void createMenuItems(Vector menuItems) {
		menuItems.addElement(GUIUtilities.loadMenu("infoviewer-menu"));
	}


	public void createOptionPanes(OptionsDialog optionsDialog) {
		optionsDialog.addOptionPane(new infoviewer.InfoViewerOptionPane());
	}
	// end EditPlugin implementation


	// begin EBPlugin implementation
	/**
	 * handle messages from the EditBus. InfoViewer reacts to messages of
	 * type ViewURL. If it sees such a message, it will veto() it, so that
	 * the sender knows that it was seen.
	 * @param message the EditBus message
	 * @see org.gjt.sp.jedit.msg.ViewURL
	 * @see org.gjt.sp.jedit.EBMessage#veto()
	 */
	public void handleMessage(EBMessage message) {
		if (message instanceof ViewURL) {
			ViewURL vu = (ViewURL) message;
			// TODO: only veto, if InfoViewer understands the URL protocol!
			vu.veto();
			gotoURL(vu.getURL());
		}
	}
	// end EBPlugin implementation


	/**
	 * this function demonstrates how ViewURL messages should be send on
	 * the EditBus.
	 * @param url an URL that should be displayed in InfoViewer
	 * @param view a View from which the message is sent
	 */
	public void sendURL(URL url, View view) {
		// create a new ViewURL message with 'this' as source and the
		// current view.
		ViewURL vu = new ViewURL(this, view, url);
		// send the message on the EditBus
		EditBus.send(vu);
		// check if the message was heard by some other component. If it
		// was veto()ed, the message was heard, otherwise no component on
		// the EditBus listened for this message.
		// (This is not really necessary *here* because this *is* the
		// InfoViewer, that sends itself a message, but I put it here for
		// demonstrational purposes.)
		if (!vu.isVetoed()) {
			// no EditBus component listened for this message. Show an error:
			GUIUtilities.error(view, "infoviewer.error.noinfoviewer", null);
			return;
		}
	}


	public void gotoURL(URL url) {
		String u = (url == null ? "" : url.toString());
		String browsertype = jEdit.getProperty("infoviewer.browsertype");

		if (u.startsWith("jeditresource:")) {
			browsertype = "internal";
		}

		Log.log(Log.DEBUG, this, "(" + browsertype + "): gotoURL: " + u);

		if ("external".equals(browsertype)) {
			// use external browser:
			String cmd = jEdit.getProperty("infoviewer.otherBrowser");
			String[] args = convertCommandString(cmd, u);
			try {
				Runtime.getRuntime().exec(args);
			}
			catch(Exception ex) {
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < args.length; i++) {
					buf.append(args[i]);
					buf.append('\n');
				}
				GUIUtilities.error(null, "infoviewer.error.invokeBrowser",
					new Object[] { ex, buf.toString() });
				return;
			}
		}

		else if ("class".equals(browsertype)) {
			// use class + method
			String clazzname = jEdit.getProperty("infoviewer.class");
			String methodname = jEdit.getProperty("infoviewer.method");
			gotoURLWithMethod(u, clazzname, methodname);
		}

		else if ("netscape".equals(browsertype)) {
			// use Netscape:
			String[] args = new String[3];
			args[0] = "sh";
			args[1] = "-c";
			args[2] = "netscape -remote openURL\\('" + u
					  + "'\\) -raise || netscape '" + u + "'";
			try {
				Runtime.getRuntime().exec(args);
			}
			catch(Exception ex) {
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < args.length; i++) {
					buf.append(args[i]);
					buf.append('\n');
				}
				GUIUtilities.error(null,"infoviewer.error.invokeBrowser",
					new Object[] { ex, buf.toString() });
			}
		}

		else {
			// use internal InfoViewer browser:
			if (infoviewer == null) {
				infoviewer = new InfoViewer();
			}
			infoviewer.setVisible(true);
			infoviewer.gotoURL(url, true);
		}
	}


	/**
	 * converts the command string, which may contain "$u" as placeholders
	 * for an url, into an array of strings, tokenized at the space char.
	 * Characters in the command string may be escaped with '\\', which
	 * in the case of space prevents tokenization.
	 * @param command  the command string.
	 * @param url  the URL
	 * @return the space separated parts of the command string, as array
	 *   of Strings.
	 */
	private String[] convertCommandString(String command, String url) {
		Vector args = new Vector();
		StringBuffer arg = new StringBuffer();
		boolean foundDollarU = false;
		boolean inQuotes = false;
		int end = command.length() - 1;

		for (int i = 0; i <= end; i++) {
			char c = command.charAt(i);
			switch (c) {
				case '$':
					if (i == end) {
						arg.append(c);
					} else {
						char c2 = command.charAt(++i);
						if (c2 == 'u') {
							arg.append(url);
							foundDollarU = true;
						} else {
							arg.append(c);
							arg.append(c2);
						}
					}
					break;

				case '"':
					inQuotes = !inQuotes;
					break;

				case ' ':
					if (inQuotes) {
						arg.append(c);
					} else {
						String newArg = arg.toString().trim();
						if (newArg.length() > 0)
							args.addElement(newArg);
						arg = new StringBuffer();
					}
					break;

				case '\\': // quote char, only for backwards compatibility
					if (i == end) {
						arg.append(c);
					} else {
						char c2 = command.charAt(++i);
						if (c2 != '\\')
							arg.append(c);
						arg.append(c2);
					}
					break;

				default:
					arg.append(c);
					break;
			}
		}

		String newArg = arg.toString().trim();
		if (newArg.length() > 0)
			args.addElement(newArg);

		if (!foundDollarU && url.length() > 0)
			args.addElement(url);

		String[] result = new String[args.size()];
		args.copyInto(result);

		for (int i = 0; i < result.length; i++)
			Log.log(Log.DEBUG, this, "args[" + i + "]=" + result[i]);

		return result;
	}


	private void gotoURLWithMethod(String url, String clazz, String method) {
		Class c = null;
		Object obj = null;

		try {
			c = Class.forName(clazz);
		}
		catch (Throwable e) {
			GUIUtilities.error(null, "infoviewer.error.classnotfound",
				new Object[] {clazz} );
			return;
		}

		if (method == null || (method != null && method.length() == 0)) {
			// no method: try to find URL or String or empty constructor
			Constructor constr = null;
			try {
				constr = c.getConstructor(new Class[] {URL.class} );
				if (constr != null)
					obj = constr.newInstance(new Object[] {new URL(url)} );
			}
			catch(Exception ex) {
				Log.log(Log.DEBUG, this, ex);
			}
			if (obj == null) {
				try {
					constr = c.getConstructor(new Class[] {String.class} );
					if (constr != null)
						obj = constr.newInstance(new Object[] {url} );
				}
				catch(Exception ex) {
					Log.log(Log.DEBUG, this, ex);
				}
			}
			if (obj == null) {
				try {
					constr = c.getConstructor(new Class[0]);
					if (constr != null)
						obj = constr.newInstance(new Object[0]);
				}
				catch(Exception ex) {
					Log.log(Log.DEBUG, this, ex);
				}
			}
			if (obj == null) {
				GUIUtilities.error(null, "infoviewer.error.classnotfound",
								   new Object[] {clazz} );
				return;
			}

		} else {
			// there is a method name:
			Method meth = null;
			boolean ok = false;
			try {
				meth = c.getDeclaredMethod(method, new Class[] {URL.class} );
				if (meth != null) {
					obj = meth.invoke(null, new Object[] {new URL(url)} );
					ok = true;
				}
			}
			catch(Exception ex) {
				Log.log(Log.DEBUG, this, ex);
			}
			if (!ok) {
				try {
					meth = c.getDeclaredMethod(method, new Class[] {String.class} );
					if (meth != null) {
						obj = meth.invoke(null, new Object[] {url} );
						ok = true;
					}
				}
				catch(Exception ex) {
					Log.log(Log.DEBUG, this, ex);
				}
			}
			if (!ok) {
				try {
					meth = c.getDeclaredMethod(method, new Class[0]);
					if (meth != null) {
						obj = meth.invoke(null, new Object[0]);
						ok = true;
					}
				}
				catch(Exception ex) {
					Log.log(Log.DEBUG, this, ex);
				}
			}
			if (!ok) {
				GUIUtilities.error(null, "infoviewer.error.methodnotfound",
					new Object[] {clazz, method} );
				return;
			}
		}

		if (obj != null) {
			if (obj instanceof Window) {
				((Window)obj).show();
			} else if (obj instanceof JComponent) {
				JFrame f = new JFrame("Infoviewer JWrapper");
				f.getContentPane().add((JComponent)obj);
				f.pack();
				f.setVisible(true);
			} else if (obj instanceof Component) {
				Frame f = new Frame("Infoviewer Wrapper");
				f.add((Component)obj);
				f.pack();
				f.setVisible(true);
			}
		}
	}

}

