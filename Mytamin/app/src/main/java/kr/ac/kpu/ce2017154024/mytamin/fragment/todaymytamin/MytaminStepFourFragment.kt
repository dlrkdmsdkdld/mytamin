package kr.ac.kpu.ce2017154024.mytamin.fragment.todaymytamin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kr.ac.kpu.ce2017154024.mytamin.R
import kr.ac.kpu.ce2017154024.mytamin.databinding.FragmentManageMentBinding
import kr.ac.kpu.ce2017154024.mytamin.databinding.FragmentMytaminStepFourBinding
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant.TAG
import kr.ac.kpu.ce2017154024.mytamin.utils.chipStringdata
import kr.ac.kpu.ce2017154024.mytamin.utils.chipStringdata.previousChildCount
import kr.ac.kpu.ce2017154024.mytamin.viewModel.todayMytaminViewModel


class MytaminStepFourFragment : Fragment(),ChipGroup.OnCheckedStateChangeListener {
    private var mBinding : FragmentMytaminStepFourBinding?=null
    private val todayMytaminViewModel by activityViewModels<todayMytaminViewModel>()
    private lateinit var chipGroup: ChipGroup
    private  lateinit var stateText:Array<String>
    private var chipChildCount:Int=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMytaminStepFourBinding.inflate(inflater,container,false)
        mBinding =binding
        chipGroup=mBinding?.mytaminStepFourChipgroup!!
        chipGroup.clearDisappearingChildren()
      //  val childCount = chipGroup.childCount
      //  Log.d(Constant.TAG,"MytaminStepFourFragment childCount $childCount")
        chipGroup.setOnCheckedStateChangeListener(this)

        Log.d(Constant.TAG,"MytaminStepFourFragment onCreateView")
        val emojitState = todayMytaminViewModel.selectemojiState.value
        setChip(emojitState!!)
        chipChildCount=chipGroup.childCount
        return mBinding?.root
    }
    override fun onDestroyView() { // 프래그먼트 삭제될때 자동으로실행
        mBinding=null
        super.onDestroyView()
        Log.d(Constant.TAG,"MytaminStepFourFragment onDestroyView")
    }
    fun setChip(state:Int){
        when(state){
            1->{
                stateText=chipStringdata.verysad
                chipStringdata.verysad.forEach { statetext ->
                    chipGroup?.addView(Chip(requireContext()).apply {
                        text  =statetext
                        isCheckable=true
                        isClickable=true
                        tag = statetext
                    })
                }
            }
            2->{
                stateText=chipStringdata.sad
                chipStringdata.sad.forEach { statetext ->
                    chipGroup?.addView(Chip(requireContext()).apply {
                        text  =statetext
                        isCheckable=true
                        isClickable=true
                        tag = statetext
                    })
                }
            }
            3-> {
                stateText=chipStringdata.soso
                chipStringdata.soso.forEach { statetext ->
                    chipGroup?.addView(Chip(requireContext()).apply {
                        text = statetext
                        isCheckable = true
                        isClickable = true
                        tag = statetext
                    })
                }
            }
            4-> {
                stateText=chipStringdata.good
                chipStringdata.good.forEach { statetext ->
                    chipGroup?.addView(Chip(requireContext()).apply {
                        text = statetext
                        isCheckable = true
                        isClickable = true
                        tag = statetext
                    })
                }
            }
            5-> {
                stateText=chipStringdata.verygood
                chipStringdata.verygood.forEach { statetext ->
                    chipGroup?.addView(Chip(requireContext()).apply {
                        text = statetext
                        isCheckable = true
                        isClickable = true
                        tag = statetext
                    })
                }
            }
        }
    }

    override fun onCheckedChanged(group: ChipGroup, checkedIds: MutableList<Int>) {
        var tmp = mutableListOf<String>()
        checkedIds.forEach {
            val chip = group.getChildAt(it-1  - previousChildCount )
            val tmpS=  chip.getTag()
            tmp.add(tmpS.toString())
        }
        Log.d(TAG,"chip  mutableListOf mutableListOf -> ${tmp}")
        todayMytaminViewModel.setdiagnosis(tmp)

    }

    override fun onDestroy() {
        previousChildCount=chipChildCount+previousChildCount
        //뷰가 삭제되도 어찌된일인지 chipgroup 새로생성되는 child id가 이전꺼로부터 갱신되어서 싱글톤변수이용함 ex. 이전꺼 childcount:15 다음꺼 childcount:30됨
        super.onDestroy()
    }
}