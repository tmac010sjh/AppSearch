package com.tmac.appsearch;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String CHINESE_REGEX = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$";
    private static final Pattern sChinesePattern = Pattern.compile(CHINESE_REGEX);

    private static final HanyuPinyinOutputFormat sPinyinFormat = new HanyuPinyinOutputFormat();

    static {
        sPinyinFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        sPinyinFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    private static final String TAG = "MainActivity";
    private ListView mListView;
    private AppListAdapter mAdapter;
    private List<AppInfo> mFilterList = new ArrayList<>();
    private List<AppInfo> mResultListCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupListView();
        new LoadAppListTask().execute();
        setupEditText();
    }

    private void setupEditText() {
        EditText editText = (EditText) findViewById(R.id.edit_text);
        editText.addTextChangedListener(new MyTextWatcher());
    }

    private class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mResultListCache == null) {
                return;
            }
            String keyword = s.toString();
            if (TextUtils.isEmpty(keyword)) {
                mAdapter.updateData(mResultListCache);
            } else {
                mFilterList.clear();
//                Pattern pattern = Pattern.compile(keyword);
                for (AppInfo model : mResultListCache) {
                    if (model.getPinyinIndex().contains(keyword)) {
                        mFilterList.add(model);
                    }
//                        if (keyword.matches("^[a-zA-Z]*")) {
//                            Matcher matcher = pattern.matcher(model.getPinyinIndex());
//                            if (matcher.find()) {
//                                mFilterList.add(model);
//                            }
//                    }else{
//                        Matcher matcher = pattern.matcher(model.getAppName());
//                        if (matcher.find()) {
//                            mFilterList.add(model);
//                        }
//                    }
                }
                mAdapter.updateData(mFilterList);
            }
        }
    }


    private void setupListView() {
        mListView = (ListView) findViewById(R.id.list_view);
        mAdapter = new AppListAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo item = (AppInfo) mAdapter.getItem(position);
                Intent intent = getPackageManager().getLaunchIntentForPackage(item.getPkgName());
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
    }

    private class LoadAppListTask extends AsyncTask<Void, Void, List<AppInfo>> {

        @Override
        protected List<AppInfo> doInBackground(Void... params) {
            List<AppInfo> appInfoList = new ArrayList<>();
            List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
            for (PackageInfo info : packages) {
                String packageName = info.packageName;
                Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent == null) continue;
                String appName = info.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable icon = info.applicationInfo.loadIcon(getPackageManager());
                AppInfo appInfo = new AppInfo();
                appInfo.setPkgName(packageName);
                appInfo.setAppName(appName);
                appInfo.setAppIcon(icon);

                Matcher matcher = sChinesePattern.matcher(appName);
                if (matcher.find()) {
                    StringBuilder builder = new StringBuilder("");
                    for (int i = 0; i < appName.length(); i++) {
                        char c = appName.charAt(i);

                        try {
                            String[] resultArray = PinyinHelper.toHanyuPinyinStringArray(c, sPinyinFormat);
                            if (resultArray.length != 0) {
                                for (String value : resultArray) {
                                    builder.append(value);
                                }
                            }
                        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                            badHanyuPinyinOutputFormatCombination.printStackTrace();
                        }
                    }
                    appInfo.setPinyinIndex(builder.toString());
                } else {
                    appInfo.setPinyinIndex(appName);
                }
                Log.d(TAG, "setPinyinIndex: " + appInfo.getPinyinIndex());
                appInfoList.add(appInfo);
            }
            //就不排序了吧。。。。
//            Collections.sort(appInfoList, new Comparator<AppInfo>() {
//                @Override
//                public int compare(AppInfo a, AppInfo b) {
//                    return String
//                            .CASE_INSENSITIVE_ORDER
//                            .compare(a.getAppName(), b.appName);
//                }
//            });
            return appInfoList;
        }

        @Override
        protected void onPostExecute(List<AppInfo> appInfoList) {
            mResultListCache = appInfoList;
            mAdapter.updateData(appInfoList);
        }
    }
}
