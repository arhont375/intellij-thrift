package com.intellij.plugins.thrift;

import com.intellij.plugins.thrift.util.ThriftTestUtils;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5;

/**
 * Created by fkorotkov.
 */
abstract public class ThriftCodeInsightFixtureTestCase extends LightJavaCodeInsightFixtureTestCase5 {
  @Override
  protected String getTestDataPath() {
    return ThriftTestUtils.BASE_TEST_DATA_PATH + "/" + getRelativePath();
  }

  protected String getThriftTestName() {
    return getTestName(true) + "." + ThriftFileType.DEFAULT_EXTENSION;
  }
}
