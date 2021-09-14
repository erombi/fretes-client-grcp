package br.com.zup.academy

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException

@Controller("/fretes")
class CalculoFreteController(
    val gRpcClient: FreteServiceGrpc.FreteServiceBlockingStub
) {

    @Get("/calcula")
    fun calcula(@QueryValue cep: String): FreteResponse {

        try {
            val request = CalculaFreteRequest.newBuilder()
                .setCep(cep)
                .build()

            val response = gRpcClient.calculaFrete(request)

            return FreteResponse(response.cep, response.valor)
        } catch (e: StatusRuntimeException) {
            val status = e.status
            val code = status.code
            val description = status.description

            if (code == Status.Code.INVALID_ARGUMENT) {
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }

            if (code == Status.Code.PERMISSION_DENIED) {

                val statusProto = StatusProto.fromThrowable(e)
                if (statusProto == null) {
                    throw HttpStatusException(HttpStatus.FORBIDDEN, description)
                }

                val detailsList = statusProto.detailsList[0]
                val details = detailsList.unpack(ErroDetails::class.java)

                throw HttpStatusException(HttpStatus.FORBIDDEN, "${details.code}: ${details.message}")
            }

            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @Get
    fun listaFretes() : List<FreteResponse> {

        try {
            val request = SemParametros.newBuilder().build()

            val listaFretes = gRpcClient.retornaFretes(request)

            val response = listaFretes.fretesList.map {
                FreteResponse(it.cep, it.valor)
            }

            return response

        } catch (e: StatusRuntimeException) {
            val status = e.status
            val code = status.code
            val description = status.description

            if (code == Status.Code.INVALID_ARGUMENT) {
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }

            if (code == Status.Code.PERMISSION_DENIED) {

                val statusProto = StatusProto.fromThrowable(e)
                if (statusProto == null) {
                    throw HttpStatusException(HttpStatus.FORBIDDEN, description)
                }

                val detailsList = statusProto.detailsList[0]
                val details = detailsList.unpack(ErroDetails::class.java)

                throw HttpStatusException(HttpStatus.FORBIDDEN, "${details.code}: ${details.message}")
            }

            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

}