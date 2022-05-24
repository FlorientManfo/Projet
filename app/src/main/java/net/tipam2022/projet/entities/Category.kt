package net.tipam2022.projet.entities

import java.io.Serializable

class Category: Serializable {
    var categoryId: Int = 0
    var categoryName: String? = null
    var categoryDescription: String? = null
    var image: String? = null

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    constructor() {}
    constructor(categoryId: Int, categoryName: String,
            categoryDescription: String ,image: String?, ) {
        this.categoryId = categoryId
        this.categoryName = categoryName
        this.categoryDescription = categoryDescription
        this.image = image
    }

    override fun equals(other: Any?): Boolean {
        return this.categoryId == (other as Category).categoryId
    }
}