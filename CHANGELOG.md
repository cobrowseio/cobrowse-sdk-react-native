# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

## [2.6.0](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.5.2...v2.6.0) (2021-06-14)


### Features

* allow styling of redacted component ([614b64e](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/614b64e56a127aec693077f336935c628b609117))


### Bug Fixes

* fix for Undefined symbols error (_OBJC_CLASS_$_RCTEventDispatcher) ([d66ef6d](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/d66ef6d6767b567db524865e002867718ca6301a))

## [2.1.0] - 2019-04-26
- Added `handleSessionRequest` API to `CobrowseIO`

## [2.0.0] - 2019-04-24
### Changed
- Switched to promises for all methods that previously took callback (e.g. `createSession`, `endSession`, `currentSession`)
- Updated example project to RN 0.59
- Renamed `loadSession` to `getSession` to match other SDKs
