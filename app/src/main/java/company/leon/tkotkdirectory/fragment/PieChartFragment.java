package company.leon.tkotkdirectory.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idtk.smallchart.chart.PieChart;
import com.idtk.smallchart.data.PieData;
import com.idtk.smallchart.interfaces.iData.IPieData;

import java.util.ArrayList;

import company.leon.tkotkdirectory.R;

/**
 * Created by Idtk on 2016/6/26.
 * Blog : http://www.idtkm.com
 * GitHub : https://github.com/Idtk
 * 描述 :
 */
public class PieChartFragment extends BaseFragment {

    private ArrayList<IPieData> mPieDataList = new ArrayList<>();
    private String temp[]={"性别","生卒","姓名","籍贯"};
    private String nation;
    private String info[];
    public void setNation(String nation){this.nation=nation;}
    public void setInfo(String info[]){this.info=info;}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_piechart,container,false);
        initData();
        PieChart pieChart = (PieChart) view.findViewById(R.id.pieChart);
        pieChart.setDataList(mPieDataList);
        pieChart.setAxisColor(Color.WHITE);
        pieChart.setAxisTextSize(pxTodp(17));

        return view;
    }

    private void initData(){
        for (int i=0; i<4; i++){
            PieData pieData = new PieData();
            pieData.setInfo(info[i]);
            pieData.setCenter(nation);
            pieData.setName(temp[i]);
            pieData.setValue((float)25);
            pieData.setColor(mColors[i]);
            mPieDataList.add(pieData);
        }
    }

}
