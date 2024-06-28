package com.intellij.plugins.thrift.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.plugins.thrift.lang.lexer.ThriftElementType;
import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.plugins.thrift.lang.psi.*;
import com.intellij.psi.tree.IElementType;
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

  static class ThriftFoldingRecursiveElementWalkingVisitor extends JavaRecursiveElementWalkingVisitor {
    private final List<FoldingDescriptor> descriptors;

    public ThriftFoldingRecursiveElementWalkingVisitor(List<FoldingDescriptor> descriptors) {
      this.descriptors = descriptors;
    }

    @Override
    public void visitElement(@NotNull PsiElement element) {
      super.visitElement(element);

      if (element instanceof ThriftEnum) {
        this.visitThriftEnum((ThriftEnum) element);
      } else if (element instanceof ThriftSenum) {
        this.visitThriftSenum((ThriftSenum) element);
      } else if (element instanceof ThriftStruct) {
        this.visitThriftStruct((ThriftStruct) element);
      } else if (element instanceof ThriftUnion) {
        this.visitThriftUnion((ThriftUnion) element);
      } else if (element instanceof ThriftException) {
        this.visitThriftException((ThriftException) element);
      } else if (element instanceof ThriftService) {
        this.visitThriftService((ThriftService) element);
      } else if (element instanceof ThriftXsdAttrs) {
        this.visitThriftXsdAttrs((ThriftXsdAttrs) element);
      } else if (element instanceof ThriftConstList) {
        this.visitThriftConstList((ThriftConstList) element);
      } else if (element instanceof ThriftConstMap) {
        this.visitThriftConstMap((ThriftConstMap) element);
      } else if (isOfThriftElementType(element.getNode().getElementType(), BLOCKCOMMENT)) {
        this.visitThriftBlockComment(element.getNode());
      }
    }

    private void visitElementWithCurlyBraceChildren(PsiElement element) {
      ASTNode[] children = element.getNode().getChildren(null);
      int beg, end;
      for (beg = 0; beg < children.length; beg++) {
        if (isOfThriftElementType(children[beg].getElementType(), LEFTCURLYBRACE)) {
          break;
        }
      }
      for (end = children.length - 1; end >= 0; end--) {
        if (isOfThriftElementType(children[end].getElementType(), RIGHTCURLYBRACE)) {
          break;
        }
      }
      if (beg < children.length && end >= 0) {
        this.descriptors.add(new FoldingDescriptor(element, new TextRange(children[beg].getStartOffset(), children[end].getStartOffset() + 1)));
      }
    }

    private void visitThriftEnum(ThriftEnum element) {
      this.visitElementWithCurlyBraceChildren(element);
    }

    private void visitThriftSenum(ThriftSenum element) {
      this.visitElementWithCurlyBraceChildren(element);
    }

    private void visitThriftStruct(ThriftStruct element) {
      this.visitElementWithCurlyBraceChildren(element);
    }

    private void visitThriftUnion(ThriftUnion element) {
      this.visitElementWithCurlyBraceChildren(element);
    }

    private void visitThriftException(ThriftException element) {
      this.visitElementWithCurlyBraceChildren(element);
    }

    private void visitThriftService(ThriftService element) {
      this.visitElementWithCurlyBraceChildren(element);
    }

    private void visitThriftXsdAttrs(ThriftXsdAttrs element) {
      this.visitElementWithCurlyBraceChildren(element);
    }

    private void visitThriftConstMap(ThriftConstMap element) {
      this.visitElementWithCurlyBraceChildren(element);
    }

    private void visitThriftConstList(ThriftConstList element) {
      ASTNode[] children = element.getNode().getChildren(null);
      int beg, end;
      for (beg = 0; beg < children.length; beg++) {
        if (isOfThriftElementType(children[beg].getElementType(), LEFTBRACKET)) {
          break;
        }
      }
      for (end = children.length - 1; end >= 0; end--) {
        if (isOfThriftElementType(children[end].getElementType(), RIGHTBRACKET)) {
          break;
        }
      }
      if (beg < children.length && end >= 0) {
        this.descriptors.add(new FoldingDescriptor(element, new TextRange(children[beg].getStartOffset(), children[end].getStartOffset() + 1)));
      }
    }

    private void visitThriftBlockComment(ASTNode node) {
      this.descriptors.add(new FoldingDescriptor(node,
          new TextRange(node.getStartOffset(), node.getStartOffset() + node.getTextLength())));
    }
  }
}
