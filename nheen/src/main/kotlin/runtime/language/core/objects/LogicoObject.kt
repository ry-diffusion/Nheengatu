package me.ryster.nheen.runtime.language.core.objects

import me.ryster.nheen.runtime.language.core.RuntimeObject

class LogicoObject(val value: Boolean) : RuntimeObject() {
    override fun representation(): String =
        if (value) {
            "autêntico"
        } else {
            "falso"
        }

    override val id: String = "Lógico"

    override fun toBoolean(): Boolean = value

    companion object {
        @JvmStatic
        fun runtimeTrue(): RuntimeObject = LogicoObject(true)
        @JvmStatic
        fun runtimeFalse(): RuntimeObject = LogicoObject(false)
    }
}