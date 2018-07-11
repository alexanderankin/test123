package com.lipstikLF.delegate;

import com.lipstikLF.util.LipstikGradients;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class LipstikTableHeaderUI extends BasicTableHeaderUI
{
    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new LipstikTableHeaderUI();
    }

    /**
     * Paint the component.
     *
     * @param g The graphics resource used to paint the component
     * @param c The component to paint.
     */
    public void paint(Graphics g, JComponent c)
    {
        if(header.getColumnModel().getColumnCount() <= 0)
            return;

        boolean ltr = header.getComponentOrientation().isLeftToRight();

        Rectangle clip = g.getClipBounds();
        Point left = clip.getLocation();
        Point right = new Point(clip.x+clip.width-1, clip.y);
        TableColumnModel cm = header.getColumnModel();

        int cMin = header.columnAtPoint(ltr ? left : right);
        int cMax = header.columnAtPoint(ltr ? right : left);
        int columnWidth;

        // This should never happen.
        if (cMin == -1)
            cMin = 0;

        // If the table does not have enough columns to fill the view we'll get -1.
        // Replace this with the index of the last column.
        if (cMax == -1)
            cMax = cm.getColumnCount()-1;

        TableColumn aColumn;
        TableColumn draggedColumn = header.getDraggedColumn();
        Rectangle cellRect = header.getHeaderRect(ltr ? cMin : cMax);

        LipstikGradients.drawGradient(g, c.getBackground(), null, 0,0,c.getWidth(),c.getHeight(), true);

        if (ltr)
        {
            for(int column = cMin; column <= cMax ; column++)
            {
                aColumn = cm.getColumn(column);
                columnWidth = aColumn.getWidth();
                cellRect.width = columnWidth;
                if (aColumn != draggedColumn)
                    paintCell(g, cellRect, column);
                cellRect.x += columnWidth;
            }
        }
        else
            for(int column = cMax; column >= cMin; column--)
            {
                aColumn = cm.getColumn(column);
                columnWidth = aColumn.getWidth();
                cellRect.width = columnWidth;
                if (aColumn != draggedColumn)
                    paintCell(g, cellRect, column);
                cellRect.x += columnWidth;
            }

        // Paint the dragged column if we are dragging.
        if (draggedColumn != null)
        {
            int draggedColumnIndex = viewIndexForColumn(draggedColumn);
            Rectangle draggedCellRect = header.getHeaderRect(draggedColumnIndex);

            // Draw a gray well in place of the moving column.
            g.setColor(header.getParent().getBackground());
            g.fillRect(draggedCellRect.x, draggedCellRect.y, draggedCellRect.width, draggedCellRect.height);

            draggedCellRect.x += header.getDraggedDistance();

            // Fill the background.
            g.setColor(header.getBackground());
            LipstikGradients.drawGradient(g, c.getBackground(), null, draggedCellRect.x, draggedCellRect.y,  draggedCellRect.width, draggedCellRect.height, true);
            paintCell(g, draggedCellRect, draggedColumnIndex);
        }
    }

    private int viewIndexForColumn(TableColumn aColumn)
    {
        TableColumnModel cm = header.getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++)
            if (cm.getColumn(column) == aColumn)
                return column;
        return -1;
    }


    private Component getHeaderRenderer(int columnIndex)
    {
        TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
        TableCellRenderer renderer = aColumn.getHeaderRenderer();
        if (renderer == null)
            renderer = header.getDefaultRenderer();

        return renderer.getTableCellRendererComponent(header.getTable(),
                        aColumn.getHeaderValue(), false, false, -1, columnIndex);
    }

    private void paintCell(Graphics g, Rectangle cellRect, int columnIndex)
    {
        Component component = getHeaderRenderer(columnIndex);
        rendererPane.paintComponent(g,
                component, header,
                cellRect.x,
                cellRect.y,
                cellRect.width,
                cellRect.height,
                true);
    }

    public void installDefaults()
    {
        super.installDefaults();
        header.addMouseMotionListener(new MouseMotionAdapter()
        {
            public void mouseMoved(MouseEvent me)
            {
                if (header != null)
                {
                    int col = header.getColumnModel().getColumnIndexAtX(me.getX());
                    if (col >= 0 && col < header.getColumnModel().getColumnCount())
                        header.setToolTipText(header.getColumnModel().getColumn(col).getHeaderValue().toString());
                }
            }
        });
    }
}
