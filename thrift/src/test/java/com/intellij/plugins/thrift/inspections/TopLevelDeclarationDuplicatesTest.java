package com.intellij.plugins.thrift.inspections;

import org.junit.jupiter.api.Test;

public class TopLevelDeclarationDuplicatesTest extends ThriftInspectionTestBase {
  public TopLevelDeclarationDuplicatesTest() {
    super("inspections/duplicates/topLevelDeclaration", ThriftTopLevelDeclarationDuplicatesInspection.class);
  }

  @Test
  public void testConst() {
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
  public void testSenum() {
    doTest();
  }

  @Test
  public void testService() {
    doTest();
  }

  @Test
  public void testStruct() {
    doTest();
  }

  @Test
  public void testTypedef() {
    doTest();
  }

  @Test
  public void testUnion() {
    doTest();
  }
}
