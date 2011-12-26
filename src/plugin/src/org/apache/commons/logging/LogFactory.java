package org.apache.commons.logging;

public class LogFactory {
    public static Log getLog(Class<?> clazz) {
        return new DefaultLogger();
    }

    private static class DefaultLogger implements Log {
        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public boolean isErrorEnabled() {
            return true;
        }

        @Override
        public boolean isFatalEnabled() {
            return isErrorEnabled();
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public boolean isTraceEnabled() {
            return false;
        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public void trace(Object arg0) {
        }

        @Override
        public void trace(Object arg0, Throwable arg1) {
        }

        @Override
        public void debug(Object arg0) {
        }

        @Override
        public void debug(Object arg0, Throwable arg1) {
        }

        @Override
        public void info(Object arg0) {
        }

        @Override
        public void info(Object arg0, Throwable arg1) {
        }

        @Override
        public void warn(Object arg0) {
        }

        @Override
        public void warn(Object arg0, Throwable arg1) {
        }

        @Override
        public void error(Object arg0) {
            System.out.println(arg0);
        }

        @Override
        public void error(Object arg0, Throwable arg1) {
            System.out.println(arg0);
            arg1.printStackTrace();
        }
    }
}
