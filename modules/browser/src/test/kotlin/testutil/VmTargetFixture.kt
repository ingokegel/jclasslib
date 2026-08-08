/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.testutil

import com.sun.tools.attach.VirtualMachine
import org.gjt.jclasslib.browser.AttachableVm
import org.gjt.jclasslib.browser.VmConnection
import org.gjt.jclasslib.browser.connectToVm
import java.io.File

object SleepTarget {
    const val CLASS_NAME = "org.gjt.jclasslib.testutil.SleepTarget"
    const val CLASS_FILE_NAME = "org/gjt/jclasslib/testutil/SleepTarget"

    @JvmStatic
    fun main(args: Array<String>) {
        // force a hidden class into the running VM
        Runnable { println("lambda") }.run()
        Thread.sleep(3_600_000)
    }
}

class VmTargetFixture {

    private var process: Process? = null
    private val connections = mutableListOf<VmConnection>()

    val pid: Long
        get() = process?.pid() ?: error("target VM not started")

    fun start(): AttachableVm {
        val javaBin = File(System.getProperty("java.home"), "bin/java")
        val started = ProcessBuilder(
                javaBin.absolutePath,
                "-cp", System.getProperty("java.class.path"),
                SleepTarget.CLASS_NAME
        ).redirectErrorStream(true).start()
        process = started

        val expectedId = started.pid().toString()
        val deadline = System.currentTimeMillis() + 30_000
        while (System.currentTimeMillis() < deadline) {
            val descriptor = VirtualMachine.list().firstOrNull { it.id() == expectedId }
            if (descriptor != null) {
                return AttachableVm(descriptor)
            }
            if (!started.isAlive) {
                error("target VM died: ${started.inputStream.readBytes().decodeToString()}")
            }
            Thread.sleep(100)
        }
        error("target VM did not appear in VirtualMachine.list()")
    }

    fun connect(attachableVm: AttachableVm): VmConnection {
        // attaching repeatedly in quick succession can fail transiently ("Premature EOF")
        var lastMessages: List<String> = emptyList()
        repeat(3) { attempt ->
            var connection: VmConnection? = null
            withFakeAlertFacade { alerts ->
                connection = connectToVm(attachableVm, null)
                lastMessages = alerts.messages.map { it.mainMessage }
            }
            val current = connection
            if (current != null) {
                connections.add(current)
                return current
            }
            if (attempt < 2) {
                Thread.sleep(500)
            }
        }
        error("attach failed: $lastMessages")
    }

    fun stop() {
        connections.forEach { it.close() }
        connections.clear()
        process?.let {
            it.destroy()
            if (!it.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)) {
                it.destroyForcibly()
            }
        }
        process = null
    }
}
