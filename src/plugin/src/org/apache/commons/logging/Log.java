package org.apache.commons.logging;

public interface Log {
    // Method descriptor #4 ()Z
    public abstract boolean isDebugEnabled();

    // Method descriptor #4 ()Z
    public abstract boolean isErrorEnabled();

    // Method descriptor #4 ()Z
    public abstract boolean isFatalEnabled();

    // Method descriptor #4 ()Z
    public abstract boolean isInfoEnabled();

    // Method descriptor #4 ()Z
    public abstract boolean isTraceEnabled();

    // Method descriptor #4 ()Z
    public abstract boolean isWarnEnabled();

    // Method descriptor #11 (Ljava/lang/Object;)V
    public abstract void trace(java.lang.Object arg0);

    // Method descriptor #12 (Ljava/lang/Object;Ljava/lang/Throwable;)V
    public abstract void trace(java.lang.Object arg0, java.lang.Throwable arg1);

    // Method descriptor #11 (Ljava/lang/Object;)V
    public abstract void debug(java.lang.Object arg0);

    // Method descriptor #12 (Ljava/lang/Object;Ljava/lang/Throwable;)V
    public abstract void debug(java.lang.Object arg0, java.lang.Throwable arg1);

    // Method descriptor #11 (Ljava/lang/Object;)V
    public abstract void info(java.lang.Object arg0);

    // Method descriptor #12 (Ljava/lang/Object;Ljava/lang/Throwable;)V
    public abstract void info(java.lang.Object arg0, java.lang.Throwable arg1);

    // Method descriptor #11 (Ljava/lang/Object;)V
    public abstract void warn(java.lang.Object arg0);

    // Method descriptor #12 (Ljava/lang/Object;Ljava/lang/Throwable;)V
    public abstract void warn(java.lang.Object arg0, java.lang.Throwable arg1);

    // Method descriptor #11 (Ljava/lang/Object;)V
    public abstract void error(java.lang.Object arg0);

    // Method descriptor #12 (Ljava/lang/Object;Ljava/lang/Throwable;)V
    public abstract void error(java.lang.Object arg0, java.lang.Throwable arg1);
}
