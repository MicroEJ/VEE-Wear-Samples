# Overview

The VEE Wear System application provides:

- an implementation of Navigator that is based on an Activity launcher and a Watchface picker.

# Usage

The sample can be executed on the VEE Wear Demo Kernel.

## Run on simulator

In Android Studio or IntelliJ:

- Open the Gradle tool window by clicking on the elephant icon on the right side,
- Expand the `wear-system-app` item,
- From the nested `Tasks` list, expand the `microej` item,
- Double-click on `runOnSimulator`,
- The Simulator starts, the traces are visible in the Run view.

## Build the Feature

To build the Feature for the default Kernel (Actions ATS3085S):

- Open the Gradle tool window by clicking on the elephant icon on the right side,
- Expand the `wear-system-app` item,
- From the nested `Tasks` list, expand the `microej` item,
- Double-click on `buildFeature`,
- At the end of the build, the Feature file (`application.fo`) can be found in the `build` directory
  in `build/application`.

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
_Copyright 2024-2025 MicroEJ Corp._  
_Use of this source code is governed by a BSD-style license that can be found with this software._
