/*
 * Copyright 2012 C Thing Software
 * All rights reserved.
 */
package org.cthing.checkstyle.checks;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;


/**
 * Checks that the declaration of an SLF4J log object conforms to a standard format.
 * The declaration must have the format:
 * <pre>
 *      private static final Logger LOG = LoggerFactory.getLogger(MyClass.class);
 * </pre>
 *
 * Where "MyClass" must be replaced with the class enclosing the declaration. Annotations
 * are allowed on the declaration and will be ignored by this check.
 */
public class LogDeclarationCheck extends AbstractCheck {

    private static final Set<Integer> EXPECTED_MODIFIERS = new HashSet<>();
    static {
        Collections.addAll(EXPECTED_MODIFIERS, TokenTypes.LITERAL_PRIVATE, TokenTypes.LITERAL_STATIC, TokenTypes.FINAL);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.CLASS_DEF };
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(final DetailAST ast) {
        final DetailAST classIdent = ast.findFirstToken(TokenTypes.IDENT);
        if (classIdent != null) {
            final DetailAST classBlock = ast.findFirstToken(TokenTypes.OBJBLOCK);
            final DetailAST logDecl = findLogDecl(classBlock);
            if (logDecl != null) {
                final int lineNo = logDecl.getLineNo();

                final DetailAST logIdent = logDecl.findFirstToken(TokenTypes.IDENT);
                if (logIdent != null) {
                    if (!"LOG".equals(logIdent.getText())) {
                        log(lineNo, "logdeclaration.badname");
                    }
                }

                final Set<Integer> modifiers = ASTUtils.findDeclModifiers(logDecl);
                if (!modifiers.equals(EXPECTED_MODIFIERS)) {
                    log(lineNo, "logdeclaration.badmodifier");
                }

                final DetailAST assign = logDecl.findFirstToken(TokenTypes.ASSIGN);
                if (assign == null) {
                    log(lineNo, "logdeclaration.assignment");
                } else {
                    final DetailAST factory = ASTUtils.findText(assign, "LoggerFactory");
                    if (factory == null) {
                        log(lineNo, "logdeclaration.badtype");
                    }

                    final DetailAST method = ASTUtils.findText(assign, "getLogger");
                    if (method == null) {
                        log(lineNo, "logdeclaration.missingcall");
                    }

                    final DetailAST extIdent = ASTUtils.findType(assign, TokenTypes.LITERAL_CLASS);
                    if (extIdent == null) {
                        log(lineNo, "logdeclaration.missingclass");
                    } else {
                        final DetailAST nameIdent = extIdent.getPreviousSibling();
                        if (nameIdent == null) {
                            log(lineNo, "logdeclaration.missingclassname");
                        } else {
                            final String className = classIdent.getText();
                            if (!className.equals(nameIdent.getText())) {
                                log(lineNo, "logdeclaration.mismatchedclass");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Locate a log declaration, if any, amidst the class member variables.
     *
     * @param block  Code block containing all class level constructs
     * @return Log declaration node or {@code null} if a log declaration is not present.
     */
    private DetailAST findLogDecl(final DetailAST block) {
        for (DetailAST child = block.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getType() == TokenTypes.VARIABLE_DEF) {
                final DetailAST type = child.findFirstToken(TokenTypes.TYPE);
                if (type != null) {
                    final DetailAST typeIdent = type.findFirstToken(TokenTypes.IDENT);
                    if ((typeIdent != null) && "Logger".equals(typeIdent.getText())) {
                        return child;
                    }
                }
            }
        }
        return null;
    }
}
