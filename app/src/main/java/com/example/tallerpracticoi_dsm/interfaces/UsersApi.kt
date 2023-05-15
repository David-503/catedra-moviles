package com.example.tallerpracticoi_dsm.interfaces

import com.example.tallerpracticoi_dsm.dto.UserDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class UserPayload (
    val dui: String,
    val name: String,
    val lastname: String,
    val email: String,
    val address: String,
    val birthdate: String,
    val phone: String,
)

interface UsersApi {
    @GET("user/find/{email}")
    fun getUser(@Path("email") email: String): Call<UserDTO>

    @POST("user/create")
    fun newUser(@Body payload: UserPayload): Call<UserDTO>
}