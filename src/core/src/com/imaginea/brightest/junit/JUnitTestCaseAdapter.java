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
package com.imaginea.brightest.junit;

import junit.framework.TestCase;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.execution.CommandExecutor;
import com.imaginea.brightest.test.CommandBasedTest;

/**
 * JUnitTest case adapter which converts command based test case into a junit test.
 */
public class JUnitTestCaseAdapter extends TestCase {
    private final CommandBasedTest delegate;

    public JUnitTestCaseAdapter(CommandBasedTest test) {
        this.delegate = test;
    }

    public void runBare() throws Throwable {
        delegate.runTest(ExecutionContext.getInstance().getSelenium());
    }

    public String toString() {
        return String.format("%s %s", this.getClass().getSimpleName(), this.getName());
    }

    public void addCommand(Command command) {
        delegate.addCommand(command);
    }

    public int commandCount() {
        return delegate.commandCount();
    }

    public void setCommandExecutor(CommandExecutor executor) {
        delegate.setCommandExecutor(executor);
    }

    @Override
    public String getName() {
        return String.format("[%s %s]", delegate.getId(), delegate.getName());
    }

    // java noise of getter setter
    public JUnitTestCaseAdapter setSuiteName(String suiteName) {
        delegate.setSuiteName(suiteName);
        return this;
    }

    public JUnitTestCaseAdapter setTestId(String testId) {
        delegate.setId(testId);
        return this;
    }

}