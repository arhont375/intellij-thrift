package com.intellij.plugins.thrift.completion;

import com.intellij.plugins.thrift.ThriftFileType;
import org.junit.jupiter.api.Test;

/**
 * Created by fkorotkov.
 */
public class ThriftReferenceCompletionTest extends ThriftCompletionTestBase {
  public ThriftReferenceCompletionTest() {
    super("completion/reference");
  }

  @Test
  public void testInclude1() {
    getFixture().addFileToProject("bar.thrift", "");
    getFixture().addFileToProject("baz.thrift", "");
    getFixture().configureByText(ThriftFileType.INSTANCE, "include 'b<caret>'");
    getFixture().completeBasic();
    checkCompletion(CheckType.INCLUDES, "bar.thrift", "baz.thrift");
  }

  @Test
  public void testInclude2() {
    getFixture().addFileToProject("foo/bar.thrift", "");
    getFixture().addFileToProject("foo/baz.thrift", "");
    getFixture().configureByText(ThriftFileType.INSTANCE, "include 'foo/b<caret>'");
    getFixture().completeBasic();
    checkCompletion(CheckType.INCLUDES, "bar.thrift", "baz.thrift");
  }

  @Test
  public void testInclude3() {
    getFixture().addFileToProject("foo/bar.thrift", "");
    getFixture().addFileToProject("foo/baz.thrift", "");
    getFixture().configureByFile("include/includeRelative1.thrift");
    getFixture().completeBasic();
    checkCompletion(CheckType.INCLUDES, "bar.thrift", "baz.thrift");
  }

  @Test
  public void testInclude4() {
    getFixture().addFileToProject("include/bar.thrift", "");
    getFixture().addFileToProject("include/baz.thrift", "");
    getFixture().configureByFile("include/includeRelative2.thrift");
    getFixture().completeBasic();
    checkCompletion(CheckType.INCLUDES, "bar.thrift", "baz.thrift");
  }

  @Test
  public void testExtends() throws Throwable {
    doTest();
  }

  @Test
  public void testSameFile() throws Throwable {
    doTest();
  }

  @Test
  public void testIncludedFile() throws Throwable {
    getFixture().copyFileToProject("sameFile.thrift", "util/included.thrift");
    doTest();
  }
}
