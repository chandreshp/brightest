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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A group of properties which honors a hierarchy, starting with local moving on to the contained properties. For
 * example if we load(a,b,c) get property will look for the property in a, if it fails then will look into b and then c.
 */
public class HierarchicalProperties extends ReplaceableProperties {
    private static final Log LOG = LogFactory.getLog(HierarchicalProperties.class);
    private static final long serialVersionUID = 7305798841488095031L;
    private Properties[] propertiesGroup = null;

    /**
     * Loads all the property files in the given order
     * 
     * @param propertyFiles
     * @throws IOException
     */
    public synchronized HierarchicalProperties addProperties(String... propertyFiles) throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Adding properties " + Arrays.asList(propertyFiles));
        }
        ReplaceableProperties[] newProperties = new ReplaceableProperties[propertyFiles.length];
        for (int i = 0; i < propertyFiles.length; i++) {
            newProperties[i] = new ReplaceableProperties();
            try {
                if (propertyFiles[i] != null) {
                    newProperties[i].load(Util.getInputStream(propertyFiles[i]));
                }
            } catch (FileNotFoundException fexc) {
                LOG.error("Problems in loading the properties file with name " + propertyFiles[i]);
                // print the stack trace only if we are in debug mode, as this would be pretty common
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Stack trace", fexc);
                }
            }
        }
        if (propertiesGroup == null) {
            propertiesGroup = newProperties;
        } else {
            int oldLength = propertiesGroup.length;
            Properties[] newCopy = new Properties[oldLength + newProperties.length];
            System.arraycopy(propertiesGroup, 0, newCopy, 0, oldLength);
            System.arraycopy(newProperties, 0, newCopy, oldLength, newProperties.length);
            propertiesGroup = newCopy;
        }
        return this;
    }

    /**
     * Resolves a property using the hierarchy
     */
    @Override
    public String getProperty(String key) {
        String value = super.getProperty(key);
        if (value == null && propertiesGroup != null) {
            for (Properties properties : propertiesGroup) {
                value = properties.getProperty(key);
                if (value != null) {
                    break;
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Resolved key [" + key + "] to value [" + value + "]");
        }
        return value;
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value == null) ? defaultValue : value;
    }

    /**
     * Not thread safe. Gets an set of all property names. The super impl allows any class type to be put, we want only
     * strings so returning a set of Strings
     * 
     * @return set of all keys in this properties object
     */
    public Set<String> propertyNameSet() {
        Set<String> propertyKeys = new HashSet<String>();
        castAndAdd(super.keySet(), propertyKeys);
        if (propertiesGroup != null) {
            for (Properties properties : propertiesGroup) {
                if (properties != null) {
                    castAndAdd(properties.keySet(), propertyKeys);
                }
            }
        }
        return propertyKeys;
    }

    /**
     * Adding a restriction for keys must be of type String
     */
    @Override
    public Object put(Object key, Object value) {
        if (key instanceof String) {
            return super.put(key, value);
        } else {
            throw new IllegalArgumentException("We only allow String keys");
        }
    }

    private void castAndAdd(Set<Object> source, Set<String> destination) {
        for (Object key : source) {
            destination.add((String) key);
        }
    }
}
