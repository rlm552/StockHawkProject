package com.udacity.stockhawk.widgets;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.udacity.stockhawk.R;

/**
 * Created by Rory on 2/20/2017.
 */

public class StockAppWidgetConfigure extends Activity {
    private static final String PREFS_NAME
            = "com.udacity.stockhawk.widgets.StockAppWidgetProvider";

    private static final String PREF_PREFIX_KEY = "prefix_";
    public static final String ABSOLUTE_CHANGE = "com.udacity.stockhawk.widgets.StockAppWidgetConfigure.ABSOLUTE_CHANGE";
    private static final String PERCENT_CHANGE = "com.udacity.stockhawk.widgets.StockAppWidgetConfigure.PERCENT_CHANGE";

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Intent resultValue;
    private boolean configured = false;

    public StockAppWidgetConfigure(){ super(); }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);

        setContentView(R.layout.appwidget_confgure);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_CANCELED, resultValue);

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
            finish();
        }

    }

    public void onRadioButtonClicked(View view){
        final Context context = this;
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()){
            case R.id.widget_percent_change:
                if (checked)
                    saveFormatPref(context, mAppWidgetId, PERCENT_CHANGE);
                break;
            case R.id.widget_dollar_change:
                if (checked)
                    saveFormatPref(context, mAppWidgetId, ABSOLUTE_CHANGE);
                break;
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        StockAppWidgetProvider.updateAppWidget(context, appWidgetManager,
                mAppWidgetId);

        setResult(RESULT_OK, resultValue);
        configured = true;
        finish();
    }

    private static void saveFormatPref(Context context, int appWidgetId, String text){
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    static String loadFormatPref(Context context, int appWidgetId){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String format = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (format != null){
            return format;
        }else {
            return context.getString(R.string.appwidget_default);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
    }

    @Override
    public void onBackPressed(){
        Toast.makeText(this, this.getString(R.string.widget_config_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        if (!configured){
            ComponentName cm = new ComponentName(this, StockAppWidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(cm);
            AppWidgetHost appWidgetHost = new AppWidgetHost(this, 0);
            for (int appWidgetId : appWidgetIds){
                appWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
        super.onDestroy();
    }

}
