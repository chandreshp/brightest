package com.imaginea.brightest.extension;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.execution.CommandInfo;

/**
 * Handler for user extensions. This is a sample file with just one command
 */
public class UserExtensionHandler {
    @CommandInfo
    public void clickAndWaitForAjaxResponse(ExecutionContext context, Command command) {
        context.getSelenium().click(command.getRuntimeArgument());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
