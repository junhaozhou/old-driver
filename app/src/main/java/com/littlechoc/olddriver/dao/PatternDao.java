package com.littlechoc.olddriver.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.littlechoc.olddriver.Application;
import com.littlechoc.olddriver.model.Pattern;
import com.littlechoc.olddriver.model.PatternCategory;
import com.littlechoc.olddriver.utils.JsonUtils;
import com.littlechoc.olddriver.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Junhao Zhou 2017/4/16
 */

public class PatternDao {

  public static final String TAG = "PatternDao";

  private static final String SP_NAME = "com.littlechoc.olddriver.pattern";

  private static final String KEY_CATEGORY = "category";

  private static final String KEY_IS_INIT = "is_init";

  private static final String PATTERN_PREFIX = "";

  ////////////// Init Begin

  private static final List<PatternCategory> DEFAULT_PATTERN_LIST = new ArrayList<>();

  private static final String[] COMMON_PATTERN_LIST = new String[]{
          "左转弯", "左急转弯", "右转弯", "右急转弯", "加速", "急加速", "刹车", "急刹车", "混合", "无"
  };

  static {
    PatternCategory commonCategory = new PatternCategory("common", PatternCategory.COMMON);
    PatternCategory sensorCategory = new PatternCategory("sensor", PatternCategory.SENSOR);
    PatternCategory obdCategory = new PatternCategory("obd", PatternCategory.OBD);
    DEFAULT_PATTERN_LIST.add(commonCategory);
    DEFAULT_PATTERN_LIST.add(sensorCategory);
    DEFAULT_PATTERN_LIST.add(obdCategory);

    List<Pattern> patterns = new ArrayList<>();
    commonCategory.setPatternList(patterns);
    int index = 0;
    for (String patternName : COMMON_PATTERN_LIST) {
      Pattern pattern = new Pattern();
      pattern.setName(patternName);
      pattern.setId(generatePatternId(commonCategory.getId(), index++));
      pattern.setCategoryId(commonCategory.getId());
      patterns.add(pattern);
    }
  }

  private final List<PatternCategory> categoryList = new ArrayList<>();

  private static volatile PatternDao instance;

  public static PatternDao getInstance() {
    if (instance == null) {
      synchronized (PatternDao.class) {
        if (instance == null) {
          instance = new PatternDao();
        }
      }
    }
    return instance;
  }

  private PatternDao() {
    init();
  }

  private void init() {
    SharedPreferences preferences = getPreferences();
    boolean isInit = preferences.getBoolean(KEY_IS_INIT, false);
    if (!isInit) {
      initDefaultData();
    } else {
      loadData();
    }
  }

  private void initDefaultData() {
    categoryList.addAll(DEFAULT_PATTERN_LIST);
    SharedPreferences.Editor editor = getPreferences().edit();
    JsonArray categoryArray = new JsonArray();
    for (PatternCategory patternCategory : DEFAULT_PATTERN_LIST) {
      JsonObject jsonObject = new JsonObject();
      categoryArray.add(jsonObject);
      jsonObject.addProperty("name", patternCategory.getName());
      jsonObject.addProperty("id", patternCategory.getId());

      List<Pattern> patterns = patternCategory.getPatternList();
      Gson gson = new Gson();
      editor.putString(PATTERN_PREFIX + patternCategory.getId(), gson.toJson(patterns));
    }
    editor.putString(KEY_CATEGORY, categoryArray.toString());
    editor.putBoolean(KEY_IS_INIT, true);
    SpUtils.commit(editor);
  }

  private void loadData() {
    SharedPreferences preferences = getPreferences();
    String categoryJson = preferences.getString(KEY_CATEGORY, "");
    if (!TextUtils.isEmpty(categoryJson)) {
      List<PatternCategory> categories;
      categories = JsonUtils.newInstance().fromJson(categoryJson, new TypeToken<List<PatternCategory>>() {
      }.getType());
      if (categories == null) {
        categories = new ArrayList<>();
      }
      categoryList.addAll(categories);
      for (PatternCategory category : categories) {
        category.setPatternList(loadPatterns(category.getId()));
      }
    }
  }

