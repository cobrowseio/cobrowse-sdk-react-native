# Initiate with push (optional)

Please see full documentation at [https://cobrowse.io/docs](https://cobrowse.io/docs).

Try our **online demo** at the bottom of our homepage at <https://cobrowse.io/#tryit>.

## Implementation

These are the app-side requirements for React Native to initiate sessions with push. More info at <https://cobrowse.io/docs#initiate-with-push>

You must first add Firebase Cloud Messaging (FCM) to your app. Please see FCM documentation at <https://firebase.google.com/docs/cloud-messaging/android/client>.

Next, whenever your device receives a registration token from FCM, pass that to the Cobrowse.io SDK, for example:

```javascript
import CobrowseIO from 'cobrowse-sdk-react-native';

CobrowseIO.deviceToken = "<your FCM token>";

```
