package elementmatcher;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

// BUG: tooltip lacks newlines on interleaved elements
public class ElementsTextAreaPainter extends TextAreaExtension {

    private final ElementMatcherPlugin elementMatcherPlugin;
    private final JEditTextArea textArea;
    private final MouseListenerImpl mouseListener = new MouseListenerImpl();

    public ElementsTextAreaPainter(JEditTextArea textArea) {
        this.elementMatcherPlugin = (ElementMatcherPlugin)jEdit.getPlugin(ElementMatcherPlugin.class.getName());
        this.textArea = textArea;
        textArea.getPainter().addExtension(TextAreaPainter.HIGHEST_LAYER, this);
        textArea.getPainter().addMouseListener(mouseListener);
    }

    public void close() {
        textArea.getPainter().removeExtension(this);
        textArea.getPainter().removeMouseListener(mouseListener);
    }

    @Override
    public void paintScreenLineRange(Graphics2D gfx, int firstLine, int lastLine, int[] physicalLines, int[] start, int[] end, int y, int lineHeight) {
        final FontMetrics fontMetrics = textArea.getPainter().getFontMetrics();
        int firstPhysicalLine = physicalLines[0];
        if (firstPhysicalLine == -1) {
            if (textArea.getBuffer().getLineCount() != 0) {
                firstPhysicalLine = 0;
            } else {
                return;
            }
        }
        int lastPhysicalLine = physicalLines[physicalLines.length - 1];
        if (lastPhysicalLine == -1) {
            lastPhysicalLine = textArea.getBuffer().getLineCount() - 1;
        }
        for (Iterator<Element<?>> it=elementMatcherPlugin.getElementManager(textArea.getBuffer()).getElements(firstPhysicalLine, lastPhysicalLine + 1);
                it.hasNext(); ) {
            final Element<?> element = it.next();
            Point point0 = textArea.offsetToXY(element.line, element.lineOffset0);
            Point point1 = textArea.offsetToXY(element.line, element.lineOffset1 - 1);
            if (point0 == null && point1 == null) {
                continue;
            }
            if (point0 == null) {
                point0 = new Point(0, point1.y);
            }
            if (point1 == null) {
                point1 = new Point(textArea.getWidth(), point0.y);
            }
            final int lineOffset = textArea.getLineStartOffset(element.line);
            final int offset0 = lineOffset + element.lineOffset0;
            if (offset0 >= textArea.getBuffer().getLength()) {
                continue;
            }
            int offset1 = lineOffset + element.lineOffset1;
            if (offset1 > textArea.getBuffer().getLength()) {
                offset1 = textArea.getBuffer().getLength();
            }
            final String text = textArea.getText(offset0, offset1 - offset0);
            final int baseLine = point1.y + fontMetrics.getHeight() - fontMetrics.getLeading() - fontMetrics.getDescent();
            gfx.setColor(element.provider.getColor());
            gfx.drawString(text, point0.x, baseLine);
            if (element.provider.isUnderline()) {
                final int descender = point1.y + fontMetrics.getHeight() - fontMetrics.getLeading();
                final int right = point1.x + fontMetrics.charWidth(text.charAt(text.length() - 1));
                gfx.drawLine(point0.x, descender, right, descender);
            }
        }
    }

    @Override
    public String getToolTipText(int x, int y) {
        final int offset = textArea.xyToOffset(x, y);
        final int line = textArea.getLineOfOffset(offset);
        final Iterator<Element<?>> it = elementMatcherPlugin.getElementManager(textArea.getBuffer()).findElement(line, offset - textArea.getLineStartOffset(line));
        if (!it.hasNext()) {
            return null;
        }
        final StringBuilder s = new StringBuilder();
        while (it.hasNext()) {
            if (s.length() != 0) {
                s.append('\n');
            }
            s.append(it.next().getToolTip());
        }
        return s.toString();
    }

    private static <T> void performDefaultAction(ElementsTextAreaPainter painter, Element<T> element) {
        final Iterator<Action> actions = element.getActions();
        if (!actions.hasNext()) {
            return;
        }
        final Action action = actions.next();
        action.actionPerformed(new ActionEvent(painter.textArea, -1, (String)action.getValue(Action.ACTION_COMMAND_KEY)));
    }

    private class MouseListenerImpl extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent evt) {
            if ((evt.getModifiers() & MouseEvent.CTRL_MASK) != 0) {
                final int offset = textArea.xyToOffset(evt.getX(), evt.getY());
                final int line = textArea.getLineOfOffset(offset);
                final Iterator<Element<?>> it = elementMatcherPlugin.getElementManager(textArea.getBuffer()).findElement(
                        line, offset - textArea.getLineStartOffset(line));
                if (!it.hasNext()) {
                    return;
                }
                final Element<?> firstElement = it.next();
                if (!it.hasNext()) {
                    switch (evt.getButton()) {
                        case MouseEvent.BUTTON1:
                            performDefaultAction(ElementsTextAreaPainter.this, firstElement);
                            break;
                    }
                    return;
                }
                final JPopupMenu popupMenu = new JPopupMenu();
                popupMenu.add(new JMenuItem(firstElement.getActions().next()));
                while (it.hasNext()) {
                    popupMenu.add(new JMenuItem(it.next().getActions().next()));
                }
                popupMenu.show(textArea, evt.getX(), evt.getY());
            }
        }

    }

}