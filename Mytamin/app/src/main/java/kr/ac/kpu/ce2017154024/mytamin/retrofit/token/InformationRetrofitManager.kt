package kr.ac.kpu.ce2017154024.mytamin.retrofit.token

import android.util.Log
import com.google.gson.JsonElement
import kr.ac.kpu.ce2017154024.mytamin.model.LoginData
import kr.ac.kpu.ce2017154024.mytamin.model.MydayData
import kr.ac.kpu.ce2017154024.mytamin.model.ProfileData
import kr.ac.kpu.ce2017154024.mytamin.model.WishList
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant.TAG
import kr.ac.kpu.ce2017154024.mytamin.utils.RESPONSE_STATUS
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Multipart

class InformationRetrofitManager {

    companion object{
        val instance=InformationRetrofitManager()
    }
    private val iInformationRetrofit:IInformationRetrofit? =TokenRetrofitClient.getClient()?.create(IInformationRetrofit::class.java)

    fun oneImageAPICall(file: MultipartBody.Part,completion:(RESPONSE_STATUS,Int?) -> Unit){
        iInformationRetrofit?.editProfileImage(file)?.enqueue(object :retrofit2.Callback<JsonElement>{
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG,"이미지전송성공response -> $response")
                response.body()?.let {
                    val body =it.asJsonObject
                    val statusCode = body.get("statusCode").asInt
                    completion(RESPONSE_STATUS.OKAY, statusCode)
                    Log.d(TAG, "user doCompleteBreath response message:${statusCode} " )
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG,"이미지전송실패 이유 -> $t")
                completion(RESPONSE_STATUS.FAIL, null)
            }

        })

    }

    fun getProfileData(completion:(RESPONSE_STATUS, ProfileData?) -> Unit){
        iInformationRetrofit?.getProfile()
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    response.body()?.let {
                        Log.d(TAG, "user getWelcomeMessage onResponse ${response}" )
                        val data =it.asJsonObject.get("data").asJsonObject
                        val nickname = data.get("nickname").asString
                        var profileImgUrl:String? =null
                        if (!data.get("profileImgUrl").isJsonNull){
                            profileImgUrl = data.get("profileImgUrl").asString

                        }
                        val beMyMessage = data.get("beMyMessage").asString

                        val result = ProfileData(nickname=nickname,profileImgUrl=profileImgUrl ,beMyMessage=beMyMessage)

                        completion(RESPONSE_STATUS.OKAY,result)
                    }
                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "user Login onFailure ${t}" )
                    completion(RESPONSE_STATUS.FAIL,null)
                }

            })

    }
    fun CorrectionBeMyMessage(beMyMessage:String,completion:(RESPONSE_STATUS,Int?) -> Unit){
        iInformationRetrofit?.CorrectionBeMyMessage(beMyMessage)
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    response.body()?.let {
                        Log.d(TAG, "user CorrectionBeMyMessage onResponse ${response}" )
                        val body =it.asJsonObject
                        //TODO 잊지말고 토큰 추가해야함
                        val nickname = body.get("statusCode").asInt

                        completion(RESPONSE_STATUS.OKAY,nickname)

                    }
                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "CorrectionBeMyMessage onFailure ${t}" )
                    completion(RESPONSE_STATUS.OKAY,null)
                }

            })

    }

    fun CorrectionNickname(nickname:String,completion:(RESPONSE_STATUS,Int?) -> Unit){
        iInformationRetrofit?.CorrectionNickname(nickname)
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    response.body()?.let {
                        Log.d(TAG, "user CorrectionNickname onResponse ${response}" )
                        val body =it.asJsonObject
                        //TODO 잊지말고 토큰 추가해야함
                        val nickname = body.get("statusCode").asInt
                        completion(RESPONSE_STATUS.OKAY,nickname)
                    }
                }
                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "CorrectionNickname onFailure ${t}" )
                    completion(RESPONSE_STATUS.OKAY,null)
                }

            })

    }
    fun getMyday(completion:(RESPONSE_STATUS,MydayData?) -> Unit){
        iInformationRetrofit?.getMyday()
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    response.body()?.let {
                        Log.d(TAG, "user getMyday onResponse ${response}" )
                        val data =it.asJsonObject.get("data").asJsonObject
                        //TODO 잊지말고 토큰 추가해야함
                        val myDayMMDD = data.get("myDayMMDD").asString
                        val dday = data.get("dday").asString
                        val comment = data.get("comment").asString
                        val result = MydayData(myDayMMDD,dday,comment)
                        completion(RESPONSE_STATUS.OKAY,result)
                    }
                }
                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "getMyday onFailure ${t}" )
                    completion(RESPONSE_STATUS.OKAY,null)
                }

            })

    }
    fun getWishlist(completion:(RESPONSE_STATUS, WishList?) -> Unit){
        iInformationRetrofit?.getWishlist()
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    response.body()?.let {
                        Log.d(TAG, "user getWishlist onResponse ${response}" )
                        val data =it.asJsonObject.get("data").asJsonObject
                        //TODO 잊지말고 토큰 추가해야함
                        if (data.get("published").isJsonNull  ){}

//                        val myDayMMDD = data.get("myDayMMDD").asString
//                        val dday = data.get("dday").asString
//                        val comment = data.get("comment").asString
//                        val result = MydayData(myDayMMDD,dday,comment)
//                        completion(RESPONSE_STATUS.OKAY,result)
                    }
                }
                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "getWishlist onFailure ${t}" )
                   // completion(RESPONSE_STATUS.OKAY,null)
                }

            })

    }

}