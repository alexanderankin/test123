
/* Copyright (C) 2008 Matthew Gilbert */

package voxspellcheck;

import javax.swing.*;
import java.awt.BorderLayout;

import org.gjt.sp.util.Log;
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
        JPanel p;
        String s;
        
        p = new JPanel(new BorderLayout());
        s = jEdit.getProperty("options.voxspellcheck.all_text_modes");
        all_text_modes = new JTextField(s, 80);
        p.add(new JLabel("All text modes: "), BorderLayout.WEST);
        p.add(all_text_modes, BorderLayout.CENTER);
        addComponent(p);
        
        p = new JPanel(new BorderLayout());
        s = jEdit.getProperty("options.voxspellcheck.non_markup_modes");
        non_markup_modes = new JTextField(s, 80);
        p.add(new JLabel("Non-markup modes: "), BorderLayout.WEST);
        p.add(non_markup_modes, BorderLayout.CENTER);
        addComponent(p);
        
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
