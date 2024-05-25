package com.intellij.plugins.thrift.editor;

import com.intellij.codeInsight.daemon.DaemonBundle;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.PsiTargetNavigator;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.plugins.thrift.ThriftBundle;
import com.intellij.plugins.thrift.lang.psi.ThriftDefinitionName;
import com.intellij.plugins.thrift.util.ThriftPsiUtil;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by fkorotkov.
 */
public class ThriftLineMarkerProvider implements LineMarkerProvider {
  static Logger logger = Logger.getInstance(ThriftLineMarkerProvider.class);

  @Nullable
  @Override
  public LineMarkerInfo<PsiElement> getLineMarkerInfo(@NotNull PsiElement element) {
    if (element.getParent() instanceof ThriftDefinitionName) {
      return findImplementationsAndCreateMarker(element);
    }

    return null;
  }

  @Nullable
  private LineMarkerInfo<PsiElement> findImplementationsAndCreateMarker(PsiElement psiElement) {
    final List<NavigatablePsiElement> implementations =
            ThriftPsiUtil.findImplementations((ThriftDefinitionName) psiElement.getParent());
    if (implementations.isEmpty()) {
      return null;
    }
    return new LineMarkerInfo<>(
      psiElement,
      psiElement.getTextRange(),
      AllIcons.Gutter.ImplementedMethod,
      element -> ThriftBundle.message("thrift.inspection.interface.implemented.too.many"),
      (e, elt) -> {
        final String title = DaemonBundle.message("navigation.title.implementation.method",
                                                  psiElement.getText(),
                                                  implementations.size());
        new PsiTargetNavigator<PsiElement>(implementations)
                .navigate(e, title, elt.getProject());
      },
        GutterIconRenderer.Alignment.RIGHT,
        () -> ThriftBundle.message("thrift.inspection.interface.implemented.too.many")
    );
  }
}
