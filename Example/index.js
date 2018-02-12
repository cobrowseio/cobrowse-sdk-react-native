import { AppRegistry } from 'react-native';
import App from './App';

import CobrowseIO from 'cobrowse-sdk-react-native';

const CobrowseIONative = require('react-native').NativeModules.CobrowseIO;
const NativeEventEmitter = require('react-native').NativeEventEmitter;

CobrowseIONative.createSession(function(err, session) {
    console.log('create', err, session);
});

const emitter = new NativeEventEmitter(CobrowseIONative);
console.log(emitter);

emitter.addListener('updated', function(session) {
    console.log('session updated', arguments);
    if (session.code) {
        CobrowseIONative.activateSession(function(err, session) {
            console.log('activiate', err, session);
        });
    }
});

emitter.addListener('ended', function() {
    console.log('session ended', arguments);
});

AppRegistry.registerComponent('Example', () => App);
