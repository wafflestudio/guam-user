package waffle.guam.user.api.router

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import waffle.guam.user.api.UserContext
import waffle.guam.user.domain.UnAuthorized
import waffle.guam.user.service.block.BlockCommandService
import waffle.guam.user.service.block.BlockCommandService.CreateBlock
import waffle.guam.user.service.block.BlockCommandService.DeleteBlock
import waffle.guam.user.service.block.BlockQueryService

@Service
class BlockApiRouter(
    private val blockCommandService: BlockCommandService,
    private val blockQueryService: BlockQueryService,
) {

    suspend fun gets(request: ServerRequest): ServerResponse {
        val userId = UserContext.getOrNull()?.id
            ?: request.headers().firstHeader("X-GATEWAY-USER-ID")?.toLong() // FIXME: letter 합z
            ?: throw UnAuthorized()

        val response = blockQueryService.getBlockList(userId)

        return ServerResponse.ok().bodyValueAndAwait(response)
    }

    suspend fun create(request: ServerRequest): ServerResponse {
        val uc = UserContext.getOrNull() ?: throw UnAuthorized()
        val req = request.awaitBody<CreateBlockRequest>()

        blockCommandService.createBlock(
            CreateBlock(userId = uc.id, blockUserId = req.blockUserId)
        )

        return ServerResponse.ok().bodyValueAndAwait(Unit)
    }

    suspend fun delete(request: ServerRequest): ServerResponse {
        val uc = UserContext.getOrNull() ?: throw UnAuthorized()
        val req = request.awaitBody<DeleteBlockRequest>()

        blockCommandService.deleteBlock(
            DeleteBlock(userId = uc.id, blockUserId = req.blockUserId)
        )

        return ServerResponse.ok().bodyValueAndAwait(Unit)
    }
}

data class CreateBlockRequest(
    val blockUserId: Long,
)

data class DeleteBlockRequest(
    val blockUserId: Long,
)
