/*
 * Copyright 2022 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
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
