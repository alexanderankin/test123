/*
 * XPathTool.java - GUI for evaluating XPath expressions
 *
 * Copyright (c) 2002 Greg Merrill
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.transform.TransformerException;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.EditorExiting;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * GUI for evaluating XPath expressions. 
 */
public class XPathTool extends JPanel implements EBComponent {

  public XPathTool (View view) {
    super(new GridBagLayout());
    this.view = view;
    EditBus.addToBus(this);

    expressionPanel = new ExpressionPanel();
    evaluatePanel = new EvaluatePanel();
    resultsPanel = new ResultsPanel();

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    add(expressionPanel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.NONE;
    add(evaluatePanel, gbc);

    gbc = new GridBagConstraints();
    gbc.gridy = 2;
    gbc.weightx = 1;
    gbc.weighty = 4;
    gbc.fill = GridBagConstraints.BOTH;
    add(resultsPanel, gbc);
  }

  /**
   * @see org.gjt.sp.jedit.EBComponent#handleMessage(EBMessage)
   */
  public void handleMessage (EBMessage msg) {
    if (msg instanceof EditorExiting) {
      String text = expressionPanel.textArea.getText();
      jEdit.setProperty("XPathTool.lastExpression", (text == null) ? "" : text);
    }
  }

  /**
   * @param xObject XObject to be converted
   * @return user-friendly string describing the supplied XObject
   */
  private String xObjectToFriendlyString (XObject xObject) throws TransformerException {
    if (xObject.getType() == XObject.CLASS_NODESET) {
      StringBuffer buf = new StringBuffer();
      NodeList nodelist = xObject.nodelist();
      buf.append(MessageFormat.format(jEdit.getProperty("XPathTool.results.nodesFound"), 
        new Object[]{new Integer(nodelist.getLength())}));
      buf.append("\n");
      for (int i=0; i < nodelist.getLength(); i++) {
        buf.append(MessageFormat.format(jEdit.getProperty("XPathTool.results.node"),
          new Object[]{nodelist.item(i).getNodeName(), nodelist.item(i).getNodeValue()}));
        buf.append("\n");
      }
      return buf.toString();
    }
    return xObject.toString();
  }

  /**
   * Panel housing the "XPath Expression" label & text area
   */
  class ExpressionPanel extends JPanel {
    ExpressionPanel () {
      super(new BorderLayout());
      JPanel topPanel = new JPanel(new BorderLayout());
      topPanel.add(new JLabel(jEdit.getProperty("XPathTool.xpathExpression.label")), BorderLayout.WEST);
      add(topPanel, BorderLayout.NORTH);
      textArea = new JTextArea();
      String text = jEdit.getProperty("XPathTool.lastExpression");
      textArea.setText((text == null) ? "" : text);
      add(new JScrollPane(textArea));
    }
    JTextArea textArea;
  }

  /**
   * Panel housing the "Evaluate" button
   */
  class EvaluatePanel extends JPanel {
    EvaluatePanel () {
      JButton button = new JButton(jEdit.getProperty("XPathTool.evaluate.button"));
      button.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent event) {
          try {
            DOMParser parser = new DOMParser();
            parser.setFeature("http://xml.org/sax/features/validation", false);
            Buffer buffer = view.getBuffer();
            String text = buffer.getText(0, buffer.getLength());
            InputSource inputSource = new InputSource(new StringReader(text));
            inputSource.setSystemId(buffer.getFile().getPath());
            parser.parse(inputSource);
            XObject xObject = XPathAPI.eval(parser.getDocument(), expressionPanel.textArea.getText());
            resultsPanel.textArea.setText(xObjectToFriendlyString(xObject));
          }
          catch (SAXException e) { // parse problem
            XSLTPlugin.processException(e, jEdit.getProperty("XPathTool.results.error.bufferUnparseable"), XPathTool.this);
          }
          catch (IOException e) { // parse problem
            XSLTPlugin.processException(e, jEdit.getProperty("XPathTool.results.error.bufferUnparseable"), XPathTool.this);
          }
          catch (TransformerException e) { // evaluation problem
            XSLTPlugin.processException(e, jEdit.getProperty("XPathTool.results.error.expressionUnevaluateable"), XPathTool.this);
          }
          catch (Exception e) { // catch-all
            XSLTPlugin.processException(e, jEdit.getProperty("XPathTool.results.error.unkownProblem"), XPathTool.this);
          }
        }
      });
      add(button);
    }
  }

  /**
   * Panel housing the "Results" label & text area
   */
  class ResultsPanel extends JPanel {
    ResultsPanel () {
      super(new BorderLayout());
      add(new JLabel(jEdit.getProperty("XPathTool.results.label")), BorderLayout.NORTH);
      textArea = new JTextArea();
      textArea.setEditable(false);
      add(new JScrollPane(textArea));
    }
    JTextArea textArea;
  }

  private View view;
  private ExpressionPanel expressionPanel;
  private EvaluatePanel evaluatePanel;
  private ResultsPanel resultsPanel;

}

