package waffle.guam.user.service.block

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.user.infra.db.Block
import waffle.guam.user.infra.db.BlockRepository
import waffle.guam.user.service.block.BlockCommandService.CreateBlock
import waffle.guam.user.service.block.BlockCommandService.DeleteBlock

interface BlockCommandService {

    suspend fun createBlock(command: CreateBlock)

    suspend fun deleteBlock(command: DeleteBlock)

    data class CreateBlock(
        val userId: Long,
        val blockUserId: Long,
    )

    data class DeleteBlock(
        val userId: Long,
        val blockUserId: Long,
    )
}

@Transactional
@Service
class BlockCommandServiceImpl(
    private val blockRepository: BlockRepository,
) : BlockCommandService {

    override suspend fun createBlock(command: CreateBlock) {
        blockRepository.save(
            Block(
                userId = command.userId,
                blockUserId = command.blockUserId
            )
        )
    }

    override suspend fun deleteBlock(command: DeleteBlock) {
        blockRepository.deleteByUserIdAndBlockUserId(
            userId = command.userId,
            blockUserId = command.blockUserId
        )
    }
}
