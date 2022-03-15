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
