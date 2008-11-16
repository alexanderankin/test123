import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*;
import cookxml.core.TagLibrary;
import cookxml.core.creator.DefaultCreator;
import cookxml.cookswing.CookSwing;
import cookxml.cookformlayout.CookFormLayout;
import cookxml.cookbsh.CookBSH;

public class CookViewer extends JPanel implements EBComponent {
	private View view;
	private CookSwing cookSwing;
	private Container renderContainer;
	private JPanel renderPanel;
	private JScrollPane scrollPane;
	private JToolBar toolBar;
 
    //{{{ +CookViewer(View view, String position) [constructor]
  public CookViewer(View view, String position) {
    super(new BorderLayout());
    this.view = view;

    try {
		// Extra library for BSH inner scripts
		CookBSH.setupTagLibrary(CookSwing.getSwingTagLibrary());
	} catch (NoClassDefFoundError ee) {
		Log.log(Log.WARNING,this,"Extension CookBsh.jar not in classpath"); 
	}

    try {
		// Extra library for CookFormLayout easy rendering
		CookFormLayout.setupTagLibrary(CookSwing.getSwingTagLibrary());
	} catch (NoClassDefFoundError ee) {
		Log.log(Log.WARNING,this,"Extension CookFormLayout and/or JGoodies not in classpath");
	}

	try {
		add(toolbarPanel(),BorderLayout.NORTH);
		renderPanel = new JPanel();
		scrollPane = new JScrollPane(renderPanel);
		add(scrollPane,BorderLayout.CENTER);
		
		
		cookSwing = new CookSwing();
	// ---------------------------------------------------------------------------
	// Experimental (2008-11-10)
	// But : remplacer dialog/frame par un composant héritable dans un Jpanel
	//       parce que jEdit ne supporte qu'un seul Dialog/Frame (lui-même)
//	cookxml.core.TagLibrary library = CookSwing.getSwingTagLibrary();
//	library.setCreator("dialog",DefaultCreator.getCreator (JInternalFrame.class));
//	library.setCreator("frame",	DefaultCreator.getCreator (JInternalFrame.class));
	// Pas terrible, il faudrait substituer une autre classe dialog/frame qui ressemble à
	// un JPanel et qui laisse tomber les éléments de "dialog"/"frame"
	// car comme il ne les comprend pas Cookswing rend un container vide
	// ---------------------------------------------------------------------------
		
	} catch (NoClassDefFoundError ee) {
		((JButton) toolBar.getComponentAtIndex(0)).setEnabled(false);
		JOptionPane.showMessageDialog(view,
			"CookXML.jar and/or CookSwing.jar", "Missing libraries",
			JOptionPane.INFORMATION_MESSAGE);
	}
  }
    //}}}

    //{{{ +renderContent() : void
  public void renderContent() {
//	Log.log(Log.DEBUG,this,"[cookxml] : "+view.getBuffer().getPath());
    try {	
		if (renderContainer != null) {
			 renderPanel.remove(renderContainer);
		}
		renderContainer = cookSwing.render(view.getBuffer().getPath());
  
		if (renderContainer instanceof JFrame || renderContainer instanceof JDialog) {
			JOptionPane.showMessageDialog(view,
				"JFrame/JDialog not allowed", "jEdit CookViewer implementation limitation",
				JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			renderPanel.add(renderContainer);
			renderPanel.validate();
			renderPanel.repaint();
		}
	} catch (Exception ee) {
		JOptionPane.showMessageDialog(view,
		"Buffer can not be parsed : incorrect content ?", "Parsing error",
		JOptionPane.INFORMATION_MESSAGE);
	}
  }
    //}}}

    //{{{ -JToolBar toolbarPanel() : JToolBar
  private JToolBar toolbarPanel() {
    toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.putClientProperty("JToolBar.isRollover",Boolean.TRUE);
    toolBar.add(makeCustomButton("cookviewer.render", new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        renderContent();
      }
    } ));
 
    return toolBar;
  }
    //}}}
 
    //{{{ +handleMessage(EBMessage) : void
  public void handleMessage(EBMessage message) {
  }    //}}}
 
    //{{{ -makeCustomButton(String, ActionListener) : AbstractButton
  private AbstractButton makeCustomButton(String name,ActionListener listener) {

    String toolTip = jEdit.getProperty(name.concat(".label"));
    AbstractButton b = new JButton(GUIUtilities.loadIcon (jEdit.getProperty(name + ".icon")));

    if (listener != null) {
      b.addActionListener(listener);
      b.setEnabled(true);
    }
	else {
      b.setEnabled(false);
    }

    b.setToolTipText(toolTip);
    b.setMargin(new Insets(0,0,0,0));
    b.setAlignmentY(0.0f);
    b.setRequestFocusEnabled(false);
    return b;
  }    //}}}
}

/* :folding=explicit:tabSize=4:indentSize=4: */
