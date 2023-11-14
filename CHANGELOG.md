# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

### [2.17.3](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.17.2...v2.17.3) (2023-11-14)


### Bug Fixes

* use header style import to avoid custom compile flags ([#44](https://github.com/cobrowseio/cobrowse-sdk-react-native/issues/44)) ([a9be650](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/a9be6506e8cddffb046a3c5ba5ccbd054d797c7e))

### [2.17.2](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.17.1...v2.17.2) (2023-09-12)

### [2.17.1](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.17.0...v2.17.1) (2023-08-24)

## [2.17.0](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.16.0...v2.17.0) (2023-08-01)


### Features

* add support for webview redaction ([#36](https://github.com/cobrowseio/cobrowse-sdk-react-native/issues/36)) ([92c1f2c](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/92c1f2c71274f57797cd1c3ce543b09fd0b2424f))

## [2.16.0](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.15.0...v2.16.0) (2023-05-11)


### Features

* Add support for specifying the device capabilities ([#30](https://github.com/cobrowseio/cobrowse-sdk-react-native/issues/30)) ([5f409fa](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/5f409fab2a9d9c5e6902ce36887f3d4021493b36))

## [2.15.0](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.14.0...v2.15.0) (2023-04-10)


### Features

* Update Redaction/Unredaction ([#25](https://github.com/cobrowseio/cobrowse-sdk-react-native/issues/25)) ([105e01e](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/105e01e17c50dc11b069ec19c781edcde1962041))


### Bug Fixes

* CobrowseView methods return elements not components ([#27](https://github.com/cobrowseio/cobrowse-sdk-react-native/issues/27)) ([4a3131c](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/4a3131c41dfdac35d99bb0e56709255db0207393))

## [2.14.0](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.13.0...v2.14.0) (2023-03-20)


### Features

* Allow full device customization ([#19](https://github.com/cobrowseio/cobrowse-sdk-react-native/issues/19)) ([7370983](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/73709834f9035c2024f1c02f3175a4cb7c4bb2ac))
* Allow session controls customization ([#20](https://github.com/cobrowseio/cobrowse-sdk-react-native/issues/20)) ([c441217](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/c441217f5e7dec7eda194d551017ec3523637d49))

## [2.13.0](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.12.0...v2.13.0) (2022-09-21)


### Features

* Xcode 14 has dropped support for iOS versions below iOS 13, as such we have also dropped support for those versions in this release. ([152691c](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/152691ce88a2b9559c6e4c14e1e8d4cfaf36dc47))

## [2.12.0](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.11.2-unredaction.0...v2.12.0) (2022-09-07)


### Features

* add support for redacting/unredacting views not managed by react native ([db4ed8a](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/db4ed8a57da53c2acc0310975f5eeda914db05f7))
* add support for redacting/unredacting views not managed by react native on Android ([00e3036](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/00e3036c2247b8a20ed705543ca9df4aa54218f6))
* support nested redaction/unredaction on Android ([f35bb82](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/f35bb821187489b03763c5f1732b9104666639bd))


### Bug Fixes

* allow nesting of redactions / unredactions (iOS only so far) ([8d598cf](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/8d598cf293def0551933588e6b11539583dffafc))
* update native SDKs ([ea56fc1](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/ea56fc136f638afdeda9d4a5eff8f15fcf096a9a))

### [2.11.1](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.11.0...v2.11.1) (2022-03-15)


### Bug Fixes

* update native android SDK ([20265f1](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/20265f177d79c9834ac89113c83f5dcd55fd2d26))

## [2.11.0](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.10.1...v2.11.0) (2022-02-07)


### Features

* add event that's called the first time a session is fetched from the server ([645da52](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/645da52ec3158ab8ec9dd697a32e211f73a94738))

### [2.10.1](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.10.0...v2.10.1) (2022-01-31)


### Bug Fixes

* update Cobrowse native SDK versions ([880fe06](https://github.com/cobrowseio/cobrowse-sdk-react-native/commit/880fe06f2b9f509511608f3b89226b5b71b93141))

## [2.10.0](https://github.com/cobrowseio/cobrowse-sdk-react-native/compare/v2.9.1...v2.10.0) (2022-01-25)


### Features

* update Cobrowse native SDKs. Note on Android there is an extra consideration for this upgrade: https://github.com/cobrowseio/cobrowse-sdk-android-binary/blob/master/CHANGELOG.md#2160-2021-12-08


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
