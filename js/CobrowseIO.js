import { Alert, EventEmitter } from 'react-native'
const CobrowseIONative = require('react-native').NativeModules.CobrowseIO
const NativeEventEmitter = require('react-native').NativeEventEmitter

const emitter = new NativeEventEmitter(CobrowseIONative)

class Session extends EventEmitter {
  constructor (session) {
    this._session = session || {}
    this._listen()
  }

  _listen () {
    const requests = emitter.addListener(CobrowseIONative.SESSION_REQUESTED, (session) => {
      if (this._session.id === undefined || (session && this._session.id === session.id)) {
        this._session = session
        this.emit(CobrowseIONative.SESSION_REQUESTED, this)
      }
    })
    const updates = emitter.addListener(CobrowseIONative.SESSION_UPDATED, (session) => {
      if (this._session.id === undefined || (session && this._session.id === session.id)) {
        this._session = session
        this.emit(CobrowseIONative.SESSION_UPDATED, this)
      }
    })
    const ended = emitter.addListener(CobrowseIONative.SESSION_ENDED, (session) => {
      if (this._session.id === undefined || (session && this._session.id === session.id)) {
        this._session = session
        requests.remove()
        updates.remove()
        ended.remove()
        this.emit(CobrowseIONative.SESSION_ENDED, this)
      }
    })
  }

  get id () {
    return this._session.id
  }

  get code () {
    return this._session.code
  }

  get state () {
    return this._session.state
  }

  get full_device () {
    return this._session.full_device
  }

  get remote_control () {
    return this._session.remote_control
  }

  get agent () {
    return this._session.agent
  }

  activate () {
    return CobrowseIONative.activateSession().then((session) => {
      this._session = session
    })
  }

  end () {
    return CobrowseIONative.endSession()
  }

  hasAgent () {
    return !!(this._session.agent)
  }

  isActive () {
    return this._session.state === 'active'
  }

  isAuthorizing () {
    return this._session.state === 'authorizing'
  }

  isPending () {
    return this._session.state === 'pending'
  }

  isEnded () {
    return this._session.state === 'ended'
  }

  setFullDevice (state) {
    return CobrowseIONative.updateSession({ full_device: state })
  }

  setRemoteControl (state) {
    return CobrowseIONative.updateSession({ remote_control: state })
  }
}

class CobrowseAccessibilityService {
  static showSetup () {
    return CobrowseIONative.accessibilityServiceShowSetup()
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

CobrowseIO.addListener(CobrowseIO.SESSION_REQUESTED, (session) => {
  CobrowseIO.handleSessionRequest(session)
})
