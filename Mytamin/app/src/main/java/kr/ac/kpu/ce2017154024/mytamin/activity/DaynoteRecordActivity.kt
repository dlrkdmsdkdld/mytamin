package kr.ac.kpu.ce2017154024.mytamin.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.*
import kotlinx.android.synthetic.main.activity_today_mytamin.*
import kr.ac.kpu.ce2017154024.mytamin.MytaminWorker
import kr.ac.kpu.ce2017154024.mytamin.R
import kr.ac.kpu.ce2017154024.mytamin.databinding.ActivityRecordDaynoteBinding
import kr.ac.kpu.ce2017154024.mytamin.model.WishList
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant.TAG
import kr.ac.kpu.ce2017154024.mytamin.viewModel.RecordViewmodel
import okhttp3.MultipartBody

class DaynoteRecordActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var mbinding: ActivityRecordDaynoteBinding
    private lateinit var navController:NavController
    // private val myRecordViewmodel: RecordViewmodel by viewModels { RecordViewModelFactory(application) }
     private lateinit var myRecordViewmodel: RecordViewmodel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_daynote_record)
        mbinding = ActivityRecordDaynoteBinding.inflate(layoutInflater)
        setContentView(mbinding.root)
        myRecordViewmodel= ViewModelProvider(this).get(RecordViewmodel::class.java)

        //네비게이션들을 담는 호스트
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.record_container) as NavHostFragment

        //네비게이션 컨트롤러 가져옴
         navController = navHostFragment.navController
        mbinding?.recordCompleteBtn.setOnClickListener(this)
        mbinding?.recordBackBtn?.setOnClickListener(this)
        if (intent.hasExtra("array_bundle")){
            val bundle=intent.getBundleExtra("array_bundle")
            val wishList  = bundle?.getSerializable("wishlistArray") as Array<WishList>
            myRecordViewmodel.setWishList(wishList)

        }

    }
    fun setEnableNextBtnPart(can:Boolean){
        mbinding.recordCompleteBtn.isEnabled = can
        if (can){
            mbinding.recordCompleteBtn.background = getDrawable(R.drawable.round_layout_background_orange)
        }else{mbinding.recordCompleteBtn.background = getDrawable(R.drawable.round_layout_background_gray)}
    }
    fun selectwishList(can:Boolean){
        if(can){
            mbinding?.recordEditText.visibility = View.INVISIBLE
            mbinding?.recordTitleText.setText("완료한 위시리스트")
        }
        else{
            mbinding?.recordEditText.visibility = View.VISIBLE
            mbinding?.recordTitleText.setText("기록 남기기")
        }
    }
    override fun onClick(p0: View?) {
        when(p0){
            mbinding?.recordBackBtn ->{
                onBackPressed()
            }
            mbinding?.recordCompleteBtn->{
                //navController.currentDestination?.id
                when(navController.currentDestination?.id){
                    R.id.recordFragment ->{
                        Log.d(TAG, " 현재 프래그먼트는 record프래그먼트")
                        val imageList = arrayListOf<MultipartBody.Part>()
                        var stringURI =ArrayList<String>()
                        val k =myRecordViewmodel.getUrlList.value?.toArray()
                        Log.d(TAG, "myRecordViewmodel ${myRecordViewmodel.getUrlList.value}")
                        myRecordViewmodel.getUrlList.value?.forEach {
                            stringURI.add(it)
                        }
                        Log.d(TAG, " stringURI stringURI $stringURI")
                        if (stringURI!=null) {
                            Log.d(TAG, " stringURI stringURI")
                            val inputData= Data.Builder().putStringArray(MytaminWorker.EXTRA_URI_ARRAY,
                                stringURI.toArray(arrayOfNulls<String>(stringURI.size))).build()


                            val uploadWorkRequest: WorkRequest =
                               OneTimeWorkRequestBuilder<MytaminWorker>()
                                   .setInputData(inputData)
                                   .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                                   .build()
                            WorkManager.getInstance(application).enqueue(uploadWorkRequest)
                        }
                        //액티비티에선 이런식으로 사용
                        //val uploadWorkRequest: WorkRequest =
                        //   OneTimeWorkRequestBuilder<UploadWorker>()
                        // .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        //       .build()
                        //제출
//    WorkManager
//    .getInstance(myContext)
//    .enqueue(uploadWorkRequest)

//    internal fun applyBlur() {
//        workManager.enqueue(OneTimeWorkRequest.from(MytaminWorker::class.java))
//    }

//                        myRecordViewmodel.getbitmapList.value?.forEach {
//                            val bitmapRequestBody = it?.let {  BitmapRequestBody(it) }
//                            val bitmapMultipartBody: MultipartBody.Part = MultipartBody.Part.createFormData("file", "file.jpeg", bitmapRequestBody)
//                            imageList.add(bitmapMultipartBody)
//                        }
//                        InformationRetrofitManager.instance.imageListAPI(imageList){ responseStatus, i ->
//                            Log.d(TAG,"InformationRetrofitManager  i -> $i")
//                        }

                    }
                    R.id.selectRecordFragment ->{
                        Log.d(TAG, " 현재 프래그먼트는 카테고리고르는 프래그먼트")
                        onBackPressed()
//                        navController.navigate(R.id.action_selectRecordFragment_to_recordFragment)
//                        navController.popBackStack()

                    }
                }
            }


        }
    }

     fun permissionDenied(text:String) {
        Toast.makeText(this, "$text", Toast.LENGTH_LONG
        ).show()
    }
}