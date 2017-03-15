package com.udacity.stockhawk.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.udacity.stockhawk.R;
import java.util.ArrayList;

/* Used code from LineChart example */

public class LineChartActivity extends Activity implements OnChartValueSelectedListener {

    private LineChart mChart;
    private final String STOCK_HISTORY = "STOCK_HISTORY";
    private final String STOCK_SYMBOL = "STOCK_SYMBOL";
    private String[] xValuesCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart);

        Bundle extras = this.getIntent().getExtras();

        String history = extras.getString(STOCK_HISTORY);
        String symbol = extras.getString(STOCK_SYMBOL);

        mChart = (LineChart) findViewById(R.id.chart);
        mChart.setDrawGridBackground(false);

        mChart.setOnChartValueSelectedListener(this);

        mChart.setContentDescription(this.getString(R.string.line_chart_content_description) + symbol);
        Description desc = new Description();
        desc.setText(this.getString(R.string.line_chart_description));
        mChart.setDescription(desc);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(-10f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);

        mChart.getAxisRight().setEnabled(false);

        // add data
        setData(symbol, history);

        mChart.setVisibleXRangeMaximum(15f);

        mChart.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(LegendForm.LINE);

        // don't forget to refresh the drawing
        mChart.invalidate();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    private void setData(String symbol, String history) {
        int i;
        ArrayList<Entry> values = new ArrayList<Entry>();

        String[] summaryWeek = history.split("\\n");

        String[][] summaryValues = new String[summaryWeek.length][4];
        String[] xValues = new String[summaryWeek.length];

        for (i = 0; i < summaryWeek.length; i++){
            summaryValues[i] = summaryWeek[i].split(",");
            float val =  Float.valueOf(summaryValues[i][3]);
            values.add(new Entry(i, val));

            //Construct the values for the x-axis e.g. month/day/year
            xValues[(summaryWeek.length - 1) - i] = summaryValues[i][0] + "/" + summaryValues[i][1] + "/" + summaryValues[i][2];
        }

        xValuesCopy = xValues;

        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new XAxisValueFormatter(xValues));

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, symbol);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }
    }

    public class XAxisValueFormatter implements IAxisValueFormatter{
        private final String[] mValues;

        public XAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis){
            return mValues[(int) value];
        }

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int position = (int) e.getX();
        String date = xValuesCopy[position];
        String closingPrice = Float.toString(e.getY());
        String contentDescription = date + getString(R.string.chart_content_description) + closingPrice;
        mChart.setContentDescription(contentDescription);
    }

    @Override
    public void onNothingSelected() {
        mChart.setContentDescription(this.getString(R.string.no_chart_value_selected));
    }
}
