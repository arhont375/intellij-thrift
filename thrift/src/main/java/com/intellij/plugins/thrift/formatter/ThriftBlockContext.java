package com.intellij.plugins.thrift.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.plugins.thrift.lang.lexer.ThriftTokenTypes;
import com.intellij.plugins.thrift.lang.psi.ThriftService;
import com.intellij.plugins.thrift.lang.psi.ThriftStruct;
import com.intellij.plugins.thrift.lang.psi.ThriftTopLevelDeclaration;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.WeakHashMap;

final class ThriftBlockContext {
  public final SpacingBuilder spacingBuilder;
  private final WeakHashMap<PsiElement, Alignment> equalsAlignmentMap = new WeakHashMap<>();
  private final WeakHashMap<PsiElement, Alignment> fieldReqAlignmentMap = new WeakHashMap<>();
  private final WeakHashMap<PsiElement, Alignment> fieldTypeAlignmentMap = new WeakHashMap<>();
  private final WeakHashMap<PsiElement, Alignment> fieldDefinitionNameAlignmentMap = new WeakHashMap<>();
  private final WeakHashMap<PsiElement, Alignment> fieldTrailingCommentAlignmentMap = new WeakHashMap<>();
  private final WeakHashMap<PsiElement, Alignment> serviceDefinitionNameAlignmentMap = new WeakHashMap<>();

  ThriftBlockContext(SpacingBuilder spacingBuilder) {
    this.spacingBuilder = spacingBuilder;
  }

  private Alignment ensureAlignment(WeakHashMap<PsiElement, Alignment> map, PsiElement psi) {
    if (psi == null) return null;
    Alignment alignment = map.get(psi);
    if (alignment == null) {
      alignment = Alignment.createAlignment(true);
      map.put(psi, alignment);
    }
    return alignment;
  }

  Alignment getAlignment(ASTNode node) {
    if (node.getElementType() == ThriftTokenTypes.EQUALS) {
      ThriftTopLevelDeclaration thriftDeclaration = PsiTreeUtil.getParentOfType(node.getPsi(), ThriftTopLevelDeclaration.class);
      return ensureAlignment(equalsAlignmentMap, thriftDeclaration);
    }

    // Struct
    ThriftStruct structParent = PsiTreeUtil.getParentOfType(node.getPsi(), ThriftStruct.class);
    if (structParent != null) {
      if (node.getElementType() == ThriftTokenTypes.FIELD_REQ) {
        return ensureAlignment(fieldReqAlignmentMap, structParent);
      }
      if (node.getElementType() == ThriftTokenTypes.FIELD_TYPE) {
        return ensureAlignment(fieldTypeAlignmentMap, structParent);
      }
      if (node.getElementType() == ThriftTokenTypes.DEFINITION_NAME && node.getTreeParent().getElementType() == ThriftTokenTypes.FIELD) {
        return ensureAlignment(fieldDefinitionNameAlignmentMap, structParent);
      }
      if (node.getElementType() == ThriftTokenTypes.COMMENT) {
        PsiElement prevSibling = node.getPsi().getPrevSibling();
        if (prevSibling != null && getPsiColumn(node.getPsi()) == getPsiColumn(prevSibling)) {
          return ensureAlignment(fieldTrailingCommentAlignmentMap, structParent);
        }
      }
    }

    // Service
    ThriftService serviceParent = PsiTreeUtil.getParentOfType(node.getPsi(), ThriftService.class);
    if (serviceParent != null) {
      if (node.getElementType() == ThriftTokenTypes.DEFINITION_NAME && node.getTreeParent().getElementType() == ThriftTokenTypes.FUNCTION) {
        return ensureAlignment(serviceDefinitionNameAlignmentMap, serviceParent);
      }
    }

    return null;
  }

  int getPsiColumn(PsiElement psiElement) {
    PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(psiElement.getProject());
    Document document = psiDocumentManager.getDocument(psiElement.getContainingFile());
    int textOffset = psiElement.getTextOffset();
    return document != null ? document.getLineNumber(textOffset) : -1;
  }
}
