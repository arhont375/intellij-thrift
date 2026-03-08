package com.intellij.plugins.thrift.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.formatter.WrappingUtil;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.openapi.util.TextRange;
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
  private static final TokenSet COMMENTS_BLOCK = TokenSet.create(COMMENT, BLOCKCOMMENT);

  private static final TokenSet CURLY_CONTAINER_BLOCK = TokenSet.create(ENUM, SENUM, STRUCT, UNION, EXCEPTION, SERVICE, XSD_ATTRS, CONST_MAP);
  private static final TokenSet CURLY_BODY_BLOCK = TokenSet.create(ENUM_FIELDS, SENUM_BODY, FIELDS, SERVICE_BODY);

  private static final Spacing LINE_BREAK = Spacing.createSpacing(0, 0, 1, true, 1);
  private static final Spacing NO_SPACE = Spacing.createSpacing(0, 0, 0, true, 0);
  private static final Spacing ONE_SPACE = Spacing.createSpacing(1, 1, 0, true, 0);

  /**
   * Wrap for function arguments.
   */
  private Wrap myFunctionArgsWrap;

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

  private int getCurlyWrapSetting() {
    return getCodeStyleSettings().CURLY_BLOCK_WRAP;
  }

  private int getThrowsWrapSetting() {
    return getCodeStyleSettings().THROWS_WRAP;
  }

  private int getAnnotationWrapSetting() {
    return getCodeStyleSettings().ANNOTATION_WRAP;
  }

  private int getConstListWrapSetting() {
    return getCodeStyleSettings().CONST_LIST_WRAP;
  }

  private int getFunctionArgsWrapSetting() {
    return getCodeStyleSettings().FUNCTION_ARGUMENTS_WRAP;
  }

  private @NotNull Wrap getBlockWrap(int wrapSetting) {
    return Wrap.createWrap(WrappingUtil.getWrapType(wrapSetting), false);
  }

  private @NotNull Wrap getFunctionArgsWrap() {
    if (myFunctionArgsWrap == null) {
      myFunctionArgsWrap = getBlockWrap(getFunctionArgsWrapSetting());
    }
    return myFunctionArgsWrap;
  }

  private @NotNull ThriftCodeStyleSettings getCodeStyleSettings() {
    CodeStyleSettings settings = myNode.getPsi() != null
        ? CodeStyleSettingsManager.getSettings(myNode.getPsi().getProject())
        : CodeStyleSettings.getDefaults();
    return settings.getCustomSettings(ThriftCodeStyleSettings.class);
  }

  private @NotNull Wrap getCurlyWrap() {
    return getBlockWrap(getCurlyWrapSetting());
  }

  private @NotNull Wrap getThrowsWrap() {
    return getBlockWrap(getThrowsWrapSetting());
  }

  private @NotNull Wrap getAnnotationWrap() {
    return getBlockWrap(getAnnotationWrapSetting());
  }

  private @NotNull Wrap getConstListWrap() {
    return getBlockWrap(getConstListWrapSetting());
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

  private @Nullable Wrap getWrapForChild(@NotNull ASTNode node) {
    IElementType parentType = node.getTreeParent() != null ? node.getTreeParent().getElementType() : null;
    if (parentType == null) {
      return null;
    }

    // curly body block
    if (CURLY_BODY_BLOCK.contains(parentType)) {
      return getCurlyWrap();
    }

    // function arg
    if (node.getElementType() == FIELD && parentType == FUNCTION) {
      return getFunctionArgsWrap();
    }

    // throws fields
    if (parentType == THROWS) {
      return getBlockBodyWrap(node, TokenSet.create(THROWS), LEFTBRACE, RIGHTBRACE, getThrowsWrap());
    }

    // annotation fields
    if (parentType == TYPE_ANNOTATION_LIST) {
      return getAnnotationWrap();
    }

    // const list and map
    if ((parentType == CONST_LIST || parentType == CONST_MAP) && node.getElementType() == CONST_VALUE) {
      return getBlockBodyWrap(node, TokenSet.create(CONST_LIST), LEFTBRACKET, RIGHTBRACKET, getConstListWrap());
    }

    return null;
  }

  private static @Nullable Wrap getBlockBodyWrap(@NotNull ASTNode node,
                                                 @NotNull TokenSet parentSet,
                                                 @NotNull IElementType start,
                                                 @NotNull IElementType end,
                                                 @NotNull Wrap wrap) {
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
    return wrap;
  }

  @Override
  public boolean isIncomplete() {
    IElementType type = myNode.getElementType();
    ASTNode lastChild = myNode.getLastChildNode();

    // curly
    if (CURLY_BODY_BLOCK.contains(type)) {
      return myNode.getTreeParent().getTreeNext() != RIGHTCURLYBRACE;
    }
    if (type == XSD_ATTRS || type == CONST_MAP) {
      return lastChild == null || lastChild.getElementType() != RIGHTCURLYBRACE;
    }

    // paren
    if (type == THROWS || type == TYPE_ANNOTATIONS) {
      return lastChild == null || lastChild.getElementType() != RIGHTBRACE;
    }
    if (type == FUNCTION) {
      return lastChild == null || myNode.getChildren(TokenSet.create(RIGHTBRACE)).length == 0;
    }

    // bracket
    if (type == CONST_LIST) {
      return lastChild == null || lastChild.getElementType() != RIGHTBRACKET;
    }

    return false;
  }

  @Override
  protected @Nullable Indent getChildIndent() {
    IElementType elementType = myNode.getElementType();

    // curly
    if (CURLY_BODY_BLOCK.contains(elementType) || elementType == CONST_MAP) {
      return Indent.getNormalIndent();
    }

    // paren
    if (elementType == THROWS || elementType == FUNCTION || elementType == TYPE_ANNOTATION_LIST) {
      return Indent.getNormalIndent();
    }

    // bracket
    if (elementType == CONST_LIST) {
      return Indent.getNormalIndent();
    }

    return Indent.getNoneIndent();
  }

  private Optional<Indent> getBlockBodyIndent(TokenSet set, IElementType start, IElementType end) {
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
    return Optional.of(Indent.getNormalIndent());
  }

  @Override
  public @Nullable Indent getIndent() {
    if (myNode.getTreeParent() == null) {
      return Indent.getNoneIndent();
    }

    // curly
    if (CURLY_BODY_BLOCK.contains(myNode.getTreeParent().getElementType())) {
      ASTNode grandParent = myNode.getTreeParent().getTreeParent();
      // special case: avoid double indent for xsd_attrs
      if (grandParent != null && grandParent.getElementType() != XSD_ATTRS) {
        return Indent.getNormalIndent();
      }
      return Indent.getNoneIndent();
    }
    if (COMMENTS_BLOCK.contains(myNode.getElementType()) && CURLY_CONTAINER_BLOCK.contains(myNode.getTreeParent().getElementType())) {
      // special case: comments at beginning and end are not included in body block
      return Indent.getNormalIndent();
    }
    Optional<Indent> indent = getBlockBodyIndent(TokenSet.create(XSD_ATTRS, CONST_MAP), LEFTCURLYBRACE, RIGHTCURLYBRACE);
    if (indent.isPresent()) {
      return indent.get();
    }

    // paren
    indent = getBlockBodyIndent(TokenSet.create(THROWS), LEFTBRACE, RIGHTBRACE);
    if (indent.isPresent()) {
      return indent.get();
    }
    indent = getBlockBodyIndent(TokenSet.create(FUNCTION), LEFTBRACE, RIGHTBRACE);
    if (indent.isPresent()) {
      return indent.get();
    }
    if (myNode.getTreeParent().getElementType() == TYPE_ANNOTATION_LIST) {
      return Indent.getNormalIndent();
    }

    // bracket
    indent = getBlockBodyIndent(TokenSet.create(CONST_LIST), LEFTBRACKET, RIGHTBRACKET);
    return indent.orElseGet(Indent::getNoneIndent);
  }

  @Override
  public @Nullable Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
    if (child1 instanceof SimpleThriftBlock && child2 instanceof SimpleThriftBlock) {
      Optional<SimpleThriftBlock> left = Optional.of((SimpleThriftBlock) child1);
      Optional<SimpleThriftBlock> right = Optional.of((SimpleThriftBlock) child2);
      IElementType parentType = myNode.getElementType();
      IElementType leftType = left.map((x -> x.getNode().getElementType())).orElse(null);
      IElementType rightType = right.map(x -> x.getNode().getElementType()).orElse(null);

      // inside curly
      // 1. enforce line breaks after '{' and before '}'
      if (leftType == LEFTCURLYBRACE || rightType == RIGHTCURLYBRACE) {
        return LINE_BREAK;
      }
      // 2. enforce line break after each field
      if (CURLY_BODY_BLOCK.contains(parentType) && leftType == rightType) {
        return LINE_BREAK;
      }
      // 3. enforce line break after each field inside CONST_MAP
      if (parentType == CONST_MAP && (leftType == LIST_SEPARATOR || leftType == CONST_VALUE) && rightType == CONST_VALUE) {
        return LINE_BREAK;
      }

      // inside paren
      // 1. enforce line breaks after '(' and before ')'
      if (leftType == LEFTBRACE || rightType == RIGHTBRACE) {
        return NO_SPACE;
      }
      // 2. enforce line break after each field
      if (TokenSet.create(THROWS, TYPE_ANNOTATION_LIST, FUNCTION).contains(parentType) && leftType == rightType) {
        return ONE_SPACE;
      }

      // inside bracket
      // 1. enforce line breaks after '[' and before ']'
      if (leftType == LEFTBRACKET || rightType == RIGHTBRACKET) {
        return NO_SPACE;
      }
      // 2. enforce line break after each field
      if (parentType == CONST_LIST && (leftType == CONST_VALUE || leftType == LIST_SEPARATOR) && rightType == CONST_VALUE) {
        return ONE_SPACE;
      }

      // outside curly
      if (rightType == LEFTCURLYBRACE) {
        return ONE_SPACE;
      }

      // outside paren
      if (parentType == THROWS && rightType == LEFTBRACE) {
        return ONE_SPACE;
      }
      if (rightType == TYPE_ANNOTATIONS || rightType == THROWS) {
        return ONE_SPACE;
      }
      if (parentType == FUNCTION_TYPE && rightType == LEFTBRACE) {
        return NO_SPACE;
      }
    }
    return mySpacingBuilder.getSpacing(this, child1, child2);
  }

  @Override
  public boolean isLeaf() {
    return myNode.getFirstChildNode() == null;
  }
}
