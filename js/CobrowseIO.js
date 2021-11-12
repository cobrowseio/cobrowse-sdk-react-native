import { Alert } from 'react-native'
const CobrowseIONative = require('react-native').NativeModules.CobrowseIO
const NativeEventEmitter = require('react-native').NativeEventEmitter

const emitter = new NativeEventEmitter(CobrowseIONative)

class CobrowseAccessibilityService {
  static showSetup () {
    CobrowseIONative.accessibilityServiceShowSetup()
  }

  static isRunning () {
    return CobrowseIONative.accessibilityServiceIsRunning()
  }
}

export default class CobrowseIO {
  static get SESSION_UPDATED () {
    return CobrowseIONative.SESSION_UPDATED
  }

  static get SESSION_ENDED () {
    return CobrowseIONative.SESSION_ENDED
  }

  static get SESSION_REQUESTED () {
    return CobrowseIONative.SESSION_REQUESTED
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
        onPress: () => this.endSession(),
        style: 'cancel'
      }, {
        text: 'Accept',
        onPress: () => this.activateSession()
      }], { cancelable: true })
  }

  static addListener (event, cb) {
    return emitter.addListener(event, cb)
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
    return CobrowseIONative.currentSession()
  }

  static createSession () {
    return CobrowseIONative.createSession()
  }

  static activateSession () {
    return CobrowseIONative.activateSession()
  }

  static getSession (codeOrId) {
    return CobrowseIONative.getSession(codeOrId)
  }

  static endSession () {
    return CobrowseIONative.endSession()
  }
}

CobrowseIO.addListener(CobrowseIO.SESSION_REQUESTED, (session) => {
  CobrowseIO.handleSessionRequest(session)
})
