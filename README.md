# CobrowseIO SDK - Remote Screenshare Service

With [Cobrowse.io](https://cobrowse.io)'s screen sharing technology for mobile apps you can see *exactly* what your customer sees on their mobile device, and provide realtime annotations to help solve customer support queries quickly.

## Try it out

You can try the Cobrowse.io service for **free** and **without signing up for an account**. Just follow the installation instructions below, then head to <https://cobrowse.io/trial> to use the trial dashboard.

## Installation

```bash
npm install --save cobrowse-sdk-react-native
react-native link
```

**Note:** For iOS you need to be using Pods to manage dependencies for `react-native link` to work out of the box (and also remember to run `pod install` after the link step). If you're not using pods you'll need to manually add the Frameworks for CobrowseIO, SocketIO (and it's dependencies) to your Xcode project.

## Usage from Javascript

We've provided a view that will do all the session creation and management for you. All you need to do is include this somewhere in your react native view hierarchy. It's not a requirement to use this UI, continue reading to learn about creating your own interface (it's easy!).

```javascript
import { CobrowseView } from 'cobrowse-sdk-react-native';

export default class App extends Component {
    render() {
        return (
            <View>
                <CobrowseView license='trial' />
            </View>
        );
    }
}
```

## API

It's likely you'll want to customise the UI for starting or managing an active session. We've provided an API as a part of this SDK that allows you to bypass the default UI we provide and create your own:

### Session Management

`CobrowseIO.createSession(callback)`

`CobrowseIO.loadSession(code, callback)`

`CobrowseIO.currentSession(callback)`

`CobrowseIO.activateSession(callback)`

`CobrowseIO.endSession(callback)`

`CobrowseIO.addListener(event, callback)`

### Properties

`CobrowseIO.license`

### Constants

`CobrowseIO.SESSION_UPDATED`

`CobrowseIO.SESSION_ENDED`


See the [CobrowseView](./js/CobrowseView.js) code for an example of how to use these APIs.


### Add your license key
Once you've signed up for a free account at [cobrowse.io](https://cobrowse.io), you'll be able to find your license key at <https://cobrowse.io/dashboard/settings>. Add this to your SDK setup:

```javascript
CobrowseIO.license = "<your license key here>";
```

Alternatively, you can pass this as the `license` prop to the `CobrowseView` if you're using the default UI.

## Requirements

* iOS 9.0, Android API 21 or above.

## Troubleshooting

**Issue: Could not find any matches for io.cobrowse:cobrowse-sdk-android:0.+ as no versions of io.cobrowse:cobrowse-sdk-android are available.**

Cobrowse uses a maven distribution on Android. Add these lines to your Project gradle.build file:

**In your project build.gradle**
Esnure that JCenter is added to your list of repositories:
```gradle
repositories {
    jcenter()
}
```

