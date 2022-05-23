package net.tipam2022.projet.entities

import java.io.Serializable

class Order: Serializable {
    var userPhoneNumber: String? = null
    var menuId: String? = null
    var quantity: Int = 0
    var price: Float = 0f
    var date: String? = null

    constructor()
    constructor(userPhoneNumber: String, menuId: String,
                quantity: Int, price: Float, date: String){
        this.menuId = menuId
        this.price = price
        this.userPhoneNumber = userPhoneNumber
        this.date = date
        this.quantity = quantity
    }

    override fun equals(other: Any?): Boolean {
        return this.userPhoneNumber == (other as Order).userPhoneNumber &&
                this.menuId == (other as Order).menuId
    }
}