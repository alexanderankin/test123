package com.lipstikLF.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;

import com.lipstikLF.LipstikLookAndFeel;
import com.lipstikLF.delegate.LipstikInternalFrameTitlePane;
import com.lipstikLF.delegate.LipstikTitlePane;
import com.lipstikLF.theme.LipstikColorTheme;

public class LipstikListenerFactory
{
    private static MouseListener _buttonRolloverMouseListener;
    private static MouseAdapter _frameButtonMouseHandler;
    
    public static MouseListener getButtonRolloverMouseListener()
    {
        if (_buttonRolloverMouseListener == null)
            _buttonRolloverMouseListener = new ButtonRolloverMouseListener();
        return _buttonRolloverMouseListener;
    }
    
    public static MouseAdapter getFrameButtonMouseHandler()
    {
        if (_frameButtonMouseHandler == null)
        	_frameButtonMouseHandler = new FrameButtonMouseHandler();
        return _frameButtonMouseHandler;
    }

    private static class ButtonRolloverMouseListener extends MouseAdapter
    {
        public void mouseEntered(MouseEvent e)
        {
            AbstractButton button = ((AbstractButton) e.getSource());
            button.getModel().setRollover(true);
            button.repaint();
        }
        public void mouseExited(MouseEvent e)
        {
            AbstractButton button = ((AbstractButton) e.getSource());
            button.getModel().setRollover(false);
            button.repaint();
        }
    }
    
	static class FrameButtonMouseHandler extends MouseAdapter
	{	
		private Color adjustColor(Component c, Color active, Color inactive)
		{
			boolean isActive = false;
			if (c instanceof LipstikInternalFrameTitlePane)
				isActive = ((LipstikInternalFrameTitlePane)c).getFrame().isSelected();
			else
			{
				Window w = ((LipstikTitlePane)c).getWindow();
				if (w != null)
					isActive = w.isActive();
			}
			return isActive ? active : inactive;
		}
		
		public void mousePressed(MouseEvent e) 
		{
			LipstikColorTheme theme = LipstikLookAndFeel.getMyCurrentTheme();
			JButton b = (JButton)e.getComponent();
			b.setBackground(theme.getInternalButtonHighlight());
			b.repaint();
			super.mouseClicked(e);
		}

		public void mouseReleased(MouseEvent e) 
		{
			LipstikColorTheme theme = LipstikLookAndFeel.getMyCurrentTheme();
			JButton b = (JButton)e.getComponent();
			Color color = adjustColor(b.getParent(), 
					theme.getInternalButtonBackground(), 
					theme.getInternalButtonInactive());
			b.setBackground(color);
			super.mouseReleased(e);
		}

		public void mouseEntered(MouseEvent e)
		{
			LipstikColorTheme theme = LipstikLookAndFeel.getMyCurrentTheme();
			JButton b = (JButton)e.getComponent();
            b.setBackground(theme.getInternalButtonHighlight());
			b.repaint();
		}
				
		public void mouseExited(MouseEvent e)
		{
			LipstikColorTheme theme = LipstikLookAndFeel.getMyCurrentTheme();
			JButton b = (JButton)e.getComponent();
			Color color = adjustColor(b.getParent(), 
					theme.getInternalButtonBackground(), 
					theme.getInternalButtonInactive());
			b.setBackground(color);
			b.repaint();
		}
	}    
}
