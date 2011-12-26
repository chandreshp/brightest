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

import com.imaginea.brightest.Command;
import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.execution.PreferenceListener;
import com.thoughtworks.selenium.Selenium;

/**
 * Strategy for executing commands. We can ask a strategy if it applies to a command. We can also ask a strategy to
 * execute it, passing unapplicable commands may have unknown results.
 * 
 * @author apurba
 */
public interface ExecutionStrategy extends PreferenceListener {
    /**
     * Executes the command. All exceptions can be thrown as RuntimeException, which can be checked in TestResult
     * 
     * @param command
     */
    public void execute(Command command);
    
    public void execute(Command command,Selenium selenium);

    /**
     * @param command
     * @return true if command can be handled by this, false otherwise
     */
    public boolean appliesTo(Command command);
}
