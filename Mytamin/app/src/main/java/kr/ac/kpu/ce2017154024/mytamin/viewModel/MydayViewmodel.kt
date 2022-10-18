package kr.ac.kpu.ce2017154024.mytamin.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kr.ac.kpu.ce2017154024.mytamin.retrofit.token.InformationRetrofitManager
import kr.ac.kpu.ce2017154024.mytamin.utils.RESPONSE_STATUS

class MydayViewmodel:ViewModel() {
    private val WishlistContent = MutableLiveData<Boolean>()
    val getWishlistContent : LiveData<Boolean>
        get() = WishlistContent
    fun setWishlistContent(time:Boolean){
        WishlistContent.value = time
    }
    private val DaynoteContent = MutableLiveData<Boolean>()
    val getDaynoteContent : LiveData<Boolean>
        get() = DaynoteContent
    fun setDaynoteContent(time:Boolean){
        DaynoteContent.value = time
    }

    init {
        getWishlistAPI()
        getdaynoteAPI()
    }
    fun getWishlistAPI(){
        InformationRetrofitManager.instance.getWishlist(){responseStatus, mydayData ->
            when(responseStatus){
                RESPONSE_STATUS.NO_CONTENT ->{
                    setWishlistContent(false)
                }
                RESPONSE_STATUS.OKAY ->{
                    setWishlistContent(true)
                }
            }

        }
    }
    fun getdaynoteAPI(){
        InformationRetrofitManager.instance.getDaynote{responseStatus, wishList ->
            when(responseStatus){
                RESPONSE_STATUS.NO_CONTENT ->setDaynoteContent(false)
                RESPONSE_STATUS.OKAY -> setDaynoteContent(true)
            }

        }
    }

    


}