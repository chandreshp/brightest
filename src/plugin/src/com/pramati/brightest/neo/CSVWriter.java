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

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Writes csv
 */
public class CSVWriter extends AbstractWriter {
    private static final String DELIMITER = "~";
    private final String outputFileName;
    private ResultInfo resultInfo;

    public CSVWriter(String filePath) {
        this.outputFileName = filePath;
    }

    protected void addResults(HSSFWorkbook workBook, ResultInfo resultInfo) {
        this.resultInfo = resultInfo;
        super.addResults(workBook, resultInfo);
    }

    public void writeCommands(String[] rawCommands) {
        final StringBuilder testContents = new StringBuilder();
        addTestCase(rawCommands, testContents);
        writeToFile(new WritingTemplate() {
            @Override
            public void doWithStream(OutputStream outputStream) throws IOException {
                outputStream.write(testContents.toString().getBytes());
            }
        }, outputFileName);
    }

    private String addTestCase(String[] rawCommands, StringBuilder testContents) {
        addRowWithContent(new String[] { "Purpose", "Steps" }, testContents);
        for (int i = 0; i < rawCommands.length; i++) {
            String formattedCommand = formatRawCommand(rawCommands[i]);
            addRowWithContent(new String[] { "", formattedCommand }, testContents);
        }
        return testContents.toString();
    }

    private void addRowWithContent(String[] columns, StringBuilder testContents) {
        for (String column : columns) {
            testContents.append(column);
            testContents.append(DELIMITER);
        }
        // eat away the last character
        testContents.deleteCharAt(testContents.length() - 1);
        testContents.append("\n");
    }

    protected void write(final HSSFWorkbook workBook) {
        writeToFile(new WritingTemplate(){

            @Override
            public void doWithStream(OutputStream outputStream) throws IOException {
                StringBuilder results = new StringBuilder();
                int totalSheets = (resultInfo.failureStep > -1) ? 2 : 1;
                for (int i = 0; i < totalSheets; i++) {
                    HSSFSheet sheet = workBook.getSheetAt(i);
                    for (int j = 0; j <= sheet.getLastRowNum(); j++) {
                        HSSFRow row = sheet.getRow(j);
                        if (row != null) {
                            StringBuilder lineBuilder = new StringBuilder();
                            for (short k = 0; k <= row.getLastCellNum(); k++) {
                                @SuppressWarnings("deprecation")
                                HSSFCell cell = row.getCell(k);
                                if (cell != null) {
                                    lineBuilder.append(getCellValue(cell)).append(",");
                                }
                            }
                            if (lineBuilder.toString().matches("(,)+") == false) {
                                results.append(lineBuilder.toString()).append("\n");
                            }
                        }
                    }
                }
                outputStream.write(results.toString().getBytes());
            }
        }, outputFileName);
    }

}
