package com.jetbrains.kmpapp.data

import platform.Foundation.NSUUID

actual fun uuid(): String {
    return NSUUID().UUIDString()
}