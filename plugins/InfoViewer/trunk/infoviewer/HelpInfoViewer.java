package infoviewer;



import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.help.HelpSearchPanel;
import org.gjt.sp.jedit.help.HelpTOCPanel;
import org.gjt.sp.jedit.help.HelpViewer;
import org.gjt.sp.util.Log;

/**
 * An infoviewer with a helpful side-bar
 * 
 * @author ezust
 * 
 */
public class HelpInfoViewer extends InfoViewer implements HelpViewer
{
	JTabbedPane tabs;
	boolean showSideBar = true;
	Component centralComponent;
	
	public void toggleSideBar () {
		showSideBar = !showSideBar;
		remove(centralComponent);
		if (showSideBar) 
		{
			splitter.setLeftComponent(tabs);
			splitter.setRightComponent(outerPanel);
			centralComponent = splitter;
		}
		else 
		{
			centralComponent = outerPanel;
		}
		add (BorderLayout.CENTER, centralComponent);
	}
	
	public HelpInfoViewer() {
		this(null, null);
	}
	
	public HelpInfoViewer(View view, String position)
	{
		super(view, position);
//		Log.log(Log.WARNING, this.getClass(), "HelpInfoViewer!");
		tabs = new JTabbedPane();
		toc = new HelpTOCPanel(this);
		tabs.addTab(jEdit.getProperty("helpviewer.toc.label"), toc);
		searchPanel = new HelpSearchPanel(this);
		tabs.addTab(jEdit.getProperty("helpviewer.search.label"), searchPanel);
		tabs.setMinimumSize(new Dimension(0, 20));
		outerPanel.setMinimumSize(new Dimension(0, 20));

		splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabs, outerPanel);
		splitter.setBorder(null);
		centralComponent = splitter;
		add(BorderLayout.CENTER, centralComponent);
		queueTOCReload();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				splitter.setDividerLocation(jEdit.getIntegerProperty(
					"infoviewer.splitter", 250));
				requestFocus();
			}
		});
	}

	// {{{ dispose() method
	public void dispose()
	{
		jEdit.setIntegerProperty("infoviewer.splitter", splitter.getDividerLocation());
	} // }}}

	// {{{ Private members

	// {{{ Instance members
	private JSplitPane splitter;

	private HelpSearchPanel searchPanel;
	private HelpTOCPanel toc;

	private boolean queuedTOCReload;

	// }}}

	// {{{ queueTOCReload() method

	public void queueTOCReload()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				queuedTOCReload = false;
				toc.load();
			}
		});
	} // }}}

	// }}}

	// {{{ Inner classes

	// {{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		// {{{ actionPerformed() class
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			String url = evt.getActionCommand();
			if (!url.equals(""))
			{
				gotoURL(url, false);
				return;
			}
		} // }}}
	} // }}}

	public void setTitle(String newTitle)
	{
		firePropertyChange("title", null, newTitle);
	}

	public Component getComponent()
	{
		return this;
	}

	public void gotoURL(String url, boolean addToHistory)
	{
		super.gotoURL(url, addToHistory);
		
	}

	public String getShortURL()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getBaseURL()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
