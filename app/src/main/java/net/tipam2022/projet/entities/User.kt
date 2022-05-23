package net.tipam2022.projet.entities

import java.io.Serializable

class User: Serializable {
    var userPhoneNumber: String? = null
    var userName: String? = null
    var email: String? = null
    var birthDay: String? = null
    var createdAt: String? = null
    var profileUrl: String? = null

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    constructor() {}
    constructor(userPhoneNumber: String,userName: String,email: String?,
                birthDay: String?, createdAt: String, profileUrl: String?, ) {
        this.userPhoneNumber = userPhoneNumber
        this.userName = userName
        this.email = email
        this.birthDay = birthDay
        this.createdAt = createdAt
        this.profileUrl = profileUrl
    }

    override fun equals(other: Any?): Boolean {
        return this.userPhoneNumber == (other as User).userPhoneNumber
    }
}