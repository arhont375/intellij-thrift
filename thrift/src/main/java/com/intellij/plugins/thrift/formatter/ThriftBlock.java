package com.intellij.plugins.thrift.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.plugins.thrift.lang.psi.*;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.formatter.common.AbstractBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class ThriftBlock extends AbstractBlock {
  private final ThriftBlockContext context;

  ThriftBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, ThriftBlockContext context) {
    super(node, wrap, alignment);
    this.context = context;
  }


  @Override
  protected List<Block> buildChildren() {
    if (isLeaf()) {
      return EMPTY;
    }
    List<Block> blocks = new ArrayList<>();
    ASTNode child = myNode.getFirstChildNode();
    while (child != null) {
      if (!FormatterUtil.containsWhiteSpacesOnly(child)) {
        Block block = new ThriftBlock(child, myWrap, context.getAlignment(child), context);
        blocks.add(block);
      }
      child = child.getTreeNext();
    }
    return blocks;
  }


  @Override
  public @Nullable Indent getIndent() {
    if (isEmpty(myNode)) {
      return Indent.getNoneIndent();
    }

    PsiElement psi = myNode.getPsi();

    if (
      // struct
        isFieldLike(new Class[]{ThriftStruct.class, ThriftFields.class}, ThriftField.class, psi) ||
            // enum
            isFieldLike(new Class[]{ThriftEnum.class, ThriftEnumFields.class}, ThriftEnumField.class, psi) ||
            // service
            isFieldLike(new Class[]{ThriftService.class, ThriftServiceBody.class}, ThriftFunction.class, psi)
    ) {
      return Indent.getNormalIndent();
    }

    return Indent.getNoneIndent();
  }

  @Override
  public @Nullable Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
    return context.spacingBuilder.getSpacing(this, child1, child2);
  }

  @Override
  public boolean isLeaf() {
    return myNode.getFirstChildNode() == null;
  }

  private static boolean isEmpty(ASTNode node) {
    return node.getElementType() == TokenType.WHITE_SPACE || node.getTextLength() == 0;
  }

  private static boolean isFieldLike(Class<? extends ThriftDeclaration>[] scopes, Class<? extends PsiElement> field, PsiElement psi) {
    PsiElement parent = psi.getParent();
    return (psi instanceof PsiComment && Arrays.stream(scopes).anyMatch(s -> s.isInstance(parent))) ||
        field.isInstance(psi);
  }
}
