// * :tabSize=4:indentSize=4:
// * :folding=explicit:collapseFolds=1:

//{{{ imports
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.Log;
//}}}

public class JumpList extends JWindow implements CaretListener
{

//{{{ fields
    public View parent;
    public JEditTextArea textArea;
    public JList itemsList;
    public int width = 25;
    public StringBuffer keyBuff = new StringBuffer();
    
    private Object[] elements;
    private boolean incremental;
//}}}

//{{{ constructor
public JumpList(View parent, Object[] list, ListModel model,
            boolean incr_search, String title, int list_width)
    {
        super(parent);
        this.parent = parent;
        this.textArea = parent.getTextArea();
        this.incremental = incr_search;
        this.elements = list;
        this.width = list_width;

        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());

        Font font = jEdit.getFontProperty("jump.list.font", new Font("Monospaced", Font.PLAIN, 11));

        JLabel label = new JLabel(title);
        label.setFont(font);
        pane.add(label, BorderLayout.NORTH);

        itemsList = new JList(model);
        itemsList.setFont(font);
        itemsList.setVisibleRowCount(list.length < 5 ? list.length : 5);
        itemsList.setSelectedIndex(0);
        itemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsList.addMouseListener(new MouseHandler());
        itemsList.addListSelectionListener(new SelectionListener());

        FontMetrics fm = getFontMetrics(font);
        itemsList.setPreferredSize(
                new Dimension(this.width * fm.charWidth('m'),
                (int) itemsList.getPreferredSize().height));
        

        JScrollPane scroll = new JScrollPane();
        scroll.getViewport().setView(itemsList);

        scroll.setBorder(null);
        pane.add(scroll, BorderLayout.SOUTH);
        pane.setBorder(LineBorder.createBlackLineBorder());
        this.getContentPane().add(pane);

        this.setBackground(Color.lightGray);

        GUIUtilities.requestFocus(this, itemsList);
        pack();
        try
        {
            int offset = textArea.getCaretPosition();
            int line = textArea.getCaretLine();
            int x,y;
            Point p = new Point();
            p = textArea.offsetToXY(line, offset - textArea.getLineStartOffset(line),p);
            x = p.x;
            y = p.y;
            
            Dimension parentSize = textArea.getSize();
            Point parentLocation = textArea.getLocationOnScreen();
            Insets parentInsets = textArea.getInsets();
            Point tapLocation = textArea.getLocationOnScreen();
            int gutt_x = textArea.getGutter().getWidth(); 
            Dimension popupSize = getSize();
            
            // *************************************
            System.out.println("popup="+popupSize+". textarea="+parentSize);
            if (popupSize.width >= parentSize.width)
            {
                setSize(parentSize.width, popupSize.height);
                popupSize = getSize();
            }
            // *************************************
            x += tapLocation.x;
            
            if ((x + popupSize.width+gutt_x) > (parentLocation.x + parentSize.width -
                    parentInsets.right))
            {
                x -= popupSize.width;
            }
            // TODO: Need to adjust this.size if x<0
            //if (x<)
            
            if ((parentSize.height-y)<popupSize.height)
            {
                y = parentSize.height - popupSize.height;
            }
            setLocation(x+gutt_x+parentLocation.x+parentInsets.right,y+parentLocation.y+parentInsets.top);
        }
        catch (Exception e)
        {
            Point parentLocation = textArea.getLocationOnScreen();
            int gutt_x = textArea.getGutter().getWidth();
            Insets parentInsets = textArea.getInsets();
            setLocation(gutt_x+parentLocation.x+parentInsets.right,parentLocation.y+parentInsets.top);     
        }
        itemsList.setSelectedIndex(0);
        setVisible(true);

        KeyHandler handler = new KeyHandler();
        itemsList.addKeyListener(handler);
        parent.setKeyEventInterceptor(handler);
        textArea.addCaretListener(this);
// TODO: Check property SHOW_STATUSBAR_MESSAGES before updateStatusBar()
        // try
        // {
        //itemsList.setSelectedIndex(0);     
        // updateStatusBar(itemsList);
        // }
        // catch(Exception e)
        // {
        //     System.out.println("Can\'t setup init status bar.");   
        // }
    }
//}}}

//{{{ void dispose()
public void dispose()
    {
        // Clear status bar messages if need
        jEdit.getActiveView().getStatus().setMessage(null);   
        
        parent.setKeyEventInterceptor(null);
        textArea.removeCaretListener(this);
        super.dispose();
        SwingUtilities.invokeLater(new Runnable()
                                   {
                                       public void run()
                                       {
                                           textArea.requestFocus();
                                       }
                                   }
                                  );
    }
//}}}

//{{{ empty methods (they override by descendants)
    public void processAction(Object o)
    {
    }

    public void processInsertAction(Object o)
    {
    }
    
    public void processActionInNewView(Object o)
    {
    }
    
    public void updateStatusBar(Object itemlist)
    {
    }
//}}}

//{{{ CaretListener inteface
    public void caretUpdate(CaretEvent evt)
    {
        dispose();
    }
