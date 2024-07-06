package com.alok.groww.Core.domain

sealed class ServerResponse<out T> {
    data class Success<out T>(val data: T) : ServerResponse<T>()
    data class Failure(val error: String) : ServerResponse<Nothing>()
    object Loading : ServerResponse<Nothing>()
}