package com.intellij.plugins.thrift.inspections;

import org.junit.jupiter.api.Test;

public class NameDuplicatesTest extends ThriftInspectionTestBase {
  public NameDuplicatesTest() {
    super("inspections/duplicates/name", ThriftNameDuplicatesInspection.class);

  }

  @Test
  public void testSenum() {
    doTest();
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
  public void testServiceMethodName() {
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
