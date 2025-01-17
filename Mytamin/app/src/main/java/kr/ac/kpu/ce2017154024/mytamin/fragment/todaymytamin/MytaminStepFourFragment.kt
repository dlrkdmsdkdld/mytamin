package kr.ac.kpu.ce2017154024.mytamin.fragment.todaymytamin

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.detaches
import kr.ac.kpu.ce2017154024.mytamin.R
import kr.ac.kpu.ce2017154024.mytamin.activity.todayMytaminActivity
import kr.ac.kpu.ce2017154024.mytamin.databinding.FragmentManageMentBinding
import kr.ac.kpu.ce2017154024.mytamin.databinding.FragmentMytaminStepFourBinding
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant.TAG
import kr.ac.kpu.ce2017154024.mytamin.utils.chipStringdata
import kr.ac.kpu.ce2017154024.mytamin.utils.dp2px
import kr.ac.kpu.ce2017154024.mytamin.utils.modify
import kr.ac.kpu.ce2017154024.mytamin.viewModel.todayMytaminViewModel


class MytaminStepFourFragment : Fragment(),ChipGroup.OnCheckedStateChangeListener,View.OnClickListener {
    private var mBinding : FragmentMytaminStepFourBinding?=null
    private val todayMytaminViewModel by activityViewModels<todayMytaminViewModel>()
    private lateinit var chipGroup: ChipGroup
    private  lateinit var stateText:Array<String>
    private var chipChildCount:Int=0
    var userchipcount= 0
    var k =0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMytaminStepFourBinding.inflate(inflater,container,false)
        mBinding =binding
        chipGroup=mBinding?.mytaminStepFourChipgroup!!
        chipGroup.clearDisappearingChildren()

