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
 * Tests the {@link SpringDaoAnnotationCheck} class.
 */
public class SpringDaoAnnotationCheckTest extends AbstractCheckTestSupport {

    @Test
    public void testGoodDaoAnnotations() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SpringDaoAnnotationCheck.class);

        verify(checkConfig, getPath("GoodDaoImpl.java"), new String[0]);
    }

    @Test
    public void testAbstractDaoAnnotations() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SpringDaoAnnotationCheck.class);

        verify(checkConfig, getPath("AbstractDaoImpl.java"), new String[0]);
    }

    @Test
    public void testDaoMissingAnnotations() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SpringDaoAnnotationCheck.class);

        verify(checkConfig, getPath("DaoMissingAnnotations.java"), new String[0]);
    }

    @Test
    public void testMissingDaoAnnotations() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SpringDaoAnnotationCheck.class);

        final String[] expected = {
                "47: Class must be annotated with @Repository. [SpringDaoAnnotation]",
                "47: Class must be annotated with @Transactional(readOnly = true). [SpringDaoAnnotation]",
                "122: Method must be annotated with @Transactional(readOnly = false). [SpringDaoAnnotation]",
                "147: Method must be annotated with @Transactional(readOnly = false). [SpringDaoAnnotation]",
        };

        verify(checkConfig, getPath("MissingAnnotationsDaoImpl.java"), expected);
    }

    @Test
    public void testIncorrectDaoAnnotations() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SpringDaoAnnotationCheck.class);

        final String[] expected = {
                "47: Class must be annotated with @Transactional(readOnly = true). [SpringDaoAnnotation]",
                "124: Method must be annotated with @Transactional(readOnly = false). [SpringDaoAnnotation]",
                "150: Method must be annotated with @Transactional(readOnly = false). [SpringDaoAnnotation]",
        };

        verify(checkConfig, getPath("IncorrectAnnotationsDaoImpl.java"), expected);
    }
}
