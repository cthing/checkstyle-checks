/*
 * Copyright 2022 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */
package org.cthing.checkstyle.checks;

import java.util.Objects;
import java.util.stream.Stream;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;


/**
 * Checks the declaration of a unit test method annotated with {@literal @}Test.
 * The method must be declared {@code public} and have a {@code void} return type.
 */
public class TestMethodDeclarationCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.METHOD_DEF };
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public void visitToken(final DetailAST methodDef) {
        final DetailAST modifiers = methodDef.findFirstToken(TokenTypes.MODIFIERS);

        if (hasTestAnnotation(modifiers)) {
            final int lineNo = methodDef.getLineNo();

            final DetailAST returnType = methodDef.findFirstToken(TokenTypes.TYPE);
            if ((returnType != null) && (returnType.findFirstToken(TokenTypes.LITERAL_VOID) == null)) {
                log(lineNo, "testmethoddeclaration.badreturn");
            }

            if (!isPurePublic(modifiers)) {
                log(lineNo, "testmethoddeclaration.badscope");
            }
        }
    }

    private boolean hasTestAnnotation(final DetailAST modifiers) {
        if (modifiers != null) {
            return Stream.iterate(modifiers.getFirstChild(), Objects::nonNull, DetailAST::getNextSibling)
                         .filter(modifier -> modifier.getType() == TokenTypes.ANNOTATION)
                         .map(modifier -> modifier.findFirstToken(TokenTypes.IDENT))
                         .anyMatch(annotIdent -> (annotIdent != null) && "Test".equals(annotIdent.getText()));
        }
        return false;
    }

    private boolean isPurePublic(final DetailAST modifiers) {
        boolean havePublic = false;

        for (DetailAST modifier = modifiers.getFirstChild(); modifier != null; modifier = modifier.getNextSibling()) {
            final int type = modifier.getType();
            if (type == TokenTypes.LITERAL_PUBLIC) {
                havePublic = true;
            } else if (type != TokenTypes.ANNOTATION) {
                return false;
            }
        }
        return havePublic;
    }
}
