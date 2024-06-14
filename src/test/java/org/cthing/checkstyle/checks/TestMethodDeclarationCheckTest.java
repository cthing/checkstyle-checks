/*
 * Copyright 2022 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */
package org.cthing.checkstyle.checks;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;


/**
 * Tests the {@link TestMethodDeclarationCheck} class.
 */
public class TestMethodDeclarationCheckTest extends AbstractCheckTestSupport {

    @Test
    public void testMethodDeclarationCheck() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(TestMethodDeclarationCheck.class);

        final String[] expected = {
                "33: Test method must have void return type. [TestMethodDeclaration]",
                "38: Test method must be a public instance method. [TestMethodDeclaration]",
                "42: Test method must be a public instance method. [TestMethodDeclaration]",
                "46: Test method must be a public instance method. [TestMethodDeclaration]",
        };

        verify(checkConfig, getPath("TestMethodDeclaration.java"), expected);
    }
}
