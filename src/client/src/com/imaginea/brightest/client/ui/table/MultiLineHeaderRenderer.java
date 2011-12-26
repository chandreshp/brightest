/*
 * Created on Sep 11, 2003
 */
package com.imaginea.brightest.client.ui.table;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import java.awt.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import java.util.Vector;


/**
 * Renderer for multi-line headers
 *
 * @version 1.0 11/09/98
 */
public class MultiLineHeaderRenderer extends JList implements TableCellRenderer {
    private static final long serialVersionUID = 2236356767637995171L;

    /**
     * Creates a new MultiLineHeaderRenderer object.
     */
    public MultiLineHeaderRenderer() {
        setOpaque(true);
        setForeground(UIManager.getColor("TableHeader.foreground"));
        setBackground(UIManager.getColor("TableHeader.background"));
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));

        ListCellRenderer renderer = getCellRenderer();

        ((JLabel) renderer).setHorizontalAlignment(JLabel.CENTER);
        setCellRenderer(renderer);
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
     *
     * @return Return the component that should be rendered
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setFont(table.getFont());

        String         str  = (value == null) ? "" : value.toString();
        BufferedReader br   = new BufferedReader(new StringReader(str));
        String         line;
        Vector<String> v    = new Vector<String>();

        try {
            while ((line = br.readLine()) != null) {
                v.addElement(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        setListData(v);

        return this;
    }
}
