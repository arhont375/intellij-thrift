package com.intellij.plugins.thrift.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.plugins.thrift.lang.lexer.ThriftElementType;
import com.intellij.psi.PsiElement;
import com.intellij.plugins.thrift.lang.psi.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiRecursiveVisitor;
import com.intellij.psi.PsiWalkingState;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IntPair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.plugins.thrift.lang.lexer.ThriftTokenTypes.*;


public class ThriftFoldingBuilder extends FoldingBuilderEx implements DumbAware {
  @Override
  public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
    List<FoldingDescriptor> descriptors = new ArrayList<>();
    root.accept(new ThriftFoldingRecursiveElementWalkingVisitor(descriptors));
    return descriptors.toArray(FoldingDescriptor.EMPTY_ARRAY);
  }

  @Override
  public @Nullable String getPlaceholderText(@NotNull ASTNode node) {
    PsiElement psiElement = node.getPsi();
    String placeholderText = "...";
    if (psiElement instanceof ThriftEnum) {
      placeholderText = "{...}";
    } else if (psiElement instanceof ThriftSenum) {
      placeholderText = "{...}";
    } else if (psiElement instanceof ThriftStruct) {
      placeholderText = "{...}";
    } else if (psiElement instanceof ThriftUnion) {
      placeholderText = "{...}";
    } else if (psiElement instanceof ThriftException) {
      placeholderText = "{...}";
    } else if (psiElement instanceof ThriftService) {
      placeholderText = "{...}";
    } else if (psiElement instanceof ThriftXsdAttrs) {
      placeholderText = "{...}";
    } else if (psiElement instanceof ThriftConstMap) {
      placeholderText = "{...}";
    } else if (psiElement instanceof ThriftConstList) {
      placeholderText = "[...]";
    } else if (isOfThriftElementType(node.getElementType(), BLOCKCOMMENT)) {
      placeholderText = "/*...*/";
    } else if (psiElement instanceof ThriftTypeAnnotations) {
      placeholderText = "(...)";
    } else if (psiElement instanceof ThriftFunction) {
      placeholderText = "(...)";
    } else if (psiElement instanceof ThriftThrows) {
      placeholderText = "(...)";
    }
    return placeholderText;
  }

  @Override
  public boolean isCollapsedByDefault(@NotNull ASTNode node) {
    return false;
  }

  static boolean isOfThriftElementType(IElementType elementType, IElementType thriftElementType) {
    if (elementType instanceof ThriftElementType) {
      return elementType.equals(thriftElementType);
    }
    return false;
  }

  static class ThriftFoldingRecursiveElementWalkingVisitor extends PsiElementVisitor implements PsiRecursiveVisitor {
    private final PsiWalkingState myWalkingState = new PsiWalkingState(this) {
    };

    private final List<FoldingDescriptor> descriptors;

    public ThriftFoldingRecursiveElementWalkingVisitor(List<FoldingDescriptor> descriptors) {
      this.descriptors = descriptors;
    }

    @Override
    public void visitElement(@NotNull PsiElement element) {
      myWalkingState.elementStarted(element);

      if (element instanceof ThriftEnum) {
        this.visitElementWithBraceChildren(element, LEFTCURLYBRACE, RIGHTCURLYBRACE);
      } else if (element instanceof ThriftSenum) {
        this.visitElementWithBraceChildren(element, LEFTCURLYBRACE, RIGHTCURLYBRACE);
      } else if (element instanceof ThriftStruct) {
        this.visitElementWithBraceChildren(element, LEFTCURLYBRACE, RIGHTCURLYBRACE);
      } else if (element instanceof ThriftUnion) {
        this.visitElementWithBraceChildren(element, LEFTCURLYBRACE, RIGHTCURLYBRACE);
      } else if (element instanceof ThriftException) {
        this.visitElementWithBraceChildren(element, LEFTCURLYBRACE, RIGHTCURLYBRACE);
      } else if (element instanceof ThriftService) {
        this.visitElementWithBraceChildren(element, LEFTCURLYBRACE, RIGHTCURLYBRACE);
      } else if (element instanceof ThriftXsdAttrs) {
        this.visitElementWithBraceChildren(element, LEFTCURLYBRACE, RIGHTCURLYBRACE);
      } else if (element instanceof ThriftConstMap) {
        this.visitElementWithBraceChildren(element, LEFTCURLYBRACE, RIGHTCURLYBRACE);
      } else if (element instanceof ThriftConstList) {
        this.visitElementWithBraceChildren(element, LEFTBRACKET, RIGHTBRACKET);
      } else if (isOfThriftElementType(element.getNode().getElementType(), BLOCKCOMMENT)) {
        this.visitThriftBlockComment(element.getNode());
      } else if (element instanceof ThriftTypeAnnotations) {
        this.visitThriftTypeAnnotations((ThriftTypeAnnotations) element);
      } else if (element instanceof ThriftFunction) {
        visitFieldsWithBraceRecovery(element);
      } else if (element instanceof ThriftThrows) {
        visitFieldsWithBraceRecovery(element);
      }
    }

    private static IntPair findIndexesOfBrace(PsiElement element, IElementType leftBrace, IElementType rightBrace) {
      ASTNode[] children = element.getNode().getChildren(null);
      int beg, end;
      for (beg = 0; beg < children.length; beg++) {
        if (isOfThriftElementType(children[beg].getElementType(), leftBrace)) {
          break;
        }
      }
      for (end = children.length - 1; end >= 0; end--) {
        if (isOfThriftElementType(children[end].getElementType(), rightBrace)) {
          break;
        }
      }
      return new IntPair(beg, end);
    }

    private void visitElementWithBraceChildren(PsiElement element, IElementType leftBrace, IElementType rightBrace) {
      IntPair indexRange = findIndexesOfBrace(element, leftBrace, rightBrace);
      ASTNode[] children = element.getNode().getChildren(null);
      if (indexRange.first < children.length && indexRange.second >= 0) {
        this.descriptors.add(new FoldingDescriptor(element, new TextRange(children[indexRange.first].getStartOffset(), children[indexRange.second].getStartOffset() + 1)));
      }
    }

    private void visitThriftBlockComment(ASTNode node) {
      this.descriptors.add(new FoldingDescriptor(node,
          new TextRange(node.getStartOffset(), node.getStartOffset() + node.getTextLength())));
    }

    private static final int BIG_BLOCK_LENGTH = 120;

    private void visitThriftTypeAnnotations(ThriftTypeAnnotations element) {
      // keep small and one-line annotation
      if (element.getTextLength() < BIG_BLOCK_LENGTH && !element.getText().contains("\n")) {
        return;
      }
      this.visitElementWithBraceChildren(element, LEFTBRACE, RIGHTBRACE);
    }

    private void visitFieldsWithBraceRecovery(PsiElement element) {
      IntPair indexRange = findIndexesOfBrace(element, LEFTBRACE, RIGHTBRACE);
      ASTNode[] children = element.getNode().getChildren(null);
      if (indexRange.first >= children.length || indexRange.second < 0) {
        return;
      }
      FoldingDescriptor fd = new FoldingDescriptor(element, new TextRange(children[indexRange.first].getStartOffset(), children[indexRange.second].getStartOffset() + 1));
      if (fd.getRange().getLength() >= BIG_BLOCK_LENGTH) {
        this.descriptors.add(fd);
        return;
      }
      for (int i = indexRange.first + 1; i < indexRange.second; i++) {
        if (children[i].getText().contains("\n")) {
          this.descriptors.add(fd);
          return;
        }
      }
    }
  }
}
