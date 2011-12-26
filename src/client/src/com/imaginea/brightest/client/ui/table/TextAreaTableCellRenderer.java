package com.imaginea.brightest.client.ui.table;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Custom renderer to draw the text that word wraps
 * 
 */
public class TextAreaTableCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 2767134233972430634L;
    private final JTextArea delegate = new JTextArea();

    /**
     * Creates a new TextAreaTableCellRenderer object.
     */
    public TextAreaTableCellRenderer() {
        delegate.setWrapStyleWord(true);
        delegate.setLineWrap(true);
        delegate.setFont(new java.awt.Font("SansSerif", 1, 10));
    }

    /**
     * Standard method to be implemented
     * 
     * @param table The table being drawn
     * @param value The value to be drawn
     * @param isSelected Is the value selected or not
     * @param hasFocus Does the value have focus
     * @param row The row being drawn
     * @param column The column being drawn
     * @return Return the component that should be rendered
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        delegate.setText((String) value);

        // fix
        int width = table.getColumnModel().getColumn(column).getWidth();

        delegate.setSize(width, 1000);

        // end of fix
        if (delegate.getPreferredSize().getHeight() > table.getRowHeight(row)) {
            table.setRowHeight(row, (int) delegate.getPreferredSize().getHeight());
        }

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        delegate.setForeground(label.getForeground());
        delegate.setBackground(label.getBackground());

        return delegate;
    }

}
