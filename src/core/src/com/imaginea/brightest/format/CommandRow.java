package com.imaginea.brightest.format;

import com.imaginea.brightest.Command;

/**
 * Row abstraction for commands for reading and writing
 */
public abstract class CommandRow {
    protected static enum LegacyRowFormat {
        PURPOSE, STEPS;
    }

    protected static enum CommandRowFormat {
        STEP, ARGUMENT, OPTIONAL_ARG;
    }

    public CommandRow() {
    }

    public abstract Command getCommand();

    protected Command parse(String commandString) {
        commandString = commandString.trim();
        String[] tokens = parseString(commandString);
        Command command = new Command();
        command.setName(tokens[0]).setArgument(tokens[1]).setOptionalArgument(tokens[2]);
        return command;
    }

    private String[] parseString(String command) {
        command = command.trim();
        String[] vals = new String[3];
        int cmdNameEnd = command.indexOf('(');
        vals[0] = command.substring(0, cmdNameEnd);
        command = command.substring(cmdNameEnd + 1);
        command = command.substring(0, command.length() - 1);
        int argEnd = -1;
        if ((argEnd = command.lastIndexOf('|')) != -1) {
        } else {
            argEnd = command.lastIndexOf(',');
        }
        if (argEnd != -1) {
            vals[1] = command.substring(0, argEnd).trim();
            vals[2] = command.substring(argEnd + 1).trim();
        } else {
            vals[1] = command.substring(0);
            if (vals[1].trim().length() == 0) {
                vals[1] = null;
            }
        }
        return vals;
    }
}
