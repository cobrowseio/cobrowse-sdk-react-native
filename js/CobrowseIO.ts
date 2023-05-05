import { Platform, Alert, NativeEventEmitter, EmitterSubscription, NativeModules } from 'react-native'
import Session from './Session'
const CobrowseIONative = NativeModules.CobrowseIO

const emitter = new NativeEventEmitter(CobrowseIONative)

// eslint-disable-next-line @typescript-eslint/no-extraneous-class
export default class CobrowseIO {
  private static sessionRequestShown = false
  private static remoteControlRequestShown = false

  /** @deprecated */
  static get SESSION_UPDATED (): 'session.updated' {
    return 'session.updated' as const
  }

  /** @deprecated */
  static get SESSION_ENDED (): 'session.ended' {
    return 'session.ended' as const
  }

  static handleSessionRequest (session: Session): void {
    if (this.sessionRequestShown) return
    this.sessionRequestShown = true
    Alert.alert(
      'Support Request',
      'A support agent would like to use this app with you. Do you accept?',
      [{
        text: 'Reject',
        onPress: () => {
          this.sessionRequestShown = false
          session.end()
        },
        style: 'cancel'
      }, {
        text: 'Accept',
        onPress: () => {
          this.sessionRequestShown = false
          session.activate()
        }
      }], { cancelable: false })
  }

  static handleRemoteControlRequest (session: Session): void {
    if (this.remoteControlRequestShown) return
    this.remoteControlRequestShown = true
    Alert.alert(
      'Remote Control Request',
      'A support agent would like to take remote control of this app. Do you accept?',
      [{
        text: 'Reject',
        onPress: () => {
          this.remoteControlRequestShown = false
          void session.setRemoteControl('rejected')
        },
        style: 'cancel'
      }, {
        text: 'Accept',
        onPress: () => {
          this.remoteControlRequestShown = false
          void session.setRemoteControl('on')
        }
      }], { cancelable: false })
  }

  static addListener (event: any, cb: (session: Session) => void): EmitterSubscription {
    return emitter.addListener(event, (session: Session) => cb(new Session(session)))
  }

  static start (): any {
    return CobrowseIONative.start()
  }

  static stop (): any {
    return CobrowseIONative.stop()
  }

  // eslint-disable-next-line accessor-pairs
  static set api (api: string) {
    CobrowseIONative.api(api)
  }

  // eslint-disable-next-line accessor-pairs
  static set license (license: string) {
    CobrowseIONative.license(license)
  }

  // eslint-disable-next-line accessor-pairs
  static set customData (customData: Record<string, unknown>) {
    CobrowseIONative.customData(customData)
  }

  // eslint-disable-next-line accessor-pairs
  static set deviceToken (token: string) {
    CobrowseIONative.deviceToken(token)
  }

  static currentSession (): Session | null {
    return CobrowseIONative.currentSession().then((session: Session | null) => (session != null) ? new Session(session) : null)
  }

  static createSession (): Session | null {
    return CobrowseIONative.createSession().then((session: Session | null) => (session != null) ? new Session(session) : null)
  }

  static getSession (codeOrId: string): Session | null {
    return CobrowseIONative.getSession(codeOrId).then((session: Session | null) => (session != null) ? new Session(session) : null)
  }

  /** @deprecated */
  static activateSession (): Session | null {
    return CobrowseIONative.activateSession().then((session: Session | null) => (session != null) ? new Session(session) : null)
  }

  /** @deprecated */
  static endSession (): void {
    return CobrowseIONative.endSession()
  }

  static showSessionControls: Boolean | null = null
  static handleFullDeviceRequest: null | ((s: Session) => void) = null
}

// the session.requested event is considered internal, it should
// not be used outside these bindings
CobrowseIO.addListener('session.requested', (session) => {
  if (CobrowseIO.showSessionControls === false) {
    CobrowseIONative.overwriteSessionIndicator()
  }

  if (Platform.OS === 'ios' && (CobrowseIO.handleFullDeviceRequest != null)) {
    CobrowseIONative.overwriteFullControlUI()
  }

  CobrowseIO.handleSessionRequest(session)
})

CobrowseIO.addListener('session.updated', (session) => {
  if (session.isActive() && session.remote_control === 'requested') {
    CobrowseIO.handleRemoteControlRequest(session)
  }

  if (session.isActive() && session.full_device_state === 'requested') {
    if (CobrowseIO.handleFullDeviceRequest != null) {
      CobrowseIO.handleFullDeviceRequest(session)
    } else if (Platform.OS === 'android') {
      // accept the incoming connection by default
      void session.setFullDevice('on')
    }
  }
})
