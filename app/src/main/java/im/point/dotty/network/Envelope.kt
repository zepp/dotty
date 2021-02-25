/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

abstract class Envelope {
    var error: String? = null
    var code: Int? = null
    var message: String? = null
}