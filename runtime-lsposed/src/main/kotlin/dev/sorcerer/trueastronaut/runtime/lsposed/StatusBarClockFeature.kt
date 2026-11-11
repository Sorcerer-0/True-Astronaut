package dev.sorcerer.trueastronaut.runtime.lsposed

import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.format.DateFormat
import android.widget.TextView
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.WeakHashMap

/** LSPosed-only status-bar clock controls; no ROM files are replaced. */
internal object StatusBarClockFeature {
    private const val KEY_SECONDS = "true_astronaut_clock_seconds"
    private const val KEY_BOLD = "true_astronaut_clock_bold"
    private val handler = Handler(Looper.getMainLooper())
    private val tickers = WeakHashMap<TextView, Runnable>()

    fun install(classLoader: ClassLoader): Boolean = try {
        val clockClass = Class.forName("com.android.systemui.statusbar.policy.Clock", false, classLoader)
        XposedBridge.hookAllMethods(clockClass, "updateClock", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val clock = param.thisObject as? TextView ?: return
                applyStyle(clock)
                configureTicker(clock)
            }
        })
        XposedBridge.log("TrueAstronaut: status-bar clock hooks installed")
        true
    } catch (t: Throwable) {
        XposedBridge.log("TrueAstronaut: status-bar clock hook unavailable: \${t.javaClass.simpleName}: \${t.message.orEmpty()}")
        false
    }

    private fun enabled(view: TextView, key: String): Boolean = try {
        Settings.System.getInt(view.context.contentResolver, key, 0) == 1
    } catch (_: Throwable) { false }

    private fun applyStyle(view: TextView) {
        view.setTypeface(view.typeface, if (enabled(view, KEY_BOLD)) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
    }

    private fun configureTicker(view: TextView) {
        if (!enabled(view, KEY_SECONDS)) {
            tickers.remove(view)?.let(handler::removeCallbacks)
            return
        }
        if (tickers.containsKey(view)) return

        val reference = WeakReference(view)
        val ticker = object : Runnable {
            override fun run() {
                val clock = reference.get() ?: return
                if (!clock.isAttachedToWindow || !enabled(clock, KEY_SECONDS)) {
                    tickers.remove(clock)
                    return
                }
                val locale: Locale = clock.resources.configuration.locales[0] ?: Locale.getDefault()
                val pattern = DateFormat.getBestDateTimePattern(locale, "ss")
                val seconds = SimpleDateFormat(pattern, locale).format(Date())
                val base = clock.text?.toString().orEmpty()
                    .replace(Regex(":\\d{2}(?=\\s?(AM|PM)$)", RegexOption.IGNORE_CASE), "")
                    .replace(Regex(":\\d{2}$"), "")
                val marker = Regex("\\s?(AM|PM)$", RegexOption.IGNORE_CASE).find(base)
                clock.text = if (marker != null) {
                    base.substring(0, marker.range.first) + ":" + seconds + base.substring(marker.range.first)
                } else base + ":" + seconds
                applyStyle(clock)
                handler.postDelayed(this, 1000L)
            }
        }
        tickers[view] = ticker
        handler.post(ticker)
    }
}
