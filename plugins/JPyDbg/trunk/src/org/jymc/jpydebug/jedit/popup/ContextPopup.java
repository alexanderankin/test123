/*
 * CodeAidPopup.java
 * Copyright (c) 1999, 2000, 2001, 2002 CodeAid team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


package org.jymc.jpydebug.jedit.popup;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;


import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import org.gjt.sp.jedit.Buffer;
import org.jymc.jpydebug.PythonSyntaxTreeNode;
import org.jymc.jpydebug.jedit.JPYJeditPlugin;
import org.jymc.jpydebug.jedit.PythonJeditPanel;
import org.jymc.jpydebug.jedit.completion.JeditCompletionContext;
import org.jymc.jpydebug.jedit.completion.UncompletedException;


/**
 * A singleton that represent the unique pop up in the program
 */
public class ContextPopup 
implements MouseListener
{
  protected Hint _hint;
  private PopupWindow _window;
    
  private JEditTextArea _textArea;
  private static ContextPopup _me = null;
  private List _contextList= new ArrayList();
  private JeditCompletionContext _currentContext;
  private int _x;
  private int _y;
  private JList _list;
  private JLabel _className;

  private final static ImageIcon _CLASSICON_  = new ImageIcon(JPYJeditPlugin.class.getResource("images/class.gif"));
  private final static ImageIcon _MODULEICON_ = new ImageIcon(JPYJeditPlugin.class.getResource("images/module.gif"));
  
  
  private ContextPopup(JEditTextArea textArea) 
  { _textArea = textArea; }
    
  /**
   * Get the Popup Instance.
   */
  public static synchronized ContextPopup getInstance(JEditTextArea textArea) 
  {
    if ( _me ==null) 
    {
      _me = new ContextPopup(textArea);
    }
    if ( _me.getTextArea() != textArea ) 
    {
      _me._textArea = textArea;
    }
    return _me;
  }
  
  public void addCompletionContext() 
  {
        
        //ToDo use exception?
        //Todo move some control to completionContext?
    if (_currentContext != null || getView().getKeyEventInterceptor() == null) 
    {
      try 
      {
        // buiuld completion list 
        JeditCompletionContext cc= new JeditCompletionContext(_textArea , _currentContext);
        List memberList = (List) cc.getMemberList() ;
        if ( memberList.size()!=0 )  
              // (memberList.size()!=1 || cc.isMethodContext() )) 
             // ||   ! ((MemberInfo) memberList.get(0)).getName().equals(cc.getPlusText()) ) ) 
          addCompletionContext(cc);
        else 
          removeCompletionContext(false);
      } catch (UncompletedException e) {
        if ( PythonJeditPanel.isDebugEnabled() ) Log.log(Log.ERROR,this,e.getMessage() );
      }
    }
  }
    
  public void addCompletionContext(JeditCompletionContext cc) 
  {
    _contextList.add(cc);
    if (_contextList.size()!=1 || _currentContext != null) 
    {
      this.refresh();
    } 
    else 
    {
      updateCurrentContext();
      updateX();
      updateY();
      show();
            
    }
  }
    
  public void removeCompletionContext(boolean force) 
  {
    if (  _contextList.size()>0 && 
         ( force || 
           ( _contextList.size()>0 && 
            !( (JeditCompletionContext) _contextList.get(_contextList.size()-1)).isMethodContext()
           )
         )
       ) 
      _contextList.remove(_contextList.size()-1);
        
    if( _contextList.isEmpty()) 
      this.hide();
    this.refresh();
  }
    
  public void removePreviousCompletionContext() 
  {
    if( _contextList.size()>1 && 
        ( ! ((JeditCompletionContext) _contextList.get(_contextList.size()-2)).isMethodContext())
      ) 
      _contextList.remove(_contextList.size()-2);
  }
    
  private void updateCurrentContext() 
  {
    if (_contextList.size()==0) 
      _currentContext = null;
    else 
      _currentContext = (JeditCompletionContext) _contextList.get(_contextList.size()-1);
  }
  
  public boolean isIncompletion() 
  {
    if (_currentContext == null) 
    {
      if (PythonJeditPanel.isDebugEnabled() ) Log.log(Log.DEBUG,this,"Not in completion");
        return false;
    }
    if (PythonJeditPanel.isDebugEnabled() ) Log.log(Log.DEBUG,this,"In completion");
        return true;
  }

  private void refresh() 
  {
    updateCurrentContext();
       
    if (_currentContext!= null) 
    {
      updateX();
      updateY();
      _window.setContentPane(createPopupComponent());
      _window.pack();
 
      if (_hint != null && _hint.isVisible() ) 
        _hint.hide();

      if (!_window.isVisible()) 
        _window.setVisible(true);
    }
  }
  
  private void hide() 
  {
        
    if (_hint != null) 
    {
      _hint.hide();
      _hint = null;
    }
    
    if (_window != null) 
    {
      _window.setVisible(false) ;
      _window.dispose() ;
      _window = null;
    }
        
    getTextArea().requestFocus();
        
  }
    
  /**
   * Show this popup at the given location.
   */
  public void show() 
  {
    _window = getPopupWindow(getView());
    calculateBounds();
    if (PythonJeditPanel.isDebugEnabled() ) Log.log(Log.DEBUG, this, "Showing popup at " + _window.getLocation());
    
    _window.setVisible(true);
        
        
    // hint = new JavaDocPopup(textArea, false, currentContext.getClassInfoFinder());
        
        
    SwingUtilities.invokeLater(
      new Runnable()
      {
        public void run() 
        {   _textArea.requestFocus(); }
    });
  }
    
  private void updateX() 
  {
    if (_currentContext == null ) 
      _x = 0;
    else 
      _x = (int) _currentContext.getTa().offsetToXY(_currentContext.getLine(), _currentContext.getCol() + 1, new Point()).getX();// - popup.getNameOffset();
  }
    
  private void updateY() 
  {
    if (_currentContext == null ) 
      _y = 0;
    else 
      _y = (int) _currentContext.getTa().offsetToXY(_currentContext.getLine() + 2, _currentContext.getCol() + 1, new Point()).getY();
  }
    
  /**
   * Sets the SelectedIndex attribute of the MemberPopup object
   */
  public void setSelectedIndex(int index) 
  {
    // for some reason this line makes it much faster
    _list.ensureIndexIsVisible(index);
    _list.setSelectedIndex(index);
    int rowCount = _list.getVisibleRowCount();
    _list.ensureIndexIsVisible(Math.max(index - (rowCount - 1) / 2, 0));
    _list.ensureIndexIsVisible(Math.min(index + rowCount / 2, _list.getModel().getSize() - 1));
    if (_hint == null) 
    //  if (jEdit.getBooleanProperty("codeaid.autoHintDisplay", true)) 
        showHint();
        
  }

  protected void useCurrentSelection(EventObject evt) 
  {
    if ( _list.getSelectedIndex() == -1 )
      // force selection on first element
      _list.setSelectedIndex(0) ;
    PythonSyntaxTreeNode value = (PythonSyntaxTreeNode)_list.getSelectedValue();
    if (value != null) 
    {
    StringBuffer s = new StringBuffer() ;  
    s.append(value.get_nodeName() ) ;
    int curpos = _textArea.getCaretPosition()-1 ; 
    Buffer buffer = (Buffer)_textArea.getBuffer() ;
    int consume = 0 ; 
    char lastChar= buffer.getText( curpos , 1).charAt(0);
        
      while ( curpos != 0  && lastChar != '.' )
      // loopback for '.' separator to determine the amount of 
      // complementary char written by user  
      {  
        consume++ ;
        curpos--  ;
        lastChar= buffer.getText( curpos , 1).charAt(0);
      }  
      if ( consume != 0 )
        s.delete(0 , consume);
      // String text = _currentContext.getPlusText();
      _textArea.requestFocusInWindow();
      // add () syntaxic closure when method or function 
      if ( value.get_type() == PythonSyntaxTreeNode.METHOD_TYPE )
      {
        s.append('(') ;
        if ( value.hasArguments() )
          s.append(' ') ;
        s.append(')') ;
        
      }  
      _textArea.getBuffer().insert(_textArea.getCaretPosition(), s.toString());
      curpos = _textArea.getCaretPosition() ;
      if ( value.hasArguments() )
        // position on blank before ')' closure 
        _textArea.setCaretPosition(curpos-2) ;
      removePreviousCompletionContext();
    }
  }

  /**
   * Gets the NameOffset attribute of the MemberPopup object
   */
    /* TEST public int getNameOffset() {
        return 1 + ((MemberListCellRenderer) list.getCellRenderer()).getNameOffset();
    } */

  public void mouseClicked(MouseEvent evt) 
  {
    if (! _currentContext.isMethodContext()) 
      useCurrentSelection(evt);
    else 
    {
      removeCompletionContext(true);
      SwingUtilities.invokeLater(
        new Runnable()
          {
            public void run() 
            {   _textArea.requestFocus();}
          });
     }
  }


  public void mouseReleased(MouseEvent evt) {}


  public void mousePressed(MouseEvent evt) {}


  public void mouseEntered(MouseEvent evt) {}


  public void mouseExited(MouseEvent evt) {}
  
  private JLabel buildModName( String name )
  {
  JLabel returned = null ;   
    returned = new JLabel(name) ; 
    returned.setIcon(_MODULEICON_) ; 
    return returned ; 
  }
    
  private JComponent createPopupComponent() 
  {
  ArrayList selected = (ArrayList)_currentContext.getMemberList() ;  
    
    if (selected.size() > 0) 
      _className = buildModName(_currentContext.get_moduleName()) ; 
      
    _list = new JList(selected.toArray());
    _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    _list.setCellRenderer(new MemberListCellRenderer(_currentContext.getMemberList().toArray()));

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createLoweredBevelBorder());
    GridBagConstraints gbc = new GridBagConstraints();
    
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.weighty = .001;
    panel.add(_className, gbc);
    
    _list.addMouseListener(this);
    _list.setVisibleRowCount(Math.min(_currentContext.getMemberList().size(), 5));
    ((Component) _list.getCellRenderer()).setFont(_textArea.getPainter().getFont());
    JScrollPane scroll = new JScrollPane(_list);
    scroll.setBorder(null);
    gbc.gridy++;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = .999;
    panel.add(scroll, gbc);
    return panel;
  }
    
  /**
   * Sets the Hint attribute of the CodeAidPopup object
   */
  /* public void setHint(Hint aHint) {
      hint = aHint;
  } */

  /**
   * Returns the text area that this popup belongs to.
   */
  protected JEditTextArea getTextArea() 
  {   return _textArea; }

  /**
   * Returns the view that of the text area that invoked this popup.
   */
  protected View getView() 
  {   return GUIUtilities.getView(_textArea); }
      //return ((Buffer)textArea.getBuffer()).getView();

  /**
   * Returns a popup window.
   */
  protected PopupWindow getPopupWindow(View view) 
  {
    if (_window == null) 
    {
      _window = new PopupWindow(view);
      try {
        Class clazz = Class.forName(getClass().getName() + "$Java14Initializer");
        PopupInitializer initializer = (PopupInitializer) clazz.newInstance();
        initializer.init(_window);
      } catch (Exception e) 
      {}
      _window.setContentPane(createPopupComponent());
      _window.pack();
    }
    return _window;
  }
   
  /**
   * Create a key listener to intercept key events.
   */
  protected KeyListener createKeyEventInterceptor() 
  { return new KeyHandler(); }

  /**
   * Returns the height of a line in the text area.
   */
  private int getLineHeight() 
  {  return _textArea.getPainter().getFontMetrics(_textArea.getPainter().getFont()).getHeight(); }

  /**
   * Show the hint popup.
   */
  private void showHint() 
  {
    if (_hint != null && (! _hint.isVisible())) 
    {
    JLayeredPane layeredPane = _textArea.getRootPane().getLayeredPane();
    Point pt = _window.getLocation();
      SwingUtilities.convertPointFromScreen(pt, layeredPane);
    
      Dimension space = layeredPane.getSize();
      int heightAbovePopup = pt.y - (getLineHeight() * 2);
      int heightBelowPopup = (int) (space.getHeight() - (pt.y + _window.getHeight() + 11));
    
      Dimension hintSize = _hint.getSize();
      if (heightAbovePopup > heightBelowPopup) 
        pt.y -= hintSize.height + (getLineHeight() * 2);
      else 
        pt.y += _window.getHeight() + 11;

      _hint.show(pt);
    }
  }

  /**
   * Calculate the bounds for this popup.
   */
  private void calculateBounds() 
  {
  Dimension size = _window.getSize();
    updateX();
    updateY();
    if (PythonJeditPanel.isDebugEnabled() ) Log.log(Log.DEBUG,this,"X="+_x+";Y="+_y);
    if (_x < 0) 
      _x = (_textArea.getWidth() - size.width) / 2;

    if (_y < 0) 
      _y = (_textArea.getHeight() - size.height) / 2;
        
    if (size.width > _textArea.getWidth() - _x) 
      size.width = _textArea.getWidth() - _x;
        
    size.width = Math.max(size.width, 250);
    Point pt = new Point(_x, _y);
    if (PythonJeditPanel.isDebugEnabled() ) Log.log(Log.DEBUG, CodeAidPopup.class, "pt before translation: " + pt);
    Point textAreaPointOnScreen = _textArea.getLocationOnScreen();
    pt.translate(textAreaPointOnScreen.x, textAreaPointOnScreen.y);
    if (PythonJeditPanel.isDebugEnabled() ) Log.log(Log.DEBUG, CodeAidPopup.class, "pt after translation: " + pt);
    _window.setLocation(pt);
    _window.setSize(size);
    _window.pack();
  }
    
  protected class KeyHandler 
  extends KeyAdapter
  {
    public void keyTyped(KeyEvent evt) 
    {
    char c = evt.getKeyChar();
      if (c != '\b' && c != '\t') 
        _textArea.userInput(c);
            
      if (c == ')' && _currentContext.isMethodContext() ) 
      {
      int bracketOffset = org.gjt.sp.jedit.TextUtilities.findMatchingBracket(_currentContext.getBuffer(), 
        _currentContext.getLine(), _currentContext.getOffset()-_currentContext.getBuffer().getLineStartOffset(_currentContext.getLine())-1);
        if(bracketOffset == _textArea.getCaretPosition()-1) 
        {
          if (PythonJeditPanel.isDebugEnabled() ) Log.log(Log.DEBUG,this,"removeall (closed bracket) ");
          removeCompletionContext(true);
        }
      }
            
      if (c != KeyEvent.CHAR_UNDEFINED && c != '\b' &&  c != '\t' ) 
        removePreviousCompletionContext();
            
    }

    public void keyPressed(KeyEvent evt) 
    {
      if (evt.getModifiers()== 0 || (evt.getModifiers()== 1 && evt.isShiftDown())) 
      {
        switch (evt.getKeyCode()) 
        {
          case KeyEvent.VK_BACK_SPACE:
            _textArea.backspace();
            removeCompletionContext(false);
            break;
    
          case KeyEvent.VK_ESCAPE:
            removeCompletionContext(true);
            evt.consume();
            break;
    
          case KeyEvent.VK_UP:
            setSelectedIndex(Math.max(_list.getSelectedIndex() - 1, 0));
            evt.consume();
            break;
    
          case KeyEvent.VK_DOWN:
            setSelectedIndex(Math.min(_list.getSelectedIndex() + 1,
            _list.getModel().getSize() - 1));
            evt.consume();
            break;
            
          case KeyEvent.VK_PAGE_UP:
            setSelectedIndex(Math.max(_list.getSelectedIndex() -
            _list.getVisibleRowCount(), 0));
            evt.consume();
            break;
    
          case KeyEvent.VK_PAGE_DOWN:
            setSelectedIndex(Math.min(_list.getSelectedIndex() +
            _list.getVisibleRowCount(),
            _list.getModel().getSize() - 1));
            evt.consume();
            break;
    
          case KeyEvent.VK_ENTER:// Fall-through, enter or tab have the same behaviour
          case KeyEvent.VK_TAB:
            if (! _currentContext.isMethodContext() && _list.getSelectedIndex()!=-1) 
            {
              evt.consume();
              useCurrentSelection(evt);
            } 
            else 
            {
              JeditCompletionContext cc = _currentContext;
              removeCompletionContext(true);
              getView().processKeyEvent(evt);
              addCompletionContext(cc);
            }
                        
            break;
                        
          case KeyEvent.VK_DELETE:
            if (! _currentContext.isMethodContext() && _list.getSelectedIndex()!=-1) 
            {
              removeCompletionContext(false);
              evt.consume();
            } 
            else 
            {
              JeditCompletionContext cc = _currentContext;
              removeCompletionContext(true);
              getView().processKeyEvent(evt);
              addCompletionContext(cc);
            }
            break;
            
          case KeyEvent.VK_F1:
             showHint();
              break;
                    
           default:
              if (! Character.isLetterOrDigit(Character.toLowerCase(evt.getKeyChar()))) 
              {
                removeCompletionContext(true);
                getView().processKeyEvent(evt);
              } 
              else 
              {
                if ( _contextList.size()>0 && 
                     ! ((JeditCompletionContext) _contextList.get(_contextList.size()-1)).isMethodContext() ) 
                {
                  _contextList.remove(_contextList.size()-1);
                }
              }
              break;
        }
      } 
      else 
      {
        removeCompletionContext(true);
        getView().processKeyEvent(evt);
      }
            
    }
        
  }


  /**
   * The popup window.
   */
  private class PopupWindow 
  extends JWindow
  {
    private KeyListener keyListener;

    /**
    * Create a new <code>PopupWindow</code>.
    */
    public PopupWindow(View aView) 
    { super(aView);}

    /**
     * Set whether this window is visible.
    */
    public void setVisible(boolean visible) 
    {
      if (visible) 
        installKeyHandler();
      else 
        uninstallKeyHandler();
      super.setVisible(visible);
    }

    /**
     * Install a handler for handling key events.
     */
    private void installKeyHandler() 
    {
      keyListener = createKeyEventInterceptor();
      addKeyListener(keyListener);
      getView().setKeyEventInterceptor(keyListener);
    }

    /**
     * Uninstall a handler from handling key events.
     */
    private void uninstallKeyHandler() 
    {
    View view = getView();
      if (view == null) 
        return;
            
      if (view.getKeyEventInterceptor() != keyListener) 
      {
        if (PythonJeditPanel.isDebugEnabled()) Log.log(Log.WARNING, this, "Key event interceptor does not belong to popup");
      } 
      else 
      {
        view.setKeyEventInterceptor(null);
      }
      removeKeyListener(keyListener);
    }
  }

  /**
   * Initializes this popup for Java 1.4.
   */
  /* REPORTED AS NEVER USED LOCALLY 
  private class Java14Initializer 
  implements PopupInitializer
  {
    public void init(PopupWindow mywindow) 
    {
      if (PythonJeditPanel.isDebugEnabled() ) Log.log(Log.DEBUG, this, "Setting up popup for JDK1.4");
      mywindow.setFocusable(false);
      mywindow.setFocusTraversalKeysEnabled(false);
    }
  }
  */

  /**
   * An interface to support component initialization of a popup.
   */
  private interface PopupInitializer
  {
      void init(PopupWindow popup);
  }
    
  /* REPORTED AS NEVER USED LOCALLY 
  private class Remover 
  extends TimerTask 
  implements Runnable 
  {
    public void run() 
    {
      if(_contextList.isEmpty()) 
      {
        hide();
      }
      refresh();
    }
  }
  */
}

