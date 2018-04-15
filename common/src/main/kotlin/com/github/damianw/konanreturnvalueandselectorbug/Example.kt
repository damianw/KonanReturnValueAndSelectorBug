package com.github.damianw.konanreturnvalueandselectorbug

expect class Decimal

expect operator fun Decimal.plus(augend: Decimal): Decimal
expect operator fun Decimal.minus(augend: Decimal): Decimal

expect object Decimals {

    val ZERO: Decimal

}

data class Product(val name: String, val price: Decimal)

data class ShoppingCart(val products: List<Product>) {

	fun firstPrice(): Decimal {
		return products.first().price
	}

    fun computeTotalCost(): Decimal {
        var sum: Decimal = Decimals.ZERO
        for (product in products) {
            sum += product.price
        }
        return sum
    }

}
