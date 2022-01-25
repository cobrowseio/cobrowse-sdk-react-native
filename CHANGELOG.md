# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

## [2.10.0](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.9.1...v2.10.0) (2022-01-25)


### Features

* update Cobrowse native SDKs. Note on Android there is an extra consideration for this upgrade: https://github.com/cobrowseio/cobrowse-sdk-android-binary/blob/master/CHANGELOG.md[#2160](https://github.com/cobrowseio/cobrowse-sdk-react-native/issues/2160)-2021-12-08 ([756d305](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/756d3054eeb352c5545d09dc48833703c7193c53)), closes [/github.com/cobrowseio/cobrowse-sdk-android-binary/blob/master/CHANGELOG.md#2160-2021-12-08](https://github.com/cobrowseio//github.com/cobrowseio/cobrowse-sdk-android-binary/blob/master/CHANGELOG.md/issues/2160-2021-12-08)


### Bug Fixes

* remove unused type definition for accessibilityService ([0741911](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/0741911486a4146e4df38393dc6591996021105a))
* update dependencies ([999fb02](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/999fb027752d96a3a37da11f0115b14415be1127))

## [2.9.0](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.8.0...v2.9.0) (2021-11-17)


### Features

* Add support for custom remote control consent prompts ([95e1f36](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/95e1f361e5c18f8f1ffb7ae6477525aab055cd7c))

## [2.8.0](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.6.0...v2.8.0) (2021-11-17)

### Features

* Added APIs to control full device and remote control state, e.g. `session.setFullDevice(true)`
* Added TypeScript definitions


## [2.6.0](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.5.2...v2.6.0) (2021-06-14)


### Features

* allow styling of redacted component ([614b64e](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/614b64e56a127aec693077f336935c628b609117))


### Bug Fixes

* fix for Undefined symbols error (_OBJC_CLASS_$_RCTEventDispatcher) ([d66ef6d](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/d66ef6d6767b567db524865e002867718ca6301a))

## [2.1.0] - 2019-04-26

### Features

- Added `handleSessionRequest` API to `CobrowseIO`


## [2.0.0] - 2019-04-24

### Features
- Switched to promises for all methods that previously took callback (e.g. `createSession`, `endSession`, `currentSession`)
- Updated example project to RN 0.59
- Renamed `loadSession` to `getSession` to match other SDKs
