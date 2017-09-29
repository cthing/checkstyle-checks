/*
 * Copyright 2012 C Thing Software
 * All rights reserved.
 */
package org.cthing.checkstyle.checks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.AbstractViolationReporter;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.Configuration;

import static org.assertj.core.api.Assertions.*;


/**
 * Base class for custom Checkstyle check tests.
 */
public abstract class AbstractCheckTestSupport {

    private static final class Logger extends DefaultLogger {

        private Logger(final OutputStream out) throws UnsupportedEncodingException {
            super(out, OutputStreamOptions.CLOSE);
        }

        @Override
        public void auditStarted(final AuditEvent evt) {
        }

        @Override
        public void fileFinished(final AuditEvent evt) {
        }

        @Override
        public void fileStarted(final AuditEvent evt) {
        }
    }


    private ByteArrayOutputStream byteOutputStream;
    private PrintStream printStream;

    /**
     * Obtains a configuration for the specified check.
     *
     * @param clazz  Class object for the check
     * @return Configuration for the check
     */
    public static DefaultConfiguration createCheckConfig(final Class<? extends AbstractViolationReporter> clazz) {
        return new DefaultConfiguration(clazz.getName());
    }

    protected Checker createChecker(final Configuration checkConfig) throws Exception {
        this.byteOutputStream = new ByteArrayOutputStream();
        this.printStream = new PrintStream(this.byteOutputStream);

        final DefaultConfiguration checkerConfig = createCheckerConfig(checkConfig);
        final Checker checker = new Checker();

        // Ensure that the tests always run with English error messages
        // so the tests don't fail in supported locales like German
        final Locale locale = Locale.ENGLISH;
        checker.setLocaleCountry(locale.getCountry());
        checker.setLocaleLanguage(locale.getLanguage());
        checker.setModuleClassLoader(Thread.currentThread().getContextClassLoader());
        checker.configure(checkerConfig);
        checker.addListener(new Logger(this.printStream));

        return checker;
    }

    protected DefaultConfiguration createCheckerConfig(final Configuration config) {
        final DefaultConfiguration checkerConfig = new DefaultConfiguration("configuration");
        final DefaultConfiguration treeWalkerConfig = createCheckConfig(TreeWalker.class);

        // Ensure that the tests always run with this charset
        checkerConfig.addAttribute("charset", "iso-8859-1");
        checkerConfig.addChild(treeWalkerConfig);
        treeWalkerConfig.addChild(config);

        return checkerConfig;
    }

    /**
     * Obtains the pathname of the specified test resource.
     *
     * @param filename  Name of the test file.
     * @return Pathname of the specified test file.
     */
    protected static String getPath(final String filename) {
        return AbstractCheckTestSupport.class.getResource("/checkstyle/" + filename).getPath();
    }

    protected void verify(final Configuration config, final String fileName, final String[] expectedLines) throws Exception {
        verify(createChecker(config), fileName, fileName, expectedLines);
    }

    protected void verify(final Checker checker, final String processedFilename, final String messageFileName,
                          final String[] expectedLines) throws Exception {
        verify(checker, new File[] { new File(processedFilename) }, messageFileName, expectedLines);
    }

    protected void verify(final Checker checker, final File[] processedFiles, final String messageFileName,
                          final String[] expectedLines) throws Exception {
        this.printStream.flush();

        final List<File> theFiles = new ArrayList<>();
        Collections.addAll(theFiles, processedFiles);
        final int numErrs = checker.process(theFiles);

        // process each of the lines
        try (ByteArrayInputStream bais = new ByteArrayInputStream(this.byteOutputStream.toByteArray())) {
            try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(bais))) {
                if (expectedLines.length != numErrs) {
                    System.err.println("Expected errors: " + expectedLines.length);
                    for (final String line : expectedLines) {
                        System.err.println("    " + line);
                    }
                    System.err.println("Actual errors: " + numErrs);
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        System.err.println("    " + line);
                    }
                } else {
                    for (int i = 0; i < expectedLines.length; i++) {
                        final String expected = "[ERROR] " + messageFileName + ":" + expectedLines[i];
                        final String actual = reader.readLine();
                        assertThat(actual).as("error message " + i).isEqualTo(expected);
                    }
                }
            }
        }

        assertThat(numErrs).as("unexpected output").isEqualTo(expectedLines.length);

        checker.destroy();
    }
}
