package org.omsi.demoproject

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform