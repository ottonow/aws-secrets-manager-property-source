package de.ottonow.secretsmanager.propertysource

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class AwsSecretManagerPropertySourceTest {

    private val awsSecretManagerSource: AwsSecretManagerSource = mockk()
    private val awsSecretManagerPropertySource = AwsSecretManagerPropertySource(
        name = "foo",
        source = awsSecretManagerSource
    )

    @Test
    fun `should get property if it starts with secret prefix`() {
        every { awsSecretManagerSource.getProperty("foo") } returns "bar"

        val value = awsSecretManagerPropertySource.getProperty("/secret/foo")

        assert(value).isEqualTo("bar")
    }

    @Test
    fun `should not invoke aws secret manager source if prefix is missing`() {
        val value = awsSecretManagerPropertySource.getProperty("foo")

        assert(value).isNull()

        verify(exactly = 0) {
            awsSecretManagerSource.getProperty(any())
        }
    }

}