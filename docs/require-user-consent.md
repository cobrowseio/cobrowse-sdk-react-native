# Requiring user consent (optional)

Please see full documentation at [https://cobrowse.io/docs](https://cobrowse.io/docs).

Try our **online demo** at the bottom of our homepage at <https://cobrowse.io/#tryit>.

## Implementation

You may want to ask the user for permission to view their screen before starting a session. You can use the following SDK hook to render your permission dialog:

```javascript

CobrowseIO.handleSessionRequest = function(session) {
    // Replace this with your own logic
    // Just be sure to call CobrowseIO.activateSession() to
    // accept the session.
    Alert.alert(
        'Session Requested',
        'A cobrowse session has been requested',
        [
            {
                text: 'Cancel',
                onPress: () => {},
                style: 'cancel',
            },
            {text: 'OK', onPress: () => CobrowseIO.activateSession()},
        ],
        {cancelable: true},
    );
}

```

## Questions?
Any questions at all? Please email us directly at [hello@cobrowse.io](mailto:hello@cobrowse.io).
