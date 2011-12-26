package com.imaginea.brightest.client.ui;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import com.imaginea.brightest.client.test.AutoXMLStep;
import com.imaginea.brightest.client.test.Step;
import com.imaginea.brightest.client.test.Test;
import com.imaginea.brightest.client.ui.table.CenterAlignedCellRenderer;
import com.imaginea.brightest.client.ui.table.MultiLineHeaderRenderer;
import com.imaginea.brightest.client.ui.table.TextAreaTableCellRenderer;

/**
 * The table model that drives the main test table in the main display
 * 
 * @author rsturim
 */
public class TestTableModel extends AbstractTableModel {
    private static final long             serialVersionUID = -1859932790675417539L;

    private static Category               logger           = Logger.getLogger(TestTableModel.class);

    private final int                     STEP             = 0;
    private final int                     COMMAND          = 1;
    private final int                     TARGET           = 2;
    private final int                     VALUE            = 3;
    private final int                     DESCRIPTION      = 4;
    private final int                     EXPECTED_RESULT  = 5;
    private final int                     STATUS           = 6;
    private final int                     NOTES            = 7;

    // private final int VIEW_XML = 9;
    private com.imaginea.brightest.client.test.Test test;
    private final String[]                columnNames      = { "Step #", "Command", "Target", "Value", "Description", "Expected Result", "Status", "Notes" };
    private final int[]                   columnWidth      = {
            30 /* STEP */,
            110 /* COMMAND */,
            100 /* TARGET */,
            100 /* VALUE */,
            150 /* DESCRIPTION */,
            150 /* EXPECTED_RESULT */,
            40 /* STATUS */,
            150                                           /* Notes */
                                                           };

    /**
     * Creates a new TestTableModel object.
     * 
     * @param test The test to be displayed by the table model
     */
    public TestTableModel(Test test) {
        this.test = test;
        if (logger.isDebugEnabled()) {
            logger.debug("TestTable instantiated");
        }
    }

    // ~ Methods
    // ��������������������������������������������������������������������������������������������������������

    /**
     * Return the number of columns to be displayed in the table
     * 
     * @return the number of columns to be displayed in the table
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Return the name of a given column
     * 
     * @param column The column whose name should be returned
     * @return The name of the specified column
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * Return the number of rows to be displayed in the table
     * 
     * @return the number of rows to be displayed in the table
     */
    public int getRowCount() {
        if (test == null) {
            return 0;
        }

        return test.getSteps().size();
    }

    /**
     * Setter for the test
     * 
     * @param test The test associated to this table model
     */
    public void setTest(com.imaginea.brightest.client.test.Test test) {
        this.test = test;
    }

    /**
     * Getter for the test
     * 
     * @return The test associated to this table model
     */
    public com.imaginea.brightest.client.test.Test getTest() {
        return test;
    }

    /**
     * Provide the value to be displayed for a specified cell
     * 
     * @param rowIndex row number
     * @param columnIndex column number
     * @return The value to be displayed
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (test == null) {
            return "";
        }

        Step step = (Step) test.getSteps().get(rowIndex);

        switch (columnIndex) {
            case STEP:
                return step.getStepNumber();

            case DESCRIPTION:
                return step.getDescription();

                // try
                // {
                // // return
                // test.processTextForTagReferences(step.getDescription());
                // }
                // catch (ParseException e)
                // {
                // logger.error(e);
                // return e.getMessage();
                // }
            case VALUE:
                return step.getDependentStepName();

            case TARGET:
                return step.getDataWorksheetString();

            case EXPECTED_RESULT:
                return step.getExpectedResult();

                // try
                // {
                // return
                // test.processTextForTagReferences(step.getExpectedResult());
                // }
                // catch (ParseException e)
                // {
                // logger.error(e);
                // return e.getMessage();
                // }
            case COMMAND:
                return step.getType();

            case STATUS:
                if (((step.getStatus() == null) || (step.getStatus().length() == 0)) && step.isValidated()) {
                    // only automated steps will ever be validated
                    AutoXMLStep autoStep = (AutoXMLStep) step;

                    if ((autoStep.getErrors() != null) && !((AutoXMLStep) step).getErrors().isEmpty()) {
                        return "Parse Error";
                    }

                    return "Ok";
                }

                return step.getStatus();

            case NOTES:
                return step.getNotes();

            default:
                return "?";
        }
    }

    /**
     * Force the table to be redrawn as a result of a status change in a specific cell
     * 
     * @param table The table being updated
     * @param stepNumber The step whose status has changed
     */
    public void dirtyStatusValue(JTable table, String stepNumber) {
        StepWithIndex stepWithIndex = getStep(stepNumber);
        if (stepWithIndex == null) {
            return;
        }
        fireTableCellUpdated(stepWithIndex.index, STATUS);
        fireTableCellUpdated(stepWithIndex.index, NOTES);
        table.getSelectionModel().setSelectionInterval(stepWithIndex.index, stepWithIndex.index);
    }

