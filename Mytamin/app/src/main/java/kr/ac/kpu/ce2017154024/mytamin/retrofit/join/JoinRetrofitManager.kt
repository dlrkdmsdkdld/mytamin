package kr.ac.kpu.ce2017154024.mytamin.retrofit.join

import android.util.Log
import com.google.gson.JsonElement
import kr.ac.kpu.ce2017154024.mytamin.model.CheckOverlapData
import kr.ac.kpu.ce2017154024.mytamin.model.LoginData
import kr.ac.kpu.ce2017154024.mytamin.model.NewUser
import kr.ac.kpu.ce2017154024.mytamin.model.ReturnLoginData
import kr.ac.kpu.ce2017154024.mytamin.retrofit.join.JoinRetrofitClient
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant.TAG
import kr.ac.kpu.ce2017154024.mytamin.utils.RESPONSE_STATUS
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinRetrofitManager {
    companion object{
        val instance = JoinRetrofitManager()
    }
    private val iJoinRetrofit: IJoinRetrofit? = JoinRetrofitClient.getClient()?.create(IJoinRetrofit::class.java)

    fun checkEmail(inputemail:String? , completion:(RESPONSE_STATUS, CheckOverlapData?) -> Unit){
        val term = inputemail ?:""
        val call = iJoinRetrofit?.checkEmail(emailAddress = term).let {
            it
        }?: return


        call.enqueue(object : retrofit2.Callback<JsonElement>{
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when(response.code()){
                    200 ->{
                        response.body()?.let {
                            val body = it.asJsonObject
                            val status = body.get("statusCode").asInt
                            val check = body.get("data").asBoolean
                            val resultData = CheckOverlapData(status = status ,result = check )
                            completion(RESPONSE_STATUS.OKAY,resultData)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                //completion(R)
            }

        })
    }
    fun checkName(inputemail:String? , completion:(RESPONSE_STATUS, CheckOverlapData?) -> Unit){
        val term = inputemail ?:""
        val call = iJoinRetrofit?.checkName(nickname = term).let {
            it
        }?: return


        call.enqueue(object : retrofit2.Callback<JsonElement>{
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                        response.body()?.let {
                            val body = it.asJsonObject
                            val status = body.get("statusCode").asInt
                            val check = body.get("data").asBoolean
                            val resultData = CheckOverlapData(status = status ,result = check )
                            completion(RESPONSE_STATUS.OKAY,resultData)
                        }

            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
            }

        })
    }

    fun newUserJoin(inputData:NewUser, completion:(RESPONSE_STATUS, Int?) -> Unit){
//        iJoinRetrofit?.postUser(inputData.email,inputData.password,inputData.nickname,inputData.mytaminHour,inputData.mytaminMin)
        iJoinRetrofit?.postUser(inputData)
            ?.enqueue(object :Callback<JsonElement>{
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                            response.body()?.let {
                                Log.d(TAG,"newUserJoin onResponse ${response} ")
                                val body = it.asJsonObject
                                val status = body.get("statusCode").asInt
                                completion(RESPONSE_STATUS.OKAY,status)
                            }
                }
                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG,"newUserJoin onFailure ${t.message} ")
                }
            })
    }
    //유저 로그인 모듈
    fun UserLogin(inputData:LoginData , completion: (RESPONSE_STATUS, ReturnLoginData?) -> Unit){
        iJoinRetrofit?.postLogin(inputData)
            ?.enqueue(object : Callback<JsonElement>{
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {

                    if (response.code()==404){
                        Log.d(TAG,"onResponse--- ${response.code()}-------------------------------------------- ")
                        completion(RESPONSE_STATUS.USER_NOT_FOUND_ERROR,null)
                    }else if (response.code()==400){
                        Log.d(TAG,"onResponse--- ${response.code()}-------------------------------------------- ")
                        completion(RESPONSE_STATUS.NO_CONTENT,null)
                    }
                    response.body()?.let {
                        Log.d(TAG, "user Login onResponse ${response}" )
                        val body = it.asJsonObject
                        val status = body.get("statusCode").asInt
                        val message = body.get("message").asString
                        val accessToken = body.get("data").asJsonObject.get("accessToken").asString
                        val refreshToken = body.get("data").asJsonObject.get("refreshToken").asString
                        val loginResult = ReturnLoginData(statusCode = status, message = message,
                        accessToken = accessToken, refreshToken = refreshToken)
                        completion(RESPONSE_STATUS.OKAY,loginResult)

                    }
                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "user Login onResponse ${t}" )
                }

            })
    }


}