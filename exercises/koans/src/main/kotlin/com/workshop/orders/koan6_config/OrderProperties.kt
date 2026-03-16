package com.workshop.orders.koan6_config

// TODO: Bind this class to the "orders" prefix in application.yml
//       Hint: Use @ConfigurationProperties(prefix = "orders")
//       You also need to make Spring aware of this class — try @Component
class OrderProperties {
    var maxItems: Int = 0
    var currency: String = ""
    var discountPercent: Int = 0
}
