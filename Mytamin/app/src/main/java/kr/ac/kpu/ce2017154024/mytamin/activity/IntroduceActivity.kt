package kr.ac.kpu.ce2017154024.mytamin.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_introduce.*
import kotlinx.android.synthetic.main.activity_today_mytamin.*
import kotlinx.android.synthetic.main.fragment_join_step_one.*
import kr.ac.kpu.ce2017154024.mytamin.LoadingDialog
import kr.ac.kpu.ce2017154024.mytamin.R
import kr.ac.kpu.ce2017154024.mytamin.ViewPager2.IntroduceViewPagerFragmentAdapter
import kr.ac.kpu.ce2017154024.mytamin.databinding.ActivityIntroduceBinding
import kr.ac.kpu.ce2017154024.mytamin.databinding.ActivityLoginBinding
import kr.ac.kpu.ce2017154024.mytamin.model.NewUser
import kr.ac.kpu.ce2017154024.mytamin.retrofit.JoinRetrofitManager
import kr.ac.kpu.ce2017154024.mytamin.utils.*
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant.TAG

class IntroduceActivity : AppCompatActivity() {
    private lateinit var mbinding: ActivityIntroduceBinding
    private val NUM_PAGES = 4
    private var inputHour = 0
    private var inputMinute = 0
     //  viewpager
     private lateinit var email:String
    private lateinit var password:String
    private lateinit var nickname:String
    private lateinit var viewPager:ViewPager2
    private lateinit var customProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding= ActivityIntroduceBinding.inflate(layoutInflater)
        setContentView(mbinding.root)
        email= intent.getStringExtra("email").toString()
        password= intent.getStringExtra("password").toString()
        nickname= intent.getStringExtra("nickname").toString()

        Log.d(Constant.TAG, "IntroduceActivity onCreate email: $email password:$password  nickname:$nickname")

        customProgressDialog= LoadingDialog(this)
        customProgressDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))


        //mbinding?.introduceViewpager.adapter = IntroduceActivity@adapter!!
        val viewPager:ViewPager2 = mbinding?.introduceViewpager
        val viewpagerFramgnetAdapter = IntroduceViewPagerFragmentAdapter(this)
        viewPager.adapter=viewpagerFramgnetAdapter
        viewPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position==3){
                    mbinding?.introduceNextBtn.text=JOINSTRING.start
                }else{
                    mbinding?.introduceNextBtn.text=JOINSTRING.next
                }
            }
        })
        val dotsIndicator=mbinding?.introduceIndicator
        dotsIndicator.attachTo(viewPager)
        mbinding?.introduceNextBtn.setOnClickListener {
            if (viewPager.currentItem!=3){
                viewPager.currentItem=viewPager.currentItem+1
            }else{
                mbinding?.introduceNextBtn.text=JOINSTRING.start
                val parseHour = inputHour.parseIntToHH()
                val parseMinute = inputMinute.parseIntToMM()
                val postUser=NewUser(email,password,nickname,parseHour,parseMinute)
                customProgressDialog.show()
                newUserJoinAPICall(postUser)
              //  goLogin()
            }

        }
//        val tmp =NewUser("dlrkdmsdkdld@nav.com","a1s2d3f4","강철폭풍","11","22")
//        newUserJoinAPICall(tmp)



    }
    fun goLogin(){
        val intent=Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun getemailData():String{
        return email
    }
    fun getnicknameData():String{
        return nickname
    }
    fun getpasswordData():String{
        return password
    }
    fun submitTime(hour:Int,minute:Int){
        inputHour=hour
        inputMinute= minute
    }
    fun canEnableNextbtn(data:Boolean){
        introduce_next_btn.isEnabled=false
    }
     fun newUserJoinAPICall(query: NewUser) {
        JoinRetrofitManager.instance.newUserJoin(inputData = query, completion = {responseStatus, intdata ->
            when(responseStatus){
                RESPONSE_STATUS.OKAY ->{
                    if (intdata==201){
                        Log.d(Constant.TAG,"api 호출 성공 회원가입완료 !!")
                        customProgressDialog.dismiss()
                        goLogin()
                    }else{
                        customProgressDialog.dismiss()
                        Toast.makeText(this,"현재 문제가 있어서 회원가입이 안되는중입니다..",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d(Constant.TAG,"IntroduceActivity onDestroy")
    }
}