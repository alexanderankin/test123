package com.lipstikLF.delegate;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.Element;
import javax.swing.text.PasswordView;
import javax.swing.text.View;

public class LipstikPasswordFieldUI extends BasicPasswordFieldUI
{
    /**
	 * Creates a UI for a {@link JPasswordField}.
	 * 
	 * @param c the password field component
	 * @return the UI
	 */
    public static ComponentUI createUI(JComponent c) 
    {
        return new LipstikPasswordFieldUI();
    }

    /**
	 * Creates and returns a view (an <code>ExtPasswordView</code>) for an element.
	 * 
	 * @param elem the element
	 * @return the view
	 */
    public View create(Element elem) 
    {
        return new LipstikPasswordView(elem);
    }
    
    
    
    public final class LipstikPasswordView extends PasswordView 
    {
        public LipstikPasswordView(Element element) 
        {
            super(element);
        }

        /*
         * Overrides the superclass behavior to paint a filled circle,
         * not the star (&quot;*&quot;) character.
         */
        protected int drawEchoCharacter(Graphics g, int x, int y, char c) 
        {
            Container container = getContainer();
            if (!(container instanceof JPasswordField))
                return super.drawEchoCharacter(g, x, y, c);

            JPasswordField field = (JPasswordField) container;
            int charWidth = getFontMetrics().charWidth(field.getEchoChar());
            int advance  = 2;
            int diameter = charWidth - advance;

            // Painting the dot with anti-alias enabled.
            Graphics2D g2 = (Graphics2D) g;
            Object oldHints = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Try to vertically align the circle with the font base line.
            g.fillOval(x, y - diameter, diameter, diameter);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldHints);
            // End of painting the dot
            
            // The following line would paint a square, not a dot.
            // g.fillRect(x, y - diameter + 1, diameter, diameter);

            return x + diameter + advance;
        }
    }  
}
