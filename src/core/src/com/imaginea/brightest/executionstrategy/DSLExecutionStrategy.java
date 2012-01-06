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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.imaginea.brightest.ApplicationPreferences;
import com.imaginea.brightest.Command;
import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.execution.CommandExecutor;
import com.imaginea.brightest.util.Util;

/**
 * Execution strategy for DSL commands. DSL commands are composite commands stored in json format as a js file.
 * 
 * @author apurba
 */
public class DSLExecutionStrategy extends AbstractExecutionStrategy {
    private final Map<String, DSLCommand> dslCommands = new HashMap<String, DSLCommand>();
    private CommandExecutor commandExecutor = null;

    protected DSLExecutionStrategy() {
    }

    public DSLExecutionStrategy(ApplicationPreferences preferences, CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        changed(preferences);
    }

    @Override
    public void execute(Command command) {
        DSLCommand compositeCommand = dslCommands.get(command.getName());
        if (compositeCommand != null) {
            for (Command simpleCommand : compositeCommand.commands) {
                simpleCommand.prepare(ExecutionContext.getInstance());
                commandExecutor.execute(simpleCommand);
            }
        }
    }

    @Override
    public boolean appliesTo(Command command) {
        return dslCommands.containsKey(command.getName());
    }

    public void changed(ApplicationPreferences preferences) {
        if (preferences != null) {
            try {
                String dslPath = preferences.getDslPath();
                if (Util.isNotBlank(dslPath)) {
                    String dslContents = Util.getContents(dslPath);
                    if (Util.isNotBlank(dslContents)) {
                        parse(dslContents);
                    }
                }
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    /**
     * Parses string contents.
     * 
     * @param dslContents
     * @return
     */
    protected List<DSLCommand> parse(String dslContents) {
        try {
            JSONArray array = new JSONArray(dslContents);
            for (int i = 0; i < array.length(); i++) {
                DSLCommand dslCommand = new DSLCommand((JSONObject) array.get(i));
                dslCommands.put(dslCommand.name, dslCommand);
            }
            return new ArrayList<DSLCommand>(dslCommands.values());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    /**
     * CompositeCommand containing name and commands
     */
    protected class DSLCommand {
        private final String name;
        private final List<Command> commands = new ArrayList<Command>();

        private DSLCommand(JSONObject object) throws JSONException {
            this.name = object.getString("name");
            JSONArray commandArray = (JSONArray) object.get("commands");
            for (int j = 0; j < commandArray.length(); j++) {
                JSONCommand jsonCommand = new JSONCommand((JSONObject) commandArray.get(j));
                add(jsonCommand.getCommand());
            }
        }

        private void add(Command command) {
            commands.add(command);
        }

        protected String getName() {
            return name;
        }

        protected List<Command> getCommands() {
            return commands;
        }
    }

    protected class JSONCommand {
        private final JSONObject map;

        private JSONCommand(JSONObject map) {
            this.map = map;
        }

        private Command getCommand() throws JSONException {
            Command command = new Command();
            command.setName(map.getString("command")).setArgument(map.getString("target")).setOptionalArgument(map.getString("value"));
            return command;
        }
    }
}
