package com.imaginea.brightest.extension;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.execution.CommandInfo;

public class UserExtensionHandler {
    @CommandInfo
    public void clickForAjaxResponse(ExecutionContext context, Command command) {
        context.getSelenium().click(command.getRuntimeArgument());
    }

    @CommandInfo
    public void waitForAjaxResponse(ExecutionContext context, Command command) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
