/*
 * Copyright 2024-2026 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

import com.microej.gradle.plugins.MicroejExtension

ext.set("samplesVersion", "2.0.3")

plugins {
	alias(libs.plugins.microej.application) apply false
}

allprojects {
	project.afterEvaluate {
		val kernelVariant = findProperty("kernelVariant") as String? ?: error("kernelVariant property is not set")
		extra["kernel"] = kernelVariant
		project.pluginManager.withPlugin(libs.plugins.microej.application.get().pluginId) {
			configure<MicroejExtension> {
				produceVirtualDeviceDuringBuild()
				produceFeatureDuringBuild()
				skippedCheckers = "changelog"
			}
		}

		tasks.findByName("shrinkRuntimeEnvironment")?.enabled = false // see M0090IDE-5189
	}
}
