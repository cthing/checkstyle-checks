/*
 * Copyright 2012 C Thing Software
 * All rights reserved.
 */
package org.cthing.checkstyle.checks;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;


/**
 * Tests the {@link LogDeclarationCheck} class.
 */
public class LogDeclarationCheckTest extends AbstractCheckTestSupport {

    @Test
    public void testLogDeclarationCheck() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(LogDeclarationCheck.class);

        final String[] expected = {
                "18: Logger variable must be declared private static final. [LogDeclaration]",
                "22: Declared type is not LoggerFactory, check that SLF4J is being used. [LogDeclaration]",
                "26: Logger must be assigned where declared. [LogDeclaration]",
                "30: Class name passed to getLogger method does not match enclosing class name. [LogDeclaration]",
                "34: Class name passed to getLogger method does not end in .class. [LogDeclaration]",
                "38: Logger variable must be named LOG. [LogDeclaration]",
                "42: getLogger method is not called, check that SLF4J is being used. [LogDeclaration]",
        };

        verify(checkConfig, getPath("LogDeclaration.java"), expected);
    }
}
