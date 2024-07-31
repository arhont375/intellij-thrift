package com.intellij.plugins.thrift.annotator;

import com.intellij.plugins.thrift.ThriftCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class ThriftEditorAnnotatorTest extends ThriftCodeInsightFixtureTestCase {
  @Override
  @NotNull
  protected String getRelativePath() {
    return "annotator";
  }

  @Test
  public void testTypeIsNotException(){
    getFixture().configureByFiles( "TypeIsNotException.thrift");
    getFixture().checkHighlighting(false, false, true);
  }
}
