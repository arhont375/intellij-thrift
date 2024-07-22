package com.intellij.plugins.thrift.rename;

import com.intellij.plugins.thrift.ThriftCodeInsightFixtureTestCase;
import com.intellij.testFramework.EdtTestUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class ThriftRenameTest extends ThriftCodeInsightFixtureTestCase {
  @Override
  @NotNull
  protected String getRelativePath() {
    return "rename";
  }

  public void doTest(String newName) {
    getFixture().configureByFiles(getThriftTestName(), getTestName(true) + "_after.thrift");
    EdtTestUtil.runInEdtAndWait(() -> getFixture().renameElementAtCaret(newName));
    getFixture().checkResultByFile(getTestName(true) + "_after.thrift");
  }

  @Test
  public void testLocalType1() {
    doTest("Foo");
  }

  @Test
  public void testLocalType2() {
    doTest("Foo");
  }
}
