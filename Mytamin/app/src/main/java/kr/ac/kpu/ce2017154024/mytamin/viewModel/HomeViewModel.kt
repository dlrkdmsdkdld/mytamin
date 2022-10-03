package kr.ac.kpu.ce2017154024.mytamin.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.ac.kpu.ce2017154024.mytamin.Repository.HomeRepository
import kr.ac.kpu.ce2017154024.mytamin.Repository.MytaminRepository
import kr.ac.kpu.ce2017154024.mytamin.model.LatestMytamin
import kr.ac.kpu.ce2017154024.mytamin.model.LoginData
import kr.ac.kpu.ce2017154024.mytamin.retrofit.home.HomeRetrofitManager
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant.TAG
import kr.ac.kpu.ce2017154024.mytamin.utils.RESPONSE_STATUS

class HomeViewModel: ViewModel() {
    private val HomeRepository= HomeRepository()  //레포지토리 ##
    private val latestMytamin = MutableLiveData<LatestMytamin>()
    val getlatestMytamin : LiveData<LatestMytamin>
        get() = latestMytamin
    fun setlatestMytamin(data:LatestMytamin?){
        Log.d(TAG,"뷰모델에서 코루틴 값  -> ${data}")
        if (data!=null){
            latestMytamin.value = data!!
        }
    }

    fun getLatestMytaminAPI(){
        viewModelScope.launch {
            val tmp = HomeRepository.getLatestMytamin()
            Log.d(TAG,"tmp 값은 제발.. $tmp")
            Log.d(TAG,"[][][][][][][]][[][]] [][][][][][]")
            Log.d(TAG,"[][][][][][][]][[][]] [][][][][][]")
            Log.d(TAG,"[][][][][][][]][[][]] [][][][][][]")
            Log.d(TAG,"[][][][][][][]][[][]] [][][][][][]")
          //  setlatestMytamin(tmp)
//            if (tmp.isCompleted){
//                Log.d(TAG,"뷰모델에서 코루틴 값  -> ${tmp.await()}")
//            }
//            if (tmp!=null){
//                setlatestMytamin(tmp.await())
//            }
        }

    }
//    fun timerStart(){
//        a=viewModelScope.launch {
//            while (_timerCount.value!! > 0 ){
//                _timerCount.value = _timerCount.value!!.minus(1)
//                delay(1000L)
//            }
//        }
//    }

    private val nickname = MutableLiveData<String>()
    val getnickname : LiveData<String>
        get() = nickname

    fun setnickname(time:String){
        nickname.value = time
    }

    private val comment = MutableLiveData<String>()
    val getcomment : LiveData<String>
        get() = comment

    fun setcomment(icomment:String){
        comment.value = icomment
    }
    init {
        wellcomAPICall()
    }

    fun wellcomAPICall(){
        HomeRetrofitManager.instance.getWelcomeMessage(completion = {responseStatus, wellcomedata ->
            Log.d(TAG,"HomeViewModel wellcomAPICall ")
            when(responseStatus){
                RESPONSE_STATUS.OKAY ->{
                    setnickname(wellcomedata!!.email)
                    setcomment(wellcomedata!!.password)
                }
            }
        })
    }

}