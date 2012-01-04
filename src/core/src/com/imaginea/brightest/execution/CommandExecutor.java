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
package com.imaginea.brightest.execution;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imaginea.brightest.ApplicationPreferences;
import com.imaginea.brightest.Command;
import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.executionstrategy.DSLExecutionStrategy;
import com.imaginea.brightest.executionstrategy.DynamicCommandExecutionStrategy;
import com.imaginea.brightest.executionstrategy.ExecutionStrategy;
import com.imaginea.brightest.executionstrategy.ReflectiveExecutionStrategy;
import com.imaginea.brightest.executionstrategy.StaticCommandExecutionStrategy;
import com.thoughtworks.selenium.Selenium;

/**
 * Responsible for running commands. Depends on selenium.
 * 
 * @author apurba
 */
public class CommandExecutor implements PreferenceListener {
    private static final Log LOG = LogFactory.getLog(CommandExecutor.class);
    private final List<ExecutionStrategy> executionStrategies = new ArrayList<ExecutionStrategy>();
    private final ApplicationPreferences preferences;
    private ExecutionContext context;
    public CommandExecutor(ApplicationPreferences preferences) {
        this.preferences = preferences;
        executionStrategies.add(new DynamicCommandExecutionStrategy());
        executionStrategies.add(new DSLExecutionStrategy(preferences, this));
        executionStrategies.add(new StaticCommandExecutionStrategy());
        executionStrategies.add(new ReflectiveExecutionStrategy());
    }

    public void execute(Command command) {
        for (ExecutionStrategy strategy : executionStrategies) {
            if (strategy.appliesTo(command)) {
                LOG.info("Running " + command + " with " + strategy);
                command.prepare(ExecutionContext.getInstance());
                strategy.execute(command);
                break;
            }
            
        }
        
    }
    public void execute(Command command,Selenium selenium) {
        for (ExecutionStrategy strategy : executionStrategies) {
            if (strategy.appliesTo(command)) {
                LOG.info("Running " + command + " with " + strategy);
                if(context==null){
                	command.prepare(ExecutionContext.getInstance());
                }else{
                	command.prepare(context);
                }
               // System.out.println("selenium object :"+selenium.toString());
                strategy.execute(command,selenium);
                break;
            }
        }
    }
    
    public void setExceutionContext(ExecutionContext context){
    	this.context=context;
    }

    /**
     * Adds a execution strategy.
     * 
     * @param order if null defaulted to 0, otherwise inserted at that position.
     * @param strategy
     */
    public void addExecutionStrategy(Integer order, ExecutionStrategy strategy) {
        if (order == null) {
            order = Integer.valueOf(0);
        }
        executionStrategies.add(order, strategy);
    }

    @Override
    public void changed(ApplicationPreferences newPreferences) {
        if (preferences.equals(newPreferences) == false) {
            for (PreferenceListener listener : executionStrategies) {
                listener.changed(newPreferences);
            }
        }
    }
}
