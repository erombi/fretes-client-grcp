package br.com.zup.academy

import io.micronaut.core.annotation.Introspected

@Introspected
data class FreteResponse(
    val cep: String,
    val valor: Double
)
