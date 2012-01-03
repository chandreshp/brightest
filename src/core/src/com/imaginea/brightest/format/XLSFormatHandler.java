/*
 * Copyright (c) 2011 Imaginea Technologies Private Ltd. 
 * Hyderabad, India
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following condition
 * is met:
 *
 *     + Neither the name of Imaginea, nor the
 *       names of its contributors may be used to endorse or promote
 *       products derived from this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.imaginea.brightest.format;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.test.BrightestTestSuite;
import com.imaginea.brightest.test.CommandBasedTest;
import com.imaginea.brightest.test.CommandBasedTestCase;

/**
 * TODO add CSVFormatHandler
 * 
 * <pre>
 * FormatHandler for xls files. Reads and writes tests from and to xls files.
 * </pre>
 * 
 * @author apurba
 */
public class XLSFormatHandler extends FormatHandler {
    private static final Log LOGGER = LogFactory.getLog(XLSFormatHandler.class);

    @Override
    protected boolean understands(String fileName) {
        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
    }

    @Override
    protected TestSuite loadSuiteInternal(String fileName) {
        TestSuite suite = new BrightestTestSuite();
        WorkBook workBook = new WorkBook(fileName);
        while (workBook.hasMoreElements()) {
            TestCaseSheet sheet = workBook.nextElement();
            suite.addTest(loadTestCase(sheet));
        }
        return suite;
    }

    private TestCase loadTestCase(TestCaseSheet sheet) {
        CommandBasedTestCase testCase = sheet.getTestCase();
        while (sheet.hasMoreElements()) {
            testCase.addCommand(loadCommand(sheet.nextElement()));
        }
        return testCase;
    }

    private Command loadCommand(CommandRow commandRow) {
        return commandRow.getCommand();
    }

    private static HSSFCell cellAt(HSSFRow row, int cellNumber) {
        if (row == null) {
            return null;
        } else {
            return row.getCell(cellNumber);
        }
    }

    private static String valueOf(HSSFCell cell) {
        if (cell == null) {
            return null;
        } else {
            String cellValue = null;
            if (cell.getCellType() == 0) {
                cellValue = "" + cell.getNumericCellValue();
            } else if (cell.getCellType() == 2) {
                cellValue = "" + cell.getBooleanCellValue();
            } else
                cellValue = "" + ((cell.getRichStringCellValue() == null) ? "" : cell.getRichStringCellValue().getString());
            return cellValue.trim();
        }
    }

    private static boolean isNotBlank(String value) {
        return (value != null && value.trim().length() > 0);
    }

    private static class WorkBook implements Enumeration<TestCaseSheet> {
        private HSSFWorkbook workBook;
        private final HSSFSheet sheet;
        private String bookName;
        private int rowIndex = 1;

        public WorkBook(String file) {
            try {
                workBook = new HSSFWorkbook(new FileInputStream(file));
                sheet = workBook.getSheet("testscript");
                if (sheet == null) {
                    throw new UnknownFormatException(file);
                }
                bookName = file;
            } catch (IOException ioExc) {
                throw new UnknownFormatException(file, ioExc);
            }
        }

        public boolean hasMoreElements() {
            HSSFRow row = sheet.getRow(rowIndex);
            while (rowIndex < sheet.getPhysicalNumberOfRows()) {
                if (isNotBlank(valueOf(cellAt(row, 0)))) {
                    return true;
                } else {
                    rowIndex++;
                }
            }
            return false;
        }

        public TestCaseSheet nextElement() {
            return new TestCaseSheet(workBook, sheet.getRow(rowIndex++), bookName);
        }
    }

    private static class TestCaseSheet implements Enumeration<CommandRow> {
        private static enum HeaderRow {
            TESTID, TYPE, DESCRIPTION, EXPECTED_RESULT, WORKSHEET, TAGS;
        }

        private final HSSFRow testRow;
        private final HSSFSheet testSheet;
        private final String bookName;
        private int rowIndex = 1;// we want to skip the first

        public TestCaseSheet(HSSFWorkbook workBook, HSSFRow testRow, String bookName) {
            this.testRow = testRow;
            this.testSheet = workBook.getSheet(getSheetName());
            this.bookName = bookName;
            if (this.testSheet == null) {
                throw new MissingWorksheetException(testRow.getRowNum(), getSheetName());
            }
        }

        public boolean hasMoreElements() {
            HSSFRow row = testSheet.getRow(rowIndex);
            while (rowIndex < testSheet.getPhysicalNumberOfRows()) {
                if (isNotBlank(valueOf(cellAt(row, 1))) || isNotBlank(valueOf(cellAt(row, 0)))) {
                    return true;
                } else {
                    rowIndex++;
                }
            }
            return false;
        }

