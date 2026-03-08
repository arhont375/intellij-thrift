package com.intellij.plugins.thrift.formatter;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.plugins.thrift.ThriftCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ThriftFormatterTest extends ThriftCodeInsightFixtureTestCase {
  @NotNull
  @Override
  protected String getRelativePath() {
    return "formatter";
  }

  @Test
  public void testFormatterDefault() throws Throwable {
    String beforeFile = "FormatterTestBefore.thrift";
    String afterFile = "FormatterTestAfter.thrift";

    getFixture().configureByFile(getTestDataPath() + "/" + beforeFile);

    WriteCommandAction.runWriteCommandAction(getFixture().getProject(), new Runnable() {
      @Override
      public void run() {
        CodeStyleManager.getInstance(getFixture().getProject()).reformat(getFixture().getFile());
      }
    });

    String firstPass = getFixture().getFile().getText();

    WriteCommandAction.runWriteCommandAction(getFixture().getProject(), new Runnable() {
      @Override
      public void run() {
        CodeStyleManager.getInstance(getFixture().getProject()).reformat(getFixture().getFile());
      }
    });

    Assertions.assertEquals(firstPass, getFixture().getFile().getText(), "Formatter must be idempotent");

    String expected = new String(
        Files.readAllBytes(Paths.get(getTestDataPath() + "/" + afterFile)),
        StandardCharsets.UTF_8
    );
    Assertions.assertEquals(expected, getFixture().getFile().getText());
  }
}
