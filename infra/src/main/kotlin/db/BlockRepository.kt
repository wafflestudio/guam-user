package waffle.guam.user.infra.db

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Service

interface BlockRepository {
    suspend fun save(block: Block): Block

    suspend fun findAllByUserId(userId: Long): List<Block>

    suspend fun deleteByUserIdAndBlockUserId(userId: Long, blockUserId: Long)
}

@Service
internal class BlockRepositoryImpl(
    private val blockDao: BlockDao,
) : BlockRepository {

    override suspend fun save(block: Block): Block =
        blockDao.save(block.toTable()).toDomain()

    override suspend fun findAllByUserId(userId: Long): List<Block> =
        blockDao.findAllByUserId(userId).map { it.toDomain() }.toList()

    override suspend fun deleteByUserIdAndBlockUserId(userId: Long, blockUserId: Long) {
        blockDao.deleteByUserIdAndBlockUserId(userId, blockUserId)
    }
}

internal interface BlockDao : CoroutineCrudRepository<BlockTable, Long> {
    fun findAllByUserId(userId: Long): Flow<BlockTable>
    suspend fun deleteByUserIdAndBlockUserId(userId: Long, blockUserId: Long)
}

@Table("blocks")
internal data class BlockTable(
    val id: Long,
    val userId: Long,
    val blockUserId: Long,
)

internal fun BlockTable.toDomain() = Block(id = id, userId = userId, blockUserId = blockUserId)
internal fun Block.toTable() = BlockTable(id = id, userId = userId, blockUserId = blockUserId)
