package com.intellij.plugins.thrift.formatter;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.lang.Language;
import com.intellij.plugins.thrift.ThriftLanguage;
import com.intellij.psi.codeStyle.CodeStyleConfigurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThriftCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
  @Override
  public CustomCodeStyleSettings createCustomSettings(@NotNull CodeStyleSettings settings) {
    return new ThriftCodeStyleSettings(settings);
  }

  @Nullable
  @Override
  public Language getLanguage() {
    return ThriftLanguage.INSTANCE;
  }

  @Override
  public String getConfigurableDisplayName() {
    return ThriftLanguage.INSTANCE.getDisplayName();
  }

  @Override
  public CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings settings, @NotNull CodeStyleSettings modelSettings) {
    return new CodeStyleAbstractConfigurable(settings, modelSettings, getConfigurableDisplayName()) {
      @Override
      protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
        return new TabbedLanguageCodeStylePanel(ThriftLanguage.INSTANCE, getCurrentSettings(), settings) {};
      }
    };
  }
}
