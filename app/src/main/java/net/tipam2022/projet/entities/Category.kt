package net.tipam2022.projet.entities

import java.io.Serializable

class Category: Serializable {
    var categoryId: String? = null
    var categoryName: String? = null
    var categoryDescription: String? = null
    var imageUrl: String? = null

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    constructor() {}
    constructor(categoryId: String, categoryName: String,
            categoryDescriptions: String ,imageUrl: String?, ) {
        this.categoryId = categoryId
        this.categoryName = categoryName
        this.categoryDescription = categoryDescription
        this.imageUrl = imageUrl
    }

    override fun equals(other: Any?): Boolean {
        return this.categoryId == (other as Category).categoryId
    }
}