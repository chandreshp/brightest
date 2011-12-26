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
package com.imaginea.brightest.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {
    public static InputStream getInputStream(String fileName) throws IOException {
        return new FileInputStream(fileName);
    }

    public static InputStream getResourceAsStream(String resourceName) throws IOException {
        return Util.class.getClassLoader().getResourceAsStream(resourceName);
    }

    public static int copyStream(InputStream source, OutputStream destination) throws IOException {
        byte[] buffer = new byte[1024];
        int totalBytesRead = 0;
        int n = 0;
        while (-1 != (n = source.read(buffer))) {
            destination.write(buffer, 0, n);
            totalBytesRead += n;
        }
        destination.flush();
        destination.close();
        source.close();
        return totalBytesRead;
    }

    public static boolean isNotBlank(String value) {
        return (value != null && value.trim().length() > 0);
    }

    public static String getContents(String dslPath) throws IOException {
        OutputStream stream = new ByteArrayOutputStream();
        copyStream(getInputStream(dslPath), stream);
        return stream.toString();
    }

    /**
     * True if both are null or blank or equal
     * 
     * @param value1
     * @param value2
     * @return
     */
    public static boolean isEqual(String value1, String value2) {
        if (value1 == null) {
            return (value2 == null);
        } else if (value1.trim().length() == 0) {
            return (value2.trim().length() == 0);
        } else {
            return value2.equals(value1);
        }
    }
}
