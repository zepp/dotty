package im.point.dotty.network

abstract class Envelope {
    var error: String? = null
    var code: Int? = null
    var message: String? = null
}