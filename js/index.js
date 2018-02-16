'use strict';

const CobrowseIONative = require('react-native').NativeModules.CobrowseIO;

const NativeEventEmitter = require('react-native').NativeEventEmitter;
const emitter = new NativeEventEmitter(CobrowseIONative);

export default class CobrowseIO {

    static addListener(event, cb) {
        emitter.addListener(event, cb);
    }

    static set api(api) {
        CobrowseIONative.api(api);
    }

    static set license(license) {
        CobrowseIONative.license(license);
    }

    static createSession(cb) {
        CobrowseIONative.createSession(cb);
    }

    static activateSession(cb) {
        CobrowseIONative.activateSession(cb);
    }

    static loadSession(codeOrId, cb) {
        CobrowseIONative.loadSession(codeOrId, cb);
    }

    static endSession(cb) {
        CobrowseIONative.endSession(cb);
    }
}
