package com.lipstikLF.util;

import com.lipstikLF.LipstikLookAndFeel;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.io.Serializable;


final public class LipstikIconFactory
{
    public static final ImageIcon vicon0 = new ImageIcon(LipstikLookAndFeel.class.getResource("icons/dots0.gif"));
    public static final ImageIcon hicon0 = new ImageIcon(LipstikLookAndFeel.class.getResource("icons/dots1.gif"));
    public static final ImageIcon vicon1 = new ImageIcon(LipstikLookAndFeel.class.getResource("icons/dots2.gif"));
    public static final ImageIcon hicon1 = new ImageIcon(LipstikLookAndFeel.class.getResource("icons/dots3.gif"));

    private static class CheckBoxMenuItemIcon implements Icon, UIResource, Serializable
    {
        private final ImageIcon checked =  new ImageIcon(LipstikLookAndFeel.class.getResource("icons/menucheckbox1.gif"));
        private final ImageIcon unchecked =  new ImageIcon(LipstikLookAndFeel.class.getResource("icons/menucheckbox0.gif"));
        private final ImageIcon dischecked =  new ImageIcon(LipstikLookAndFeel.class.getResource("icons/menucheckbox1_disabled.gif"));
        private final ImageIcon disunchecked =  new ImageIcon(LipstikLookAndFeel.class.getResource("icons/menucheckbox0_disabled.gif"));

		public int getIconWidth()     { return checked.getIconWidth();  }
		public int getIconHeight()    { return checked.getIconHeight(); }

 		public void paintIcon(Component c, Graphics g, int x, int y)
        {
		    JMenuItem b = (JMenuItem) c;
            if (b.isSelected())
            {
                if (b.isEnabled())
                    checked.paintIcon(c,g,x,y);
                else
                    dischecked.paintIcon(c,g,x,y);
            }
            else
                if (b.isEnabled())
                    unchecked.paintIcon(c,g,x,y);
                else
                    disunchecked.paintIcon(c,g,x,y);
 		}
    }


    private static class RadioButtonMenuIcon implements Icon, UIResource, Serializable
    {
        private final ImageIcon checked =  new ImageIcon(LipstikLookAndFeel.class.getResource("icons/menuradiobutton1.gif"));
        private final ImageIcon unchecked =  new ImageIcon(LipstikLookAndFeel.class.getResource("icons/menuradiobutton0.gif"));
        private final ImageIcon dischecked =  new ImageIcon(LipstikLookAndFeel.class.getResource("icons/menuradiobutton1_disabled.gif"));
        private final ImageIcon disunchecked =  new ImageIcon(LipstikLookAndFeel.class.getResource("icons/menuradiobutton0_disabled.gif"));

        public int getIconWidth()     { return checked.getIconWidth();  }
        public int getIconHeight()    { return checked.getIconHeight(); }

        public void paintIcon(Component c, Graphics g, int x, int y)
        {
            JMenuItem b = (JMenuItem) c;
            if (b.isSelected())
            {
                if (b.isEnabled())
                    checked.paintIcon(c,g,x,y);
                else
                    dischecked.paintIcon(c,g,x,y);
            }
            else
                if (b.isEnabled())
                    unchecked.paintIcon(c,g,x,y);
                else
                    disunchecked.paintIcon(c,g,x,y);
        }
    }

    /**
     * The minus sign button icon used in trees
     */
    private static class ExpandedTreeIcon implements Icon, Serializable
    {
        //private final ImageIcon expanded =  new ImageIcon(getClass().getResource("icons/expanded.gif"));

    	static final int SIZE      = 9;
        static final int HALF_SIZE = 4;

        public void paintIcon(Component c, Graphics g, int x, int y)
        {
        	//expanded.paintIcon(c,g,x,y);
            Color backgroundColor = c.getBackground();
            g.setColor(backgroundColor != null ? backgroundColor : Color.white);
            g.fillRect(x, y, SIZE - 1, SIZE - 1);
            g.setColor(Color.gray);
            g.drawRect(x, y, SIZE - 1, SIZE - 1);
            g.drawLine(x + 2, y + HALF_SIZE, x + (SIZE - 3), y + HALF_SIZE);
        }

        public int getIconWidth()  { return SIZE; }
        public int getIconHeight() { return SIZE; }
    }


    /**
     * The plus sign button icon used in trees.
     */
    private static class CollapsedTreeIcon extends ExpandedTreeIcon
    {
        //private final ImageIcon collapsed =  new ImageIcon(getClass().getResource("icons/collapsed.gif"));
    	public void paintIcon(Component c, Graphics g, int x, int y)
        {
    	    //collapsed.paintIcon(c, g, x, y);
            super.paintIcon(c, g, x, y);
            g.drawLine(x + HALF_SIZE, y + 2, x + HALF_SIZE, y + (SIZE - 3));
        }
    }

    public final static Icon checkBoxMenuIcon = new CheckBoxMenuItemIcon();
    public final static Icon radioButtonMenuIcon = new RadioButtonMenuIcon();
    public final static Icon expandedTreeIcon = new ExpandedTreeIcon();
    public final static Icon collapsedTreeIcon = new CollapsedTreeIcon();

}