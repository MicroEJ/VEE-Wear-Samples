/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

group = "com.microej.example.wear"
version = rootProject.ext.get("samplesVersion") as String

plugins {
	alias(libs.plugins.microej.application)
}

microej {
	applicationEntryPoint = "com.microej.example.wear.settings.SettingsAppEntryPoint"
}

dependencies {
	implementation(libs.microej.edc)
	implementation(libs.microej.kf)
	implementation(libs.microej.microui)
	implementation(libs.microej.drawing)
	implementation(libs.microej.microvg)

	implementation(libs.microej.mwt)
	implementation(libs.microej.widget)
	implementation(libs.microej.motion)

	implementation(libs.microej.veewear.services)
	implementation(libs.microej.veewear.util)

	microejVee(rootProject.extra.get("kernel") as String)
	microejApplication(project(":wear-system-app"))
}
