package de.ottonow.secretsmanager.propertysource

import org.springframework.core.env.PropertySource

class AwsSecretManagerPropertySource(
    name: String,
    source: AwsSecretManagerSource
) : PropertySource<AwsSecretManagerSource>(name, source) {

    companion object {
        const val PREFIX_SECRET = "/secret/"
    }

    override fun getProperty(propertyName: String): Any? {
        return if (propertyName.startsWith(PREFIX_SECRET)) {
            source.getProperty(propertyName.replace(PREFIX_SECRET, ""))
        } else null
    }
}