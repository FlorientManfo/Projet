package net.tipam2022.projet.adapters

import net.tipam2022.projet.entities.Order

class Cart {
    var cartId: String? = null
    var orders: ArrayList<Order>? = null
    var date: String? = null
    var totalPrice: Float = 0f

    constructor()
    constructor(cartId: String, order: ArrayList<Order>?,
                date: String, totalPrice: Float){
        this.cartId = cartId
        this.orders = order
        this.date = date
        this.totalPrice = totalPrice
    }

    override fun equals(other: Any?): Boolean {
        return this.cartId == (other as Cart).cartId
    }
}