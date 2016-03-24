/*
 * Copyright 2012 C Thing Software
 * All rights reserved.
 */
package org.cthing.checkstyle.checks;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;


/**
 * Tests the {@link TestMethodDeclarationCheck} class.
 */
public class TestMethodDeclarationCheckTest extends AbstractCheckTestSupport {

    @Test
    public void testMethodDeclarationCheck() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(TestMethodDeclarationCheck.class);

        final String[] expected = {
                "22: Test method must have void return type. [TestMethodDeclaration]",
                "27: Test method must be a public instance method. [TestMethodDeclaration]",
                "31: Test method must be a public instance method. [TestMethodDeclaration]",
                "35: Test method must be a public instance method. [TestMethodDeclaration]",
        };

        verify(checkConfig, getPath("TestMethodDeclaration.java"), expected);
    }
}
