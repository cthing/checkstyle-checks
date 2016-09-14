/*
 * Copyright 2013 C Thing Software
 * All rights reserved.
 */
package org.cthing.checkstyle.checks;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import static org.assertj.core.api.Assertions.*;


/**
 * Tests the {@link ASTUtils} utility class.
 */
public class ASTUtilsTest extends AbstractCheckTestSupport {

    public static class TestCheck extends AbstractCheck {

        public static DetailAST ast;

        @Override
        public int[] getDefaultTokens() {
            return new int[] { TokenTypes.CLASS_DEF };
        }

        @Override
        @SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
        public void visitToken(final DetailAST astDetails) {
            ast = astDetails;
        }
    }


    @Test
    @SuppressWarnings("ConstantConditions")
    public void testFinds() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(TestCheck.class);
        final Checker checker = createChecker(checkConfig);

        final List<File> theFiles = new ArrayList<>();
        Collections.addAll(theFiles, new File(getPath("ClassDeclaration.java")));
        checker.process(theFiles);

        assertThat(ASTUtils.findDeclModifiers(TestCheck.ast)).contains(TokenTypes.LITERAL_PUBLIC);
        final DetailAST variableDef = ASTUtils.findType(TestCheck.ast, TokenTypes.VARIABLE_DEF);
        assertThat(variableDef).isNotNull();
        assertThat(ASTUtils.getIdent(variableDef)).isEqualTo("HELLO");
        assertThat(ASTUtils.findText(variableDef, "HELLO")).isNotNull();

        final DetailAST classDef = ASTUtils.findEnclosingClass(variableDef);
        assertThat(classDef.getType()).isEqualTo(TokenTypes.CLASS_DEF);

        checker.destroy();
    }
}
