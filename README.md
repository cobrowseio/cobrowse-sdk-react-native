# Cobrowse.io - React Native SDK

Cobrowse.io is 100% free and easy to try out in your own apps. Please see full documentation at [https://docs.cobrowse.io](https://docs.cobrowse.io).

Try our **online demo** at the bottom of our homepage at <https://cobrowse.io/#tryit>.

## Installation

```bash
npm install --save cobrowse-sdk-react-native
react-native link
```
**Note:** For iOS you need to be using Pods to manage dependencies for `react-native link` to work out of the box (and also remember to run `pod install` after the link step).

### Add your License Key

Please register an account and generate your free License Key at <https://cobrowse.io/dashboard/settings>.

This will associate sessions from your mobile app with your Cobrowse account.

```javascript
import CobrowseIO from 'cobrowse-sdk-react-native';

CobrowseIO.license = "<your license key here>";
CobrowseIO.start();

```

## Try it out

Once you have your app running in the iOS Simulator or on a physical device, navigate to <https://cobrowse.io/dashboard> to see your device listed. You can click the "Connect" button to initiate a Cobrowse session!

## Optional features

[Identify your devices](https://docs.cobrowse.io/sdk-features/identify-your-devices)

[Use 6-digit codes](https://docs.cobrowse.io/sdk-features/6-digit-codes)

[Redact sensitive data](https://docs.cobrowse.io/sdk-features/redact-sensitive-data)

[Customize the interface](https://docs.cobrowse.io/sdk-features/customize-the-interface)

[Initiate sessions with push](https://docs.cobrowse.io/sdk-features/initiate-sessions-with-push)

[Full device capabilities](https://docs.cobrowse.io/sdk-features/full-device-capabilities)

[Advanced features](https://docs.cobrowse.io/sdk-features/advanced-features)

## Questions?
Any questions at all? Please email us directly at [hello@cobrowse.io](mailto:hello@cobrowse.io).

## Requirements

* iOS 9.0 or later
* Android API version 19 (4.4 KitKat) or later
