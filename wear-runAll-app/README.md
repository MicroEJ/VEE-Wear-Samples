# Overview

This application is a convenient way to start all the sample applications of the repository at once on the
simulator.
This application is not meant to be deployed on the device as a Feature: it will do nothing.

# Usage

The sample can be executed on the VEE Wear Demo Kernel.

## Run on simulator

In Android Studio or IntelliJ:

- Open the Gradle tool window by clicking on the elephant icon on the right side,
- Expand the `wear-runAll-app` item,
- From the nested `Tasks` list, expand the `microej` item,
- Double-click on `runOnSimulator`,
- The Simulator starts, the traces are visible in the Run view.

To open an app:

- Click any button of the watch to open the application launcher,
- Tap the app icon to open the app.

To quit an app:

- Click any button of the watch to return to the current watchface.

To open a watchface:

- Long press on the current watchface to open the watchface picker,
- Swipe to browse the installed watchfaces,
- Tap any watchface thumbnail to open the watchface.

# Requirements

* MICROEJ SDK 6.
* A VEE Wear Kernel 1.1.0 or higher.

# Dependencies

_All dependencies are retrieved transitively by Gradle_.

# Source

N/A.

# Restrictions

None.

---
_Copyright 2025 MicroEJ Corp._  
_Use of this source code is governed by a BSD-style license that can be found with this software._
