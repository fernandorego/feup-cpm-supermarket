package org.feup.group4.supermarket.model

import java.time.LocalDate

data class Receipt (
    val date: LocalDate,
    val total: Double
)