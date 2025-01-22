package com.jetbrains.kmpapp.data

import java.util.UUID.randomUUID

actual fun uuid(): String {
    return randomUUID().toString()
}