  private List<Pattern> loadPatterns(int categoryId) {
    List<Pattern> patterns;
    SharedPreferences preferences = getPreferences();
    String patternJson = preferences.getString(PATTERN_PREFIX + categoryId, "");
    if (!TextUtils.isEmpty(patternJson)) {
      patterns = JsonUtils.newInstance().fromJson(patternJson, new TypeToken<List<Pattern>>() {
      }.getType());
    } else {
      patterns = new ArrayList<>();
    }
    return patterns;
  }

  public void reset() {
    SharedPreferences.Editor editor = getPreferences().edit();
    editor.putBoolean(KEY_IS_INIT, false);
    SpUtils.commit(editor);
  }

  //////////////// Init End

  public List<PatternCategory> getCategoryList() {
    return categoryList;
  }

  /**
   * @param categoryId {@link PatternCategory#COMMON}, {@link PatternCategory#OBD}, {@link PatternCategory#SENSOR} or {@link PatternCategory#getId()}
   * @return pattern list
   */
  public List<Pattern> getPatternListByCategory(int categoryId) {
    for (PatternCategory category : categoryList) {
      if (category.getId() == categoryId) {
        return category.getPatternList();
      }
    }
    return new ArrayList<>();
  }

  /**
   * @param categoryName category name
   */
  public void addNewPatternCategory(String categoryName) {
    List<PatternCategory> categories = getCategoryList();
    int id = categories.size();
    PatternCategory category = new PatternCategory(categoryName, id);
    category.setPatternList(new ArrayList<Pattern>());
    categoryList.add(category);
    saveCategories();
  }

  /**
   * @param categoryId  category id
   * @param patternName patternName
   */
  public void addNewPattern(int categoryId, String patternName) {
    List<Pattern> patterns = getPatternListByCategory(categoryId);
    Pattern pattern = new Pattern();
    pattern.setCategoryId(categoryId);
    pattern.setId(generatePatternId(categoryId, patterns.size()));
    pattern.setName(patternName);
    patterns.add(pattern);

    savePatterns(categoryId);
  }

  /**
   * save
   */
  public void saveCategories() {
    SharedPreferences.Editor editor = getPreferences().edit();
    JsonArray categoryArray = new JsonArray();
    for (PatternCategory patternCategory : DEFAULT_PATTERN_LIST) {
      JsonObject jsonObject = new JsonObject();
      categoryArray.add(jsonObject);
      jsonObject.addProperty("name", patternCategory.getName());
      jsonObject.addProperty("id", patternCategory.getId());
    }
    editor.putString(KEY_CATEGORY, categoryArray.toString());
    SpUtils.commit(editor);
  }

  /**
   * @param categoryId category id
   */
  public void savePatterns(int categoryId) {
    List<Pattern> patterns = getPatternListByCategory(categoryId);
    Gson gson = new Gson();
    SharedPreferences.Editor editor = getPreferences().edit();
    editor.putString(PATTERN_PREFIX + categoryId, gson.toJson(patterns));
    SpUtils.commit(editor);
  }

  /**
   * @param id pattern id
   * @return pattern
   */
  public Pattern getPatternById(int id) {
    if (id == Pattern.UNKNOWN.getId()) {
      return Pattern.UNKNOWN;
    }
    int categoryId = getCategoryIdByPatternId(id);
    List<Pattern> patterns = getPatternListByCategory(categoryId);
    for (Pattern pattern : patterns) {
      if (pattern.getId() == id) {
        return pattern;
      }
    }
    return Pattern.UNKNOWN;
  }

  /**
   * mask of category id in pattern id
   */
  private static final int CATEGORY_MASK = 100;

  /**
   * @param categoryId category id
   * @param index      index of pattern in category
   * @return generated pattern id
   */
  private static int generatePatternId(int categoryId, int index) {
    return categoryId * CATEGORY_MASK + index;
  }

  /**
   * @param patternId pattern id
   * @return id of category which pattern belongs
   */
  private static int getCategoryIdByPatternId(int patternId) {
    return patternId / CATEGORY_MASK;
  }

  private static SharedPreferences getPreferences() {
    return Application.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
  }
}
