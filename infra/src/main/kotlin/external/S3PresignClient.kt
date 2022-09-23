package waffle.guam.user.infra.external

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

interface S3PresignClient {
    suspend fun getPresigendUrl(path: String): String
}

@EnableConfigurationProperties(S3Properties::class)
@Service
class S3PresignClientImpl(
    private val s3Properties: S3Properties,
) : S3PresignClient {
    private val presigner: S3Presigner by lazy { buildClient() }

    override suspend fun getPresigendUrl(path: String): String {
        val request: PutObjectRequest = PutObjectRequest.builder()
            .bucket(s3Properties.bucket)
            .key(path)
            .acl("public-read")
            .build()

        val putObjectPresignRequest: PutObjectPresignRequest = PutObjectPresignRequest
            .builder()
            .signatureDuration(Duration.ofMinutes(10))
            .putObjectRequest(request)
            .build()

        val presignedPutObjectRequest = presigner.presignPutObject(putObjectPresignRequest)

        return presignedPutObjectRequest.url().toExternalForm()
    }

    private fun buildClient(): S3Presigner =
        S3Presigner.builder()
            .region(Region.of(s3Properties.region))
            .credentialsProvider(StaticCredentialsProvider.create(s3Properties.credentials))
            .build()
}

@ConstructorBinding
@ConfigurationProperties("user.aws.s3")
data class S3Properties(
    val accessKey: String = "",
    val secretKey: String = "",
    val bucket: String = "",
    val region: String = "",
) {
    val credentials: AwsCredentials
        get() = AwsBasicCredentials.create(accessKey, secretKey)
}
