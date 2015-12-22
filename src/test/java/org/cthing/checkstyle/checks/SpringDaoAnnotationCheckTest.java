/*
 * Copyright 2013 C Thing Software
 * All rights reserved.
 */
package org.cthing.checkstyle.checks;

import org.junit.Test;

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
                "36: error: Class must be annotated with @Repository.",
                "36: error: Class must be annotated with @Transactional(readOnly = true).",
                "111: error: Method must be annotated with @Transactional(readOnly = false).",
                "136: error: Method must be annotated with @Transactional(readOnly = false).",
        };

        verify(checkConfig, getPath("MissingAnnotationsDaoImpl.java"), expected);
    }

    @Test
    public void testIncorrectDaoAnnotations() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SpringDaoAnnotationCheck.class);

        final String[] expected = {
                "36: error: Class must be annotated with @Transactional(readOnly = true).",
                "113: error: Method must be annotated with @Transactional(readOnly = false).",
                "139: error: Method must be annotated with @Transactional(readOnly = false).",
        };

        verify(checkConfig, getPath("IncorrectAnnotationsDaoImpl.java"), expected);
    }
}
