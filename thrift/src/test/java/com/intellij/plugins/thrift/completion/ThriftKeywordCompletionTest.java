package com.intellij.plugins.thrift.completion;

import org.junit.jupiter.api.Test;

/**
 * Created by fkorotkov.
 */
public class ThriftKeywordCompletionTest extends ThriftCompletionTestBase {
  public ThriftKeywordCompletionTest() {
    super("completion/keyword");
  }

  @Test
  public void testEmpty() throws Throwable {
    doTest();
  }

  @Test
  public void testTopLevelAfterDefinition() throws Throwable {
    doTest();
  }

  @Test
  public void testType() throws Throwable {
    doTest();
  }

  @Test
  public void testField() throws Throwable {
    doTest();
  }
}
