package kr.ac.kpu.ce2017154024.mytamin.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.view.drawToBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.ac.kpu.ce2017154024.mytamin.R
import kr.ac.kpu.ce2017154024.mytamin.UI.LoadingDialog
import kr.ac.kpu.ce2017154024.mytamin.UI.MytaminCorrectionDialog
import kr.ac.kpu.ce2017154024.mytamin.activity.*
import kr.ac.kpu.ce2017154024.mytamin.databinding.FragmentInformationBinding
import kr.ac.kpu.ce2017154024.mytamin.databinding.FragmentManageMentBinding
import kr.ac.kpu.ce2017154024.mytamin.fragment.information.QuitDialog
import kr.ac.kpu.ce2017154024.mytamin.retrofit.token.HistoryRetrofitManager
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant.TAG
import kr.ac.kpu.ce2017154024.mytamin.utils.PreferenceUtil
import kr.ac.kpu.ce2017154024.mytamin.viewModel.HomeViewModel
import kr.ac.kpu.ce2017154024.mytamin.viewModel.InformationViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class UserInformationFragment : Fragment(),View.OnClickListener {
    private var mBinding : FragmentInformationBinding?=null
    private val myInformationViewModel: InformationViewModel by viewModels()
    private lateinit var customProgressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentInformationBinding.inflate(inflater,container,false)
        mBinding =binding
        Log.d(Constant.TAG,"UserInformationFragment onCreateView")
        customProgressDialog= LoadingDialog(requireContext())
        bindClickListener()
        val tmp = arguments?.getByteArray("Image")
        if (tmp!=null){
            Log.d(Constant.TAG," 전달받은 사진있음")

        }else{
            Log.d(Constant.TAG,"전달받은 사진없음")

        }
        myInformationViewModel.getprofile.observe(viewLifecycleOwner , Observer {
            mBinding?.informationNicknameText?.text = "내가 될 ${it.nickname}"
            mBinding?.informationBemyText?.text = it.beMyMessage
            mBinding?.informationProviderText?.text = it.provider
            Log.d(TAG, "profileImgUrl -> ${it.profileImgUrl} ")
            if(it.profileImgUrl !=null){
                mBinding?.informationUserImage?.let { it1 ->
                    Glide.with(this)
                        .load("${it.profileImgUrl}")
                        .into(it1)
                }
            }

        })

        myInformationViewModel.getMydayData.observe(viewLifecycleOwner, Observer {
            Log.d(TAG,"getMydayData -> $it   ")
            mBinding?.userTimeText?.text = it.myDayMMDD
            mBinding?.userTimeBtn?.text = it.dday
            mBinding?.userStateText?.text = it.comment
        })

        return mBinding?.root
    }
    override fun onDestroyView() { // 프래그먼트 삭제될때 자동으로실행
        mBinding=null
        super.onDestroyView()
        Log.d(Constant.TAG,"UserInformationFragment onDestroyView")
    }

    override fun onClick(p0: View?) {
        when(p0){
            mBinding?.informationUserImage->{
                goEditActivity()
            }
            mBinding?.informationEditBtn ->{
                goEditActivity()
            }
            mBinding?.userMydayBtn ->{
                val intent = Intent(context,MydayActivity::class.java)
                startActivity(intent)
            }

            mBinding?.informationSettingBtn ->{
                val intent = Intent(context , SettingActivity::class.java)
                startActivity(intent)
            }
            mBinding?.informationHelpBtn ->{
                val intent = Intent(Intent.ACTION_VIEW , Uri.parse("https://mitamin.notion.site/44cd80e96a314911b79650ee55944017"))
                startActivity(intent)
            }
            //////////////
            mBinding?.informationTermsofserviceBtn ->{
                val intent = Intent(Intent.ACTION_VIEW , Uri.parse("https://mitamin.notion.site/db7bcaab097344e8a8c8ce38bfd7c100"))
                startActivity(intent)
            }
            mBinding?.informationTreatmentBtn ->{
                val intent = Intent(Intent.ACTION_VIEW , Uri.parse("https://mitamin.notion.site/836c999489f64ce5a88aca635127aa01"))
                startActivity(intent)
            }
            mBinding?.informationInitBtn ->{
                val i = Intent(context,UserInitActivity::class.java)
                startActivity(i)
            }
            mBinding?.informationExitBtn ->{
                PreferenceUtil.clearUserData()
                val intent = Intent(context,firstActivity::class.java)
                startActivity(intent)
                activity?.finishAffinity()
            }
            mBinding?.informationQuitBtn ->{
                Log.d(TAG,"클릭됨 ")
                val dialog=QuitDialog(requireContext())
                dialog.show()
                dialog.setOnClickListener(object : QuitDialog.OnClickedDialogBtn {
                    override fun OnNegativeBtn() {
                        Log.d(TAG,"OnNegativeBtn")
                        dialog.dismiss()

                    }

                    override fun OnPositiveBtn() {
                        Log.d(TAG,"OnPositiveBtn")
                        dialog.dismiss()
                        customProgressDialog.show()
                        HistoryRetrofitManager.instance.quitMytamin {
                            customProgressDialog.dismiss()
                            PreferenceUtil.clearUserData()
                            val i = Intent(requireActivity(),firstActivity::class.java)
                            startActivity(i)
                            activity?.finishAffinity()
                        }
                    }

                })

            }
            mBinding?.informationPasswordBtn ->{
                val i = Intent(context,repasswordActivity::class.java)
                startActivity(i)

            }


        }

    }
    fun goEditActivity(){
        val bitmap:Bitmap = mBinding?.informationUserImage!!.drawToBitmap()
        //mBinding?.informationUserImage!!.get
        val bos:ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,20,bos)
        //일단 jpeg로함
        val intent= Intent(context, ProfileEditActivity::class.java)
        intent.putExtra("profile_image",bos.toByteArray())
        intent.putExtra("nickname",myInformationViewModel.getprofile.value?.nickname)
        intent.putExtra("tobemessage",myInformationViewModel.getprofile.value?.beMyMessage)
        startActivity(intent)

    }
    private fun bindClickListener(){
        mBinding?.informationUserImage?.setOnClickListener(this)
        mBinding?.userMydayBtn?.setOnClickListener(this)
        mBinding?.informationEditBtn?.setOnClickListener(this)
        mBinding?.informationSettingBtn?.setOnClickListener(this)
        mBinding?.informationHelpBtn?.setOnClickListener(this)
        mBinding?.informationTermsofserviceBtn?.setOnClickListener(this)
        mBinding?.informationTreatmentBtn?.setOnClickListener(this)
        mBinding?.informationInitBtn?.setOnClickListener(this)
        mBinding?.informationExitBtn?.setOnClickListener(this)
        mBinding?.informationQuitBtn?.setOnClickListener(this)
        mBinding?.informationPasswordBtn?.setOnClickListener(this)
        mBinding?.informationTermsofserviceSubbtn?.setOnClickListener { mBinding?.informationTermsofserviceBtn?.performClick() }
        mBinding?.informationTreatmentSubbtn?.setOnClickListener { mBinding?.informationTreatmentBtn?.performClick() }
        mBinding?.informationPasswordSubbtn?.setOnClickListener { mBinding?.informationPasswordBtn?.performClick() }

        mBinding?.informationInitSubbtn?.setOnClickListener { mBinding?.informationInitBtn?.performClick() }

        mBinding?.informationExitSubbtn?.setOnClickListener { mBinding?.informationExitBtn?.performClick() }


    }



}