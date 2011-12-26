/*
 * Copyright (c) 2008 Pramati Technologies Private Ltd. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Pramati
 * Technologies. You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the source code
 * license agreement you entered into with Pramati Technologies.
 *
 */
package com.imaginea.brightest.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ReplacableProperies - understands placeholders, so it is possible to write a property like
 * "The IQ of ReplaceableProperties is ${IQ}" where there is another property called IQ which may have a value of 80, so
 * when this property is actually retrieved it will appear as "The IQ of ReplaceableProperties is 80"
 */
public class ReplaceableProperties extends Properties {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("((\\$)(\\{)(.+)(\\}))");
    private static final long serialVersionUID = 8214090952591816219L;

    public ReplaceableProperties() {
        super();
    }

    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        super.load(inStream);
    }

    @Override
    public String getProperty(String dumbKey) {
        String value = super.getProperty(dumbKey);
        if (value != null) {
            StringBuffer buf = new StringBuffer();
            Matcher matcher = VARIABLE_PATTERN.matcher(value);
            while (matcher.find()) {
                String key = matcher.group();
                key = key.substring(2, key.length() - 1);
                if (super.getProperty(key) != null)
                    matcher.appendReplacement(buf, super.getProperty(key));
            }
            matcher.appendTail(buf);
            value = buf.toString();
        } else {
            Matcher matcher = VARIABLE_PATTERN.matcher(dumbKey);
            if (matcher.matches()) {
                String key = dumbKey.substring(2, dumbKey.length() - 1);
                value = super.getProperty(key);
            }
        }
        return value;
    }
}
