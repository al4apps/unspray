package com.al4gms.unspray.data.network

import com.al4gms.unspray.data.modelsui.ItemsWrapper
import com.al4gms.unspray.data.modelsui.SearchResultEntity
import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.data.modelsui.user.User
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApi {

    @GET("/photos")
    suspend fun getPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): List<Content.Photo>

    @GET("/search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): SearchResultEntity

    @GET("/photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: String,
    ): Content.Photo

    @POST("/photos/{id}/like")
    suspend fun likePhoto(
        @Path("id") id: String,
    ): ItemsWrapper

    @DELETE("/photos/{id}/like")
    suspend fun unLikePhoto(
        @Path("id") id: String,
    )

    @GET("/collections")
    suspend fun getCollections(
        @Query("page") page: Int,
    ): List<Content.Collection>

    @GET("/collections/{id}")
    suspend fun getCollectionInfo(
        @Path("id") id: String,
    ): Content.Collection

    @GET("/collections/{id}/photos")
    suspend fun getCollectionsPhotos(
        @Path("id") id: String,
        @Query("page") page: Int,
    ): List<Content.Photo>

    @GET("/me")
    suspend fun getCurrentUserInfo(): User

    @GET("/users/{username}/photos")
    suspend fun getUserSPhoto(
        @Path("username") username: String,
        @Query("page") page: Int,
    ): List<Content.Photo>

    @GET("/users/{username}/likes")
    suspend fun getUserSLikedPhoto(
        @Path("username") username: String,
        @Query("page") page: Int,
    ): List<Content.Photo>

    @GET("/users/{username}/collections")
    suspend fun getUserSCollections(
        @Path("username") username: String,
        @Query("page") page: Int,
    ): List<Content.Collection>
}
