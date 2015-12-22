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
                "18: error: Logger variable must be declared private static final.",
                "22: error: Declared type is not LoggerFactory, check that SLF4J is being used.",
                "26: error: Logger must be assigned where declared.",
                "30: error: Class name passed to getLogger method does not match enclosing class name.",
                "34: error: Class name passed to getLogger method does not end in .class.",
                "38: error: Logger variable must be named LOG.",
                "42: error: getLogger method is not called, check that SLF4J is being used.",
        };

        verify(checkConfig, getPath("LogDeclaration.java"), expected);
    }
}
