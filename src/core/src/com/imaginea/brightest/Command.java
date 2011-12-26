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
package com.imaginea.brightest;

import com.imaginea.brightest.util.Util;

/**
 * Command represents a selenium command, which has a name, argument and may have an optional argument. Any decent
 * implementation would expect it to have run method, however in selenium run is highly volatile, with webdriver coming
 * into picture though commands would stay same the execution logic would heavily change.
 */
public class Command {
    private String name;
    private String argument;
    private String optionalArgument;
    private String replacedArgument;
    private String replacedOptionalArgument;

    public String toString() {
        return String.format("%s [%s {%s} {%s}]", this.getClass().getSimpleName(), this.name, this.getRuntimeArgument(), this.getRuntimeOptionalArgument());
    }

    /**
     * Prepares the arguments. Some of the arguments are linked with variables, this method is used to replace these
     * with their actual values.
     * 
     * @param properties
     */
    public void prepare(ExecutionContext context) {
        replacedArgument = context.getValue(argument);
        replacedOptionalArgument = context.getValue(optionalArgument);
    }

    public String getName() {
        return name;
    }

    public Command setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns the original or the replaced argument. Replacements happen when the original argument is actually a
     * placeholder and gets filled in with actual value at runtime
     * 
     * @return
     */
    public String getRuntimeArgument() {
        return (replacedArgument == null) ? argument : replacedArgument;
    }

    public Command setArgument(String value) {
        this.argument = value;
        return this;
    }

    /**
     * Same as getRuntimeArgument except now it is OptionalArgument
     * 
     * @return
     */
    public String getRuntimeOptionalArgument() {
        return (replacedOptionalArgument == null) ? optionalArgument : replacedOptionalArgument;
    }

    public Command setOptionalArgument(String value) {
        this.optionalArgument = value;
        return this;
    }

    public Command setReplacedArgument(String value) {
        this.replacedArgument = value;
        return this;
    }

    public Command setReplacedOptionalArgument(String value) {
        this.replacedOptionalArgument = value;
        return this;
    }

    /**
     * True if the optional argument is optional, false otherwise.
     * 
     * @return
     */
    public boolean hasOptionalArg() {
        return Util.isNotBlank(getRuntimeOptionalArgument());
    }
}