    private static class StepWithIndex {
        int index;
    }

    private StepWithIndex getStep(String stepNumber) {
        StepWithIndex stepWithIndex = new StepWithIndex();
        if ((stepNumber == null) || (test == null) || (test.getSteps() == null)) {
            // Not initialized yet
            return null;
        }
        for (int i = 0; i < test.getSteps().size(); i++) {
            Step step = (Step) test.getSteps().get(i);

            if (step.equals(stepNumber)) {
                stepWithIndex.index = i;
                return stepWithIndex;
            }
        }
        return null;
    }

    /**
     * Force the table to be redrawn as a result of a data change
     */
    public void dirtyTable() {
        fireTableDataChanged();
    }

    /**
     * Initialization routine to set the prefered widths of the columns
     * 
     * @param table The table to be displayed
     */
    public void formatTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        MultiLineHeaderRenderer multiLineHeaderRenderer = new MultiLineHeaderRenderer();

        for (int i = 0; i < getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidth[i]);
            table.getColumnModel().getColumn(i).setHeaderRenderer(multiLineHeaderRenderer);
        }

        // table.getColumnModel().getColumn(STEP).setResizable(false);
        fixColumnWidth(table, STEP);
        // fixColumnWidth(table, TYPE);
        fixColumnWidth(table, VALUE);
        fixColumnWidth(table, STATUS);

        setCellRenderers(table);

        table.getColumnModel().addColumnModelListener(new ResizeListener());
    }

    /**
     * Initialization routine to set the table cell renderers of certain columns to control alignment
     * 
     * @param table The table to be displayed
     */
    private void setCellRenderers(JTable table) {
        TableColumnModel tcm = table.getColumnModel();

        tcm.getColumn(STEP).setCellRenderer(new CenterAlignedCellRenderer());
        tcm.getColumn(VALUE).setCellRenderer(new CenterAlignedCellRenderer());

        // tcm.getColumn(TYPE).setCellRenderer(new CenterAlignedCellRenderer());
        tcm.getColumn(DESCRIPTION).setCellRenderer(new TextAreaTableCellRenderer());
        tcm.getColumn(EXPECTED_RESULT).setCellRenderer(new TextAreaTableCellRenderer());
        tcm.getColumn(NOTES).setCellRenderer(new TextAreaTableCellRenderer());
    }

    private void fixColumnWidth(JTable table, int col) {
        table.getColumnModel().getColumn(col).setPreferredWidth(table.getColumnModel().getColumn(col).getWidth());
        table.getColumnModel().getColumn(col).setMaxWidth(table.getColumnModel().getColumn(col).getWidth());
        table.getColumnModel().getColumn(col).setMinWidth(table.getColumnModel().getColumn(col).getWidth());
        table.getColumnModel().getColumn(col).setResizable(false);
    }

    // ~ Classes
    // ��������������������������������������������������������������������������������������������������������

    /**
     * DOCUMENT ME!
     * 
     * @author $author$
     * @version $Revision$
     */
    public class ResizeListener implements TableColumnModelListener {
        // ~ Methods
        // ����������������������������������������������������������������������������������������������������

        /*
         * Do nothing
         */
        public void columnAdded(TableColumnModelEvent e) {
            // Do nothing
        }

        /*
         * Fire table data changed so new cell size will be computed as a result of a column resize
         */
        public void columnMarginChanged(ChangeEvent e) {
            fireTableDataChanged();
        }

        /*
         * Do nothing
         */
        public void columnMoved(TableColumnModelEvent e) {
            // Do nothing
        }

        /*
         * Do nothing
         */
        public void columnRemoved(TableColumnModelEvent e) {
            // Do nothing
        }

        /*
         * Do nothing
         */
        public void columnSelectionChanged(ListSelectionEvent e) {
            // Do nothing
        }
    }
}
