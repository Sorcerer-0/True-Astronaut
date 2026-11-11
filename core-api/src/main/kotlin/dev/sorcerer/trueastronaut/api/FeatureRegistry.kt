package dev.sorcerer.trueastronaut.api

/**
 * Registry contains feature backends selected by the composition root.
 * It deliberately knows nothing about Android, LSPosed, or a particular ROM.
 */
class FeatureRegistry(backends: Collection<FeatureBackend>) {
    private val byId: Map<FeatureId, FeatureBackend> = backends.associateBy { it.featureId }

    init {
        require(byId.size == backends.size) { "Duplicate feature backend IDs" }
    }

    fun featureIds(): Set<FeatureId> = byId.keys

    fun status(id: FeatureId): FeatureStatus? =
        byId[id]?.readStatus()

    fun apply(id: FeatureId, config: FeatureConfig): FeatureApplyResult {
        val backend = byId[id]
            ?: return FeatureApplyResult.Unsupported("No backend registered for ${id.value}")

        val capability = backend.probe()
        if (capability.level == CapabilityLevel.UNSUPPORTED) {
            return FeatureApplyResult.Unsupported(
                capability.reason ?: "Required device or runtime capability is unavailable"
            )
        }

        return backend.apply(config)
    }
}
