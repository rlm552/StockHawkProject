package com.udacity.stockhawk.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by Rory on 2/13/2017.
 */

public class StockAppWidgetProvider extends AppWidgetProvider{
    public static final String TOAST_ACTION = "com.udacity.stockhawk.widgets.TOAST_ACTION";
    public static final String EXTRA_ITEM = "com.udacity.stockhawk.widgets.EXTRA_ITEM";
    public static final String PREF_FORMAT = "com.udacity.stockhawk.widgets.PREF_FORMAT";

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int i = 0; i < appWidgetIds.length; i++){
            StockAppWidgetConfigure.deleteTitlePref(context, appWidgetIds[i]);
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) { super.onReceive(context, intent);  }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        for (int i = 0; i < appWidgetIds.length; ++i) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId){
        String format = StockAppWidgetConfigure.loadFormatPref(context, appWidgetId);

        Intent intent = new Intent(context, StockAppWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra(PREF_FORMAT, format);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget);
        rv.setRemoteAdapter(appWidgetId, R.id.list_view, intent);
        rv.setEmptyView(R.id.list_view, R.id.empty_view);

        // Adding collection list item handler
        final Intent onItemClick = new Intent(context, StockAppWidgetProvider.class);
        onItemClick.setAction(TOAST_ACTION);
        onItemClick.setData(Uri.parse(onItemClick
                .toUri(Intent.URI_INTENT_SCHEME)));
        final PendingIntent onClickPendingIntent = PendingIntent
                .getBroadcast(context, 0, onItemClick,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.list_view,
                onClickPendingIntent);

        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
        rv.setOnClickPendingIntent(R.id.widget_price, pendingIntent);
        //rv.setPendingIntentTemplate(R.id.list_view, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, rv);

    }
}
