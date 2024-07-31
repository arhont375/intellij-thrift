package com.intellij.plugins.thrift.usages;

import com.intellij.plugins.thrift.ThriftCodeInsightFixtureTestCase;
import com.intellij.plugins.thrift.ThriftFileType;
import com.intellij.usages.Usage;
import org.awaitility.Awaitility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ThriftFindUsagesTest extends ThriftCodeInsightFixtureTestCase {
  @Override
  @NotNull
  protected String getRelativePath() {
    return "usages";
  }

  @BeforeEach
  void setUp() {
    getFixture().configureByFile(getTestName(true) + "." + ThriftFileType.DEFAULT_EXTENSION);
  }

  protected void doTest(int size) throws Throwable {
    Awaitility
        .await()
        .atMost(Duration.ofSeconds(10))
        .untilAsserted(() -> {
          final Collection<Usage> elements = getFixture().testFindUsagesUsingAction();
          assertNotNull(elements);
          assertEquals(size, elements.size());
        });
  }

  @Test
  public void testUsages1() throws Throwable {
    doTest(2);
  }

  @Test
  public void testUsages2() throws Throwable {
    doTest(2);
  }
}
