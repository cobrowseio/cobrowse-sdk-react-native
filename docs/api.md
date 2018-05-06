# CobrowseIO SDK - Remote Screenshare Service

## API

We've provided an API as a part of this SDK that allows you to bypass the default UI we provide and create your own:

### Session Management

`CobrowseIO.createSession(callback)`

`CobrowseIO.loadSession(code, callback)`

`CobrowseIO.currentSession(callback)`

`CobrowseIO.activateSession(callback)`

`CobrowseIO.endSession(callback)`

`CobrowseIO.addListener(event, callback)`

### Properties

`CobrowseIO.license`

`CobrowseIO.deviceToken`

`CobrowseIO.customData`

### Constants

`CobrowseIO.SESSION_UPDATED`

`CobrowseIO.SESSION_ENDED`


See the [CobrowseView](./js/CobrowseView.js) code for an example of how to use these APIs.
