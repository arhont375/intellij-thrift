package com.intellij.plugins.thrift.editor;

import com.google.common.base.Stopwatch;
import com.intellij.codeInsight.navigation.PsiTargetNavigator;
import com.intellij.codeInsight.navigation.impl.PsiTargetPresentationRenderer;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.plugins.thrift.ThriftBundle;
import com.intellij.plugins.thrift.index.ThriftDeclarationIndex;
import com.intellij.plugins.thrift.lang.psi.ThriftDeclaration;
import com.intellij.plugins.thrift.lang.psi.ThriftStruct;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import javax.swing.Icon;

public class GoToThriftDefinition extends AnAction {
  static Logger logger = Logger.getInstance(GoToThriftDefinition.class);

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    PsiElement psiElement = event.getData(CommonDataKeys.PSI_ELEMENT);
    List<ThriftDeclaration> thriftMatches = getThriftDeclarations(psiElement, event.getProject());
    if (thriftMatches.size() == 1) {
      // navigate directly if there's only 1 matched declaration
      thriftMatches.get(0).navigate(true);
    } else if (thriftMatches.size() > 1) {
      final PsiTargetPresentationRenderer<ThriftDeclaration> presentationRenderer =
              new PsiTargetPresentationRenderer<ThriftDeclaration>() {
                @Nls
                @Nullable
                @Override
                public String getContainerText(@NotNull ThriftDeclaration element) {
                  Project project = event.getProject();
                  Path containingFilePath = Paths.get(element.getContainingFile().getVirtualFile().getPath());
                  if (project != null && project.getBasePath() != null) {
                    Path projectBasePath = Paths.get(project.getBasePath()).normalize();
                    return commonRoot(projectBasePath, containingFilePath).relativize(containingFilePath)
                                                                          .toString();
                  } else {
                    return containingFilePath.toString();
                  }
                }

                @NotNull
                public Path commonRoot(@NotNull Path pathA, @NotNull Path pathB) {
                  Path rootCandidate = pathA;
                  while (rootCandidate.getParent() != null && rootCandidate.relativize(pathB).startsWith("..")) {
                    rootCandidate = rootCandidate.getParent();
                  }
                  return rootCandidate;
                }

                @Nullable
                @Override
                protected Icon getIcon(@NotNull ThriftDeclaration element) {
                  return super.getIcon(element);
                }

                @Nls
                @NotNull
                @Override
                public String getElementText(@NotNull ThriftDeclaration element) {
                  if (element instanceof ThriftStruct) {
                    return element.getName();
                  } else {
                    return element.getText().substring(0, 20);
                  }
                }
              };
        // show popup if there are multiple matched declarations
        new PsiTargetNavigator<>(thriftMatches)
                .presentationProvider(presentationRenderer)
                .createPopup(event.getProject(), ThriftBundle.message("thrift.goto.source"))
                .showInBestPositionFor(event.getDataContext());
    }
  }

  @NotNull
  private List<ThriftDeclaration> getThriftDeclarations(PsiElement psiElement, Project project) {
    // FIXME: The PsiClass might not valid in Non-Java editors such as GoLand
    try {
      if (psiElement instanceof PsiClass) {
        PsiClass psiClassElement = (PsiClass) psiElement;
        String name = psiClassElement.getName();
        if (name != null) { // todo - handle multiple matches
          return ThriftDeclarationIndex.findDeclaration(name, project, GlobalSearchScope.allScope(project));
        }
      }
    } catch (NoClassDefFoundError e) {
      logger.warn("NoClassDefFoundError at GoToThriftDefinition.java:90");
    }
    return Lists.newArrayList();
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
    Presentation p = e.getPresentation();
    Stopwatch stopWatch = Stopwatch.createStarted();
    boolean enabled = !getThriftDeclarations(psiElement, e.getProject()).isEmpty();
    Duration time = stopWatch.elapsed();
    if (time.toMillis() > 20) {
      logger.warn("GoToThriftDefinition action update took " + time.toMillis() + "ms");
    }
    p.setEnabledAndVisible(enabled);
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.EDT;
  }
}
