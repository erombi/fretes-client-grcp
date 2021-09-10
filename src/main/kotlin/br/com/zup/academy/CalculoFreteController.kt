package br.com.zup.academy

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue

@Controller("/fretes/calcula")
class CalculoFreteController(
    val gRpcClient: FreteServiceGrpc.FreteServiceBlockingStub
) {

    @Get
    fun calcula(@QueryValue cep: String): FreteResponse {
        val request = CalculaFreteRequest.newBuilder()
            .setCep(cep)
            .build()

        val response = gRpcClient.calculaFrete(request)

        return FreteResponse(response.cep, response.valor)
    }


}