        @Override
        public CommandRow nextElement() {
            HSSFRow row = testSheet.getRow(rowIndex++);
            return new CommandRow(row, this);
        }

        private String getSheetName() {
            return valueOf(cellAt(testRow, HeaderRow.WORKSHEET.ordinal()));
        }

        public CommandBasedTestCase getTestCase() {
            CommandBasedTestCase testCase = new CommandBasedTestCase();
            testCase.setId(getTestId()).setSuiteName(bookName).setTestType(getType()).setDescription(getDescription()).setTags(getTags());
            return testCase;
        }
        
        public CommandBasedTest getDriverTestCase() {
            CommandBasedTest testCase = new CommandBasedTest();
            testCase.setId(getTestId()).setSuiteName(bookName).setTestType(getType()).setDescription(getDescription()).setTags(getTags());
            return testCase;
        }

        public String toString() {
            return String.format("%s [ Sheet: %s]", this.getClass().getSimpleName(), ((testSheet == null) ? "NONE" : getSheetName()));
        }

        // stupid java methods
        public String getDescription() {
            return valueOf(cellAt(testRow, HeaderRow.DESCRIPTION.ordinal()));
        }

        public String getTags() {
            return valueOf(cellAt(testRow, HeaderRow.TAGS.ordinal()));
        }

        public String getTestId() {
            return valueOf(cellAt(testRow, HeaderRow.TESTID.ordinal()));
        }

        public String getType() {
            return valueOf(cellAt(testRow, HeaderRow.TYPE.ordinal()));
        }
    }

    protected static class CommandRow {
        private static enum LegacyRowFormat {
            PURPOSE, STEPS;
        }

        private static enum CommandRowFormat {
            STEP, ARGUMENT, OPTIONAL_ARG;
        }

        private final HSSFRow row;
        private final TestCaseSheet parentSheet;

        public CommandRow() {
            this(null, null);
        }

        public CommandRow(HSSFRow row, TestCaseSheet testCaseSheet) {
            this.row = row;
            this.parentSheet = testCaseSheet;
        }

        public Command getCommand() {
            try {
                Command command = null;
                if (isNotBlank(valueOf(cellAt(row, CommandRowFormat.STEP.ordinal())))) {
                    command = new Command();
                    command.setName(valueOf(cellAt(row, CommandRowFormat.STEP.ordinal())));
                    command.setArgument(valueOf(cellAt(row, CommandRowFormat.ARGUMENT.ordinal())));
                    command.setOptionalArgument(valueOf(cellAt(row, CommandRowFormat.OPTIONAL_ARG.ordinal())));
                } else {
                    String consCommand = valueOf(cellAt(row, LegacyRowFormat.STEPS.ordinal()));
                    command = parse(consCommand);
                }
                return command;
            } catch (RuntimeException exc) {
                LOGGER.error("Problems with row " + row.getRowNum() + " of worksheet " + parentSheet);
                throw exc;
            }
        }

        protected Command parse(String commandString) {
            commandString = commandString.trim();
            String[] tokens = parseString(commandString);
            Command command = new Command();
            command.setName(tokens[0]).setArgument(tokens[1]).setOptionalArgument(tokens[2]);
            return command;
        }

        private String[] parseString(String command) {
            command = command.trim();
            String[] vals = new String[3];
            int cmdNameEnd = command.indexOf('(');
            vals[0] = command.substring(0, cmdNameEnd);
            command = command.substring(cmdNameEnd + 1);
            command = command.substring(0, command.length() - 1);
            int argEnd = -1;
            if ((argEnd = command.lastIndexOf('|')) != -1) {
            } else {
                argEnd = command.lastIndexOf(',');
            }
            if (argEnd != -1) {
                vals[1] = command.substring(0, argEnd).trim();
                vals[2] = command.substring(argEnd + 1).trim();
            } else {
                vals[1] = command.substring(0);
                if (vals[1].trim().length() == 0) {
                    vals[1] = null;
                }
            }
            return vals;
        }
    }

    /**
     * The worksheet is missing, not sure about the worksheet name can be null. row num better bet
     * 
     * @author apurba
     */
    private static class MissingWorksheetException extends RuntimeException {
        private static final long serialVersionUID = -7510201304146643706L;

        public MissingWorksheetException(int rowNum, String sheetName) {
            super("Test case at rownum [" + rowNum + "] refers to unknown sheet name [" + sheetName + "]");
        }
    }

	@Override
	public CommandBasedTest loadDriverTest(String fileName) {
		
		WorkBook workBook = new WorkBook(fileName);
		TestCaseSheet sheet = workBook.nextElement();
		CommandBasedTest testCase = sheet.getDriverTestCase();
			while (sheet.hasMoreElements()) {
				testCase.addCommand(loadCommand(sheet.nextElement()));
	        }
		return testCase;
	}
}
