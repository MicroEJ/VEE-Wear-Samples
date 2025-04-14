/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

import com.microej.gradle.plugins.MicroejExtension

version = "1.2.0"

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
			}
		}
	}
}


