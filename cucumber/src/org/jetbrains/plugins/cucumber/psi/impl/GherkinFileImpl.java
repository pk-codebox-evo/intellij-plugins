package org.jetbrains.plugins.cucumber.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.cucumber.psi.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yole
 */
public class GherkinFileImpl extends PsiFileBase implements GherkinFile {
  public GherkinFileImpl(FileViewProvider viewProvider) {
    super(viewProvider, GherkinLanguage.INSTANCE);
  }

  @NotNull
  public FileType getFileType() {
    return GherkinFileType.INSTANCE;
  }

  @Override
  public String toString() {
    return "GherkinFile:" + getName();
  }

  public List<String> getStepKeywords() {
    final GherkinKeywordProvider provider = CucumberLanguageService.getInstance(getProject()).getKeywordProvider();
    List<String> result = new ArrayList<String>();

    // find language comment
    final String language = getLocaleLanguage();

    // step keywords
    final GherkinKeywordTable table = provider.getKeywordsTable(language);
    result.addAll(table.getStepKeywords());

    return result;
  }

  public String getLocaleLanguage() {
    final ASTNode node = getNode();
    assert node != null;
    
    final ASTNode comment = node.findChildByType(GherkinTokenTypes.COMMENT);
    if (comment != null) {
      // remove '#' sign and spaces
      final String text = comment.getText().substring(1).trim();

      final String lang = GherkinLexer.fetchLocationLanguage(text);
      if (lang != null) {
        return lang;
      }
    }
    return getDefaultLocale();
  }

  public static String getDefaultLocale() {
    return "en";
  }

  @Override
  public PsiElement findElementAt(int offset) {
    PsiElement result = super.findElementAt(offset);
    if (result == null && offset == getTextLength()) {
      final PsiElement last = getLastChild();
      result = last != null ? last.getLastChild() : last;
    }
    return result;
  }
}
