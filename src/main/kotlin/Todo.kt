package com.example

import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    val id: Int,
    val title: String,
    val description: String,
    val completed: Boolean = false,
    val dueDate: String, // Format: YYYY-MM-DD
    val createdAt: String // Format: ISO 8601
)

@Serializable
data class CreateTodoRequest(
    val title: String,
    val description: String,
    val dueDate: String,
    val completed: Boolean = false
)

@Serializable
data class UpdateTodoRequest(
    val title: String? = null,
    val description: String? = null,
    val completed: Boolean? = null,
    val dueDate: String? = null
)

@Serializable
data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T? = null
)