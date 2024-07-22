package com.intellij.plugins.thrift.inspections;

import org.junit.jupiter.api.Test;

public class IDDuplicatesTest extends ThriftInspectionTestBase {
  public IDDuplicatesTest() {
    super("inspections/duplicates/id", ThriftIDDuplicatesInspection.class);
  }

  @Test
  public void testEnum() {
    doTest();
  }

  @Test
  public void testException() {
    doTest();
  }

  @Test
  public void testServiceMethodArg() {
    doTest();
  }

  @Test
  public void testStruct() {
    doTest();
  }

  @Test
  public void testThrow() {
    doTest();
  }

  @Test
  public void testUnion() {
    doTest();
  }
}
