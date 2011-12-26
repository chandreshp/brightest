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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.util.DiscoveryService;

/**
 * Execution Strategy for dynamic commands. This strategy discovers extension classes from the classpath and adds them
 * for execution.
 */
public class DynamicCommandExecutionStrategy extends CommandMethodExecutionStrategy {
    private static final Log LOG = LogFactory.getLog(StaticCommandExecutionStrategy.class);

    /**
     * Discovers new command classes and creates commands from them.
     */
    public DynamicCommandExecutionStrategy() {
        DiscoveryService discoveryService = new DiscoveryService();
        List<Class<?>> commandClasses = discoveryService.discoverClasses("com.imaginea.brightest.extension");
        LOG.debug("Discovered new command classes " + commandClasses);
        for (Class<?> commandClass : commandClasses) {
            discoverAndAddCommands(commandClass);
        }
    }

    @Override
    public void execute(Command command) {
        CommandMethod commandMethod = commandSet.get(command.getName());
        commandMethod.invoke(ExecutionContext.getInstance(), command, commandMethod.commandHandler);
    }
}
