/**
 * @format
 */

import {AppRegistry, Alert} from 'react-native';
import App from './App';
import {name as appName} from './app.json';

AppRegistry.registerComponent(appName, () => App);

import CobrowseIO from 'cobrowse-sdk-react-native';
CobrowseIO.license = "trial";
CobrowseIO.start();



CobrowseIO.handleSessionRequest = function(session) {
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
