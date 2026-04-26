package com.tasklist.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform