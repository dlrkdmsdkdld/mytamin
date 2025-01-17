package kr.ac.kpu.ce2017154024.mytamin.retrofit.token

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonElement
import kotlinx.coroutines.*
import kr.ac.kpu.ce2017154024.mytamin.MyApplication
import kr.ac.kpu.ce2017154024.mytamin.model.*
import kr.ac.kpu.ce2017154024.mytamin.utils.Constant.TAG
import kr.ac.kpu.ce2017154024.mytamin.utils.PrivateUserDataSingleton.Createdmonth
import kr.ac.kpu.ce2017154024.mytamin.utils.PrivateUserDataSingleton.Createdyear
import kr.ac.kpu.ce2017154024.mytamin.utils.RESPONSE_STATUS
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.math.log


class InformationRetrofitManager {

    companion object {
        val instance = InformationRetrofitManager()
    }

    private val iInformationRetrofit: IInformationRetrofit? =
        TokenRetrofitClient.getClient()?.create(IInformationRetrofit::class.java)

    private fun String?.toPlainRequestBody() =
        requireNotNull(this).toRequestBody("text/plain".toMediaTypeOrNull())

    fun editProfile(
        file: MultipartBody.Part?,
        body: EditProfile,
        completion: (RESPONSE_STATUS, Int?) -> Unit
    ) {
        val bool: RequestBody = body.isImgEdited.toRequestBody()
        val nicknameRequestBody: RequestBody = body.nickname.toRequestBody()
        val beMyMessageRequestBody: RequestBody = body.beMyMessage.toRequestBody()
        iInformationRetrofit?.editProfile(
            file = file,
            isImgEdited = bool,
            nickname = nicknameRequestBody,
            beMyMessage = beMyMessageRequestBody
        )
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>,
                    response: Response<JsonElement>
                ) {
                    Log.d(TAG, "이미지전송성공response -> $response")
                    response.body()?.let {
                        val body = it.asJsonObject
                        val statusCode = body.get("statusCode").asInt
                        completion(RESPONSE_STATUS.OKAY, statusCode)
                        Log.d(TAG, "user doCompleteBreath response message:${statusCode} ")
                    }
                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "이미지전송실패 이유 -> $t")
                    completion(RESPONSE_STATUS.FAIL, null)
                }

            })

    }

    fun getProfileData(completion: (RESPONSE_STATUS, ProfileData?) -> Unit) {
        iInformationRetrofit?.getProfile()?.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>,
                response: Response<JsonElement>
            ) {
                response.body()?.let {
                    Log.d(TAG, "user getWelcomeMessage onResponse ${response}")
                    val data = it.asJsonObject.get("data").asJsonObject
                    val nickname = data.get("nickname").asString
                    var profileImgUrl: String? = null
                    if (!data.get("profileImgUrl").isJsonNull) {
                        profileImgUrl = data.get("profileImgUrl").asString

                    }
                    val beMyMessage = data.get("beMyMessage").asString
                    val provider = data.get("provider").asString

                    val result = ProfileData(
                        nickname = nickname,
                        profileImgUrl = profileImgUrl,
                        beMyMessage = beMyMessage,
                        provider = provider
                    )

                    completion(RESPONSE_STATUS.OKAY, result)
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "user Login onFailure ${t}")
                completion(RESPONSE_STATUS.FAIL, null)
            }

        })


    }

    fun CorrectionBeMyMessage(beMyMessage: String, completion: (RESPONSE_STATUS, Int?) -> Unit) {
        iInformationRetrofit?.CorrectionBeMyMessage(beMyMessage)
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>,
                    response: Response<JsonElement>
                ) {
                    response.body()?.let {
                        Log.d(TAG, "user CorrectionBeMyMessage onResponse ${response}")
                        val body = it.asJsonObject
                        //TODO 잊지말고 토큰 추가해야함
                        val nickname = body.get("statusCode").asInt

                        completion(RESPONSE_STATUS.OKAY, nickname)

                    }
                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "CorrectionBeMyMessage onFailure ${t}")
                    completion(RESPONSE_STATUS.OKAY, null)
                }

            })


    }

    fun CorrectionNickname(nickname: String, completion: (RESPONSE_STATUS, Int?) -> Unit) {
        iInformationRetrofit?.CorrectionNickname(nickname)
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>,
                    response: Response<JsonElement>
                ) {
                    response.body()?.let {
                        Log.d(TAG, "user CorrectionNickname onResponse ${response}")
                        val body = it.asJsonObject
                        //TODO 잊지말고 토큰 추가해야함
                        val nickname = body.get("statusCode").asInt
                        completion(RESPONSE_STATUS.OKAY, nickname)
                    }
                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "CorrectionNickname onFailure ${t}")
                    completion(RESPONSE_STATUS.OKAY, null)
                }

            })

    }

    fun getMyday(completion: (RESPONSE_STATUS, MydayData?) -> Unit) {
        iInformationRetrofit?.getMyday()?.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                response.body()?.let {
                    Log.d(TAG, "user getMyday onResponse ${response}")
                    val data = it.asJsonObject.get("data").asJsonObject
                    //TODO 잊지말고 토큰 추가해야함
                    val myDayMMDD = data.get("myDayMMDD").asString
                    val dday = data.get("dday").asString
                    val comment = data.get("comment").asString
                    val result = MydayData(myDayMMDD, dday, comment)
                    completion(RESPONSE_STATUS.OKAY, result)
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "getMyday onFailure ${t}")
                completion(RESPONSE_STATUS.OKAY, null)
            }

        })
    }


    fun sendNewWishlist(wishtext: String, completion: (RESPONSE_STATUS, WishList?) -> Unit) {
        iInformationRetrofit?.sendNewWishlist(wishtext)
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    Log.d(TAG, "response.code : ${response.code()}")
                    if (response.code() == 409) {
                        completion(RESPONSE_STATUS.WISH_ALREADY_EXIST_ERROR, null)
                    } else if (response.code() == 201) {
                        response.body()?.let {
                            val statusCode = it.asJsonObject.get("statusCode").asInt
                            Log.d(TAG, " statusCode : $statusCode")
                            if (statusCode == 201) {
                                val wishId =
                                    it.asJsonObject.get("data").asJsonObject.get("wishId").asInt
                                val wishText =
                                    it.asJsonObject.get("data").asJsonObject.get("wishText").asString
                                val count =
                                    it.asJsonObject.get("data").asJsonObject.get("count").asInt
                                val result = WishList(wishId, wishText, count)
                                completion(RESPONSE_STATUS.OKAY, result)
                            }
                            completion(RESPONSE_STATUS.FAIL, null)

                        }
                    }


                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    completion(RESPONSE_STATUS.FAIL, null)
                }

            })


    }

    fun getWishlist(completion: (RESPONSE_STATUS, ArrayList<WishList>?) -> Unit) {
        iInformationRetrofit?.getWishlist()
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    response.body()?.let {
                        Log.d(TAG, "user getWishlist onResponse ${response}")
                        val data = it.asJsonObject.get("data").asJsonArray
                        val dataCount = data.count()
                        //                            val pulished = data.get("published").asJsonArray
                        //                            val pulishedCount = pulished.count()
                        //                            val hidden = data.get("hidden").asJsonArray
                        //                            val hiddenCount = hidden.count()
                        if (dataCount == 0) {
                            completion(RESPONSE_STATUS.NO_CONTENT, null)
                        } else {
                            Log.d(TAG, "pulished.count() ${data.count()} ")
                            val totalWishList = ArrayList<WishList>()
                            data.forEach {
                                val resultItem = it.asJsonObject
                                val wishId = resultItem.get("wishId").asInt
                                val wishText = resultItem.get("wishText").asString
                                val count = resultItem.get("count").asInt
                                val tmp = WishList(wishId, wishText, count)
                                totalWishList.add(tmp)
                            }
                            completion(RESPONSE_STATUS.OKAY, totalWishList)
                        }


                    }
                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "getWishlist onFailure ${t}")
                    completion(RESPONSE_STATUS.FAIL, null)
                }

            })

    }

    fun getDaynote(completion: (RESPONSE_STATUS, ArrayList<daynoteDataParent>?) -> Unit) {
        iInformationRetrofit?.getDaynote()
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    response.body()?.let {
                        Log.d(TAG, "user getWishlist onResponse $response")
                        val data = it.asJsonObject.get("data").asJsonObject

                        if (data.size() == 0) {
                            completion(RESPONSE_STATUS.NO_CONTENT, null)
                            Log.d(TAG, "daynoteList onResponse nudaynoteList.size() 0")

                        } else {
                            val result = ArrayList<daynoteDataParent>()
                            val iterator = data.keySet()
                            iterator.forEach {
                                val Mydaydata = ArrayList<daynoteData>()
                                val yeardata = data.get(it).asJsonArray
                                yeardata.forEach {
                                    val parent = it.asJsonObject
                                    val noteid = parent.get("daynoteId").asInt
                                    val imgList = parent.get("imgList").asJsonArray
                                    val parseimgList = ArrayList<String>()
                                    imgList.forEach {
                                        parseimgList.add(it.asString)
                                    }
                                    val year = parent.get("year").asInt
                                    val month = parent.get("month").asInt
                                    val wishText = parent.get("wishText").asString
                                    val wishid = parent.get("wishId").asInt
                                    val note = parent.get("note").asString
                                    val result = daynoteData(
                                        year = year,
                                        month = month,
                                        wishText = wishText,
                                        note = note,
                                        imgList = parseimgList,
                                        daynoteId = noteid, wishId = wishid
                                    )
                                    Log.d(TAG, "result -> result :$result ")
                                    Mydaydata.add(result)
                                }
                                val tmp = daynoteDataParent(it.toInt(), Mydaydata)
                                result.add(tmp)
                            }
                            completion(RESPONSE_STATUS.OKAY, result)
                            Log.d(TAG, "daynoteList iterator keySet->${iterator}")
                            Log.d(TAG, "daynoteList result result->${result}")

                            Log.d(TAG, "daynoteList onResponse daynoteList.size() ->${data.size()}")

                        }


                    }
                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "getWishlist onFailure ${t}")
                    // completion(RESPONSE_STATUS.OKAY,null)
                }

            })

    }

    fun imageListAPI(file: List<MultipartBody.Part?>, completion: (RESPONSE_STATUS, Int?) -> Unit) {
        iInformationRetrofit?.sendImageList(file = file)
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>,
                    response: Response<JsonElement>
                ) {
                    Log.d(TAG, "이미지전송성공response -> $response")
                    response.body()?.let {
                        val body = it.asJsonObject
                        val statusCode = body.get("statusCode").asInt
                        completion(RESPONSE_STATUS.OKAY, statusCode)
                        Log.d(TAG, "imageListAPI response message:${statusCode} ")
                    }
                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "이미지전송실패 이유 -> $t")
                    completion(RESPONSE_STATUS.FAIL, null)
                }

            })
    }

    fun checkrecord(day: String, completion: (RESPONSE_STATUS, Boolean?) -> Unit) {
        iInformationRetrofit?.checkDaynote(day)
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    response.body()?.let {
                        Log.d(TAG, "user checkrecord onResponse $response")
                        val data = it.asJsonObject.get("data").asBoolean
                        completion(RESPONSE_STATUS.OKAY, data)

                    }
                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "checkrecord onFailure ${t}")
                    completion(RESPONSE_STATUS.OKAY, null)
                }

            })

    }
    //@Part file:List<MultipartBody.Part?>, @Part("wishText") wishText: RequestBody,@Part("note") note: RequestBody
    //        ,@Part("date") date: RequestBody

    fun newdaynote(
        fileList: List<MultipartBody.Part?>,
        wishid: Int,
        note: String,
        date: String,
        completion: (RESPONSE_STATUS, Int?) -> Unit
    ) {
        //  val wishtextRequestBody: RequestBody = wishtext.toRequestBody()
        val noteRequestBody: RequestBody = note.toRequestBody()
        val dateRequestBody: RequestBody = date.toRequestBody()
        Log.d(TAG, " date : $date note :$note  wishtext $wishid")
        iInformationRetrofit?.newDaynote(
            fileList = fileList,
            wishId = wishid,
            note = noteRequestBody,
            date = dateRequestBody
        )
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>,
                    response: Response<JsonElement>
                ) {
                    Log.d(TAG, "데이노트 작성 성공 response -> $response")
                    response.body()?.let {
                        val body = it.asJsonObject
                        val statusCode = body.get("statusCode").asInt
                        completion(RESPONSE_STATUS.OKAY, statusCode)
                        Log.d(TAG, "데이노트 작성 성공h response message:${statusCode} ")

                    }
                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "데이노트 작성 실패 이유 -> $t")
                    completion(RESPONSE_STATUS.FAIL, null)

                }

            })

    }
    suspend fun newdaynoteCall(
        fileList: List<MultipartBody.Part?>,
        wishid: Int,
        note: String,
        date: String
    ) :Boolean{
        //  val wishtextRequestBody: RequestBody = wishtext.toRequestBody()
        val noteRequestBody: RequestBody = note.toRequestBody()
        val dateRequestBody: RequestBody = date.toRequestBody()
        var status = RESPONSE_STATUS.USER_NOT_FOUND_ERROR
        val res = CompletableDeferred<Boolean>() // 완료하거나 취소할수있는 deferred
        Log.d(TAG, " date : $date note :$note  wishtext $wishid")
        iInformationRetrofit?.newDaynote(fileList = fileList, wishId = wishid, note = noteRequestBody, date = dateRequestBody)?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    Log.d(TAG, "데이노트 작성 성공 response -> $response")
                    response.body()?.let {
                        val body = it.asJsonObject
                        val statusCode = body.get("statusCode").asInt
                        Log.d(TAG, "데이노트 작성 성공h response message:${statusCode} ")
                        status=RESPONSE_STATUS.OKAY
                        res.complete(true)
                        Log.d(TAG,"RES 완료 ? $res")
                    }
                }
                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "데이노트 작성 실패 이유 -> $t")
                    status=RESPONSE_STATUS.FAIL
                    res.complete(false)
                }

            })

