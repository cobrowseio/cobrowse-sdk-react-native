# Cobrowse.io - React Native SDK

Cobrowse.io is 100% free and easy to try out in your own apps. Please see full documentation at [https://cobrowse.io/docs](https://cobrowse.io/docs).

Try our **online demo** at the bottom of our homepage at <https://cobrowse.io/#tryit>.

## Installation

```bash
npm install --save cobrowse-sdk-react-native
react-native link
```
**Note:** For iOS you need to be using Pods to manage dependencies for `react-native link` to work out of the box (and also remember to run `pod install` after the link step).

Once you've signed up for a free account at [cobrowse.io](https://cobrowse.io), you'll be able to find your license key at <https://cobrowse.io/dashboard/settings>. You can add this to your SDK setup:

```javascript
import CobrowseIO from 'cobrowse-sdk-react-native';

CobrowseIO.license = "<your license key here>";

```

### Further Reading

[User Initiated Sessions](./docs/user-initiated-sessions.md)

[API Documentation](./docs/api.md)

## Optional features

[Initiate sessions with push](https://cobrowse.io/docs#initiate-with-push)

[Use 6-digit codes](https://cobrowse.io/docs#user-generated-codes)

[Redact sensitive data](https://cobrowse.io/docs#redact-sensitive-data)

## Questions?
Any questions at all? Please email us directly at [hello@cobrowse.io](mailto:hello@cobrowse.io).

## Requirements

* iOS 9.0, Android API 19 or above.
