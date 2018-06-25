# Cobrowse.io - React Native SDK

Cobrowse.io is 100% free and easy to try out in your own apps. Please see full documentation at [https://cobrowse.io/docs](https://cobrowse.io/docs).

Try our **online demo** at the bottom of our homepage at <https://cobrowse.io/#tryit>.

*Clients may access and integrate full source code for our SDKs directly upon request.*

## Installation

```bash
npm install --save cobrowse-sdk-react-native
react-native link
```

Once you've signed up for a free account at [cobrowse.io](https://cobrowse.io), you'll be able to find your license key at <https://cobrowse.io/dashboard/settings>. You can add this to your SDK setup:

```javascript
import CobrowseIO from 'cobrowse-sdk-react-native';

CobrowseIO.license = "<your license key here>";

```

**Note:** For iOS you need to be using Pods to manage dependencies for `react-native link` to work out of the box (and also remember to run `pod install` after the link step).

## Agent-initiated Sessions

Without any additional UI in your app, authenticated support agents are able to initiate sessions remotely via our online dashboard. To do this in an efficient way, we send an invisible push notification to the target device with a custom payload. To set up agent-initiated sessions:

1. Set up Firebase Cloud Messaging for your app. See the latest Firebase documentation for instructions at <https://firebase.google.com/docs/cloud-messaging/>.
2. Enter your FCM Server Key from the FCM admin settings into your Cobrowse.io account at https://cobrowse.io/dashboard/settings.
3. For Android devices, when you FCM token changes pass it into the Cobrowse SDK:

```javascript
import CobrowseIO from 'cobrowse-sdk-react-native';

CobrowseIO.deviceToken = "<your FCM token>";

```

### Add your license key
Please register an account and generate your free License Key at <https://cobrowse.io/dashboard/settings>.

This will associate sessions from your mobile app with your Cobrowse.io account. Add this to your SDK setup:

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
