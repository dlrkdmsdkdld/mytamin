package kr.ac.kpu.ce2017154024.mytamin.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kr.ac.kpu.ce2017154024.mytamin.R
import kr.ac.kpu.ce2017154024.mytamin.databinding.FragmentHistoryBinding
import kr.ac.kpu.ce2017154024.mytamin.model.weeklyMental
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant.TAG
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant.primaryColor
import kr.ac.kpu.ce2017154024.mytamin.utils.PreferenceUtil
import kr.ac.kpu.ce2017154024.mytamin.viewModel.HistoryViewModel


class HistoryFragment : Fragment(),View.OnClickListener {
    private var mBinding : FragmentHistoryBinding?=null
    private val myviewmodel : HistoryViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHistoryBinding.inflate(inflater,container,false)
        mBinding =binding

        Log.d(TAG,"HistoryFragment onCreateView")

        myviewmodel.getrandomcare.observe(viewLifecycleOwner, Observer {
            mBinding?.historyRandomCare1?.text = it.careMsg1
            mBinding?.historyRandomCare2?.text = it.careMsg2
            mBinding?.historyRadomTake?.text = it.takeAt
        })
        myviewmodel.getmostFeel.observe(viewLifecycleOwner, Observer {
            var count =1
            it.forEach {
                when(count){
                    1->{
                        mBinding?.historyMost1Status?.text= it.feeling
                        mBinding?.historyMost1Count?.text = "${it.count}회"
                    }
                    2->{
                        mBinding?.historyMost2Status?.text = it.feeling
                        mBinding?.historyMost2Count?.text = "${it.count}회"
                    }
                    3->{
                        mBinding?.historyMost3Status?.text = it.feeling
                        mBinding?.historyMost3Count?.text = "${it.count}회"
                    }
                }
                count+=1

            }
        })
        myviewmodel.getweekMental.observe(viewLifecycleOwner, Observer {
            drawLineChart(it)
        })

        mBinding?.historyRefreshBtn?.setOnClickListener(this)
        return mBinding?.root
    }
    inner class MyXAxisFormatter(data :ArrayList<String>) : ValueFormatter() {
        private val data = data
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {

            return data.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }
    private fun drawLineChart(data: ArrayList<weeklyMental>){
        val lineChart = mBinding!!.historyChart

//        val legend = chart?.legend
//        legend?.textSize =
        var entries = ArrayList<Entry>()
        var datacount = 0
        data.forEach {
            entries.add(Entry(datacount.toFloat(),it.mentalConditionCode.toFloat()))
            datacount+=1
        }
        Log.d(TAG, " entries :$entries")
        lineChart.legend.apply {
            this.isEnabled = false
        }

        val xAxis = lineChart.xAxis
        xAxis.apply {
            position=XAxis.XAxisPosition.BOTTOM
            granularity=1f
            textSize=12f
            textColor=ContextCompat.getColor(requireContext(), R.color.Gray)
            spaceMax =0.1f
            spaceMin=0.1f
            setDrawAxisLine(true)
            setDrawGridLines(false)
            
            textColor = ContextCompat.getColor(requireContext(), R.color.subGray)
            axisLineColor=ContextCompat.getColor(requireContext(), R.color.LineColorshort)

        }

        lineChart.axisLeft.apply {
            //isEnabled=false
            axisLineWidth=2f
            this.setDrawLabels(false)
            setDrawGridLines(true)
            this.setDrawAxisLine(false)
            this.axisMaximum = 5f
            this.axisMinimum=0f
            granularity=1f
            this.gridColor=  ContextCompat.getColor(requireContext(), R.color.LineColorshort)

        }

        lineChart.axisRight.apply {
            isEnabled = false
        }
        var stringData = ArrayList<String>()
        data.forEach {
            stringData.add(it.dayOfWeek)
        }
        Log.d(TAG,"stringData :$stringData")
        xAxis.valueFormatter = IndexAxisValueFormatter(stringData)

       // xAxis.valueFormatter = MyXAxisFormatter(stringData)
        Log.d(TAG,"valueFormatter :${xAxis.valueFormatter}")
        val lineDataSet1 = LineDataSet(entries,"entries")
        val chartData = LineData()
        chartData.addDataSet(lineDataSet1)
        lineDataSet1.apply {
            circleRadius=4f
            lineWidth=2f
            circleHoleRadius=3f
            setDrawValues(true)
            setDrawCircles(true)
            setDrawCircleHole(true)
            circleHoleColor = ContextCompat.getColor(requireContext(), R.color.background_white)
            color=ContextCompat.getColor(requireContext(), R.color.primary)
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.primary))
            setDrawHighlightIndicators(true)
            setDrawHorizontalHighlightIndicator(true)
        }
        lineChart.apply {
            description.isEnabled=false

            this.data  =chartData
            extraBottomOffset=15f
            invalidate()
        }


        Log.d(TAG, "entries ->$entries ")




    }
    override fun onDestroyView() { // 프래그먼트 삭제될때 자동으로실행
        mBinding=null
        super.onDestroyView()
        Log.d(TAG,"HistoryFragment onDestroyView")
    }

    override fun onClick(p0: View?) {
        when(p0){
            mBinding?.historyRefreshBtn ->{
                myviewmodel.randomCareAPI()
            }
        }
    }

}