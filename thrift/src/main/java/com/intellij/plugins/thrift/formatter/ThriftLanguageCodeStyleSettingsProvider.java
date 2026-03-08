package com.intellij.plugins.thrift.formatter;

import com.intellij.lang.Language;
import com.intellij.plugins.thrift.ThriftLanguage;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThriftLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
  @Override
  public @NotNull Language getLanguage() {
    return ThriftLanguage.INSTANCE;
  }

  @Override
  public @Nullable CustomCodeStyleSettings createCustomSettings(@NotNull CodeStyleSettings settings) {
    return new ThriftCodeStyleSettings(settings);
  }

  @Override
  public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
    if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) {
      consumer.showCustomOption(
          ThriftCodeStyleSettings.class,
          "CURLY_BLOCK_WRAP",
          "Curly blocks",
          CodeStyleSettingsCustomizable.WRAPPING_BRACES,
          CodeStyleSettingsCustomizable.WRAP_OPTIONS,
          CodeStyleSettingsCustomizable.WRAP_VALUES
      );
      consumer.showCustomOption(
          ThriftCodeStyleSettings.class,
          "THROWS_WRAP",
          "Throws block",
          CodeStyleSettingsCustomizable.WRAPPING_BRACES,
          CodeStyleSettingsCustomizable.WRAP_OPTIONS,
          CodeStyleSettingsCustomizable.WRAP_VALUES
      );
      consumer.showCustomOption(
          ThriftCodeStyleSettings.class,
          "FUNCTION_ARGUMENTS_WRAP",
          "Function arguments",
          CodeStyleSettingsCustomizable.WRAPPING_BRACES,
          CodeStyleSettingsCustomizable.WRAP_OPTIONS,
          CodeStyleSettingsCustomizable.WRAP_VALUES
      );
      consumer.showCustomOption(
          ThriftCodeStyleSettings.class,
          "ANNOTATION_WRAP",
          "Annotations",
          CodeStyleSettingsCustomizable.WRAPPING_BRACES,
          CodeStyleSettingsCustomizable.WRAP_OPTIONS,
          CodeStyleSettingsCustomizable.WRAP_VALUES
      );
      consumer.showCustomOption(
          ThriftCodeStyleSettings.class,
          "CONST_LIST_WRAP",
          "Const list",
          CodeStyleSettingsCustomizable.WRAPPING_BRACES,
          CodeStyleSettingsCustomizable.WRAP_OPTIONS,
          CodeStyleSettingsCustomizable.WRAP_VALUES
      );
    }
  }

  @Override
  public @NotNull String getCodeSample(@NotNull SettingsType settingsType) {
    return "service Calculator {\n"
        + "  i32 add(\n"
        + "    1: i32 left,\n"
        + "    2: i32 right,\n"
        + "    3: optional string traceId\n"
        + "  ) throws (\n"
        + "    1: InvalidOperation error\n"
        + "  )\n"
        + "}\n";
  }
}
