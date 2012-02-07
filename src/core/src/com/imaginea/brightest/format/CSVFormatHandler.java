package com.imaginea.brightest.format;

import static com.imaginea.brightest.util.Util.isNotBlank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csvreader.CsvReader;
import com.imaginea.brightest.Command;
import com.imaginea.brightest.test.CommandBasedTest;
import com.imaginea.brightest.test.CommandBasedTestGroup;

public class CSVFormatHandler extends FormatHandler {
    private static final Log LOGGER = LogFactory.getLog(CSVFormatHandler.class);

    @Override
    protected boolean understands(String fileName) {
        return fileName.endsWith(".csv");
    }

    @Override
    protected CommandBasedTestGroup loadTestSuiteInternal(String fileName) {
        CommandBasedTestGroup group = new CommandBasedTestGroup();
        try {
            TestCaseCsv csv = new TestCaseCsv(fileName, '~');
            group.addTest(loadTestCase(csv));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return group;
    }

    @Override
    protected CommandBasedTest loadTestCaseInternal(String fileName) {
        CommandBasedTest testCase = null;
        try {
            TestCaseCsv csv = new TestCaseCsv(fileName, '~');
            testCase = csv.getTest();
            int noOfRows = csv.getNoOfRows();
            for (int i = 1; i < noOfRows; i++) {
                testCase.addCommand(loadCommand(new CSVCommandRow(i, csv)));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No File with the given name could be found <" + fileName + ">", e);
        }
        return testCase;
    }

    private CommandBasedTest loadTestCase(TestCaseCsv csv) {
        CommandBasedTest test = csv.getTest();
        int noOfRows = csv.getNoOfRows();
        for (int i = 1; i < noOfRows; i++) {
            test.addCommand(loadCommand(new CSVCommandRow(i, csv)));
        }
        return test;
    }

    private Command loadCommand(CommandRow commandRow) {
        return commandRow.getCommand();
    }

    protected class TestCaseCsv extends CsvReader {
        private final Hashtable<Integer, ArrayList<String>> csvTable;
        private int row, noOfRows;
        private ArrayList<String> columns, temp;
        private String data;
        private final String fileName;

        public TestCaseCsv(String fileName, char delimiter, Charset charset) throws FileNotFoundException {
            super(fileName, delimiter, charset);
            this.fileName = fileName;
            csvTable = new Hashtable<Integer, ArrayList<String>>();
            this.storeData();
        }

        public TestCaseCsv(String fileName, char delimiter) throws FileNotFoundException {
            super(fileName, delimiter);
            this.fileName = fileName;
            csvTable = new Hashtable<Integer, ArrayList<String>>();
            this.storeData();
        }

        public TestCaseCsv(String fileName) throws FileNotFoundException {
            super(fileName);
            this.fileName = fileName;
            csvTable = new Hashtable<Integer, ArrayList<String>>();
            this.storeData();
        }

        private void storeData() {
            try {
                for (row = 0; super.readRecord(); row++) {
                    columns = new ArrayList<String>();
                    int count = super.getColumnCount();
                    for (int i = 0; i < count; i++) {
                        columns.add((super.get(i)));
                    }
                    csvTable.put(row, columns);
                }
                noOfRows = row;
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }

        public String getData(int row, int column) {
            temp = null;
            data = "";
            try {
                temp = csvTable.get(row);
            } catch (NullPointerException e) {
                System.out.println("Row not found in the said CSV.");
            }
            try {
                data = temp.get(column);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Data not found for said columns");
            }
            return data;
        }

        public int getNoOfRows() {
            return noOfRows;
        }

        public CommandBasedTest getTest() {
            CommandBasedTest testCase = new CommandBasedTest();
            testCase.setName(new File(fileName).getName());
            testCase.setId(new File(fileName).getName());
            return testCase;
        }
    }

    protected static class CSVCommandRow extends CommandRow {
        private final Integer row;
        private final TestCaseCsv csv;

        public CSVCommandRow() {
            this(null, null);
        }

        public CSVCommandRow(Integer row, TestCaseCsv csv) {
            this.row = row;
            this.csv = csv;
        }

        public Command getCommand() {
            try {
                Command command = null;
                if (isNotBlank(csv.getData(row, CommandRowFormat.STEP.ordinal()))) {
                    command = new Command();
                    command.setName(csv.getData(row, CommandRowFormat.STEP.ordinal()));
                    command.setArgument(csv.getData(row, CommandRowFormat.ARGUMENT.ordinal()));
                    command.setOptionalArgument(csv.getData(row, CommandRowFormat.OPTIONAL_ARG.ordinal()));
                } else {
                    String consCommand = csv.getData(row, LegacyRowFormat.STEPS.ordinal());
                    command = parse(consCommand);
                }
                return command;
            } catch (RuntimeException exc) {
                LOGGER.error("Problems with row " + row + " of worksheet " + csv.getTest());
                throw exc;
            }
        }
    }
}
