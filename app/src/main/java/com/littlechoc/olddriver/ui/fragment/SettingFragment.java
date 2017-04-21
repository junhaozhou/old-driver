package com.littlechoc.olddriver.ui.fragment;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;

import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.dao.PatternDao;
import com.littlechoc.olddriver.model.Pattern;
import com.littlechoc.olddriver.model.PatternCategory;

import java.util.List;

/**
 * @author Junhao Zhou 2017/4/18
 */

public class SettingFragment extends PreferenceFragment {

  public static SettingFragment newInstance() {

    Bundle args = new Bundle();

    SettingFragment fragment = new SettingFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addPreferencesFromResource(R.xml.setting_preferences);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    initPatternPreference();
  }

  private void initPatternPreference() {
    List<PatternCategory> patternCategories = PatternDao.getInstance().getCategoryList();
    PreferenceScreen preferenceScreen
            = (PreferenceScreen) getPreferenceScreen().findPreference("key_edit_pattern");

    PreferenceCategory newPatternCategory = new PreferenceCategory(getActivity());
    newPatternCategory.setTitle("添加新模式");
    preferenceScreen.addPreference(newPatternCategory);

    ListPreference listPreference = new ListPreference(getActivity());
    listPreference.setTitle("选择分类");
    String[] patternCategoryName = new String[patternCategories.size()];
    for (int i = 0; i < patternCategories.size(); i++) {
      patternCategoryName[i] = patternCategories.get(i).getName();
    }
    listPreference.setEntries(patternCategoryName);
    newPatternCategory.addPreference(listPreference);

    EditTextPreference editTextPreference = new EditTextPreference(getActivity());
    editTextPreference.setTitle("模式名");
    newPatternCategory.addPreference(editTextPreference);

    for (PatternCategory patternCategory : patternCategories) {
      PreferenceCategory preferenceCategory = new PreferenceCategory(getActivity());
      preferenceCategory.setTitle(patternCategory.getName());
      preferenceScreen.addPreference(preferenceCategory);
      List<Pattern> patterns = patternCategory.getPatternList();
      for (Pattern pattern : patterns) {
        Preference preference = new Preference(getActivity());
        preference.setTitle(pattern.getName());
        preferenceCategory.addPreference(preference);
      }
    }
  }
}
