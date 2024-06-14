/*
 * Copyright 2022 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */
package org.cthing.checkstyle.checks;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;


/**
 * Tests the {@link LogDeclarationCheck} class.
 */
public class LogDeclarationCheckTest extends AbstractCheckTestSupport {

    @Test
    public void testLogDeclarationCheck() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(LogDeclarationCheck.class);

        final String[] expected = {
                "29: Logger variable must be declared private static final. [LogDeclaration]",
                "33: Declared type is not LoggerFactory, check that SLF4J is being used. [LogDeclaration]",
                "37: Logger must be assigned where declared. [LogDeclaration]",
                "41: Class name passed to getLogger method does not match enclosing class name. [LogDeclaration]",
                "45: Class name passed to getLogger method does not end in .class. [LogDeclaration]",
                "49: Logger variable must be named LOG. [LogDeclaration]",
                "53: getLogger method is not called, check that SLF4J is being used. [LogDeclaration]",
        };

        verify(checkConfig, getPath("LogDeclaration.java"), expected);
    }
}
