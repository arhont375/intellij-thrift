package com.intellij.plugins.thrift.inspections;

import com.intellij.spellchecker.inspections.SpellCheckingInspection;
import org.junit.jupiter.api.Test;

public class SpellcheckingTest extends ThriftInspectionTestBase {
  public SpellcheckingTest() {
    super("inspections/spell", SpellCheckingInspection.class);
  }

  @Test
  public void testUsages1() {
    doTest();
  }
}
