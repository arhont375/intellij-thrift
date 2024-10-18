package com.intellij.plugins.thrift.formatter;

import com.intellij.formatting.*;
import com.intellij.plugins.thrift.ThriftLanguage;
import com.intellij.plugins.thrift.lang.lexer.ThriftTokenTypes;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;

final class ThriftFormattingModelBuilder implements FormattingModelBuilder {
  @Override
  public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
    PsiFile file = formattingContext.getContainingFile();
    CodeStyleSettings codeStyleSettings = formattingContext.getCodeStyleSettings();
    ThriftBlockContext context = new ThriftBlockContext(createSpaceBuilder(codeStyleSettings));
    return FormattingModelProvider.createFormattingModelForPsiFile(
        file,
        new ThriftBlock(
            formattingContext.getNode(),
            null,
            null,
            context
        ),
        codeStyleSettings
    );
  }

  private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
    return new SpacingBuilder(settings, ThriftLanguage.INSTANCE)
        // Include
        .betweenInside(ThriftTokenTypes.IDENTIFIER, ThriftTokenTypes.LITERAL, ThriftTokenTypes.INCLUDE).spaces(1)
        .around(ThriftTokenTypes.EQUALS).spaces(1)
        // Struct
        .between(ThriftTokenTypes.FIELD_ID, ThriftTokenTypes.FIELD_REQ).spaces(1)
        .between(ThriftTokenTypes.FIELD_ID, ThriftTokenTypes.FIELD_TYPE).spaces(1)
        .between(ThriftTokenTypes.FIELD_REQ, ThriftTokenTypes.FIELD_TYPE).spaces(1)
        .between(ThriftTokenTypes.FIELD_TYPE, ThriftTokenTypes.DEFINITION_NAME).spaces(1)
        .between(ThriftTokenTypes.DEFINITION_NAME, ThriftTokenTypes.COMMENT).spaces(1)
        .between(ThriftTokenTypes.FIELD, ThriftTokenTypes.COMMENT).spaces(1)
        .between(ThriftTokenTypes.FIELDS, ThriftTokenTypes.COMMENT).spaces(1)
        // Service
        .between(ThriftTokenTypes.FUNCTION_TYPE, ThriftTokenTypes.DEFINITION_NAME).spaces(1)
        .before(ThriftTokenTypes.LEFTCURLYBRACE).spaces(1);
  }
}
