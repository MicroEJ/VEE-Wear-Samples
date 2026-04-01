===========
 Changelog
===========

All notable changes to this project will be documented in this file.

The format is based on `Keep a Changelog <https://keepachangelog.com/en/1.0.0/>`_,
and this project adheres to `Semantic Versioning <https://semver.org/spec/v2.0.0.html>`_.

------------------
2.0.3 - 2026-04-01
------------------

Changed
=====

- Update SDK version to 1.5.0

------------------
2.0.2 - 2025-10-10
------------------

Changed
=====

- Update Kernel dependency to version 2.0.3.

------------------
2.0.1 - 2025-09-30
------------------

Changed
=====

- Update ``Readme`` to link to Kernel Virtual Device 2.0.2.

------------------
2.0.0 - 2025-09-29
------------------

Added
=====

- Add the Breath application.
- Add the System application with navigator, watchface picker and activity launcher.

Changed
=======

- Update MicroEJ SDK plugin to version 1.3.1.
- Update Kernel dependency to version 2.0.2.
- Update wear-services dependency to version 2.0.0.
- Update wear-util dependency to version 2.0.0.
- Update the UI of the ``wear-weather-app``.
- Replace the old ``VectorLabel`` with UI pack's ``Label``.
- Use ``ResourceImage`` whenever possible.
- Use kernel ``FontServiceProvider`` instead of fonts in resources in ``wear-stopwatch-app``, ``wear-weather-app`` and ``wear-training-app``.
- Replace ``Scroll`` with ``SwipeContainer`` in ``wear-training-app``.
- Change all SVG resources to Android Vector Drawables in ``wear-training-app``.
- Use ``BufferedVectorImage`` to cache drawing of texts in ``wear-health-app`` and ``wear-settings-app`` with custom ``RenderableVectorLabel``.
- Remove interpolation on swipe in ``wear-health-app`` with custom ``SwipeEventHandler``.
- Use ``ResourceService`` to load all images.

Fixed
=====

- Fix code quality issues and null-analysis errors. 

------------------
1.4.2 - 2025-06-30
------------------

Changed
=======

- Update video preview in root README.

------------------
1.4.1 - 2025-06-30
------------------

Changed
=======

- Use non-deprecated selector in ``wear-stopwatch-app``.
- Update ``Readme`` to document Virtual Device use.

------------------
1.4.0 - 2025-06-10
------------------

Changed
=======

- Update background implementation in ``wear-health-app``.
- Update default kernel to ATS3085S Kernel 1.3.0.
- Update microui dependency to version 3.6.0.
- Update microvg dependency to version 1.5.1.
- Update mwt dependency to version 3.6.1.
- Update widget dependency to version 5.3.1.

------------------
1.3.2 - 2025-05-05
------------------

Changed
=======

- Update default kernel to ATS3085S Kernel 1.2.1.

------------------
1.3.1 - 2025-04-30
------------------

Changed
=======

- Removed unused import in ``wear-health-app``.

------------------
1.3.0 - 2025-04-30
------------------

Added
=====

- Add name and bluetooth address of the device in ``wear-settings-app``.

Changed
=======

- Optimize vertical scroll in ``wear-health-app``.
- Change scroll animation speed and motion functions for ``wear-health-app``, ``wear-settings-app`` and ``wear-training-app``.
- Align all submodules versions with a variable declared in the root ``build.gradle.kts``.
- Update default kernel to ATS3085S Kernel 1.2.0.
- Update wear-services dependency to version 1.1.0.

Removed
=======

- Remove all submodules changelogs, only the root Changelog will be maintained from now on.

------------------
1.2.0 - 2025-04-14
------------------

Added
=====

- Add the RunAll application.
- Add battery complication in ``wear-settings-app``.
- Add the Training Application.
- Update MicroEJ SDK plugin to version 1.2.0.

Changed
=======

- Update default kernel to ATS3085S Kernel 1.1.1.
- Improve README.
- Changed image format to pre-multiplied alpha when needed.
- Update lap time calculation to allow pauses in ``wear-stopwatch-app``.
- Change displayed lap time from cumulative to individual lap time in ``wear-stopwatch-app``.
- Update icons for ``wear-compass-app``, ``wear-fitness-app``, ``wear-health-app``, ``wear-settings-app``, and ``wear-weather-app``.


------------------
1.1.0 - 2025-03-12
------------------

Changed
=======

- Update wear-services dependency to version 1.0.0.
- Update wear-util dependency to version 1.0.2.
- Update default kernel to ATS3085S Kernel 1.1.0.
- Document how to use a local Kernel and/or VEE Port in the README.
- Document how to configure the images heap size in the README.
- Rename the file ``Jenkinsfile_FireFinch`` into ``Jenkinsfile``.

Removed
=======

- Remove properties ``groupVariant`` and ``buildFeatures``.

------------------
1.0.0 - 2025-02-20
------------------

Added
=====

- Add the Compass application.
- Add the Fitness application.
- Add the Flower application.
- Add the Health application.
- Add the Hello World application.
- Add the Settings application.
- Add the Sport application.
- Add the Stopwatch application.
- Add the Weather application.


--------------

.. ReStructuredText
.. Copyright 2024-2026 MicroEJ Corp. All rights reserved.
.. Use of this source code is governed by a BSD-style license that can be found with this software.
