package com.tmac.appsearch;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ListView mListView;
    private AppListAdapter mAdapter;
    private List<AppInfo> mFilterList = new ArrayList<>();
    private RealmResults<AppInfo> mResultListCache;
    private Realm mRealm;
    private RealmAsyncTask mTransactionAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        createRealm();
        initData();
    }

    private void initView() {
        setupListView();
        setupEditText();
    }

    private void initData() {
        if (!loadFromDB()) {
            refreshData();
        }
    }

    private boolean loadFromDB() {
        mResultListCache = mRealm.where(AppInfo.class).findAll();
        if (!mResultListCache.isEmpty()) {
            mAdapter.replaceData(mResultListCache);
            return true;
        } else {
            return false;
        }
    }

    private void refreshData() {
        mTransactionAsync = mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
                for (PackageInfo info : packages) {
                    String packageName = info.packageName;
                    Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                    if (intent == null) continue;
                    String appName = info.applicationInfo.loadLabel(getPackageManager()).toString();
                    Drawable icon = info.applicationInfo.loadIcon(getPackageManager());

                    AppInfo appInfo = realm.createObject(AppInfo.class);

                    appInfo.setPkgName(packageName);
                    appInfo.setAppName(appName);

                    Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    appInfo.setIconByte(stream.toByteArray());

//                    appInfo.setAppIcon(icon);
                    Matcher matcher = PinyinUtils.sChinesePattern.matcher(appName);
                    if (matcher.find()) {
                        StringBuilder builder = new StringBuilder("");
                        for (int i = 0; i < appName.length(); i++) {
                            char c = appName.charAt(i);

                            try {
                                String[] resultArray = PinyinHelper.toHanyuPinyinStringArray(c, PinyinUtils.getPinyinFormat());
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
                        appInfo.setPinyinIndex(appName.toLowerCase());
                    }
                    Log.d(TAG, "setPinyinIndex: " + appInfo.getPinyinIndex());
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                loadFromDB();
                Log.d(TAG, "onSuccess: transaction");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "onError: transaction");
            }
        });
    }

    private void createRealm() {
        mRealm = Realm.getInstance(new RealmConfiguration.Builder(this).build());
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
                mAdapter.replaceData(mResultListCache);
            } else {
                mFilterList.clear();
//                Pattern pattern = Pattern.compile(keyword);
                for (AppInfo model : mResultListCache) {
                    String index = model.getPinyinIndex();

                    // TODO: 2016/5/22 使用regex完善
                    if (index.contains(keyword)) {
                        mFilterList.add(model);
                    } else {
                        boolean containKey = false;
                        //对输入的每一个字符进行查找
                        for (int i = 0; i < keyword.length(); i++) {
                            if (index.contains(keyword.substring(i, i + 1))) {
                                containKey = true;
                            } else {
                                //主要有一个字符没有找到就跳过
                                containKey = false;
                                break;
                            }
                            //匹配过的字符跳过
//                            index = index.substring()
                        }
                        if (containKey) {
                            mFilterList.add(model);
                        }
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
                mAdapter.replaceData(mFilterList);
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
                startActivity(intent);
                finish();
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                boolean canLoad = scrollState != AbsListView.OnScrollListener.SCROLL_STATE_FLING;
                mAdapter.setCanLoadBitmap(canLoad);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mTransactionAsync != null && !mTransactionAsync.isCancelled()) {
            mTransactionAsync.cancel();
            mTransactionAsync = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTransactionAsync != null && !mTransactionAsync.isCancelled()) {
            mTransactionAsync.cancel();
            mTransactionAsync = null;
        }
        mRealm.close();
    }
}
