package dev.sorcerer.trueastronaut.api

/**
 * Selects an available runtime adapter without exposing its implementation
 * types to feature code. Selection policy can later support multiple runtimes.
 */
class RuntimeSelector(adapters: Collection<RuntimeAdapter>) {
    private val candidates = adapters.toList()

    fun availableAdapters(): List<Pair<RuntimeAdapter, RuntimeProbe>> =
        candidates.map { it to it.probe() }.filter { it.second.available }

    fun select(preferredId: String? = null): RuntimeAdapter? {
        val available = availableAdapters()
        return available.firstOrNull { it.first.id == preferredId }?.first
            ?: available.firstOrNull()?.first
    }
}
