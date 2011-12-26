package com.pramati.brightest.neo;

import org.junit.Assert;
import org.junit.Test;

public class ExcelWriterTest {
    @Test
    public void formatRawCommand() {
        ExcelWriter testWriter = new ExcelWriter("somedummyfile");
        String formattedCommand = testWriter.formatRawCommand("login~~");
        Assert.assertEquals("login()", formattedCommand);
    }

    @Test(expected = IllegalArgumentException.class)
    public void formatIllegalCommand() {
        ExcelWriter testWriter = new ExcelWriter("somedummyfile");
        testWriter.formatRawCommand("login~a~b~c");
    }
}
