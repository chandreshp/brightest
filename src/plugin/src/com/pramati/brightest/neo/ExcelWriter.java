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
package com.pramati.brightest.neo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * ExcelWriter responsible for writing commands to excel files
 */
public class ExcelWriter extends AbstractWriter {
    private final String outputFileName;

    /**
     * Constructor used to set destination filename
     * 
     * @param outputFileName the destination file
     */
    public ExcelWriter(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    /**
     * @param rawCommands
     */
    public void writeCommands(String[] rawCommands) {
        try {
            HSSFWorkbook workBook = createWorkBook();
            HSSFSheet outputFileWorkSheet = workBook.createSheet("TestScript");
            HSSFCellStyle style = workBook.createCellStyle();
            HSSFFont font = workBook.createFont();
            // add the TestScript
            addTestScript(outputFileWorkSheet, style, font);
            // add the test case
            addTestCase(rawCommands, workBook);
            write(workBook);
        } catch (Exception exc) {
            exc.printStackTrace();
            throw new RuntimeException(exc);
        }
    }

    public void writeResults(String[] rawResults) {
        try {
            for (String rawResult : rawResults) {
                ResultInfo resultInfo = new ResultInfo(rawResult);
                HSSFWorkbook workBook = new HSSFWorkbook(new BufferedInputStream(new FileInputStream(resultInfo.testFileName)));
                addResults(workBook, resultInfo);
                write(workBook);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            throw new RuntimeException(exc);
        }
    }

    private void addTestCase(String[] rawCommands, HSSFWorkbook workBook) {
        HSSFSheet testCaseSheet = workBook.createSheet("TestCase");
        addRowWithContent(new String[] { "Purpose", "Steps" }, testCaseSheet.createRow(0));
        for (int i = 0; i < rawCommands.length; i++) {
            String formattedCommand = formatRawCommand(rawCommands[i]);
            addRowWithContent(new String[] { "", formattedCommand }, testCaseSheet.createRow(i + 1));
        }
    }

    private void addTestScript(HSSFSheet outputFileWorkSheet, HSSFCellStyle style, HSSFFont font) {
        String[] scriptHeaders = { "Test id", "Type", "Description", "Expected Result", "Worksheet", "Tags" };
        HSSFRow headerRow = outputFileWorkSheet.createRow(0);
        setHeaderRowStyle(headerRow, style, font);
        addRowWithContent(scriptHeaders, headerRow);
        HSSFRow bodyRow = outputFileWorkSheet.createRow(1);
        addRowWithContent(new String[] { "1", "AUTO", "", "", "TestCase", "" }, bodyRow);
    }

    private void addRowWithContent(String[] rowData, HSSFRow row) {
        for (int i = 0; i < rowData.length; i++) {
            String headerData = rowData[i];
            addCellWithContent(row, i, headerData);
        }
    }

    /**
     * Sets the font of content of header row cell's and fill the background color of header row
     * 
     * @param outputRow the row to which cell style is to be applied
     * @param style the type of style properties
     * @param font to set the font of header row cell's
     */
    private void setHeaderRowStyle(HSSFRow outputRow, HSSFCellStyle style, HSSFFont font) {
        style.setFillForegroundColor(HSSFColor.ORANGE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);
        for (@SuppressWarnings("rawtypes")
        Iterator cell = outputRow.iterator(); cell.hasNext();) {
            ((HSSFCell) cell.next()).setCellStyle(style);
        }
    }

    /**
     * write the updated contents(copied source file contents with newly appended cell) to the destination file
     * 
     * @param workBook workbook associated with the destination file
     */
    protected void write(final HSSFWorkbook workBook) {
        writeToFile(new WritingTemplate() {
            @Override
            public void doWithStream(OutputStream outputStream) throws IOException {
                workBook.write(outputStream);
            }
        }, outputFileName);
    }

    /**
     * creates a new workbook which is associated with the file
     * 
     * @return the instance of newly created work book
     * @throws FileNotFoundException
     */
    private HSSFWorkbook createWorkBook() throws FileNotFoundException {
        HSSFWorkbook outputFileWorkBook = new HSSFWorkbook();
        return outputFileWorkBook;
    }
}
