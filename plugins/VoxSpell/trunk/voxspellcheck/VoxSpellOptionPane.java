
/*
Copyright (C) 2008 Matthew Gilbert

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package voxspellcheck;

import javax.swing.*;
import java.awt.BorderLayout;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

public class VoxSpellOptionPane extends AbstractOptionPane
{
    private JTextField all_text_modes;
    private JTextField non_markup_modes;
    private JCheckBox start_checking_on_activate;
    
    public VoxSpellOptionPane()
    {
        super("VoxSpellOptionPane");
    }
    
    public void _init()
    {
        String s;
        s = jEdit.getProperty("options.voxspellcheck.all_text_modes");
        all_text_modes = new JTextField(s);
        addComponent(new JLabel("All text modes: "), all_text_modes);
        
        s = jEdit.getProperty("options.voxspellcheck.non_markup_modes");
        non_markup_modes = new JTextField(s);
        addComponent(new JLabel("Non-markup modes: "), non_markup_modes);
        
        s = jEdit.getProperty("options.voxspellcheck.start_checking_on_activate");
        boolean b = s.equals("true");
        s = jEdit.getProperty("options.voxspellcheck.start_checking_on_activate.title");
        start_checking_on_activate = new JCheckBox(s, b);
        addComponent(start_checking_on_activate);
    }
    
    public void _save()
    {
        String s;
        
        s = all_text_modes.getText();
        jEdit.setProperty("options.voxspellcheck.all_text_modes", s);
        
        s = non_markup_modes.getText();
        jEdit.setProperty("options.voxspellcheck.non_markup_modes", s);
        
        s = String.valueOf(start_checking_on_activate.isSelected());
        jEdit.setProperty("options.voxspellcheck.start_checking_on_activate", s);
    }
}
