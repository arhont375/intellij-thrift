package com.intellij.plugins.thrift.formatter;

import com.intellij.openapi.editor.DefaultLineWrapPositionStrategy;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThriftLineWrapPositionStrategy extends DefaultLineWrapPositionStrategy {
  @Override
  public int calculateWrapPosition(@NotNull Document document, @Nullable Project project, int startOffset, int endOffset, int maxPreferredOffset, boolean allowToBeyondMaxPreferredOffset, boolean isSoftWrap) {
    return super.calculateWrapPosition(document, project, startOffset, endOffset, maxPreferredOffset, allowToBeyondMaxPreferredOffset, isSoftWrap);
  }
}