//        j.join()
        Log.d(TAG, "상태 리턴 : ${status}")
        return res.await()
    }
    fun modifynote(
        fileList: List<MultipartBody.Part?>,
        wishid: Int,
        note: String,
        noteId: Int,
        completion: (RESPONSE_STATUS, Int?) -> Unit
    ) {
        val noteRequestBody: RequestBody = note.toRequestBody()
        iInformationRetrofit?.modifyDaynote(
            fileList = fileList,
            wishId = wishid,
            note = noteRequestBody,
            daynoteId = noteId
        )
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(
                    call: Call<JsonElement>,
                    response: Response<JsonElement>
                ) {
                    response.body()?.let {
                        val body = it.asJsonObject
                        val statusCode = body.get("statusCode").asInt
                        completion(RESPONSE_STATUS.OKAY, statusCode)
                        Log.d(TAG, "데이노트 수정 성공 response message:${statusCode} ")
                    }
                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.d(TAG, "데이노트 수정 실패 이유 -> $t")
                    completion(RESPONSE_STATUS.FAIL, null)

                }

            })

    }

    fun daynoteDelete(noteId: Int,completion: (RESPONSE_STATUS) -> Unit) {
        iInformationRetrofit?.deleteDaynote(noteId)
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    response.body().let {
                        val body = it?.asJsonObject
                        val statusCodes = body?.get("statusCode")?.asInt
                        if (statusCodes == 200) {
                            completion(RESPONSE_STATUS.OKAY)

                        } else {
                            completion(RESPONSE_STATUS.FAIL)

                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(
                                    MyApplication.instance,
                                    "데이노트 삭제실패",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(MyApplication.instance, "데이노트 삭제실패", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            })
    }

    fun deleteWishlist(wishid: Int) {
        Log.d(TAG, "deleteWishlist wishid : $wishid")
        iInformationRetrofit?.deleteWish(wishid)?.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                response.body().let {
                    val body = it?.asJsonObject
                    val statusCodes = body?.get("statusCode")?.asInt
                    if (statusCodes == 200) {

                    } else {

                    }
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {

            }

        })
    }

    fun modifyWishlist(wishid: Int, wishText: String) {
        iInformationRetrofit?.modifyWishlist(wishid, wishText)
            ?.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {

                }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {

                }

            })
    }


}