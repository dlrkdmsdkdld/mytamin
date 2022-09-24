package kr.ac.kpu.ce2017154024.mytamin.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_join_step_one.*
import kr.ac.kpu.ce2017154024.mytamin.R
import kr.ac.kpu.ce2017154024.mytamin.activity.joinActivity
import kr.ac.kpu.ce2017154024.mytamin.databinding.FragmentJoinStepOneBinding
import kr.ac.kpu.ce2017154024.mytamin.retrofit.JoinRetrofitManager
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant.TAG
import kr.ac.kpu.ce2017154024.mytamin.utils.RESPONSE_STATUS
import kr.ac.kpu.ce2017154024.mytamin.viewModel.joinViewModel
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class joinStepOneFragment : Fragment() {
    private var mBinding : FragmentJoinStepOneBinding?=null
    private var emailValue:String?=null
    private var passwordValue:String?=null
    private var RepasswordValue:Boolean?=null
    //옵저버블 통합제거를 위한 compositeDisposable
    private var EmailCompositeDisposable = CompositeDisposable()

    private val joinViewModel by activityViewModels<joinViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentJoinStepOneBinding.inflate(inflater,container,false)
        mBinding =binding
        Log.d(Constant.TAG,"joinStepOneFragment onCreateView")
        //CheckEmailAPICall("mytamin@naver.com")

        //이메일 텍스트 옵저버블
        val edittextChangeObservable = mBinding?.joinStepOneEmailText?.textChanges()

        val checkEmailTextSubscription : Disposable =//디바운스 -> 필터링조건
            edittextChangeObservable!!.debounce(500,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onNext = {
                        if((it.toString()=="") || (!Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()) ){
                            //아이디 검색조건에 맞지않음
                        }else{
                            //ui 쓰레드에서돌리기
                            Handler(Looper.getMainLooper()).post(Runnable {
                                CheckEmailAPICall(it.toString())
                            })
                        }
                        Log.d(TAG,"checkEmailTextSubscription onNext : $it")
                    }, onComplete = {
                        Log.d(TAG,"checkEmailTextSubscription onComplete ")

                    }, onError = {
                        Log.d(TAG,"checkEmailTextSubscription onError $it")

                    }

                )
        EmailCompositeDisposable.add(checkEmailTextSubscription)


        return mBinding?.root

    }
    fun submitpasswordValue(): String {
        return this.passwordValue!!
    }
    fun submitUseremailValue(): String {

        return this.emailValue!!
    }
    private fun CheckEmailAPICall(query:String): Boolean? {
        var result :Boolean = true
        JoinRetrofitManager.instance.checkEmail(inputemail = query, completion = { responseStatus, checkEmailData ->
            when(responseStatus){
                RESPONSE_STATUS.OKAY -> {
                    Log.d(TAG,"api 호출 성공 check  = $checkEmailData")
                    if (checkEmailData?.status==200){
                        result = checkEmailData.result
                        if (result==false){//"@drawable/ic_baseline_check_24"
                            join_step_one_email_layout.endIconDrawable= resources.getDrawable(R.drawable.ic_baseline_check_24)
                            join_step_one_email_layout.helperText="사용 가능한 이메일입니다"
                            emailValue=query
                            OkAllItem()


                        }else{
                            join_step_one_email_layout.endIconDrawable= resources.getDrawable(R.drawable.ic_baseline_error_24)
                            join_step_one_email_layout.helperText="이미 사용 중인 이메일입니다"
                            OkAllItem()
                        }
                    }
                }
            }

        })
        return result
    }

    //join_step_one_email_text --> 이메일 입력창
    //join_step_one_password_text --> 패스워드 입력창
    //join_step_one_password_confirm_text -- >패스워드 확인창
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        join_step_one_password_text.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d(TAG,"join_step_one_password_text afterTextChanged $p0")
                if (Pattern.matches("^[a-zA-Z0-9]*\$", p0) and ((8 <=p0!!.count()) and (p0!!.count()<31) )) {
                    join_step_one_password_layout.helperText="사용 가능한 비밀번호입니다"
                    passwordValue=p0.toString()
                    OkAllItem()
                }
                else {
                    join_step_one_password_layout.helperText="영문, 숫자를 포함한 8~30자리 조합으로 설정해주세요."
                    passwordValue=null
                    OkAllItem()
                }

            }

        })
        join_step_one_password_confirm_text.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (passwordValue == p0.toString()){
                    join_step_one_password_confirm_layout.helperText="비밀번호가 일치합니다."
                    RepasswordValue=true
                    OkAllItem()

                }else{join_step_one_password_confirm_layout.helperText="비밀번호가 일치하지 않습니다."
                    RepasswordValue=false
                    OkAllItem()
                }
            }

        })


    }
    override fun onDestroyView() { // 프래그먼트 삭제될때 자동으로실행
        mBinding=null
        this.EmailCompositeDisposable.clear()
        super.onDestroyView()
        Log.d(Constant.TAG,"joinStepOneFragment onDestroyView")
    }
    private fun OkAllItem(){
        if (RepasswordValue==true && passwordValue!=null   && emailValue!=null  ){
            (activity as joinActivity).canEnableNextbtn(true)
            Log.d(TAG,"joinStepOneFragment OkAllItem -> RepasswordValue : $RepasswordValue" +
                    "  passwordValue : $passwordValue  emailValue:$emailValue")

            joinViewModel.setemail(emailValue!!)
            joinViewModel.setpassword(passwordValue!!)
        }else{
            (activity as joinActivity).canEnableNextbtn(false)

        }
    }


}