package com.intellij.plugins.thrift.formatter;

import com.intellij.formatting.FormattingContext;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.WrapType;
import com.intellij.plugins.thrift.ThriftLanguage;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;

import static com.intellij.plugins.thrift.lang.lexer.ThriftTokenTypes.*;

public class ThriftFormattingBuilderModel implements FormattingModelBuilder {
  @Override
  public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
    CodeStyleSettings settings = formattingContext.getCodeStyleSettings();
    SpacingBuilder spacingBuilder = createSpacingBuilder(settings);
    final SimpleThriftBlock block = new SimpleThriftBlock(formattingContext.getNode(), Wrap.createWrap(WrapType.NONE, false), null, spacingBuilder);
    return FormattingModelProvider.createFormattingModelForPsiFile(formattingContext.getContainingFile(), block, settings);
  }

  static @NotNull SpacingBuilder createSpacingBuilder(CodeStyleSettings settings) {
    final CommonCodeStyleSettings commonSettings = settings.getCommonSettings(ThriftLanguage.INSTANCE);

    return new SpacingBuilder(settings, ThriftLanguage.INSTANCE)
        .withinPair(LEFTBRACKET, RIGHTBRACKET).spaceIf(commonSettings.SPACE_WITHIN_BRACKETS, true)
        .withinPair(LEFTBRACE, RIGHTBRACE).spaceIf(commonSettings.SPACE_WITHIN_PARENTHESES, true)
        .withinPair(LEFTCURLYBRACE, RIGHTCURLYBRACE).spaceIf(commonSettings.SPACE_WITHIN_BRACES, true)
        .before(LEFTBRACKET).spaces(1)
        // Do not force a space before '(' globally (e.g. `f(` should stay as-is).
        .before(LEFTBRACE).spaces(0)
        .before(LEFTCURLYBRACE).spaces(1)
        .before(COLON).spaces(0)
        .after(COLON).spaces(1)
        // Field IDs are parsed as a composite element (e.g. `1:`), so enforce spacing after it as well.
        .after(FIELD_ID).spaces(1)
        .before(COMMA).spaces(0)
        .after(COMMA).spaces(1)
        .around(EQUALS).spaces(1);
  }
}
