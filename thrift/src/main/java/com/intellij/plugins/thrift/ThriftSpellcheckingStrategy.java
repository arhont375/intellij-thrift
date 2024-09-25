package com.intellij.plugins.thrift;

import com.intellij.plugins.thrift.lang.psi.ThriftEnumField;
import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import org.jetbrains.annotations.NotNull;

public final class ThriftSpellcheckingStrategy extends SpellcheckingStrategy {
  @Override
  public @NotNull Tokenizer getTokenizer(PsiElement element) {
    PsiElement parent = element.getParent();
    if (parent instanceof ThriftEnumField && ((ThriftEnumField) parent).getIdentifier() == element) {
      return SpellcheckingStrategy.TEXT_TOKENIZER;
    }
    return super.getTokenizer(element);
  }
}
