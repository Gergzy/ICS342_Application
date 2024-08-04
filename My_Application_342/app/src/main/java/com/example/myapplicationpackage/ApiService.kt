package com.example.myapplicationpackage


import retrofit2.http.*

interface ApiService {

    @GET("/api/users/{user_id}/todos")
    suspend fun getUserTodos(
        @Header("Authorization") bearerToken: String,
        @Path("user_id") userId: String,
        @Query("apikey") apiKey: String,
    ): List<TodoItemReceiver>

    @POST("/api/users/{user_id}/todos")
    suspend fun createUserTodo(
        @Header("Authorization") bearerToken: String,
        @Path("user_id") userId: String,
        @Query("apikey") apiKey: String,
        @Body newTodo: TodoItem
    ): TodoItem

    @PUT("/api/users/{user_id}/todos/{id}")
    suspend fun updateUserTodo(
        @Path("user_id") userId: String,
        @Path("id") todoId: String,
        @Query("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Body updatedTodo: TodoItem
    ): TodoItem

    @POST("/api/users/register")
    suspend fun registerUser(
        @Query("apikey") apiKey: String,
        @Body user: User
    ): UserResponse

    @POST("/api/users/login")
    suspend fun loginUser(
        @Query("apikey") apiKey: String,
        @Body user: User
    ): UserResponse
}



