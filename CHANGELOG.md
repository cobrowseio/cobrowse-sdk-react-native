# Changelog

## [Unreleased]
- None yet

## [2.1.0] - 2019-04-26
- Added `handleSessionRequest` API to `CobrowseIO`

## [2.0.0] - 2019-04-24
### Changed
- Switched to promises for all methods that previously took callback (e.g. `createSession`, `endSession`, `currentSession`)
- Updated example project to RN 0.59
- Renamed `loadSession` to `getSession` to match other SDKs
