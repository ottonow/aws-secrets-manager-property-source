package de.ottonow.secretsmanager.propertysource

import com.amazonaws.services.secretsmanager.AWSSecretsManager
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import java.util.concurrent.TimeUnit

class AwsSecretManagerSource(private val client: AWSSecretsManager) {

    companion object {
        const val SEPARATOR_JSON_PROPERTY_NAME = "."
    }

    private val objectMapper = ObjectMapper()

    private val loadingCache = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build(object : CacheLoader<String, String>() {
            override fun load(key: String): String? {
                return getSecretValueString(key)
            }
        })

    fun getProperty(propertyName: String): Any? {
        val isJsonProperty = isJsonProperty(propertyName)
        val propertyNameSplit = propertyName.split(SEPARATOR_JSON_PROPERTY_NAME)

        val propertyNameWithoutDot = propertyNameSplit[0]

        val secretAsString = loadingCache.get(propertyNameWithoutDot)

        return if (!isJsonProperty) {
            secretAsString
        } else {
            val jsonTree = objectMapper.readTree(secretAsString)
            val jsonPropertyName = propertyNameSplit[1]
            jsonTree.get(jsonPropertyName).textValue()
        }
    }

    private fun getSecretValueString(propertyName: String): String {
        val getSecretValueRequest = GetSecretValueRequest().withSecretId(propertyName)

        val secretValue = client.getSecretValue(getSecretValueRequest)
        return secretValue.secretString
    }

    private fun isJsonProperty(propertyName: String): Boolean {
        return propertyName.contains(SEPARATOR_JSON_PROPERTY_NAME)
    }
}