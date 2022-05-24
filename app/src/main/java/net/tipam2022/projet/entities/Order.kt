package net.tipam2022.projet.entities

import java.io.Serializable

class Order: Serializable {
    var orderId: Int = 0
    var userPhoneNumber: Long = 0
    var menuId: Int = 0
    var quantity: Int = 0
    var price: Float = 0f
    var date: String? = null

    constructor()
    constructor(userPhoneNumber: Long, orderId: Int, menuId: Int,
                quantity: Int, price: Float, date: String){
        this.menuId = menuId
        this.orderId = orderId
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