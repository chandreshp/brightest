package com.imaginea.brightest.format;

import com.imaginea.brightest.test.CommandBasedTest;
import com.imaginea.brightest.test.CommandBasedTestGroup;

/**
 * Same as selenium Formatters, they can read file formats and create a test suite from them
 * 
 * @author apurba
 */
public abstract class FormatHandler {
    /**
     * Loads a test suite from the given file name
     * 
     * @param fileName
     * @return a test suite or null if the format handler does not understand it
     */
    public CommandBasedTestGroup loadTestSuite(String fileName) {
        if (understands(fileName)) {
            return loadTestSuiteInternal(fileName);
        }
        return null;
    }

    /**
     * override to provide if the concrete format handler understands this filename
     * 
     * @param fileName
     * @return true if it understands, false otherwise
     */
    protected abstract boolean understands(String fileName);

    /**
     * Concrete implementations should override this to create the actual test suite. This method is called only if the
     * format handler indicates that it understand the supplied file
     * 
     * @see #understands(String)
     * @param fileName
     * @return
     */
    protected abstract CommandBasedTestGroup loadTestSuiteInternal(String fileName);

    /**
     * Loads a test case from the given file
     * 
     * @param fileName
     * @return CommandBasedTest if the format handler understands the file, null otherwise
     */
    public CommandBasedTest loadTestCase(String fileName) {
    	if (understands(fileName)) {
            return loadTestCaseInternal(fileName);
        }
        return null;
    }

    /**
     * Concrete implementation should provide the actual way of loading the test case from the given file. Called only
     * if the format handler indicates that it understands the file.
     * 
     * @param fileName
     * @return
     */
    protected abstract CommandBasedTest loadTestCaseInternal(String fileName);

}
