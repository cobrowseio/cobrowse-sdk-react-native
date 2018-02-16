import { AppRegistry } from 'react-native';
import App from './App';

import CobrowseIO from 'cobrowse-sdk-react-native';

CobrowseIO.api = 'https://api.staging.cobrowse.io';
CobrowseIO.license = 'trial';

CobrowseIO.createSession(function(err, session) {
    console.log('create', err, session);
});

CobrowseIO.addListener('session_updated', function(session) {
    console.log('session updated', arguments);
    if (session.code) {
        CobrowseIO.activateSession(function(err, session) {
            console.log('activiate', err, session);
        });
    }
});

CobrowseIO.addListener('session_ended', function() {
    console.log('session ended', arguments);
});

AppRegistry.registerComponent('Example', () => App);
