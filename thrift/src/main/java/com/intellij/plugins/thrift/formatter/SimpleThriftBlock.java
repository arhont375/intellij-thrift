package com.intellij.plugins.thrift.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.WrapType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.intellij.plugins.thrift.lang.lexer.ThriftTokenTypes.*;

public class SimpleThriftBlock extends AbstractBlock {
  private static final TokenSet CURLY_CONTAINER_BLOCK = TokenSet.create(ENUM, SENUM, STRUCT, UNION, EXCEPTION, SERVICE, XSD_ATTRS, CONST_MAP);
  // Some rules also expose dedicated body nodes.
  private static final TokenSet CURLY_BODY_BLOCK = TokenSet.create(FIELDS, SERVICE_BODY, ENUM_FIELDS, SENUM_BODY);
  private static final TokenSet CURLY_BLOCK = TokenSet.orSet(CURLY_CONTAINER_BLOCK, CURLY_BODY_BLOCK);
  private static final TokenSet THROWS_PAREN_BLOCK = TokenSet.create(THROWS);
  private static final TokenSet ANNOTATION_PAREN_BLOCK = TokenSet.create(TYPE_ANNOTATION_LIST);
  private static final TokenSet FUNCTION_PAREN_BLOCK = TokenSet.create(FUNCTION);
  private static final TokenSet BRACKET_BLOCK = TokenSet.create(CONST_LIST);
  private static final TokenSet BLOCK_BODY = TokenSet.orSet(CURLY_BLOCK, THROWS_PAREN_BLOCK, ANNOTATION_PAREN_BLOCK, FUNCTION_PAREN_BLOCK, BRACKET_BLOCK);

  private static final Spacing LINE_BREAK = Spacing.createSpacing(0, 0, 1, true, 0);
  private static final Spacing ONE_SPACE = Spacing.createSpacing(1, 1, 0, true, 0);

  private static int countDirectChildrenOfType(@NotNull ASTNode node, @NotNull IElementType type) {
    int count = 0;
    for (ASTNode child = node.getFirstChildNode(); child != null; child = child.getTreeNext()) {
      if (child.getElementType() == type) {
        count++;
      }
    }
    return count;
  }

  private static int countTypeAnnotations(@NotNull ASTNode typeAnnotationsNode) {
    for (ASTNode child = typeAnnotationsNode.getFirstChildNode(); child != null; child = child.getTreeNext()) {
      if (child.getElementType() == TYPE_ANNOTATION_LIST) {
        return countDirectChildrenOfType(child, TYPE_ANNOTATION);
      }
    }
    return 0;
  }

  /**
   * Wrap for function arguments. Uses chop-down so that long arg lists become one-arg-per-line.
   */
  private Wrap myFunctionArgsWrap;

  /**
   * Wrap for trailing signature parts (`throws`, trailing type annotations). Kept separate from
   * args so that when args are multi-line, we don't force `(deprecated)` onto its own line.
   */
  private Wrap myFunctionTailWrap;

  private final SpacingBuilder mySpacingBuilder;

  public SimpleThriftBlock(@NotNull ASTNode node,
                           @Nullable Wrap wrap,
                           @Nullable Alignment alignment,
                           SpacingBuilder spacingBuilder) {
    super(node, wrap, alignment);
    mySpacingBuilder = spacingBuilder;
  }

  private static boolean isEmpty(ASTNode node) {
    return node.getElementType() == TokenType.WHITE_SPACE || node.getTextLength() == 0;
  }

  private @NotNull Wrap getFunctionArgsWrap() {
    if (myFunctionArgsWrap == null) {
      myFunctionArgsWrap = Wrap.createWrap(WrapType.CHOP_DOWN_IF_LONG, false);
    }
    return myFunctionArgsWrap;
  }

  private @NotNull Wrap getFunctionTailWrap() {
    if (myFunctionTailWrap == null) {
      myFunctionTailWrap = Wrap.createWrap(WrapType.NORMAL, false);
    }
    return myFunctionTailWrap;
  }

