package itp341.liang.briana.finalproject;


import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;

import itp341.liang.briana.finalproject.model.managers.FluidManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class PieChartFragment extends Fragment {
    private PieChart fluidPieChart;


    public PieChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        fluidPieChart = view.findViewById(R.id.total_pie_chart);
        setPieChart();
        return view;

    }

    // set Pie Chart
    private void setPieChart(){
        fluidPieChart.clear();
        List<PieEntry> entries = new ArrayList<>();
        double mornAmt, afterAmt, eveningAmt;
        mornAmt = FluidManager.getDefaultManager().getTotalMorningFluids();
        afterAmt = FluidManager.getDefaultManager().getTotalAfternoonFluids();
        eveningAmt = FluidManager.getDefaultManager().getTotalEveningFluids();
        entries.add(new PieEntry((float)mornAmt, "Morning"));
        entries.add(new PieEntry((float)afterAmt, "Afternoon"));
        entries.add(new PieEntry((float)eveningAmt, "Evening"));
        PieDataSet set = new PieDataSet(entries, "Fluid % Breakdown");
        ArrayList<Integer> colorList = new ArrayList<>();
        colorList.add(getResources().getColor(R.color.morning));
        colorList.add(getResources().getColor(R.color.afternoon));
        colorList.add(getResources().getColor(R.color.evening));
        set.setColors(colorList);
        PieData data = new PieData(set);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);
        fluidPieChart.setData(data);
        fluidPieChart.setUsePercentValues(true);
        fluidPieChart.setDrawHoleEnabled(false);
        fluidPieChart.setHoleColor(R.color.colorPrimaryDark);
        fluidPieChart.getDescription().setEnabled(false);
        Legend legend = fluidPieChart.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        legend.setWordWrapEnabled(true);
        fluidPieChart.invalidate(); // refresh
    }

}
