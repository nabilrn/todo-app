package com.example

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TodoService {
    private val todos = mutableListOf<Todo>()
    private var nextId = 1

    init {
        // Add some sample data
        addTodo(CreateTodoRequest(
            title = "Belajar Cloud Computing",
            description = "Mengerjakan tugas besar komputasi awan",
            dueDate = "2025-06-25",
            completed = false
        ))

        addTodo(CreateTodoRequest(
            title = "Membuat API Todo",
            description = "Implementasi REST API untuk aplikasi Todo",
            dueDate = "2025-06-30",
            completed = false
        ))
    }

    fun getAllTodos(): List<Todo> = todos.toList()

    fun getTodoById(id: Int): Todo? = todos.find { it.id == id }

    fun addTodo(request: CreateTodoRequest): Todo {
        val currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z"
        val todo = Todo(
            id = nextId++,
            title = request.title,
            description = request.description,
            completed = request.completed,
            dueDate = request.dueDate,
            createdAt = currentTime
        )
        todos.add(todo)
        return todo
    }

    fun updateTodo(id: Int, request: UpdateTodoRequest): Todo? {
        val index = todos.indexOfFirst { it.id == id }
        if (index == -1) return null

        val existingTodo = todos[index]
        val updatedTodo = existingTodo.copy(
            title = request.title ?: existingTodo.title,
            description = request.description ?: existingTodo.description,
            completed = request.completed ?: existingTodo.completed,
            dueDate = request.dueDate ?: existingTodo.dueDate
        )

        todos[index] = updatedTodo
        return updatedTodo
    }

    fun deleteTodo(id: Int): Boolean {
        return todos.removeIf { it.id == id }
    }
}