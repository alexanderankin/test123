/*
 * XPathExpressionPanel.java - Holds XPath expression text field
 *
 * Copyright (C) 2003 Robert McKinnon
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

package xslt;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.gjt.sp.jedit.jEdit;

/**
 * Panel housing the "XPath Expression" label & text area.
 *
 * @author Robert McKinnon
 */
public class XPathExpressionPanel extends JPanel implements KeyListener, DocumentListener {

  private static final String LAST_EXPRESSION = "xpath.last-expression";
  private static final String EXPRESSIONS = "xpath.expression";
  
  private JTextArea textArea = new JTextAreaWithoutTab();
  private int historyLength = Integer.parseInt(jEdit.getProperty("xpath.expression.history-length"));
  private int historyIndex;


  public XPathExpressionPanel() {
    super(new BorderLayout());
   
    String label = jEdit.getProperty("xpath.expression.label");
	String text = jEdit.getProperty(LAST_EXPRESSION);

    textArea.getDocument().addDocumentListener(this);
    textArea.addKeyListener(this);
    textArea.setText((text == null) ? "" : text);

    add(new JLabel(label), BorderLayout.NORTH);
    add(new JScrollPane(textArea));

    historyIndex = getExpressionList().size() - 1;
  }


  /**
   * Stores the current expression at the end of the expression history, and then returns the expression.
   */
  public String getExpression() {
    return textArea.getText();
  }


  /**
   * Adds supplied expression to the expression history.
   * @param expression expression to be stored in the history.
   */
  public void addToHistory(String expression) {
    List expressionList = getExpressionList();

    for(int i = 0; i < expressionList.size(); i++) {
      String oldExpression = (String)expressionList.get(i);

      if(oldExpression.equals(expression)) {
        expressionList.remove(i);
      }
    }

    if(expressionList.size() == historyLength) {
      expressionList.remove(0);
    }

    expressionList.add(expression);
    PropertyUtil.setEnumeratedProperty(EXPRESSIONS, expressionList, jEdit.getProperties());
    historyIndex = expressionList.size() - 1;
  }


  public void changedUpdate(DocumentEvent e) {
    storeExpression();
  }


  public void insertUpdate(DocumentEvent e) {
    storeExpression();
  }


  public void removeUpdate(DocumentEvent e) {
    storeExpression();
  }


  private void storeExpression() {
    String text = textArea.getText();
    jEdit.setProperty(LAST_EXPRESSION, (text == null) ? "" : text);
  }


  public void keyTyped(KeyEvent e) {
  }


  public void keyPressed(KeyEvent e) {
    switch(e.getKeyCode()) {
      case KeyEvent.VK_PAGE_UP:
        displayPreviousExpression();
        break;
      case KeyEvent.VK_PAGE_DOWN:
        displayNextExpression();
        break;
    }
  }


  public void keyReleased(KeyEvent e) {
  }

  private void displayPreviousExpression() {
    List expressionList = getExpressionList();

    if(historyIndex > 0) {
      historyIndex--;
      String expression = (String)expressionList.get(historyIndex);
      textArea.setText(expression);
    }
  }


  private void displayNextExpression() {
    List expressionList = getExpressionList();
    int lastIndex = expressionList.size() - 1;

    if(historyIndex < lastIndex && historyIndex > -1) {
      historyIndex++;
      String expression = (String)expressionList.get(historyIndex);
      textArea.setText(expression);
    }
  }


  private List getExpressionList() {
    return PropertyUtil.getEnumeratedProperty(EXPRESSIONS, jEdit.getProperties());
  }


  /**
   * JTextArea that let's tab key change focus to the next component.
   */
  public class JTextAreaWithoutTab extends JTextArea {
    public boolean isManagingFocus() {
      return false;
    }
  }
}
