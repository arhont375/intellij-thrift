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
  public void testFormatter() throws Throwable {
    getFixture().configureByFile(getTestDataPath() + "/FormatterTestBefore.thrift");
    WriteCommandAction.runWriteCommandAction(getFixture().getProject(), () -> {
      CodeStyleManager.getInstance(getFixture().getProject()).reformat(getFixture().getFile());
    });
    String firstPass = getFixture().getFile().getText();
    WriteCommandAction.runWriteCommandAction(getFixture().getProject(), () -> {
      CodeStyleManager.getInstance(getFixture().getProject()).reformat(getFixture().getFile());
    });
    Assertions.assertEquals(firstPass, getFixture().getFile().getText(), "Formatter must be idempotent");
    String content = new String(Files.readAllBytes(Paths.get(getTestDataPath() + "/FormatterTestAfter.thrift")), StandardCharsets.UTF_8);
    Assertions.assertEquals(content, getFixture().getFile().getText());
  }
}
