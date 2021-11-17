import { Alert } from 'react-native'
import Session from './Session'
import CobrowseAccessibilityService from './CobrowseAccessibilityService'
const CobrowseIONative = require('react-native').NativeModules.CobrowseIO
const NativeEventEmitter = require('react-native').NativeEventEmitter

const emitter = new NativeEventEmitter(CobrowseIONative)

export default class CobrowseIO {
  /** @deprecated */
  static get SESSION_UPDATED () {
    return 'session.updated'
  }

  /** @deprecated */
  static get SESSION_ENDED () {
    return 'session.ended'
  }

  static get accessibilityService () {
    return CobrowseAccessibilityService
  }

  static handleSessionRequest (session) {
    Alert.alert(
      'Support Request',
      'A support agent would like to use this app with you. Do you accept?',
      [{
        text: 'Reject',
        onPress: () => session.end(),
        style: 'cancel'
      }, {
        text: 'Accept',
        onPress: () => session.activate()
      }], { cancelable: true })
  }

  static addListener (event, cb) {
    return emitter.addListener(event, (session) => cb(new Session(session)))
  }

  static start () {
    return CobrowseIONative.start()
  }

  static stop () {
    return CobrowseIONative.stop()
  }

  static set api (api) {
    CobrowseIONative.api(api)
  }

  static set license (license) {
    CobrowseIONative.license(license)
  }

  static set customData (customData) {
    CobrowseIONative.customData(customData)
  }

  static set deviceToken (token) {
    CobrowseIONative.deviceToken(token)
  }

  static currentSession () {
    return CobrowseIONative.currentSession().then((session) => session ? new Session(session) : null)
  }

  static createSession () {
    return CobrowseIONative.createSession().then((session) => session ? new Session(session) : null)
  }

  static getSession (codeOrId) {
    return CobrowseIONative.getSession(codeOrId).then((session) => session ? new Session(session) : null)
  }

  /** @deprecated */
  static activateSession () {
    return CobrowseIONative.activateSession().then((session) => session ? new Session(session) : null)
  }

  /** @deprecated */
  static endSession () {
    return CobrowseIONative.endSession()
  }
}

// the session.requested event is considered internal, it should
// not be used outside these bindings
CobrowseIO.addListener('session.requested', (session) => {
  CobrowseIO.handleSessionRequest(session)
})