  @Override
  protected List<Block> buildChildren() {
    if (isLeaf()) {
      return Collections.emptyList();
    }
    List<Block> blocks = new ArrayList<>();
    ASTNode[] children = getNode().getChildren(TokenSet.ANY);
    for (ASTNode child : children) {
      if (isEmpty(child)) {
        continue;
      }

      Wrap wrap = getWrapForChild(child);
      blocks.add(new SimpleThriftBlock(child, wrap, null, mySpacingBuilder));
    }
    return blocks;
  }

  private boolean isFunctionArg(@NotNull ASTNode node) {
    if (myNode.getElementType() != FUNCTION || node.getElementType() != FIELD) {
      return false;
    }
    ASTNode[] startNode = myNode.getChildren(TokenSet.create(LEFTBRACE));
    if (startNode.length != 0 && node.getStartOffset() <= startNode[0].getStartOffset()) {
      return false;
    }
    ASTNode[] endNode = myNode.getChildren(TokenSet.create(RIGHTBRACE));
    if (endNode.length != 0 && node.getStartOffset() >= endNode[0].getStartOffset()) {
      return false;
    }
    return true;
  }

  @Override
  public boolean isIncomplete() {
    IElementType type = myNode.getElementType();
    ASTNode lastChild = myNode.getLastChildNode();
    if (CURLY_BLOCK.contains(type)) {
      return lastChild == null || lastChild.getElementType() != RIGHTCURLYBRACE;
    }
    if (type == THROWS || type == TYPE_ANNOTATIONS || type == TYPE_ANNOTATION_LIST || type == FUNCTION) {
      return lastChild == null || lastChild.getElementType() != RIGHTBRACE;
    }
    if (type == CONST_LIST) {
      return lastChild == null || lastChild.getElementType() != RIGHTBRACKET;
    }
    return false;
  }

  @Override
  public @NotNull ChildAttributes getChildAttributes(int newChildIndex) {
    if (myNode.getElementType() == FIELDS && myNode.getTreeParent() != null && myNode.getTreeParent().getElementType() == FUNCTION) {
      return new ChildAttributes(Indent.getNoneIndent(), null);
    }
    if (CURLY_BODY_BLOCK.contains(myNode.getElementType()) || myNode.getElementType() == THROWS || myNode.getElementType() == TYPE_ANNOTATION_LIST || myNode.getElementType() == CONST_LIST) {
      return new ChildAttributes(Indent.getNormalIndent(), null);
    }
    if (myNode.getElementType() == FUNCTION) {
      return new ChildAttributes(Indent.getNoneIndent(), null);
    }
    return new ChildAttributes(Indent.getNoneIndent(), null);
  }

  @Override
  protected @Nullable Indent getChildIndent() {
    IElementType elementType = myNode.getElementType();
    if (elementType == FIELDS && myNode.getTreeParent() != null && myNode.getTreeParent().getElementType() == FUNCTION) {
      return Indent.getNoneIndent();
    }
    if (CURLY_BODY_BLOCK.contains(elementType) || elementType == THROWS || elementType == TYPE_ANNOTATION_LIST || elementType == CONST_LIST) {
      return Indent.getNormalIndent();
    }
    return Indent.getNoneIndent();
  }

  private Optional<Indent> getBlockBodyIndent(TokenSet set, IElementType start, IElementType end, boolean indentBody) {
    IElementType my = myNode.getElementType();
    ASTNode parentNode = myNode.getTreeParent();
    if (parentNode == null) {
      return Optional.empty();
    }
    IElementType parent = parentNode.getElementType();
    if (!set.contains(parent)) {
      return Optional.empty();
    }
    if (myNode.getElementType() == start || myNode.getElementType() == end) {
      return Optional.of(Indent.getNoneIndent());
    }
    ASTNode[] startNode = parentNode.getChildren(TokenSet.create(start));
    if (startNode.length != 0 && myNode.getStartOffset() <= startNode[0].getStartOffset()) {
      return Optional.of(Indent.getNoneIndent());
    }
    ASTNode[] endNode = parentNode.getChildren(TokenSet.create(end));
    if (endNode.length != 0 && myNode.getStartOffset() >= endNode[0].getStartOffset()) {
      return Optional.of(Indent.getNoneIndent());
    }
    return Optional.of(indentBody ? Indent.getNormalIndent() : Indent.getNoneIndent());
  }


