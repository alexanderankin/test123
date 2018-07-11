package com.lipstikLF.delegate;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import com.lipstikLF.util.LipstikIconFactory;


class LipstikSplitPaneDivider extends BasicSplitPaneDivider
{
    public LipstikSplitPaneDivider(BasicSplitPaneUI ui)
    {
        super(ui);
    }

    /**
     * Paint the component.
     *
     * @param g The graphics resource used to paint the component
     */
    public void paint(Graphics g)
    {
        boolean isHorizontal = (orientation == JSplitPane.HORIZONTAL_SPLIT);
        int iw, ih, w=getWidth(), h=getHeight();

        ImageIcon icon = isHorizontal ?
                    LipstikIconFactory.vicon1 : LipstikIconFactory.hicon1;

        iw = icon.getIconWidth();
        ih = icon.getIconHeight();

        g.setColor(UIManager.getColor("SplitPane.background"));
        g.fillRect(0, 0, getWidth(), getHeight());

        int ix = (w-iw)>>1 , iy = (h-ih)>>1;

        g.drawImage(icon.getImage(), ix, iy, null);
        paintComponents(g);
    }


    /**
     * Creates and return an instance of JButton that can be used to
     * collapse the left component in the metal split pane.
     */
    protected JButton createLeftOneTouchButton()
    {
        JButton b=new JButton()
        {
            int[][] buffer = {
                                    { 0, 0, 0, 0, 1, 0, 0, 0, 0 },
                                    { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
                                    { 0, 0, 1, 1, 1, 1, 1, 0, 0 },
                                    { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
                                    { 0, 0, 0, 0, 0, 0, 0, 0, 0 }
                             };

            public void setBorder(Border b) {}

            /**
             * Paint the component.
             *
             * @param g The graphics resource used to paint the component
             */
            public void paint(Graphics g)
            {
                if (splitPane != null)
                {
                    int oneTouchSize = BasicSplitPaneDivider.ONE_TOUCH_SIZE;
                    int blockSize = Math.min(getDividerSize(), oneTouchSize);

                    Color back = UIManager.getColor("SplitPane.background");

                    // Initialize the color array
                    Color colors[]={ this.getBackground(), Color.GRAY, Color.LIGHT_GRAY };

                    // Fill the background first ...
                    g.setColor(back);
                    g.fillRect(0, 0, this.getWidth(), this.getHeight());

                    // ... then draw the arrow.
                    if(getModel().isPressed())
                        colors[1]=colors[2];

                    if(orientation==JSplitPane.VERTICAL_SPLIT)
                    {
                        // Draw the image for a vertical split
                        for(int i=1; i<=buffer[0].length; i++)
                        {
                            for(int j=1; j<blockSize; j++)
                            {
                                if(buffer[j-1][i-1]==0)
                                    continue;
                                else
                                    g.setColor(colors[buffer[j-1][i-1]]);
                                g.drawLine(i, j, i, j);
                            }
                        }
                    }
                    else
                    {
                        // Draw the image for a horizontal split
                        // by simply swaping the i and j axis.
                        // Except the drawLine() call this code is
                        // identical to the code block above. This was done
                        // in order to remove the additional orientation
                        // check for each pixel.
                        for(int i=1; i<=buffer[0].length; i++)
                        {
                            for(int j=1; j<blockSize; j++)
                            {
                                if(buffer[j-1][i-1]==0)
                                {
                                    // Nothing needs
                                    // to be drawn
                                    continue;
                                }
                                else
                                {
                                    // Set the color from the
                                    // color map
                                    g.setColor(colors[buffer[j-1][i-1]]);
                                }

                                // Draw a pixel
                                g.drawLine(j, i, j, i);
                            }
                        }
                    }
                }
            }


            // Don't want the button to participate in focus traversable.
            public boolean isFocusTraversable()
            {
                return false;
            }
        };
        b.setRequestFocusEnabled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        return b;
    }


    /**
     * Creates and return an instance of JButton that can be used to
     * collapse the right component in the metal split pane.
     */

    protected JButton createRightOneTouchButton()
    {
        JButton b=new JButton()
        {
            int[][] buffer = {
                                    { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                                    { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
                                    { 0, 0, 1, 1, 1, 1, 1, 0, 0 },
                                    { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
                                    { 0, 0, 0, 0, 1, 0, 0, 0, 0 }
                             };


            public void setBorder(Border border) {}

            /**
             * Paint the component.
             *
             * @param g The graphics resource used to paint the component
             */
            public void paint(Graphics g)
            {
                if (splitPane!=null)
                {
                    int oneTouchSize = BasicSplitPaneDivider.ONE_TOUCH_SIZE;
                    int blockSize = Math.min(getDividerSize(), oneTouchSize);

                    Color back = UIManager.getColor("SplitPane.background");

                    // Initialize the color array
                    Color colors[]={ this.getBackground(), Color.GRAY, Color.LIGHT_GRAY };

                    // Fill the background first ...
                    g.setColor(back);
                    g.fillRect(0, 0, this.getWidth(), this.getHeight());

                    // ... then draw the arrow.
                    if(getModel().isPressed())
                        colors[1]=colors[2];

                    if(orientation==JSplitPane.VERTICAL_SPLIT)
                    {
                        // Draw the image for a vertical split
                        for(int i=1; i<=buffer[0].length; i++)
                        {
                            for(int j=1; j<blockSize; j++)
                            {
                                if(buffer[j-1][i-1]==0)
                                    continue;
                                else
                                    g.setColor(colors[buffer[j-1][i-1]]);
                                g.drawLine(i, j, i, j);
                            }
                        }
                    }
                    else
                    {
                        // Draw the image for a horizontal split
                        // by simply swaping the i and j axis.
                        // Except the drawLine() call this code is
                        // identical to the code block above. This was done
                        // in order to remove the additional orientation
                        // check for each pixel.
                        for(int i=1; i<=buffer[0].length; i++)
                        {
                            for(int j=1; j<blockSize; j++)
                            {
                                if(buffer[j-1][i-1]==0)
                                {
                                    // Nothing needs
                                    // to be drawn
                                    continue;
                                }
                                else
                                {
                                    // Set the color from the
                                    // color map
                                    g.setColor(colors[buffer[j-1][i-1]]);
                                }

                                // Draw a pixel
                                g.drawLine(j, i, j, i);
                            }
                        }
                    }
                }
            }


            // Don't want the button to participate in focus traversable.
            public boolean isFocusTraversable()
            {
                return false;
            }
        };
        b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setRequestFocusEnabled(false);
        return b;
    }
}
