package com.intellij.plugins.thrift.resolve;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.plugins.thrift.ThriftCodeInsightFixtureTestCase;
import com.intellij.plugins.thrift.ThriftFileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.EdtTestUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by fkorotkov.
 */
public class ThriftResolveTest extends ThriftCodeInsightFixtureTestCase {
  @Override
  @NotNull
  protected String getRelativePath() {
    return "resolve";
  }

  protected Collection<PsiElement> doTest(int expectedSize) {
    PsiReference reference = getFixture().getReferenceAtCaretPosition();
    assertNotNull(reference, "no reference");
    final Collection<PsiElement> elements = EdtTestUtil.runInEdtAndGet(() -> TargetElementUtil.getInstance().getTargetCandidates(reference));
    assertNotNull(elements);
    assertEquals(expectedSize, elements.size());
    return elements;
  }

  private void configureDefault() {
    getFixture().configureByFile(getTestName(true) + "." + ThriftFileType.DEFAULT_EXTENSION);
  }

  @Test
  public void testInclude() {
    getFixture().addFileToProject("foo.thrift", "");
    getFixture().configureByText(ThriftFileType.INSTANCE, "include 'foo<caret>.thrift'");
    doTest(1);
  }

  @Test
  public void testGlobalType1() {
    getFixture().addFileToProject("data.thrift", "struct Impression {}");
    configureDefault();
    doTest(1);
  }

  @Test
  public void testGlobalType2() {
    getFixture().addFileToProject("util/data.thrift", "struct Impression {}");
    configureDefault();
    doTest(1);
  }

  @Test
  public void testGlobalType3() {
    getFixture().addFileToProject("util/data.thrift", "struct Impression {}");
    configureDefault();
    Collection<PsiElement> elements = doTest(1);
    assertInstanceOf(PsiFile.class, elements.iterator().next());
  }

  @Test
  public void testGlobalType4() {
    getFixture().addFileToProject("data.thrift", "struct Impression {}");
    configureDefault();
    doTest(1);
  }

  @Test
  public void testLocalType() {
    configureDefault();
    doTest(1);
  }
}
