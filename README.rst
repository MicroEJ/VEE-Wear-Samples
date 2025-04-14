.. image:: https://shields.microej.com/endpoint?url=https://repository.microej.com/packages/badges/sdk_6.0.json
   :alt: sdk_6.0 badge
   :align: left
.. image:: https://shields.microej.com/endpoint?url=https://repository.microej.com/packages/badges/arch_8.0.json
   :alt: arch_8.0 badge
   :align: left
.. image:: https://shields.microej.com/endpoint?url=https://repository.microej.com/packages/badges/gui_3.json
   :alt: gui_3 badge
   :align: left

.. class:: center

Overview
========

This project provides samples of typical smartwatch applications.
These applications serve as code examples, demonstrating best practices and how to use the VEE Wear Kernel APIs.

Each sample provides a ``README.md`` that contains instructions on how to run it.

To run all the sample applications at once in the simulator, please refer to the ``README.md`` of the ``wear-runAll-app``.

To learn more about application development with the VEE Wear Framework, see https://docs.microej.com/en/latest/VEEWearUserGuide/framework.html.

Details
=======

Watchfaces: Flower App & Sport App
----------------------------------

These two applications provide respectively a stylish and a sport-like watchface to customize the device.

.. image:: images/watch_faces.png

Compass App
-----------

This application provides an example for a compass implementation.

.. image:: images/compass_app.png

Fitness App
-----------

This application offers an example to monitor steps and calorie intake.

.. image:: images/fitness_app.png

Health App
----------

This application allows you to take a quick look at all the health-related data monitored by the watch.

.. image:: images/health_app.png

Hello World
-----------

A simple application displaying 'Hello World' in an independent Activity.

Settings App
------------

This application provides an example for a Settings Activity.

.. image:: images/settings_app.png

Stopwatch App
-------------

This application provides a Stopwatch Activity.

.. image:: images/stopwatch_app.png

Training App
------------

This application is an example for a Training Activity to monitor heart rate, distance and speed during a training.

.. image:: images/training_app.png

Weather App
-----------

This application offers an implementation of a Weather Activity.

.. image:: images/weather_app.png

Troubleshooting
===============

Image Format
------------

ARGB8888, ARGB1555, and ARGB4444 transparent images may need to be pre-multiplied to be rendered properly by the GPU.

This can be achieved by adding the suffix ``_PRE`` to the image format in the ``*.images.list`` resource files.

For more details about image output format, see https://docs.microej.com/en/latest/ApplicationDeveloperGuide/UI/MicroUI/images.html#standard-output-formats.

--------------

.. ReStructuredText
.. Copyright 2024-2025 MicroEJ Corp. All rights reserved.
.. Use of this source code is governed by a BSD-style license that can be found with this software.