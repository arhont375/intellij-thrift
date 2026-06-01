package com.intellij.plugins.thrift.formatter;

import com.intellij.plugins.thrift.ThriftLanguage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ThriftCodeStyleSettingsProviderTest {

  @Test
  public void testGetLanguage() {
    ThriftCodeStyleSettingsProvider provider = new ThriftCodeStyleSettingsProvider();
    Assertions.assertEquals(ThriftLanguage.INSTANCE, provider.getLanguage(), "getLanguage should return ThriftLanguage.INSTANCE");
  }

  @Test
  public void testGetConfigurableDisplayName() {
    ThriftCodeStyleSettingsProvider provider = new ThriftCodeStyleSettingsProvider();
    Assertions.assertEquals(ThriftLanguage.INSTANCE.getDisplayName(), provider.getConfigurableDisplayName());
  }
}
