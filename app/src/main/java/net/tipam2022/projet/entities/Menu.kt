package net.tipam2022.projet.entities

import java.io.Serializable

class Menu: Serializable {

    var categoryId: String? = null
    var menuId: String? = null
    var menuName: String? = null
    var menuPrice: Float = 0f
    var menuImageUrl: String? = null
    var menuDescription: String? = null

    constructor()
    constructor(categoryId: String, menuId: String, menuImageUrl: String?,
                menuName: String, menuPrice: Float, menuDescription: String?){
        this.categoryId = categoryId
        this.menuId = menuId
        this.menuName = menuName
        this.menuPrice = menuPrice
        this.menuImageUrl = menuImageUrl
        this.menuDescription = menuDescription
    }

    override fun equals(other: Any?): Boolean {
        return this.menuId == (other as Menu).menuId &&
                this.categoryId == (other as Menu).categoryId
    }
}