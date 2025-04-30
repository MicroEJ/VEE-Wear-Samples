/*
 * Copyright 2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

group = "com.microej.example.wear"
version = rootProject.ext.get("samplesVersion") as String

plugins {
	alias(libs.plugins.microej.application)
}

microej {
	applicationEntryPoint = "com.microej.example.wear.RunAllAppEntryPoint"
}

dependencies {
	implementation(libs.microej.edc)
	implementation(libs.microej.kf)

	// list of apps to be started with this app
	microejApplication(project(":wear-compass-app"))
	microejApplication(project(":wear-fitness-app"))
	microejApplication(project(":wear-flower-app"))
	microejApplication(project(":wear-health-app"))
	microejApplication(project(":wear-helloworld-app"))
	microejApplication(project(":wear-settings-app"))
	microejApplication(project(":wear-sport-app"))
	microejApplication(project(":wear-stopwatch-app"))
	microejApplication(project(":wear-training-app"))
	microejApplication(project(":wear-weather-app"))

	microejVee(rootProject.extra.get("kernel") as String)
}
