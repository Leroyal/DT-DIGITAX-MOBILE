# DT-DIGITAX-MOBILE

DigiTax mobile serves mostly as an expense report generator on the go. Objectively, this app
aims to automate and make simpler the tracking of tax related receipts, donations, and other
expenses. Some features include allowing the user to take photos of receipts and log expenses. 
When you take a photo of a receipt, the app automatically reads the receipt and translates it 
into a logged expense. You can organize your expenses by categories, like mileage, travel and food.

All expenses that are tax related are synchronized online and pre-populated in related forms for 
when the user starts the tax filing process.

[![GitHub issues](https://img.shields.io/github/issues/Leroyal/DT-DIGITAX-MOBILE)](https://github.com/Leroyal/DT-DIGITAX-MOBILE/issues) [![Build Status](https://github.com/Leroyal/DT-DIGITAX-MOBILE/workflows/CI/badge.svg)](https://github.com/Leroyal/DT-DIGITAX-MOBILE/workflows/CI/badge.svg)

## Table of Contents

* [Technologies](#technologies)
    * [HTTP Stack](#http-stack)
    * [Firebase Analytics](#firebase-analytics)
    * [Google Maps](#maps)   
* [Principles of Navigation](#navigation)    
* [Feedback and Support](#giving-feedback)
* [TODOs & Optimizations](#optimizations)
* [CHANGELOG](#changelog)

<a name="technologies"></a>
## Technologies

Below is a short list of technologies currently being used for DigiTax mobile app:

<a name="http-stack"></a>
### HTTP Stack

OkHTTP is an open source project designed to be an efficient HTTP client. It supports 
the SPDY protocol. SPDY is the basis for HTTP 2.0 and allows multiple HTTP requests to be 
multiplexed over one socket connection.

Source: [https://square.github.io/okhttp/](https://square.github.io/okhttp/)

<a name="firebase-analytics"></a>
### Google Firebase Analytics

Google Analytics for Firebase provides free, unlimited reporting on 
up to 500 distinct events. The SDK automatically captures certain key events and user properties, 
and you can define your own custom events to measure the things that uniquely matter to your business.

Source: [https://firebase.google.com/products/analytics](https://firebase.google.com/products/analytics)

<a name="maps"></a>
### Google Maps

With the Google Maps SDK for Android, you can add maps based on Google Maps data to your application.

Source: [https://developers.google.com/maps/documentation/android-sdk/intro](https://developers.google.com/maps/documentation/android-sdk/intro)

<a name="navigation"></a>
## Principles of Navigation

Navigation refers to the interactions that allow users to navigate across, into, and back out from 
the different pieces of content within your app.

Source: [https://developer.android.com/topic/libraries/architecture/navigation#fixed](https://developer.android.com/topic/libraries/architecture/navigation#fixed)

<a name="giving-feedback"></a>
## Feedback and Support

For any comments, questions or concerns, reach out to through email, support@digitaltaxusa.com, 
or Slack channel #dt_digitax_mobile_support

<a name="optimizations"></a>
## TODOs & Optimizations

Here is a list of potential optimizations required for the project. This list omits standard constant 
updates such as gradle dependencies and properties.

1. Layouts; can convert all layouts to ConstraintLayouts for UI/UX and rendering optimizations./

<a name="changelog"></a>
## CHANGELOG

1. 2020-06-14: Add README to repo 1.0.0/
