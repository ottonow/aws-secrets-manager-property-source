package de.ottonow.secretsmanager.propertysource

import assertk.assert
import assertk.assertions.isEqualTo
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClient
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class AwsSecretManagerSourceTest {

    private val client: AWSSecretsManagerClient = mockk()
    private val awsSecretManagerSource = AwsSecretManagerSource(client)

    @Test
    fun `should get simple property`() {
        val propertyName = "myProperty"

        every { client.getSecretValue(GetSecretValueRequest().withSecretId(propertyName)) } returns GetSecretValueResult().withSecretString(
            "mySecret"
        )

        val propertyValue = awsSecretManagerSource.getProperty(propertyName)
        assert(propertyValue).isEqualTo("mySecret")
    }

    @Test
    fun `should get json property field value`() {
        val propertyName = "myProperty"

        every { client.getSecretValue(GetSecretValueRequest().withSecretId(propertyName)) } returns GetSecretValueResult().withSecretString(
            """{
                "username": "foo",
                "password": "bar"
                }
                """
        )

        assert(awsSecretManagerSource.getProperty("$propertyName.username")).isEqualTo("foo")
        assert(awsSecretManagerSource.getProperty("$propertyName.password")).isEqualTo("bar")
    }

}