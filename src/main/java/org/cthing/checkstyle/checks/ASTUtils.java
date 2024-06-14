/*
 * Copyright 2022 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */
package org.cthing.checkstyle.checks;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;


/**
 * Utility methods for working with the AST.
 */
public final class ASTUtils {

    private ASTUtils() {
    }

    /**
     * Obtains the set of modifiers on the specified declaration.
     *
     * @param decl  Declaration whose modifiers are desired
     * @return The set of modifiers on the specified declaration.
     */
    public static Set<Integer> findDeclModifiers(final DetailAST decl) {
        final Set<Integer> modifierTypes = new HashSet<>();
        final DetailAST modifiers = decl.findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers != null) {
            for (DetailAST modifier = modifiers.getFirstChild(); modifier != null; modifier = modifier.getNextSibling()) {
                if (modifier.getType() != TokenTypes.ANNOTATION) {
                    modifierTypes.add(modifier.getType());
                }
            }
        }
        return modifierTypes;
    }

    /**
     * Recursively searches for a node of the specified type under the specified node.
     *
     * @param node  Node under which to search for the specified type
     * @param type  Type of node to find
     * @return Node of the specified type or {@code null} if not found.
     */
    public static DetailAST findType(final DetailAST node, final int type) {
        if (node.getType() == type) {
            return node;
        }
        return Stream.iterate(node.getFirstChild(), Objects::nonNull, DetailAST::getNextSibling)
                     .map(child -> findType(child, type))
                     .filter(Objects::nonNull)
                     .findFirst()
                     .orElse(null);
    }

    /**
     * Recursively searches for a node containing the specified text. Comparison is case-sensitive.
     *
     * @param node  Starting node for the text search
     * @param text  Text to search for
     * @return Node containing the specified text or {@code null} if not found
     */
    public static DetailAST findText(final DetailAST node, final String text) {
        if (text.equals(node.getText())) {
            return node;
        }
        return Stream.iterate(node.getFirstChild(), Objects::nonNull, DetailAST::getNextSibling)
                     .map(child -> findText(child, text))
                     .filter(Objects::nonNull)
                     .findFirst()
                     .orElse(null);
    }

    /**
     * Obtains the identifier corresponding to the specified AST node.
     *
     * @param node  AST node whose identifier is desired
     * @return Identifier corresponding to the specified node or the empty string
     *      if the identifier could not be found.
     */
    public static String getIdent(final DetailAST node) {
        final DetailAST ident = node.findFirstToken(TokenTypes.IDENT);
        return (ident == null) ? "" : ident.getText();
    }

    /**
     * Attempts to find the AST node representing the class that encloses the
     * specified node.
     *
     * @param node  AST node whose enclosing class is desired
     * @return AST node of the enclosing class or {@code null} if the enclosing
     *      class cannot be determined.
     */
    public static DetailAST findEnclosingClass(final DetailAST node) {
        if ((node == null) || (node.getType() == TokenTypes.CLASS_DEF)) {
            return node;
        }
        return findEnclosingClass(node.getParent());
    }
}
