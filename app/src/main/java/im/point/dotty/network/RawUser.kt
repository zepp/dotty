package im.point.dotty.network

import com.google.gson.annotations.SerializedName

abstract class RawUser() : Envelope() {
    var about: String?= null
    var xmpp: String?= null
    var name: String?= null
    var created: String?= null
    var gender: Boolean?= null
    var birthdate: String?= null
    var avatar: String?= null
    var skype: String?= null
    var icq: String?= null
    var homepage: String?= null
    var email: String?= null
    var location: String? = null
}