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
                "36: Class must be annotated with @Repository. [SpringDaoAnnotation]",
                "36: Class must be annotated with @Transactional(readOnly = true). [SpringDaoAnnotation]",
                "111: Method must be annotated with @Transactional(readOnly = false). [SpringDaoAnnotation]",
                "136: Method must be annotated with @Transactional(readOnly = false). [SpringDaoAnnotation]",
        };

        verify(checkConfig, getPath("MissingAnnotationsDaoImpl.java"), expected);
    }

    @Test
    public void testIncorrectDaoAnnotations() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SpringDaoAnnotationCheck.class);

        final String[] expected = {
                "36: Class must be annotated with @Transactional(readOnly = true). [SpringDaoAnnotation]",
                "113: Method must be annotated with @Transactional(readOnly = false). [SpringDaoAnnotation]",
                "139: Method must be annotated with @Transactional(readOnly = false). [SpringDaoAnnotation]",
        };

        verify(checkConfig, getPath("IncorrectAnnotationsDaoImpl.java"), expected);
    }
}
