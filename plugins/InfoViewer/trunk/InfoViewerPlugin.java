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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.*;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.msg.ViewURL;
import org.gjt.sp.util.Log;


public class InfoViewerPlugin extends EBPlugin {
    
    /** the shared InfoViewer instance */
    private static InfoViewer infoviewer = null;    

    
    // begin EditPlugin implementation
    public void start() {
        jEdit.addAction(new infoviewer_open());
        jEdit.addAction(new infoviewer_open_buffer());
        jEdit.addAction(new infoviewer_open_sel());
    }

    public void createMenuItems(View view, Vector menus, Vector menuItems) {
        menus.addElement(GUIUtilities.loadMenu(view, "infoviewer-menu"));
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
            showURL(vu.getURL());
        }
    }
    // end EBPlugin implementation


    /**
     * this function demonstrates how ViewURL messages should be send on
     * the EditBus.
     * @param url an URL that should be displayed in InfoViewer
     * @param view a View from which the message is sent
     */
    protected void sendURL(URL url, View view) {
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
            // no EditBus component listened for this message. Show an
            // error:
            GUIUtilities.error(view, "infoviewer.error.noinfoviewer", null);
            return;
        }            
    }
    

    private void showURL(URL url) {
        String u = (url==null ? "" : url.toString());
        String browsertype = jEdit.getProperty("infoviewer.browsertype");
        if (u.startsWith("jeditresource:")) {
            browsertype = "internal";
        }
        Log.log(Log.DEBUG, this, "gotoURL (" + browsertype + "): " + u);
        
        if ("external".equals(browsertype)) {
            // use external browser:
            String cmd = jEdit.getProperty("infoviewer.otherBrowser");
            String[] args = convertCommandString(cmd, u);
            try {
                Runtime.getRuntime().exec(args);
            }
            catch(Exception ex) {
                GUIUtilities.error(null, "infoviewer.error.invokeBrowser",
                    new Object[] { ex, args.toString() });
                return;
            }
        } else if ("class".equals(browsertype)) {
            // use class + method
            String clazzname = jEdit.getProperty("infoviewer.class");
            String methodname = jEdit.getProperty("infoviewer.method");            
            showURLWithMethod(u, clazzname, methodname);
        } else if ("netscape".equals(browsertype)) {
            String[] args = new String[3];
            args[0] = "sh";
            args[1] = "-c";
            args[2] = "netscape -remote openURL\\(\'" + convertURL(u) 
                      + "\'\\) -raise || netscape \'" + convertURL(u) + "\'";
            try {
                Runtime.getRuntime().exec(args);
            }
            catch(Exception ex) {
                GUIUtilities.error(null,"infoviewer.error.invokeBrowser",
                    new Object[] { ex, args.toString() });
            }            
        } else {
            // use internal InfoViewer browser:
            showInternalInfoViewer(url);
        }
    }


    /** 
     * this is a convenience method to force InfoViewerPlugin to use the
     * internal InfoViewer. The static instance will be created, opened,
     * brought to front and loaded with the url. This method is the
     * default, if class/method invocation is used.
     * @param url the URL.
     */
    public void showInternalInfoViewer(URL url) {
        if (infoviewer == null) {
            infoviewer = new InfoViewer();
        }
        infoviewer.setVisible(true);
        infoviewer.gotoURL(url, true);
    }
    

    /**
     * converts the command string, which may contain "$u" as placeholders
     * for an url, into an array of strings, tokenized at the space char.
     * Characters in the command string may be escaped with '\\', which
     * in the case of space prevents tokenization.
     * @param command the command string.
     * @param url the URL. Spaces in the URL are converted to "%20".
     * @return the space separated parts of the command string, as array
     *   of Strings.
     */
    private String[] convertCommandString(String command, String url) {
        String convURL = convertURL(url);
        Vector args = new Vector();
        StringBuffer buf = new StringBuffer();
        
        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);
            switch (c) {
                case '$':
                    if (i == command.length() - 1)
                        buf.append(c);
                    else {
                        char c2 = command.charAt(++i);
                        switch (c2) {
                            case 'u':
                                buf.append(convURL);
                                break;
                            default:
                                buf.append(c2);
                                break;
                        }
                    }
                    break;
                case ' ':
                    args.addElement(buf.toString());
                    buf = new StringBuffer();
                    break;
                case '\\': // quote char
                    if (i == command.length() - 1)
                        buf.append(c);
                    else
                        buf.append(command.charAt(++i));
                    break;
                default:
                    buf.append(c);
                    break;
            }
         }
         
         args.addElement(buf.toString());         
         String[] result = new String[args.size()];
         args.copyInto(result);
         return result;
    }

    /** 
     * returns a new copy of the URL string, where the spaces are converted 
     * to "%20".
     */
    private String convertURL(String url) {
        StringBuffer buf = new StringBuffer();
        char c;
        for (int i = 0; i < url.length(); i++)
            if ((c = url.charAt(i)) == ' ')
                buf.append("%20");
            else
                buf.append(c);
        return buf.toString();
    }
    

    private void showURLWithMethod(String url, String clazz, String method) {
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
        

    /**********************************************************************/
    
    
    /**
     * an action for opening the InfoViewer
     */
    protected class infoviewer_open extends EditAction {
        public infoviewer_open() {
            super("infoviewer-open");
        }
        
        public void actionPerformed(ActionEvent evt) {
            // sends a help message to itself
            sendURL(null, getView(evt));
        }
    }    
 

    /**********************************************************************/
    
    
    /**
     * an action for opening the current buffer file in the InfoViewer.
     */
     
    // stolen from Slava Pestov's HTML plugin, file browser_open_url.java
    
    public class infoviewer_open_buffer extends EditAction {        
        public infoviewer_open_buffer() {
            super("infoviewer-open-buffer");
        }
        
        public void actionPerformed(ActionEvent evt) {
            View view = getView(evt);
            Buffer buffer = view.getBuffer();
    
            if (buffer.isDirty()) {
                String[] args = { buffer.getName() };
                int result = JOptionPane.showConfirmDialog(view,
                    jEdit.getProperty("notsaved.message",args),
                    jEdit.getProperty("notsaved.title"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    buffer.save(view, null);
                } else if (result != JOptionPane.NO_OPTION) {
                    return;
                }
            }
            
            URL u = buffer.getURL();
            if (u == null) {
                String bufpath = "file:" + buffer.getPath();
                try {
                    u = new URL(bufpath);
                } catch (java.net.MalformedURLException e) {
                    GUIUtilities.error(view, "infoviewer.error.badurl", 
                                       new String[] { bufpath } );
                    return;
                }
            }
            sendURL(u, getView(evt));
        }
    }
    
    
    /**********************************************************************/
    
    
    /**
     * an action for opening the selected URL in the InfoViewer
     */
     
    // stolen from Slava Pestov's HTML plugin, file browser_open_sel.java
    
    protected class infoviewer_open_sel extends EditAction {
        public infoviewer_open_sel() {
            super("infoviewer-open-sel");
        }
        
        public void actionPerformed(ActionEvent evt) {
            View view = getView(evt);
            Buffer buffer = view.getBuffer();
            String selection = view.getTextArea().getSelectedText();
            
            if (selection == null) {
                GUIUtilities.error(view, "infoviewer.error.noselection", null);
                return;
            }
            
            URL u;
            try {
                u = new URL(selection);
            } catch (java.net.MalformedURLException e) {
                GUIUtilities.error(view, "infoviewer.error.badurl", 
                                   new String[] { selection } );
                return;
            }
            sendURL(u, getView(evt));
        }
    } 
    
}

