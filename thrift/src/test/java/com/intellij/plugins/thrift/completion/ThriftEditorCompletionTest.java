package com.intellij.plugins.thrift.completion;

import com.intellij.plugins.thrift.ThriftCodeInsightFixtureTestCase;
import com.intellij.plugins.thrift.ThriftFileType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

/**
 * Created by fkorotkov.
 */
public class ThriftEditorCompletionTest extends ThriftCodeInsightFixtureTestCase {
  @Override
  @NotNull
  protected String getRelativePath() {
    return "completion/editor";
  }

  @Test
  public void testTypeParam(){
    getFixture().configureByText(ThriftFileType.INSTANCE, "struct Foo {1: list<caret>}");
    getFixture().type('<');
    getFixture().checkResult("struct Foo {1: list<<caret>>}");
  }

  @Test
  public void testExistingClosingBrace1(){
    getFixture().configureByText(ThriftFileType.INSTANCE, "struct Foo {1: list<Foo<caret>>}");
    getFixture().type('>');
    getFixture().checkResult("struct Foo {1: list<Foo><caret>}");
  }

  @Test
  public void testExistingClosingBrace2(){
    getFixture().configureByText(ThriftFileType.INSTANCE, "struct Foo {1: list<Foo<caret>> list}");
    getFixture().type('>');
    getFixture().checkResult("struct Foo {1: list<Foo><caret> list}");
  }

  @Test
  public void testExistingCurlyBrace(){
    getFixture().configureByText(ThriftFileType.INSTANCE, "service Foo {<caret>}");
    getFixture().type('}');
    getFixture().checkResult("service Foo {}<caret>");
  }
}
