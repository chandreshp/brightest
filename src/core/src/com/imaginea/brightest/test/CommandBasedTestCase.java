/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.imaginea.brightest.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.execution.CommandExecutor;

/**
 * Test case containing commands. These commands are executed as part of the actual test.
 * 
 * @author apurba
 */
public class CommandBasedTestCase extends TestCase {
    private final List<Command> commands = new ArrayList<Command>();
    private CommandExecutor executor = null;
    private String id;
    private String suiteName;
    private String testType;
    private String description;
    private String expectedResult;
    private String tags;

    public void runBare() throws Throwable {
        if (executor == null) {
            executor = ExecutionContext.getInstance().getExecutor();
        }
        for (Command command : commands) {
            executor.execute(command);
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

    public void setCommandExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    @Override
    public String getName() {
        return String.format("[%s %s]", this.id, this.suiteName);
    }

    // java noise of getter setter
    public CommandBasedTestCase setSuiteName(String suiteName) {
        this.suiteName = suiteName;
        return this;
    }

    public CommandBasedTestCase setTestId(String testId) {
        this.id = testId;
        return this;
    }

    public CommandBasedTestCase setId(String id) {
        this.id = id;
        return this;
    }

    public CommandBasedTestCase setTestType(String testType) {
        this.testType = testType;
        return this;
    }

    public CommandBasedTestCase setDescription(String description) {
        this.description = description;
        return this;
    }

    public CommandBasedTestCase setExpectedResult(String expectedResult) {
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

    public CommandBasedTestCase setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public Command commandAt(int i) {
        return commands.get(i);
    }
}