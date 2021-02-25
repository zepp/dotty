/*
 * Copyright (c) 2019-2021 Pavel A. Sokolov
 */
package im.point.dotty.network

data class Author(var login: String? = null,
                  var id: Long? = null,
                  var avatar: String? = null,
                  var name: String? = null)