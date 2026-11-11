package dev.sorcerer.trueastronaut.runtime.lsposed

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import dev.sorcerer.trueastronaut.api.HookCallback
import dev.sorcerer.trueastronaut.api.HookInstallResult
import dev.sorcerer.trueastronaut.api.HookInvocation
import dev.sorcerer.trueastronaut.api.HookRequest
import dev.sorcerer.trueastronaut.api.RuntimeAdapter
import dev.sorcerer.trueastronaut.api.RuntimeProbe
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * Legacy Xposed API adapter isolated from all feature modules.
 * This adapter must be runtime-tested on ProjectInfinity-X/API 36.
 */
class LegacyXposedRuntimeAdapter(
    private val classLoader: ClassLoader
) : RuntimeAdapter {
    override val id: String = "xposed-legacy"
    override val displayName: String = "LSPosed (legacy Xposed API)"

    private val installed = ConcurrentHashMap<String, XC_MethodHook.Unhook>()

    override fun probe(): RuntimeProbe = try {
        val version = XposedBridge.getXposedVersion()
        RuntimeProbe(
            available = true,
            runtimeVersion = "Xposed API runtime $version",
            apiLevel = version,
            detail = "Adapter loaded inside an Xposed-compatible process"
        )
    } catch (t: Throwable) {
        RuntimeProbe(
            available = false,
            detail = "Xposed bridge probe failed: ${t.javaClass.simpleName}"
        )
    }

    override fun install(request: HookRequest, callback: HookCallback): HookInstallResult {
        if (installed.containsKey(request.hookId)) return HookInstallResult.AlreadyInstalled

        return try {
            val targetClass = Class.forName(request.targetClass, false, classLoader)
            val parameterTypes = request.parameterTypeNames.map { resolveType(it) }.toTypedArray()
            val method: Method = targetClass.getDeclaredMethod(request.targetMethod, *parameterTypes)
            method.isAccessible = true

            val hook = object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    callback.before(param.toNeutralInvocation())
                }

                override fun afterHookedMethod(param: MethodHookParam) {
                    callback.after(param.toNeutralInvocation())
                }
            }

            val unhook = XposedBridge.hookMethod(method as Member, hook)
            val previous = installed.putIfAbsent(request.hookId, unhook)
            if (previous != null) {
                unhook.unhook()
                HookInstallResult.AlreadyInstalled
            } else {
                HookInstallResult.Installed
            }
        } catch (e: ClassNotFoundException) {
            HookInstallResult.Unsupported("Target class not found: ${request.targetClass}")
        } catch (e: NoSuchMethodException) {
            HookInstallResult.Unsupported(
                "Target method/signature not found: ${request.targetClass}#${request.targetMethod}"
            )
        } catch (t: Throwable) {
            HookInstallResult.Failed(
                "Hook install failed: ${t.javaClass.simpleName}: ${t.message.orEmpty()}"
            )
        }
    }

    override fun uninstall(hookId: String): HookInstallResult {
        val handle = installed.remove(hookId) ?: return HookInstallResult.Unsupported("Hook is not installed")
        return try {
            handle.unhook()
            HookInstallResult.Installed
        } catch (t: Throwable) {
            HookInstallResult.Failed("Unhook failed: ${t.javaClass.simpleName}")
        }
    }

    private fun resolveType(name: String): Class<*> = when (name) {
        "boolean" -> Boolean::class.javaPrimitiveType!!
        "byte" -> Byte::class.javaPrimitiveType!!
        "char" -> Char::class.javaPrimitiveType!!
        "short" -> Short::class.javaPrimitiveType!!
        "int" -> Int::class.javaPrimitiveType!!
        "long" -> Long::class.javaPrimitiveType!!
        "float" -> Float::class.javaPrimitiveType!!
        "double" -> Double::class.javaPrimitiveType!!
        else -> Class.forName(name, false, classLoader)
    }

    private fun XC_MethodHook.MethodHookParam.toNeutralInvocation(): HookInvocation {
        val hookParam = this
        val argumentView = object : AbstractMutableList<Any?>() {
            override val size: Int
                get() = hookParam.args.size

            override fun get(index: Int): Any? = hookParam.args[index]

            override fun set(index: Int, element: Any?): Any? {
                val previous = hookParam.args[index]
                hookParam.args[index] = element
                return previous
            }

            override fun add(index: Int, element: Any?) {
                // Xposed argument arrays have a fixed length; insertion is unsupported.
                throw UnsupportedOperationException("Hook argument count cannot be changed")
            }

            override fun removeAt(index: Int): Any? {
                // Xposed argument arrays have a fixed length; removal is unsupported.
                throw UnsupportedOperationException("Hook argument count cannot be changed")
            }
        }

        return object : HookInvocation {
            override val receiver: Any? get() = hookParam.thisObject
            override val arguments: MutableList<Any?> = argumentView
            override var result: Any?
                get() = hookParam.result
                set(value) { hookParam.result = value }
            override var throwable: Throwable?
                get() = hookParam.throwable
                set(value) {
                    if (value == null) hookParam.setResult(hookParam.result)
                    else hookParam.setThrowable(value)
                }
        }
    }
}
