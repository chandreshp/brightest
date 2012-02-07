package com.imaginea.brightest.test;

import java.util.ArrayList;
import java.util.List;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.execution.CommandExecutor;
import com.imaginea.brightest.util.Util;
import com.thoughtworks.selenium.Selenium;

/**
 * Each instance of this class represents a test with a name and id. This can be used by the junit and testng wrappers
 * for the actual execution
 */
public class CommandBasedTest {
	private final List<Command> commands = new ArrayList<Command>();
    private String id;
    private String suiteName;
    private String testType;
    private String description;
    private String expectedResult;
    private String tags;
    private String testName;

    public void runTest(Selenium selenium, CommandExecutor executor) {
        for (Command command : commands) {
            executor.execute(command,selenium);
        }
    }

    public String toString() {
        return String.format("%s %s", this.getClass().getSimpleName(), this.getName());
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public int commandCount() {
        return commands.size();
    }

    public String getName() {
        if (Util.isNotBlank(this.testName)) {
            return this.testName;
        } else {
            return String.format("[%s %s]", this.id, this.suiteName);
        }
    }
    public void setName(String name){
    	this.testName=name;
    }

    // java noise of getter setter
    public CommandBasedTest setSuiteName(String suiteName) {
        this.suiteName = suiteName;
        return this;
    }

    public CommandBasedTest setTestId(String testId) {
        this.id = testId;
        return this;
    }

    public CommandBasedTest setId(String id) {
        this.id = id;
        return this;
    }

    public CommandBasedTest setTestType(String testType) {
        this.testType = testType;
        return this;
    }

    public CommandBasedTest setDescription(String description) {
        this.description = description;
        return this;
    }

    public CommandBasedTest setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getTestType() {
        return testType;
    }

    public String getDescription() {
        return description;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public String getTags() {
        return tags;
    }

    public CommandBasedTest setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public Command commandAt(int i) {
        return commands.get(i);
    }
}