  @Override
  public @Nullable Indent getIndent() {
    // Body containers should not add an extra indentation level; their children will be indented.
    if (myNode.getElementType() == FIELDS && myNode.getTreeParent() != null && myNode.getTreeParent().getElementType() == FUNCTION) {
      // Function args list should be indented relative to the signature,
      // but each arg line should not add another level.
      return Indent.getNormalIndent();
    }
    if (CURLY_BODY_BLOCK.contains(myNode.getElementType())) {
      return Indent.getNoneIndent();
    }

    Optional<Indent> indent = getBlockBodyIndent(CURLY_BLOCK, LEFTCURLYBRACE, RIGHTCURLYBRACE, true);
    if (indent.isPresent()) {
      return indent.get();
    }
    indent = getBlockBodyIndent(THROWS_PAREN_BLOCK, LEFTBRACE, RIGHTBRACE, true);
    if (indent.isPresent()) {
      return indent.get();
    }
    indent = getBlockBodyIndent(ANNOTATION_PAREN_BLOCK, LEFTBRACE, RIGHTBRACE, true);
    if (indent.isPresent()) {
      return indent.get();
    }
    indent = getBlockBodyIndent(FUNCTION_PAREN_BLOCK, LEFTBRACE, RIGHTBRACE, true);
    if (indent.isPresent()) {
      return indent.get();
    }
    indent = getBlockBodyIndent(BRACKET_BLOCK, LEFTBRACKET, RIGHTBRACKET, true);
    // For everything remaining, no indent.
    return indent.orElseGet(Indent::getNoneIndent);
  }

  private static @Nullable Wrap getBlockBodyWrap(@NotNull ASTNode node,
                                                 @NotNull TokenSet parentSet,
                                                 @NotNull IElementType start,
                                                 @NotNull IElementType end,
                                                 @NotNull WrapType wrapType) {
    ASTNode parentNode = node.getTreeParent();
    if (parentNode == null) {
      return null;
    }
    if (!parentSet.contains(parentNode.getElementType())) {
      return null;
    }
    if (node.getElementType() == start || node.getElementType() == end) {
      return null;
    }
    ASTNode[] startNode = parentNode.getChildren(TokenSet.create(start));
    if (startNode.length != 0 && node.getStartOffset() <= startNode[0].getStartOffset()) {
      return null;
    }
    ASTNode[] endNode = parentNode.getChildren(TokenSet.create(end));
    if (endNode.length != 0 && node.getStartOffset() >= endNode[0].getStartOffset()) {
      return null;
    }
    return Wrap.createWrap(wrapType, false);
  }

  private @Nullable Wrap getWrapForChild(@NotNull ASTNode node) {
    if (isFunctionArg(node)) {
      return getFunctionArgsWrap();
    }

    IElementType parentType = node.getTreeParent() != null ? node.getTreeParent().getElementType() : null;
    if (parentType == null) {
      return null;
    }
    if (CURLY_BLOCK.contains(parentType)) {
      return getBlockBodyWrap(node, CURLY_BLOCK, LEFTCURLYBRACE, RIGHTCURLYBRACE, WrapType.ALWAYS);
    }
    if (parentType == THROWS) {
      return getBlockBodyWrap(node, THROWS_PAREN_BLOCK, LEFTBRACE, RIGHTBRACE, WrapType.ALWAYS);
    }
    if (parentType == TYPE_ANNOTATION_LIST) {
      return getBlockBodyWrap(node, ANNOTATION_PAREN_BLOCK, LEFTBRACE, RIGHTBRACE, WrapType.CHOP_DOWN_IF_LONG);
    }
    if (parentType == FUNCTION) {
      if (node.getElementType() == THROWS || node.getElementType() == TYPE_ANNOTATIONS) {
        return getFunctionTailWrap();
      }
      return getBlockBodyWrap(node, FUNCTION_PAREN_BLOCK, LEFTBRACE, RIGHTBRACE, WrapType.CHOP_DOWN_IF_LONG);
    }
    if (parentType == CONST_LIST) {
      return getBlockBodyWrap(node, BRACKET_BLOCK, LEFTBRACKET, RIGHTBRACKET, WrapType.CHOP_DOWN_IF_LONG);
    }
    return null;
  }

