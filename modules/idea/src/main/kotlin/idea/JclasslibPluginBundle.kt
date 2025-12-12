package org.gjt.jclasslib.idea

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
private const val BUNDLE = "messages.jclasslibBundle"

const val I18N_PLACEHOLDER = "#"

object JclasslibPluginBundle {
    private val bundle = DynamicBundle(JclasslibPluginBundle::class.java, BUNDLE)

    @Nls
    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
        return bundle.getMessage(key, *params)
    }
}