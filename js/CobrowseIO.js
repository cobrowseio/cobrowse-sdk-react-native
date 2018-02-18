'use strict';

const CobrowseIONative = require('react-native').NativeModules.CobrowseIO;

const NativeEventEmitter = require('react-native').NativeEventEmitter;
const emitter = new NativeEventEmitter(CobrowseIONative);

export default class CobrowseIO {

    static get SESSION_UPDATED() {
        return CobrowseIONative.SESSION_UPDATED;
    }

    static get SESSION_ENDED() {
        return CobrowseIONative.SESSION_ENDED;
    }

    static addListener(event, cb) {
        return emitter.addListener(event, cb);
    }

    static set api(api) {
        CobrowseIONative.api(api);
    }

    static set license(license) {
        CobrowseIONative.license(license);
    }

    static currentSession(cb) {
        CobrowseIONative.currentSession(cb);
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
