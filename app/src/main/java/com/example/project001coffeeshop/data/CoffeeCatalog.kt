package com.example.project001coffeeshop.data

import com.example.project001coffeeshop.R
import com.example.project001coffeeshop.model.Coffee

object CoffeeCatalog {
    val categories = listOf("All Coffee", "Macchiato", "Latte", "Americano")
    val coffees = listOf(
        Coffee("mocha", "Caffe Mocha", "Deep Foam", "Latte", 453, R.drawable.coffee_mocha, "4.8",
            "A little chocolate. A lot to love. Rich espresso meets velvety steamed milk and dark cocoa, finished with a delicate layer of foam. Crafted fresh, just for your moment."),
        Coffee("flat-white", "Flat White", "Espresso", "Latte", 353, R.drawable.coffee_flat_white, "4.8",
            "Small cup, wonderfully full flavor. A double shot of our signature espresso folded into silky microfoam for a smooth, beautifully balanced coffee."),
        Coffee("macchiato", "Macchiato", "Caramel Drizzle", "Macchiato", 475, R.drawable.coffee_flat_white, "4.9",
            "Bold espresso, soft milk foam, and a ribbon of caramel. Sweet, aromatic, and made to brighten the everyday. Our baristas prepare every cup to order."),
        Coffee("americano", "Americano", "Rich & Smooth", "Americano", 325, R.drawable.coffee_hero, "4.7",
            "Our carefully roasted espresso, lengthened with hot water. Clean, rich, and deeply aromatic, with gentle notes of toasted nuts and dark chocolate."),
        Coffee("vanilla", "Vanilla Latte", "Silky & Sweet", "Latte", 425, R.drawable.coffee_flat_white, "4.8",
            "Comfort in a cup. Smooth espresso and steamed milk meet a touch of vanilla, topped with hand-poured latte art. Lovely hot or over ice."),
        Coffee("cappuccino", "Cappuccino", "Classic Roast", "Latte", 410, R.drawable.coffee_mocha, "4.9",
            "The timeless favorite: equal parts aromatic espresso, steamed milk, and airy foam. A warm, textured classic for slow mornings and good conversations."),
    )
    fun find(id: String): Coffee = coffees.firstOrNull { it.id == id } ?: coffees.first()
}
