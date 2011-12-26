/*
 * Created on Aug 10, 2003
 */
package com.imaginea.brightest.client.ui.table;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import java.awt.Component;

/**
 * Custom renderer to draw the text in the middle of the cell
 * 
 * @author bsturim
 */
public class CenterAlignedCellRenderer extends JLabel implements TableCellRenderer {

    private static final long                    serialVersionUID = 2448462397097330812L;
    /**
     * The cell renderer for center-aligned text
     */
    public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

    /**
     * Creates a new CenterAlignedCellRenderer object.
     */
    public CenterAlignedCellRenderer() {
        super();
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
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        ((JLabel) renderer).setOpaque(true);
        ((JLabel) renderer).setHorizontalAlignment(JLabel.CENTER);

        return renderer;
    }
}
