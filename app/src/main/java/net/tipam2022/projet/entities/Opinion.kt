package net.tipam2022.projet.entities

import java.io.Serializable

class Opinion: Serializable {
    var userPhoneNumber: Long = 0
    var menuId: Int = 0
    var categoryId: Int = 0
    var date: String? = null
    var comment: String? = null
    var status: Boolean = false

    constructor()
    constructor(userPhoneNumber: Long, menuId: Int, categoryId: Int,
                date: String, status: Boolean, comment: String?){
        this.userPhoneNumber = userPhoneNumber
        this.menuId = menuId
        this.categoryId = categoryId
        this.date = date
        this.status = status
        this. comment = comment
    }

    override fun equals(other: Any?): Boolean {
        var item = (other as Opinion)
        return "${this.userPhoneNumber}${this.menuId}${this.categoryId}" ==
            "${item.userPhoneNumber}${item.menuId}${item.categoryId}"
    }
}