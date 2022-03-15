/*
 * Copyright 2022 C Thing Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
