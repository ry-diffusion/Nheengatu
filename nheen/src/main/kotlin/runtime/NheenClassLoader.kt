package me.ryster.nheen.runtime

class NheenClassLoader: ClassLoader() {
    fun loadClass(name: String, bytes: ByteArray): Class<*> {
        return defineClass(name, bytes, 0, bytes.size)
    }
}