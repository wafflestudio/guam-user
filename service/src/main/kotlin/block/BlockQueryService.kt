package waffle.guam.user.service.block

import org.springframework.stereotype.Service
import waffle.guam.user.domain.BlockListInfo
import waffle.guam.user.infra.db.BlockRepository
import waffle.guam.user.service.user.UserQueryService

interface BlockQueryService {

    suspend fun getBlockList(userId: Long): BlockListInfo
}

@Service
class BlockQueryServiceImpl(
    private val blockRepository: BlockRepository,
    private val userQueryService: UserQueryService,
) : BlockQueryService {

    override suspend fun getBlockList(userId: Long): BlockListInfo {
        return blockRepository.findAllByUserId(userId)
            .map { it.blockUserId }
            .let {
                BlockListInfo(
                    userId = userId,
                    blockUsers = userQueryService.getUsers(it)
                )
            }
    }
}
