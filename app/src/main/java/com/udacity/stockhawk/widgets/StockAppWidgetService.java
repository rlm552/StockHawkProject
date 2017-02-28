package com.udacity.stockhawk.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rory on 2/15/2017.
 */

public class StockAppWidgetService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static int mCount;
    private List<WidgetItem> mWidgetItems = new ArrayList<WidgetItem>();
    private Context mContext;
    private int mAppWidgetId;
    private String formatPref;
    private Cursor mCursor;

    public ListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        formatPref = intent.getStringExtra(StockAppWidgetProvider.PREF_FORMAT);
    }

    public void onCreate(){ setmWidgetItems() ;}

    public void onDestroy() {
        mCursor.close();
        mWidgetItems.clear();
    }

    public int getCount() {
        mCount = mCursor.getCount();
        mCursor.close();
        return mCount;
    }

    public RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.
        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_quote);
        rv.setTextViewText(R.id.widget_symbol, mWidgetItems.get(position).symbol);
        rv.setTextViewText(R.id.widget_price, mWidgetItems.get(position).price);
        if (formatPref.equals(StockAppWidgetConfigure.ABSOLUTE_CHANGE)) {
            rv.setTextViewText(R.id.widget_change, mWidgetItems.get(position).absoluteChange);
        }else {
            rv.setTextViewText(R.id.widget_change, mWidgetItems.get(position).percentChange);
        }
        rv.setInt(R.id.widget_change, "setBackgroundResource", mWidgetItems.get(position).resource);
        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putInt(StockAppWidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.setAction(StockAppWidgetProvider.TOAST_ACTION);
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.symbol, fillInIntent);

        return rv;
    }

    public RemoteViews getLoadingView() { return null; }

    public int getViewTypeCount() {
        return 1;
    }
    public long getItemId(int position) {
        return position;
    }
    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() { setmWidgetItems(); }

    public void setmWidgetItems() {
        mWidgetItems.clear();

        String[] projection = {
                Contract.Quote.COLUMN_SYMBOL ,
                Contract.Quote.COLUMN_PRICE ,
                Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
                Contract.Quote.COLUMN_PERCENTAGE_CHANGE
        };
        String sortOrder = Contract.Quote.COLUMN_SYMBOL + " ASC";

        mCursor = mContext.getContentResolver().query(
                Contract.Quote.URI,
                projection,
                null,
                null,
                sortOrder
        );

        while (mCursor.moveToNext()) {
            mWidgetItems.add(new WidgetItem(mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)) ,
                mCursor.getFloat(mCursor.getColumnIndex(Contract.Quote.COLUMN_PRICE)),
                    mCursor.getFloat(mCursor.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE)),
                mCursor.getFloat(mCursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE))));
        }
    }
}


