# Cobrowse.io - React Native SDK

Cobrowse.io is 100% free and easy to try out in your own apps. Please see full documentation at [https://cobrowse.io/docs](https://cobrowse.io/docs).

Try our **online demo** at the bottom of our homepage at <https://cobrowse.io/#tryit>.

*Clients may access and integrate full source code for our SDKs directly upon request.*

## Installation

```bash
npm install --save cobrowse-sdk-react-native
react-native link
```

**Note:** For iOS you need to be using Pods to manage dependencies for `react-native link` to work out of the box. If you're not using pods you'll need to manually add the Frameworks for CobrowseIO, SocketIO (and it's dependencies) to your Xcode project.

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
Please register an account and generate your free License Key at <https://cobrowse.io/dashboard/settings>.

This will associate sessions from your mobile app with your Cobrowse.io account. Add this to your SDK setup:

```javascript
CobrowseIO.license = "<your license key here>";
```

Alternatively, you can pass this as the `license` prop to the `CobrowseView` if you're using the default UI.

## Optional features

[Initiate sessions with push](https://cobrowse.io/docs#initiate-with-push)

[Use 6-digit codes](https://cobrowse.io/docs#user-generated-codes)

[Redact sensitive data](https://cobrowse.io/docs#redact-sensitive-data)

## Questions?
Any questions at all? Please email us directly at [hello@cobrowse.io](mailto:hello@cobrowse.io).

## Requirements

* iOS 9.0, Android API 19 or above.
