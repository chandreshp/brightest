package com.imaginea.brightest.execution;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.ExecutionContext;
import com.thoughtworks.selenium.Selenium;

/**
 * Command Handler which addresses some of the gaps between the js and the java driver. Adds the methods available by
 * default in js part to java driver.
 * 
 * @author apurba
 */
public class BrightestCommandHandler {
    @CommandInfo
    public boolean assertValue(ExecutionContext context, Command command) {
        return context.getSelenium().isElementPresent(command.getRuntimeArgument());
    }

    @CommandInfo
    public boolean assertTextValue(ExecutionContext context, Command command) {
        String textValue = context.getSelenium().getText(command.getRuntimeArgument());
        return command.getRuntimeOptionalArgument().equals(textValue);
    }

    @CommandInfo
    public boolean assertTitle(ExecutionContext context, Command command) {
        // In linux with firefox or konqueror selenium.getTitle() is getting modified title text with
        // addition of '\n' and spaces in between. e.g Pramati Tech\n\t|BrighTest comes as this instead of Pramati Tech|
        // BrighTest so we need to remove the \n\t, to add to the fun the \t is converted to spaces by selenium.
        String[] subStrs = context.getSelenium().getTitle().split("\n");
        String title = "";
        for (int i = 0; i < subStrs.length; i++) {
            // first trim the string
            subStrs[i] = subStrs[i].trim();
            // in case of other than the first substring, add a leading space
            if (i != 0) {
                subStrs[i] = " " + subStrs[i];
            }
            title += subStrs[i];
        }
        return title.equals(command.getRuntimeArgument());
    }

    @CommandInfo
    public void clickAndWait(ExecutionContext context, Command command) {
        String timeout = context.getPreferences().getTimeout();
        Selenium selenium = context.getSelenium();
        selenium.click(command.getRuntimeArgument());
        selenium.waitForPageToLoad(timeout);
    }

    @CommandInfo
    public void sleep(ExecutionContext context, Command command) {
        String waitTime = command.getRuntimeArgument();
        long timeToSleep = Long.parseLong(waitTime);
        if (timeToSleep < 0L || timeToSleep > 10000L) {
            System.out.println("Pause was " + waitTime + " which is not allowed.");
        }
        long now = System.currentTimeMillis();
        long then = now + timeToSleep;
        while (System.currentTimeMillis() < then) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Overrides the actual store, we convert store from a js command to a java command
     * 
     * @param context
     * @param command
     */
    @CommandInfo(order = 1)
    public void store(ExecutionContext context, Command command) {
        context.putValue(command.getRuntimeArgument(), command.getRuntimeOptionalArgument());
    }

    @CommandInfo
    public void waitForTextPresent(ExecutionContext context, Command command) {
        context.getSelenium().waitForCondition("selenium.isTextPresent('" + command.getRuntimeArgument() + "')", context.getPreferences().getTimeout());
    }

    @CommandInfo
    public void waitForElementPresent(ExecutionContext context, Command command) {
        context.getSelenium().waitForCondition("selenium.isElementPresent('" + command.getRuntimeArgument() + "')", context.getPreferences().getTimeout());
    }
}
