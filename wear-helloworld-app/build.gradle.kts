/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

group = "com.microej.example.wear"
version = "1.1.0"

plugins {
	alias(libs.plugins.microej.application)
}

microej {
	applicationEntryPoint = "com.microej.example.wear.helloworld.HelloWorldAppEntryPoint"
}

dependencies {
	implementation(libs.microej.edc)
	implementation(libs.microej.kf)
	implementation(libs.microej.microui)
	implementation(libs.microej.microvg)

	implementation(libs.microej.mwt)

	implementation(libs.microej.veewear.services)
	implementation(libs.microej.veewear.util)

	microejVee(rootProject.extra.get("kernel") as String)
}
