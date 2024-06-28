package com.intellij.plugins.thrift.folding;

import com.intellij.plugins.thrift.ThriftCodeInsightFixtureTestCase;
import org.junit.jupiter.api.Test;

public class ThriftFoldingBuilderTest extends ThriftCodeInsightFixtureTestCase {
  @Override
  protected String getBasePath() {
    return "folding";
  }

  protected void doTest() throws Throwable {
    myFixture.configureByFiles();
    String file = getTestName(true) + ".thrift";
    myFixture.configureByFile(file);
    myFixture.testFolding(getTestDataPath() + "/" + file);
  }

  @Test
  public void testFoldingOnce() throws Throwable {
    doTest();
  }

  public void testFoldingTwice() throws Throwable {
    doTest();
  }
}
