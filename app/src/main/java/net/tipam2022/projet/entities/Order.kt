package net.tipam2022.projet.entities

import java.io.Serializable

enum class OrderStatus{
    Validated,
    NotValidated,
    Confirmed,
    NotConfirmed,
    Delivered
}
class Order: Serializable {
    var orderId: String? = null
    var userPhoneNumber: Long = 0
    var menuId: Int = 0
    var categoryId: Int = 0
    var quantity: Int = 0
    var price: Float = 0f
    var date: String? = null
    var statute: OrderStatus = OrderStatus.NotValidated

    constructor()
    constructor( orderId: String, userPhoneNumber: Long, menuId: Int,
                 categoryId: Int, quantity: Int, price: Float, date: String, statute: OrderStatus){
        this.menuId = menuId
        this.categoryId = categoryId
        this.orderId = orderId
        this.price = price
        this.userPhoneNumber = userPhoneNumber
        this.date = date
        this.quantity = quantity
        this.statute = statute
    }

    override fun equals(other: Any?): Boolean {
        return this.userPhoneNumber == (other as Order).userPhoneNumber &&
                this.menuId == (other as Order).menuId
    }
}