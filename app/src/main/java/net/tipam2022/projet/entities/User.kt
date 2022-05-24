package net.tipam2022.projet.entities

import java.io.Serializable

class User: Serializable {
    var userPhoneNumber: Long = 0
    var userName: String? = null
    var email: String? = null
    var birthDay: String? = null
    var createdAt: String? = null
    var profile: String? = null

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    constructor() {}
    constructor(userPhoneNumber: Long,userName: String,email: String?,
                birthDay: String?, createdAt: String, profile: String?, ) {
        this.userPhoneNumber = userPhoneNumber
        this.userName = userName
        this.email = email
        this.birthDay = birthDay
        this.createdAt = createdAt
        this.profile = profile
    }

    override fun equals(other: Any?): Boolean {
        return this.userPhoneNumber == (other as User).userPhoneNumber
    }
}