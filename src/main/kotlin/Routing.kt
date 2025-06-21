package com.example

import com.asyncapi.kotlinasyncapi.context.service.AsyncApiExtension
import com.asyncapi.kotlinasyncapi.ktor.AsyncApiPlugin
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val todoService = TodoService()

    routing {
        get("/") {
            call.respondText("Todo API is running! Visit /api/todos to see all todos.")
        }

        route("/api") {
            route("/todos") {
                // GET /api/todos - Get all todos
                get {
                    val todos = todoService.getAllTodos()
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(
                            status = "success",
                            message = "Data retrieved successfully",
                            data = todos
                        )
                    )
                }

                // POST /api/todos - Create new todo
                post {
                    try {
                        val request = call.receive<CreateTodoRequest>()

                        // Basic validation
                        if (request.title.isBlank()) {
                            call.respond(
                                HttpStatusCode.BadRequest,
                                ApiResponse<Nothing>(
                                    status = "error",
                                    message = "Title cannot be empty"
                                )
                            )
                            return@post
                        }

                        val newTodo = todoService.addTodo(request)
                        call.respond(
                            HttpStatusCode.Created,
                            ApiResponse(
                                status = "success",
                                message = "Todo created successfully",
                                data = newTodo
                            )
                        )
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Nothing>(
                                status = "error",
                                message = "Invalid request data"
                            )
                        )
                    }
                }

                // GET /api/todos/{id} - Get todo by ID
                get("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Nothing>(
                                status = "error",
                                message = "Invalid ID format"
                            )
                        )
                        return@get
                    }

                    val todo = todoService.getTodoById(id)
                    if (todo != null) {
                        call.respond(
                            HttpStatusCode.OK,
                            ApiResponse(
                                status = "success",
                                message = "Data retrieved successfully",
                                data = todo
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ApiResponse<Nothing>(
                                status = "error",
                                message = "To-do with the given ID not found"
                            )
                        )
                    }
                }

                // PUT /api/todos/{id} - Update todo by ID
                put("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Nothing>(
                                status = "error",
                                message = "Invalid ID format"
                            )
                        )
                        return@put
                    }

                    try {
                        val request = call.receive<UpdateTodoRequest>()
                        val updatedTodo = todoService.updateTodo(id, request)
                        if (updatedTodo != null) {
                            call.respond(
                                HttpStatusCode.OK,
                                ApiResponse(
                                    status = "success",
                                    message = "Todo updated successfully",
                                    data = updatedTodo
                                )
                            )
                        } else {
                            call.respond(
                                HttpStatusCode.NotFound,
                                ApiResponse<Nothing>(
                                    status = "error",
                                    message = "To-do with the given ID not found"
                                )
                            )
                        }
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Nothing>(
                                status = "error",
                                message = "Invalid request data"
                            )
                        )
                    }
                }

                // DELETE /api/todos/{id} - Delete todo by ID
                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Nothing>(
                                status = "error",
                                message = "Invalid ID format"
                            )
                        )
                        return@delete
                    }

                    val deleted = todoService.deleteTodo(id)
                    if (deleted) {
                        call.respond(
                            HttpStatusCode.OK,
                            ApiResponse<Nothing>(
                                status = "success",
                                message = "Todo deleted successfully"
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ApiResponse<Nothing>(
                                status = "error",
                                message = "To-do with the given ID not found"
                            )
                        )
                    }
                }
            }
        }
    }
}