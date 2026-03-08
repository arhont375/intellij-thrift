package com.intellij.plugins.thrift.formatter;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

public class ThriftCodeStyleSettings extends CustomCodeStyleSettings {
  /**
   * Wrap style for curly-enclosed block bodies.
   */
  public int CURLY_BLOCK_WRAP = CommonCodeStyleSettings.WRAP_ALWAYS;

  /**
   * Wrap style for throws(...) block entries.
   */
  public int THROWS_WRAP = CommonCodeStyleSettings.DO_NOT_WRAP;

  /**
   * Wrap style for function arguments. Uses CommonCodeStyleSettings.WRAP_* constants.
   */
  public int FUNCTION_ARGUMENTS_WRAP = CommonCodeStyleSettings.DO_NOT_WRAP;

  /**
   * Wrap style for annotation entries in (...).
   */
  public int ANNOTATION_WRAP = CommonCodeStyleSettings.DO_NOT_WRAP;

  /**
   * Wrap style for const list entries in [...].
   */
  public int CONST_LIST_WRAP = CommonCodeStyleSettings.DO_NOT_WRAP;

  public ThriftCodeStyleSettings(CodeStyleSettings container) {
    super("ThriftCodeStyleSettings", container);
  }
}
