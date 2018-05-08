# Cobrowse.io SDK for React Native

With [Cobrowse.io](https://cobrowse.io)'s screen sharing technology for mobile apps you can see *exactly* what your customer sees on their mobile device, and provide realtime annotations to help solve customer support queries quickly.

You can try the Cobrowse.io service for **free** and **without signing up for an account**. Just follow the installation instructions below using the license key `trial`, then head to <https://cobrowse.io/trial> to use the trial dashboard.

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

## Further Reading

[User Initiated Sessions](./docs/user-initiated-sessions.md)

[API Documentation](./docs/api.md)

## Requirements

* iOS 9.0, Android API 19 or above.
