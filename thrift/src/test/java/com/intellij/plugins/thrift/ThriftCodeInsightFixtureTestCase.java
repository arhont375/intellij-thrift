package com.intellij.plugins.thrift;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.lang.LanguageBraceMatching;
import com.intellij.plugins.thrift.util.ThriftTestUtils;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Created by fkorotkov.
 */
abstract public class ThriftCodeInsightFixtureTestCase extends LightJavaCodeInsightFixtureTestCase5 {

  private boolean oldAutoInsertPairBracket;

  @BeforeEach
  void enableAutoInsertPairBracket() {
    // ParsingTestCase (used in parser tests) initializes some Language-level caches without plugin.xml EPs,
    // which may break brace matching in subsequent editor tests unless we pin our brace matcher explicitly.
    LanguageBraceMatching.INSTANCE.addExplicitExtension(ThriftLanguage.INSTANCE, new ThriftBraceMatcher());

    // Keep tests deterministic: some fixture types (e.g. ParsingTestCase) may tweak these global settings.
    CodeInsightSettings settings = CodeInsightSettings.getInstance();
    oldAutoInsertPairBracket = settings.AUTOINSERT_PAIR_BRACKET;
    settings.AUTOINSERT_PAIR_BRACKET = true;
  }

  @AfterEach
  void restoreAutoInsertPairBracket() {
    CodeInsightSettings.getInstance().AUTOINSERT_PAIR_BRACKET = oldAutoInsertPairBracket;
  }

  @Override
  protected String getTestDataPath() {
    return ThriftTestUtils.BASE_TEST_DATA_PATH + "/" + getRelativePath();
  }

  protected String getThriftTestName() {
    return getTestName(true) + "." + ThriftFileType.DEFAULT_EXTENSION;
  }
}
