package com.lipstikLF.delegate;

import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class LipstikInternalFrameUI extends BasicInternalFrameUI
{
	/**	The title pane for the JInternalFrame */
	private LipstikInternalFrameTitlePane titlePane;

	/**	Listens for property changes of the JInternalFrame */
	private static final PropertyChangeListener metalPropertyChangeListener=
		new MetalPropertyChangeHandler();

	private static final Border handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);

	private static String IS_PALETTE = "JInternalFrame.isPalette";
	private static String FRAME_TYPE = "JInternalFrame.frameType";
	private static String PALETTE_FRAME = "palette";
	private static String OPTION_DIALOG = "optionDialog";


	/**	Creates an instance for the specified JInternalFrame */
	public LipstikInternalFrameUI(JInternalFrame b)
	{
		super(b);
	}


    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */
	public static ComponentUI createUI(JComponent c)
	{
		return new LipstikInternalFrameUI((JInternalFrame) c);
	}


    /**
     * Install the UI delegate for the given component
     *
     * @param c The component for which to install the ui delegate.
     */
	public void installUI(JComponent c)
	{
		super.installUI(c);

		Object paletteProp = c.getClientProperty(IS_PALETTE);
		if (paletteProp != null)
			setPalette(((Boolean) paletteProp).booleanValue());

		stripContentBorder(frame.getContentPane());
	}


    /**
     * Uninstall the UI delegate for the given component
     *
     * @param c The component for which to install the ui delegate.
     */
	public void uninstallUI(JComponent c)
	{
		frame = (JInternalFrame) c;

		Container cont = ((JInternalFrame) (c)).getContentPane();
		if (cont instanceof JComponent)
		{
			JComponent content = (JComponent) cont;
			if (content.getBorder() == handyEmptyBorder)
				content.setBorder(null);
		}
		super.uninstallUI(c);
	}


	/**	Installs the property listener with the associated JInternalFrame */
	protected void installListeners()
	{
		super.installListeners();
		frame.addPropertyChangeListener(metalPropertyChangeListener);
	}


	/**	Uninstalls any listeners registered with the associated JInternalFrame */
	protected void uninstallListeners()
	{
		frame.removePropertyChangeListener(metalPropertyChangeListener);
		super.uninstallListeners();
	}


	/**	Uninstalls any components installed for the associated JInternalFrame */
	protected void uninstallComponents()
	{
		titlePane = null;
		super.uninstallComponents();
	}


	/**	Removes the previous content border from the specified component, and
	 * 	sets an empty border if there has been no border or an UIResource instance
	 * 	before.
	 */
	private static void stripContentBorder(Object c)
	{
		if (c instanceof JComponent)
		{
			JComponent contentComp = (JComponent) c;
			Border contentBorder = contentComp.getBorder();
			if (contentBorder == null || contentBorder instanceof UIResource)
				contentComp.setBorder(handyEmptyBorder);
		}
	}


	/**	Creates the title pane for the specified JInternalFrame */
	protected JComponent createNorthPane(JInternalFrame w)
	{
		titlePane = new LipstikInternalFrameTitlePane(w);
		return titlePane;
	}


	/**	Sets the frame type according to the specified type constant. This must
	 * 	be one of the SwingConstants.
	 */
	private void setFrameType(String frameType)
	{
		if (frameType.equals(OPTION_DIALOG))
		{
			LookAndFeel.installBorder(frame, "InternalFrame.optionDialogBorder");
			titlePane.setPalette(false);
		}
		else if (frameType.equals(PALETTE_FRAME))
		{
			LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
			titlePane.setPalette(true);
		}
		else
		{
			LookAndFeel.installBorder(frame, "InternalFrame.border");
			titlePane.setPalette(false);
		}
	}

	/**	Sets whether this JInternalFrame is to use a palette border or not */
	public void setPalette(boolean isPalette)
	{
		if (isPalette)
			LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
		else
			LookAndFeel.installBorder(frame, "InternalFrame.border");

		titlePane.setPalette(isPalette);
	}

	private static class MetalPropertyChangeHandler implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String name = e.getPropertyName();
			JInternalFrame jif = (JInternalFrame) e.getSource();
			LipstikInternalFrameUI ui = (LipstikInternalFrameUI) jif.getUI();

			if (name.equals(FRAME_TYPE))
			{
				if (e.getNewValue() instanceof String)
					ui.setFrameType((String) e.getNewValue());
			}
			else
			if (name.equals(IS_PALETTE))
			{
				if (e.getNewValue() != null)
					ui.setPalette(((Boolean) e.getNewValue()).booleanValue());
				else
					ui.setPalette(false);
			}
			else
			if (name.equals(JInternalFrame.CONTENT_PANE_PROPERTY))
				ui.stripContentBorder(e.getNewValue());

		}
	} // end class MetalPropertyChangeHandler
}
