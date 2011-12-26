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
package com.imaginea.brightest.executionstrategy;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.execution.CommandExecutor;
import com.imaginea.brightest.executionstrategy.DSLExecutionStrategy.DSLCommand;

public class DSLExecutionStrategyTest {
    private static final String jsonString = "[{\"name\":\"dsl1\",\"commands\":[{\"command\":\"c1\",\"target\":\"xpath\", \"value\":\"val1\"}]},{\"name\":\"dsl2\",\"commands\":[{\"command\":\"c21\",\"target\":\"xpath21\",\"value\":\"val21\"}, {\"command\":\"c22\",\"target\":\"xpath22\",\"value\":\"val22\"}]}]";

    @Test
    public void checkParsedDSLCommands() {
        DSLExecutionStrategy testStrategy = new DSLExecutionStrategy();
        List<DSLCommand> dslCommands = testStrategy.parse(jsonString);
        Assert.assertEquals(2, dslCommands.size());
        DSLCommand dslCommand = dslCommands.get(0);
        Assert.assertEquals("dsl1", dslCommand.getName());
        Assert.assertEquals(1, dslCommand.getCommands().size());
        Command cmd = dslCommand.getCommands().get(0);
        Assert.assertEquals("c1", cmd.getName());
    }

    @Test
    public void checkParsedMultiCommandDSLCommand() {
        DSLExecutionStrategy testStrategy = new DSLExecutionStrategy();
        List<DSLCommand> dslCommands = testStrategy.parse(jsonString);
        DSLCommand dslCommand = dslCommands.get(1);
        Assert.assertEquals("dsl2", dslCommand.getName());
        Assert.assertEquals(2, dslCommand.getCommands().size());
        Command cmd = dslCommand.getCommands().get(0);
        Assert.assertEquals("c21", cmd.getName());
    }

    @Test
    public void appliesTo() {
        DSLExecutionStrategy testStrategy = getStrategy(jsonString);
        Assert.assertTrue(testStrategy.appliesTo(new Command().setName("dsl1")));
        Assert.assertTrue(testStrategy.appliesTo(new Command().setName("dsl2")));
    }

    @Test
    public void execute() {
        DSLExecutionStrategy testStrategy = getStrategy(jsonString);
        StaticMockCommandExecutor staticMock = new StaticMockCommandExecutor();
        testStrategy.setCommandExecutor(staticMock);
        Command dslCommand = new Command().setName("dsl2");
        Assert.assertTrue(testStrategy.appliesTo(dslCommand));
        testStrategy.execute(dslCommand);
        List<Command> commands = staticMock.commands;
        Assert.assertEquals(2, commands.size());
    }

    private DSLExecutionStrategy getStrategy(String jsonString) {
        DSLExecutionStrategy testStrategy = new DSLExecutionStrategy();
        testStrategy.parse(jsonString);
        return testStrategy;
    }

    private class StaticMockCommandExecutor extends CommandExecutor {
        private final List<Command> commands = new ArrayList<Command>();

        public StaticMockCommandExecutor() {
            super(null);
        }

        public void execute(Command command) {
            commands.add(command);
        }
    }
}
