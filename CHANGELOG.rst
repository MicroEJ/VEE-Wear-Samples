===========
 Changelog
===========

All notable changes to this project will be documented in this file.

The format is based on `Keep a Changelog <https://keepachangelog.com/en/1.0.0/>`_,
and this project adheres to `Semantic Versioning <https://semver.org/spec/v2.0.0.html>`_.

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
.. Copyright 2024-2025 MicroEJ Corp. All rights reserved.
.. Use of this source code is governed by a BSD-style license that can be found with this software.
