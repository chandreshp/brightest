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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import junit.framework.ComparisonFailure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.execution.CommandInfo;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Base class for all command method execution strategies.
 */
public abstract class CommandMethodExecutionStrategy extends AbstractExecutionStrategy {
    private static final Log LOG = LogFactory.getLog(CommandMethodExecutionStrategy.class);
    protected final Map<String, CommandMethod> commandSet = new HashMap<String, CommandMethod>();

    /**
     * Discover and add commands found in the class
     * 
     * @param commandHandlerClass
     */
    protected void discoverAndAddCommands(Class<?> commandHandlerClass) {
        try {
            Object commandHandlerInstance = commandHandlerClass.newInstance();
            Method[] methods = commandHandlerClass.getMethods();
            for (Method method : methods) {
                CommandInfo commandInfo = null;
                if ((commandInfo = method.getAnnotation(CommandInfo.class)) != null) {
                    String commandName = commandInfo.name();
                    if (commandName == null || commandName.trim().length() == 0) {
                        commandName = method.getName();
                    }
                    if (method.getReturnType() == java.lang.Void.TYPE) {
                        commandSet.put(commandName, new CommandMethod(commandName, method, commandHandlerInstance));
                    } else {
                        commandSet.put(commandName, new AssertMethod(commandName, method, commandHandlerInstance));
                    }
                }
            }
        } catch (Exception exc) {
            LOG.error("Ignoring exception while discovering commands ", exc);
        }
    }

    /**
     * All command method classes apply to commands that are present in the set.
     */
    @Override
    public boolean appliesTo(Command command) {
        return commandSet.containsKey(command.getName());
    }


    /**
     * Method wrapper around a command.
     */
    protected static class CommandMethod {
        protected final String commandName;
        protected final Method method;
        protected final Object commandHandler;

        public CommandMethod(String commandName, Method method, Object commandHandler) {
            this.commandName = commandName;
            this.method = method;
            this.commandHandler = commandHandler;
        }

        @Override
        public String toString() {
            return String.format("%s [command: %s, method: %s]", this.getClass().getSimpleName(), this.commandName, this.method.getName());
        }

        public void invoke(ExecutionContext context, Command command, Object object) {
            try {
                method.invoke(object, context, command);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof SeleniumException) {
                    throw ((SeleniumException) e.getCause());
                } else {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                LOG.error("Problems in running " + this, e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Wrapper for assert commands.
     */
    protected static class AssertMethod extends CommandMethod {
        public AssertMethod(String commandName, Method method, Object commandHandler) {
            super(commandName, method, commandHandler);
        }

        public void invoke(ExecutionContext context, Command command, Object object) {
            try {
                Boolean result = (Boolean) method.invoke(object, context, command);
                if (result == false) {
                    throw new ComparisonFailure("Failed " + command, command.getRuntimeOptionalArgument(), command.getRuntimeArgument());
                }
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof SeleniumException) {
                    throw ((SeleniumException) e.getCause());
                } else {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                LOG.error("Problems in running " + this, e);
                throw new RuntimeException(e);
            }
        }
    }
}
