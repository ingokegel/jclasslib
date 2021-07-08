/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import com.exe4j.runtime.LauncherEngine
import com.sun.tools.attach.VirtualMachine
import com.sun.tools.attach.VirtualMachineDescriptor
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.attach.AttachDialog
import org.gjt.jclasslib.util.AlertType
import org.gjt.jclasslib.util.alertFacade
import org.jclasslib.agent.AgentMain
import org.jclasslib.agent.CommunicatorMBean
import java.awt.Window
import java.io.File
import javax.management.MBeanServerInvocationHandler
import javax.management.ObjectName
import javax.management.remote.JMXConnector
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

class AttachableVm(val descriptor: VirtualMachineDescriptor) {
    override fun toString(): String = "[" + descriptor.id() + "] " + descriptor.displayName().ifBlank { "<unknown>" }
}

class VmConnection(val communicator: CommunicatorMBean, private val connection: JMXConnector, private val vm: VirtualMachine) {
    fun close() {
        try {
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            vm.detach()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun attachToVm(parentWindow: Window?): VmConnection? =
    selectVm(parentWindow)?.let { attachableVm ->
        val vm = try {
            VirtualMachine.attach(attachableVm.descriptor.id()).also { vm ->
                vm.loadAgent(getAgentPath())
            }
        } catch (e: Exception) {
            alertFacade.showMessage(parentWindow, getString("message.attach.failed.0", e.message ?: ""), null, AlertType.ERROR)
            return@let null
        }
        val connectorAddress = try {
            vm.getConnectorAddress() ?: vm.run {
                startLocalManagementAgent()
                getConnectorAddress()
            }
        } catch (e: Exception) {
            alertFacade.showMessage(parentWindow, getString("message.management.agent.error.0", e.message ?: ""), null, AlertType.ERROR)
            return@let null
        }
        val connection = try {
            JMXConnectorFactory.connect(JMXServiceURL(connectorAddress))
        } catch (e: Exception) {
            alertFacade.showMessage(parentWindow, getString("message.connection.failed.0", e.message ?: ""), null, AlertType.ERROR)
            return@let null
        }
        val communicator = MBeanServerInvocationHandler.newProxyInstance(
                connection.mBeanServerConnection,
                ObjectName(AgentMain.MBEAN_NAME),
                CommunicatorMBean::class.java,
                false
        )
        VmConnection(communicator, connection, vm)
    }

private fun getAgentPath(): String =
    (File(
            System.getProperty(LauncherEngine.PROPNAME_MODULE_NAME)?.let { File(it, "../../lib/") }
                    ?: File("build/gradle/agent/libs/"),
            "jclasslib-agent.jar"
    )).canonicalPath

private fun VirtualMachine.getConnectorAddress(): String? = agentProperties.getProperty("com.sun.management.jmxremote.localConnectorAddress")

private fun selectVm(parentWindow: Window?): AttachableVm? {
    val vms = VirtualMachine.list().map { AttachableVm(it) }
    return AttachDialog(vms, parentWindow).select()
}