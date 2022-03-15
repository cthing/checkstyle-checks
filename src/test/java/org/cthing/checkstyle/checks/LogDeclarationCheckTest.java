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