        Log.d(Constant.TAG,"MytaminStepFourFragment onCreateView")
        val emojitState = todayMytaminViewModel.selectemojiState.value
        setChip(emojitState!!)
        mBinding?.wishlistCompleteBtn?.setOnClickListener(this)
        return mBinding?.root
    }
    override fun onDestroyView() { // 프래그먼트 삭제될때 자동으로실행
        mBinding=null
        super.onDestroyView()
        Log.d(Constant.TAG,"MytaminStepFourFragment onDestroyView")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chipGroup.setOnCheckedStateChangeListener(this)
    }
    fun setChip(state:Int){
        when(state){
            1->{
                stateText=chipStringdata.verysad
                chipStringdata.verysad.forEach { statetext ->
                    chipGroup?.addView(createTagChip(requireContext(),statetext,k))
                    k+=1
                }
                   chipGroup?.addView( lastcreateTagChip(requireContext(),k))
            }
            2->{
                stateText=chipStringdata.sad
                chipStringdata.sad.forEach { statetext ->
                    chipGroup?.addView(createTagChip(requireContext(),statetext,k))
                    k+=1
                }
                   chipGroup?.addView( lastcreateTagChip(requireContext(),k))
            }
            3-> {
                stateText=chipStringdata.soso
                chipStringdata.soso.forEach { statetext ->
                    chipGroup?.addView(createTagChip(requireContext(),statetext,k))
                    k+=1
                }

                   chipGroup?.addView( lastcreateTagChip(requireContext(),k))
            }
            4-> {
                stateText=chipStringdata.good
                chipStringdata.good.forEach { statetext ->
                    chipGroup?.addView(createTagChip(requireContext(),statetext,k))
                    k+=1
                }
                   chipGroup?.addView( lastcreateTagChip(requireContext(),k))
            }
            5-> {
                stateText=chipStringdata.verygood
                chipStringdata.verygood.forEach { statetext ->
                    chipGroup?.addView(createTagChip(requireContext(),statetext,k))
                    k+=1
                }
                Log.d(TAG,"k 값은 :$k")
                chipGroup?.addView( lastcreateTagChip(requireContext(),k))
            }
        }
        chipGroup.children.forEach {

            Log.d(TAG,"chipGroup.checkedChipIds ${it.id}")

        }
        userchipcount=k
        k+=1

    }

    override fun onCheckedChanged(group: ChipGroup, checkedIds: MutableList<Int>) {
        var tmp = mutableListOf<String>()

        if (checkedIds.count()>=1){okBtn(true)}
        else{okBtn(false)}

        Log.d(TAG,"checkedIds.count() ->${checkedIds.count()}")
        Log.d(TAG,"checkedId ->${checkedIds}")
        if (checkedIds.count()>=3){
            //액티비티가 파괴되면 그전에 있던 id값들이 누적돼서 이렇게해야함
            for(i in 0 until chipGroup.childCount){
                if(i in checkedIds){
                    Log.d(TAG,"can click = $i")
                }
                else{
                    group.getChildAt(i).isClickable=false
                }
            }
        }else{
            for(i in 0 until group.childCount){
                group.getChildAt(i).isClickable=true
            }

        }
        group.children.forEach {

        }

        checkedIds.forEach {
            // 가끔 태그 받아오는거 안됨 잘봐야함
            val chip = group.getChildAt(it ) //동적추가 없을때
            //val chip = group.getChildAt(it -1 - previousChildCount )
            chip?.let {
                val tmpS=  chip.getTag()?:"error"
                if (tmpS!="error" ) tmp.add(tmpS.toString())
            }
        }
        if(tmp.isNotEmpty()) todayMytaminViewModel.setdiagnosis(tmp)
        Log.d(TAG,"chip  mutableListOf mutableListOf -> ${todayMytaminViewModel.selectediagnosis.value}")

    }
    fun okBtn(d:Boolean){
        if (todayMytaminViewModel.domodify== modify.modify){
            (activity as todayMytaminActivity).setEnableCorrection(d)
        }else{
            (activity as todayMytaminActivity).setEnableNextBtn(d)
        }

    }

    private fun createTagChip(context: Context, statetext: String,idData:Int): Chip {
        return Chip(context).apply {
            setChipBackgroundColorResource(R.color.chip_background_color)
            setTextColor(resources.getColorStateList(R.color.chip_text_color))
//            setBackgroundResource(R.drawable.ic_chip)
            isCheckedIconVisible=false
            text  =statetext
            isCheckable=true
            isClickable=true
            tag = statetext
            id = idData
            chipStrokeWidth= 1F
            setChipStrokeColorResource(R.color.Gray)
        }

    }
    private fun lastcreateTagChip(context: Context,idData:Int): Chip {
        return Chip(context).apply {
            setChipBackgroundColorResource(R.color.chip_background_color)
            setTextColor(resources.getColorStateList(R.color.chip_text_color))
//            setBackgroundResource(R.drawable.ic_chip)
            isCheckedIconVisible=false
            text  ="직접 입력"
            isCheckable=true
            isClickable=true
            tag = "error"
            id = idData
            this.setOnCheckedChangeListener { compoundButton, b ->
                if (b){
                    mBinding?.mytaminUserLayout?.visibility=View.VISIBLE
                     }else{
                    mBinding?.mytaminUserLayout?.visibility=View.GONE

                }


            }
            chipStrokeWidth= 1F
            chipIcon = resources.getDrawable(R.drawable.icon_pencil_gray,null)
            chipIconSize =dp2px(resources,18).toFloat()
            chipStartPadding=dp2px(resources,10).toFloat()
            setChipStrokeColorResource(R.color.Gray)
        }
    }

    override fun onClick(p0: View?) {
        when(p0){
            mBinding?.wishlistCompleteBtn ->{
                val newChipText = mBinding?.wishlistNewWishlist?.text.toString()
                if (newChipText.length>10){
                    Toast.makeText(requireContext(),"감정 태그는 10자 이내로 해주세요!",Toast.LENGTH_SHORT).show()

                }
                else{
                    chipGroup?.addView(createTagChip(requireContext(),mBinding?.wishlistNewWishlist?.text.toString(),k))
                    k+=1
                    Log.d(TAG,"chipGroup.childCount ->${chipGroup.childCount}")
//                chipGroup.getChildAt(chipGroup.childCount -2- previousChildCount).performClick()
                    chipGroup.getChildAt(userchipcount).performClick()
                    mBinding?.wishlistNewWishlist?.setText("")
                }
            }
        }
    }

}