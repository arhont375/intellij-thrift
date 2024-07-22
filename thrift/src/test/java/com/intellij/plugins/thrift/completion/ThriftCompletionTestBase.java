package com.intellij.plugins.thrift.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.util.text.CharFilter;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugins.thrift.ThriftCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by fkorotkov.
 */
abstract public class ThriftCompletionTestBase extends ThriftCodeInsightFixtureTestCase {
  enum CheckType {EQUALS, INCLUDES, EXCLUDES}

  private final String myPath;

  public ThriftCompletionTestBase(String... path) {
    myPath = getPath(path);
  }

  private static String getPath(String... args) {
    final StringBuilder result = new StringBuilder();
    for (String folder : args) {
      result.append("/");
      result.append(folder);
    }
    return result.toString();
  }

  @Override
  @NotNull
  protected String getRelativePath() {
    return myPath;
  }

  protected void doTest() throws Throwable {
    getFixture().configureByFile(getTestName(true) + ".thrift");
    doTestVariantsInner(getTestName(true) + ".txt");
  }

  protected void doTest(char charToType) {
    getFixture().configureByFile(getTestName(true) + ".thrift");
    getFixture().type(charToType);
    getFixture().checkResultByFile(getTestName(true) + ".txt");
  }

  protected void doTestVariantsInner(String fileName) throws Throwable {
    final VirtualFile virtualFile = getFixture().copyFileToProject(fileName);
    final Scanner in = new Scanner(virtualFile.getInputStream());

    final CompletionType type = CompletionType.valueOf(in.next());
    final int count = in.nextInt();
    final CheckType checkType = CheckType.valueOf(in.next());

    final List<String> variants = new ArrayList<String>();
    while (in.hasNext()) {
      final String variant = StringUtil.strip(in.next(), CharFilter.NOT_WHITESPACE_FILTER);
      if (variant.length() > 0) {
        variants.add(variant);
      }
    }

    getFixture().complete(type, count);
    checkCompletion(checkType, variants);
  }

  protected void checkCompletion(CheckType checkType, String... variants) {
    checkCompletion(checkType, new ArrayList<String>(Arrays.asList(variants)));
  }

  protected void checkCompletion(CheckType checkType, List<String> variants) {
    List<String> stringList = getFixture().getLookupElementStrings();
    if (stringList == null) {
      stringList = Collections.emptyList();
    }

    if (checkType == CheckType.EQUALS) {
      assertThat(stringList).containsExactlyElementsOf(variants);
    }
    else if (checkType == CheckType.INCLUDES) {
      variants.removeAll(stringList);
      assertTrue(variants.isEmpty(), "Missing variants: " + variants);
    }
    else if (checkType == CheckType.EXCLUDES) {
      variants.retainAll(stringList);
      assertTrue(variants.isEmpty(), "Unexpected variants: " + variants);
    }
  }
}
