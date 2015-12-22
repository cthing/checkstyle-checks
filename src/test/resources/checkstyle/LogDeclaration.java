/*
 * Copyright 2012 C Thing Software
 * All rights reserved.
 */
package checkstyle;


public class LogDeclaration {
    private static final Logger LOG = LoggerFactory.getLogger(LogDeclaration.class);
}

class AnnotatedLogDeclaration {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedLogDeclaration.class);
}

class BadModifiers {
    private Logger LOG = LoggerFactory.getLogger(BadModifiers.class);
}

class BadFactory {
    private static final Logger LOG = Logger.getLogger(BadFactory.class);
}

class MissingAssign {
    private static final Logger LOG;
}

class WrongClass {
    private static final Logger LOG = LoggerFactory.getLogger(LogDeclaration.class);
}

class MissingClass {
    private static final Logger LOG = LoggerFactory.getLogger(MissingClass.getClass());
}

class BadVariableName {
    private static final Logger log = LoggerFactory.getLogger(BadVariableName.class);
}

class SyntaxError1 {
    private static final Logger LOG = LoggerFactory(SyntaxError1.class);
}
