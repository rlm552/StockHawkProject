package com.udacity.stockhawk.widgets;

import android.util.Log;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Rory on 2/15/2017.
 */

public class WidgetItem {
    public String symbol;
    public String price;
    public String percentChange;
    public String absoluteChange;
    public int resource;

    private final DecimalFormat dollarFormat;
    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat percentageFormat;

    //float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);



    public WidgetItem(String symbol, Float price, Float absoluteChange, Float percentChange){

        this.symbol = symbol;

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        this.price = dollarFormat.format(price);

        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");

        this.absoluteChange = dollarFormatWithPlus.format(absoluteChange);
        Log.v("WIDGETITEM" , "Absolute Change " + absoluteChange);

        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        this.percentChange = percentageFormat.format(percentChange / 100);

        if (percentChange > 0){
            this.resource = R.drawable.percent_change_pill_green;
        } else {
            this.resource = R.drawable.percent_change_pill_red;
        }


    }
}
