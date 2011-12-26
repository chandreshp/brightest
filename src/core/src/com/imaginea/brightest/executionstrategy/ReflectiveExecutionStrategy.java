package com.imaginea.brightest.executionstrategy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.execution.CommandFailureException;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Execution strategy that delegates to selenium for actual work.
 */
public class ReflectiveExecutionStrategy extends AbstractExecutionStrategy {
    private static final Log LOG = LogFactory.getLog(ReflectiveExecutionStrategy.class);

    @Override
    public void execute(Command command) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing " + command);
        }
        Class<?>[] parameterTypes = new Class[] { String.class };
        if (command.hasOptionalArg()) {
            parameterTypes = new Class[] { String.class, String.class };
        }
        try {
            // TODO could use a cache
            Method method = getSelenium().getClass().getMethod(command.getName(), parameterTypes);
            if (command.hasOptionalArg()) {
                method.invoke(getSelenium(), command.getRuntimeArgument(), command.getRuntimeOptionalArgument());
            } else {
                method.invoke(getSelenium(), command.getRuntimeArgument());
            }
        } catch (NoSuchMethodException exc) {
            StringBuilder errorMsgBuilder = new StringBuilder("Unknown command ").append(command);
            String errorMsg = errorMsgBuilder.toString();
            LOG.error(errorMsg, exc);
            throw new CommandFailureException(errorMsg, exc);
        } catch (Exception exc) {
            StringBuilder errorMsgBuilder = new StringBuilder("Problems in executing command ").append(command);
            if (exc instanceof InvocationTargetException) {
                InvocationTargetException invocationTargetException = (InvocationTargetException) exc;
                Throwable targetException = invocationTargetException.getTargetException();
                if (targetException instanceof SeleniumException) {
                    SeleniumException seleniumException = (SeleniumException) targetException;
                    errorMsgBuilder.append("\nDetailMessage: ").append(seleniumException.getMessage());
                }
            }
            String errorMsg = errorMsgBuilder.toString();
            LOG.error(errorMsg, exc);
            throw new CommandFailureException(errorMsg, exc);
        }
    }

    /**
     * Last line of defence, so tries to run everything
     */
    @Override
    public boolean appliesTo(Command command) {
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    /**
     * Retrieves selenium from execution context. Does not keep a local copy.
     * 
     * @return
     */
    private Selenium getSelenium() {
        return ExecutionContext.getInstance().getSelenium();
    }
}
