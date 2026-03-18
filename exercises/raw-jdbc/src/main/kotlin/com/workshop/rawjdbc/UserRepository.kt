package com.workshop.rawjdbc

interface UserRepository {
    fun fetchUser(userId: Long): User?
}
