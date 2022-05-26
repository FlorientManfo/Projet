package net.tipam2022.projet.entities

import java.io.Serializable

class Menu: Serializable {

    var categoryId: Int = 0
    var menuId: Int= 0
    var menuName: String? = null
    var menuPrice: Float = 0f
    var menuImage: String? = null
    var menuDescription: String? = null

    constructor()
    constructor(categoryId: Int, menuId: Int, menuImage: String?,
                menuName: String, menuPrice: Float, menuDescription: String?){
        this.categoryId = categoryId
        this.menuId = menuId
        this.menuName = menuName
        this.menuPrice = menuPrice
        this.menuImage = menuImage
        this.menuDescription = menuDescription
    }

    override fun equals(other: Any?): Boolean {
        return this.menuId == (other as Menu).menuId &&
                this.categoryId == (other as Menu).categoryId
    }
}