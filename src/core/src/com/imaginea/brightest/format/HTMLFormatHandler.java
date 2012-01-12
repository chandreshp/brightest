package com.imaginea.brightest.format;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.test.CommandBasedTest;
import com.imaginea.brightest.test.CommandBasedTestGroup;

/**
 * Format handler for html selenese scripts
 */
public class HTMLFormatHandler extends FormatHandler {
    private SAXParser saxParser;

    public HTMLFormatHandler() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            saxParser = factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean understands(String fileName) {
        return fileName.endsWith("html") || fileName.endsWith("htm");
    }

    @Override
    protected CommandBasedTestGroup loadTestSuiteInternal(String fileName) {
        CommandBasedTestGroup group = new CommandBasedTestGroup();
        group.addTest(loadTestCaseInternal(fileName));
        return group;
    }

    @Override
    protected CommandBasedTest loadTestCaseInternal(String fileName) {
        FaultTolerantHandler handler = new FaultTolerantHandler();
        try {
            saxParser.parse(new File(fileName), handler);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return handler.seleniumTest;
    }

    /**
     * Fault tolerant does not propagate any parsing exceptions
     */
    private static class FaultTolerantHandler extends DefaultHandler {
        private final Stack<String> commandStack = new Stack<String>();
        private StringBuilder bodyText = new StringBuilder();
        private CommandBasedTest seleniumTest;
        private Stage currentStage = Stage.TestName;

        public FaultTolerantHandler() {
        }

        private enum Stage {
            CommandName, CommandArg, OptionalArg, TestName;
        }

        /**
         * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
         *      org.xml.sax.Attributes)
         */
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            // Create a new text holding buffer
            if (qName.equalsIgnoreCase("td")) {
                bodyText = new StringBuilder();
            }
        }

        /**
         * @see org.xml.sax.ContentHandler#characters(char[], int, int)
         */
        public void characters(char[] ch, int start, int length) throws SAXException {
            // Store in the contents StringBuffer
            bodyText.append(ch, start, length);
        }

        /**
         * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equalsIgnoreCase("td")) {
                commandStack.push(bodyText.toString());
                switch (currentStage) {
                    case TestName:
                        seleniumTest = new CommandBasedTest();
                        break;
                    case OptionalArg:
                        String optionalArg = commandStack.pop();
                        String arg = commandStack.pop();
                        String commandName = commandStack.pop();
                        Command command = new Command();
                        command.setArgument(arg).setName(commandName).setOptionalArgument(optionalArg);
                        seleniumTest.addCommand(command);
                        break;
                }
                incrementCurrentStage();
            }
        }

        private void incrementCurrentStage() {
            switch (currentStage) {
                case TestName:
                    currentStage = Stage.CommandName;
                    break;
                case CommandName:
                    currentStage = Stage.CommandArg;
                    break;
                case CommandArg:
                    currentStage = Stage.OptionalArg;
                    break;
                case OptionalArg:
                    currentStage = Stage.CommandName;
            }
        }

        public InputSource resolveEntity(String publicId, String systemId) {
            return new InputSource(new StringReader(""));
        }
        /**
         * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
         */
        public void error(SAXParseException e) throws SAXException {
        }

        /**
         * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
         */
        public void fatalError(SAXParseException e) throws SAXException {
        }

        /**
         * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
         */
        public void warning(SAXParseException e) throws SAXException {
        }
    }
}