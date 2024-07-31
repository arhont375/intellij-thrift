package com.intellij.plugins.thrift.folding;

import com.intellij.plugins.thrift.ThriftCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class ThriftFoldingBuilderTest extends ThriftCodeInsightFixtureTestCase {
  @Override
  @NotNull
  protected String getRelativePath() {
    return "folding";
  }

  protected void doTest() throws Throwable {
    String file = getTestName(true) + ".thrift";
    getFixture().testFolding(getTestDataPath() + "/" + file);
  }

  @Test
  public void testFoldingOnce() throws Throwable {
    doTest();
  }

  @Test
  public void testFoldingTwice() throws Throwable {
    doTest();
  }

  @Test
  public void testFoldingAnnotation() throws Throwable {
    doTest();
  }
}
