/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

open class Envelope {
    var error: String? = null
    var code: Int? = null
    var message: String? = null

    fun checkSuccessful() {
        if (error != null) {
            throw Exception(error)
        } else if (code != null) {
            throw Exception(message)
        }
    }
}