package de.ottonow.secretsmanager.propertysource

import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment

class AwsSecretManagerPropertySourceEnvironmentPostProcessor : EnvironmentPostProcessor {

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        if (!initialized) {
            val client = AWSSecretsManagerClientBuilder.standard()
                .build()

            val awsSecretManagerPropertySource = AwsSecretManagerPropertySource(
                name = PARAMETER_STORE_PROPERTY_SOURCE_NAME,
                source = AwsSecretManagerSource(client)
            )

            environment.propertySources.addFirst(awsSecretManagerPropertySource)
            initialized = true
        }
    }

    companion object {

        private const val PARAMETER_STORE_PROPERTY_SOURCE_NAME = "AWSParameterStorePropertySource"

        private var initialized: Boolean = false
    }
}
