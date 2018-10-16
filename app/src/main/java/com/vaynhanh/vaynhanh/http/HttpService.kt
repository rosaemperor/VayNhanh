package club.rosaemperor.myeyesopen.http

import com.vaynhanh.vaynhanh.http.beans.ImageJson
import com.vaynhanh.vaynhanh.http.beans.UserMessages
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface HttpService {
    @POST("customerCwral")
     fun upLoadUserMessage(@Body messages: UserMessages): Call<Any>

    @POST("face/basic/ocr")
     fun upLoadFaceImage(@Body imageJson: ImageJson) : Call<Any>
}