  @Override
  public @Nullable Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
    if (child1 instanceof SimpleThriftBlock && child2 instanceof SimpleThriftBlock) {
      SimpleThriftBlock left = (SimpleThriftBlock) child1;
      SimpleThriftBlock right = (SimpleThriftBlock) child2;
      IElementType parentType = myNode.getElementType();
      IElementType leftType = left.getNode().getElementType();
      IElementType rightType = right.getNode().getElementType();

      // Curly blocks: enforce line breaks after '{' and before '}'.
      if (leftType == LEFTCURLYBRACE || rightType == RIGHTCURLYBRACE) {
        return LINE_BREAK;
      }

      // Throws/type-annotations: enforce multi-line layouts when present.
      if (parentType == THROWS && (leftType == LEFTBRACE || rightType == RIGHTBRACE)) {
        return LINE_BREAK;
      }

      // Ensure `throws (` has a space before '('.
      if (parentType == THROWS && rightType == LEFTBRACE) {
        return ONE_SPACE;
      }

      // Put each entry on its own line inside throws.
      if (parentType == THROWS && rightType == FIELD) {
        return LINE_BREAK;
      }

      // Type annotations: keep single entry inline, but format multiple annotations as a multi-line block.
      if (parentType == TYPE_ANNOTATIONS) {
        int annotations = countTypeAnnotations(myNode);
        if (annotations > 1) {
          if (leftType == LEFTBRACE || rightType == RIGHTBRACE) {
            return LINE_BREAK;
          }
        }
      }

      // TypeAnnotationList: put each annotation entry on its own line.
      if (parentType == TYPE_ANNOTATION_LIST && rightType == TYPE_ANNOTATION) {
        return LINE_BREAK;
      }

      // Put each entry on its own line inside curly-body containers.
      if ((parentType == FIELDS && rightType == FIELD)
          || (parentType == SERVICE_BODY && rightType == FUNCTION)
          || (parentType == ENUM_FIELDS && rightType == ENUM_FIELD)
          || (parentType == SENUM_BODY && rightType == SENUM_FIELD)) {
        return LINE_BREAK;
      }

      // Function args: keep `, ` between args when they stay on one line.
      // (The comma/semicolon is part of the preceding FIELD node in the grammar.)
      if (parentType == FUNCTION && leftType == FIELD && rightType == FIELD) {
        return ONE_SPACE;
      }

      // Keep `) throws (` on the same line.
      if (parentType == FUNCTION && rightType == THROWS) {
        return ONE_SPACE;
      }

      // Ensure a space before inline type annotations (e.g. `} (` or `) (deprecated)`).
      if ((leftType == RIGHTCURLYBRACE || leftType == RIGHTBRACE) && rightType == TYPE_ANNOTATIONS) {
        return ONE_SPACE;
      }

      // Ensure a space between `xsd_attrs { ... }` and a following annotation block.
      if ((leftType == XSD_ATTRS || leftType == XSD_FIELD_OPTIONS) && rightType == TYPE_ANNOTATIONS) {
        return ONE_SPACE;
      }

      // Ensure a space between a throws clause and trailing type annotations (e.g. `(deprecated)`).
      if (leftType == THROWS && rightType == TYPE_ANNOTATIONS) {
        return ONE_SPACE;
      }

      // Lists/field separators: break after ListSeparator / ';' (but not for function args).
      if ((leftType == LIST_SEPARATOR || leftType == SEMICOLON) && parentType != FUNCTION) {
        return LINE_BREAK;
      }

      // Annotation list: break after commas to put each annotation on its own line (when multi-line).
      if (parentType == TYPE_ANNOTATION_LIST && leftType == COMMA) {
        return LINE_BREAK;
      }

      // Const list: prefer one element per line when it becomes multi-line.
      if (parentType == CONST_LIST && leftType == COMMA) {
        return LINE_BREAK;
      }

      // Const map: put entries on separate lines.
      if (parentType == CONST_MAP && leftType == COMMA) {
        return LINE_BREAK;
      }
    }
    return mySpacingBuilder.getSpacing(this, child1, child2);
  }

  @Override
  public boolean isLeaf() {
    return myNode.getFirstChildNode() == null;
  }
}
