/*
 * Copyright 2013 C Thing Software
 * All rights reserved.
 */
package org.cthing.checkstyle.checks;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.beanutils.ConversionException;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;


/**
 * Checks that a DAO implementation class for use with the Spring framework contains the
 * appropriate annotations. Public classes ending with {@code DaoImpl} are considered DAO implementation
 * classes. The expected annotations are:
 * <ul>
 *      <li>Classes must be marked {@literal @}Repository</li>
 *      <li>Classes must be marked {@literal @}Transactional(readOnly = true)</li>
 *      <li>All public {@code insert*} methods must be marked {@literal @}Transactional(readOnly = false)</li>
 *      <li>All public {@code update*} methods must be marked {@literal @}Transactional(readOnly = false)</li>
 *      <li>All public {@code delete*} methods must be marked {@literal @}Transactional(readOnly = false)</li>
 * </ul>
 */
public class SpringDaoAnnotationCheck extends AbstractCheck {

    private Pattern includePattern = Pattern.compile("^.*DaoImpl$");

    private Pattern excludePattern = Pattern.compile("^Abstract.+$");


    /**
     * Regular expression for the DAO class names to check. Class names included by this pattern
     * can be filtered by the {@link #excludePattern}.
     *
     * @return Regular expression for the inclusion
     */
    public Pattern getIncludePattern() {
        return this.includePattern;
    }

    /**
     * Regular expression for the DAO class names to check. Class names included by this pattern
     * can be filtered by the {@link #excludePattern}.
     *
     * @param incPattern  Regular expression for the inclusion
     */
    public void setIncludePattern(final String incPattern) {
        try {
            this.includePattern = CommonUtil.createPattern(incPattern);
        } catch (final PatternSyntaxException ex) {
            throw new ConversionException("unable to parse " + incPattern, ex);
        }
    }

    /**
     * Regular expression for the DAO class names to exclude from checking. This pattern is applied
     * to the classes included by the {@link #includePattern}.
     *
     * @return Regular expression for the exclusion
     */
    public Pattern getExcludePattern() {
        return this.excludePattern;
    }

    /**
     * Regular expression for the DAO class names to exclude from checking. This pattern is applied
     * to the classes included by the {@link #includePattern}.
     *
     * @param exclPattern  Regular expression for the exclusion.
     */
    public void setExcludePattern(final String exclPattern) {
        try {
            this.excludePattern = CommonUtil.createPattern(exclPattern);
        } catch (final PatternSyntaxException ex) {
            throw new ConversionException("unable to parse " + exclPattern, ex);
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.CLASS_DEF, TokenTypes.METHOD_DEF };
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
    public void visitToken(final DetailAST ast) {
        if (ast.getType() == TokenTypes.CLASS_DEF) {
            checkClass(ast);
        } else {
            checkMethod(ast);
        }
    }

    private void checkClass(final DetailAST ast) {
        if (isNotPublic(ast) || isNotIncluded(ASTUtils.getIdent(ast))) {
            return;
        }

        boolean isTransactional = false;
        boolean isRepository = false;

        final DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers != null) {
            for (DetailAST modifier = modifiers.getFirstChild(); modifier != null; modifier = modifier.getNextSibling()) {
                if (modifier.getType() == TokenTypes.ANNOTATION) {
                    final String annotName = ASTUtils.getIdent(modifier);
                    if ("Repository".equals(annotName)) {
                        isRepository = true;
                    } else if ("Transactional".equals(annotName)) {
                        for (DetailAST annotMember = modifier.getFirstChild(); annotMember != null; annotMember = annotMember.getNextSibling()) {
                            if (annotMember.getType() == TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR) {
                                if ("readOnly".equals(ASTUtils.getIdent(annotMember))) {
                                    isTransactional = (ASTUtils.findType(annotMember, TokenTypes.LITERAL_TRUE) != null);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!isRepository) {
            log(ast.getLineNo(), "springdaoannotation.missingclassrepository");
        }
        if (!isTransactional) {
            log(ast.getLineNo(), "springdaoannotation.missingclasstransactional");
        }
    }

    private void checkMethod(final DetailAST ast) {
        if (isNotPublic(ast)) {
            return;
        }

        final DetailAST classDef = ASTUtils.findEnclosingClass(ast);
        if ((classDef == null) || isNotPublic(classDef) || isNotIncluded(ASTUtils.getIdent(classDef))) {
            return;
        }

        final String methodName = ASTUtils.getIdent(ast);
        if (!methodName.startsWith("insert") && !methodName.startsWith("update") && !methodName.startsWith("delete")) {
            return;
        }

        boolean isTransactional = false;

        final DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers != null) {
            for (DetailAST modifier = modifiers.getFirstChild(); modifier != null; modifier = modifier.getNextSibling()) {
                if (modifier.getType() == TokenTypes.ANNOTATION) {
                    final String annotName = ASTUtils.getIdent(modifier);
                    if ("Transactional".equals(annotName)) {
                        for (DetailAST annotMember = modifier.getFirstChild(); annotMember != null; annotMember = annotMember.getNextSibling()) {
                            if (annotMember.getType() == TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR) {
                                if ("readOnly".equals(ASTUtils.getIdent(annotMember))) {
                                    isTransactional = (ASTUtils.findType(annotMember, TokenTypes.LITERAL_FALSE) != null);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!isTransactional) {
            log(ast.getLineNo(), "springdaoannotation.missingmethodtransactional");
        }
    }

    private boolean isNotPublic(final DetailAST decl) {
        return !ASTUtils.findDeclModifiers(decl).contains(TokenTypes.LITERAL_PUBLIC);
    }

    private boolean isNotIncluded(final String className) {
        return (this.includePattern != null) && !this.includePattern.matcher(className).matches()
                || (this.excludePattern != null) && this.excludePattern.matcher(className).matches();
    }
}