//}}}

//{{{ class SelectionListener

    class SelectionListener implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e) 
        {
            //System.out.println("valueChanged");
            updateStatusBar((JList)e.getSource());     
        }
    }
//}}}

//{{{ class KeyHandler
    class KeyHandler extends KeyAdapter
    {
        
        int selected;
        int CHAR_DELTA = 1000;
        long m_time = 0;
        String m_key = "";
        
//{{{ void keyReleased
//        public void keyReleased(KeyEvent evt)
//        {
//
//        }
//}}}
        
//{{{ void keyPressed
        public void keyPressed(KeyEvent evt)
        {
            View view = jEdit.getActiveView();
            switch (evt.getKeyCode())
            {
            case KeyEvent.VK_ENTER:
                if (evt.isShiftDown())
                {
                    JumpList.this.processActionInNewView((Object) itemsList);    
                }
                else
                {
                    JumpList.this.processAction((Object) itemsList);
                }
                dispose();
                evt.consume();
                break;

            case KeyEvent.VK_INSERT:
                JumpList.this.processInsertAction((Object) itemsList);
                dispose();
                evt.consume();
                break;

            case KeyEvent.VK_SPACE:
                if (evt.isShiftDown())
                {
                    JumpList.this.processActionInNewView((Object) itemsList);    
                }
                else
                {
                    JumpList.this.processAction((Object) itemsList);
                }
                dispose();
                evt.consume();
                break;

            case KeyEvent.VK_ESCAPE:
                dispose();
                evt.consume();
                break;

            case KeyEvent.VK_HOME:
                itemsList.setSelectedIndex(0);
                itemsList.ensureIndexIsVisible(0);
                evt.consume();
                break;

            case KeyEvent.VK_END:
                itemsList.setSelectedIndex(
                        itemsList.getModel().getSize() - 1);
                itemsList.ensureIndexIsVisible(
                        itemsList.getModel().getSize() - 1);
                evt.consume();
                break;

            case KeyEvent.VK_PAGE_UP:
                int selected = itemsList.getSelectedIndex();
                selected -= 5;
                if (selected < 0)
                    selected = itemsList.getModel().getSize() - 1;
                itemsList.setSelectedIndex(selected);
                itemsList.ensureIndexIsVisible(selected);
                evt.consume();
                break;

            case KeyEvent.VK_PAGE_DOWN:
                selected = itemsList.getSelectedIndex();
                selected += 5;
                if (selected >= itemsList.getModel().getSize())
                    selected = 0;
                itemsList.setSelectedIndex(selected);
                itemsList.ensureIndexIsVisible(selected);
                evt.consume();
                break;

            case KeyEvent.VK_UP:
                selected = itemsList.getSelectedIndex();
                if (selected == 0)
                {
                    selected = itemsList.getModel().getSize() - 1;
                }
                else
                {
                    selected--;
                }
                
                itemsList.setSelectedIndex(selected);
                itemsList.ensureIndexIsVisible(selected);
                evt.consume();
                break;

            case KeyEvent.VK_DOWN:
                selected = itemsList.getSelectedIndex();
                if (selected == itemsList.getModel().getSize() - 1)
                {
                    selected = 0;
                }
                else
                {
                    selected++;
                }
                
                itemsList.setSelectedIndex(selected);
                itemsList.ensureIndexIsVisible(selected);
                evt.consume();
                break;
            }
        }
//}}}

//{{{ void keyTyped
        public void keyTyped(KeyEvent evt)
        {

            char ch = evt.getKeyChar();
            if (evt.getKeyCode() == KeyEvent.VK_INSERT)
            {
                JumpList.this.processInsertAction((Object) itemsList);
                dispose();
                evt.consume();
                return;
            }

            if (!Character.isLetterOrDigit(ch))
                return;

            if (m_time + CHAR_DELTA < System.currentTimeMillis())
                m_key = "";

            m_time = System.currentTimeMillis();
            m_key += Character.toLowerCase(ch);

            int len = itemsList.getModel().getSize();

            for (int i = 0; i < len; i++)
            {
                String item = new String(
                        itemsList.getModel().getElementAt(i).toString().toLowerCase());
                if (item.startsWith(m_key))
                {
                    itemsList.setSelectedIndex(i);
                    itemsList.ensureIndexIsVisible(i);
                    break;
                }

            }
            itemsList.ensureIndexIsVisible(itemsList.getSelectedIndex());  

        }
//}}}
    }
//}}}

//{{{ class MouseHandler
    class MouseHandler extends MouseAdapter
    {
//{{{ MouseHandler.mouseClicked
        public void mouseClicked(MouseEvent me)
        {
                if (me.isShiftDown() == true)
                {
                    JumpList.this.processActionInNewView((Object) itemsList);
                }
                else if (me.isControlDown() == true)
                {
                    JumpList.this.processInsertAction((Object) itemsList);
                }
                else
                {
                    JumpList.this.processAction((Object) itemsList);
                }
                dispose();
                return;
            //}

            
            //dispose();
        }
//}}}
    }
//}}}

}
