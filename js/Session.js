const CobrowseIONative = require('react-native').NativeModules.CobrowseIO
const NativeEventEmitter = require('react-native').NativeEventEmitter
console.log("🚀 ~ file: Session.js ~ line 2 ~ CobrowseIONative", CobrowseIONative)

const emitter = new NativeEventEmitter(CobrowseIONative)

export const FULL_DEVICE_STATES = {
  OFF: CobrowseIONative.kCBIOFullDeviceStateOff,
  ON: CobrowseIONative.kCBIOFullDeviceStateOn,
  REQUESTED: CobrowseIONative.kCBIOFullDeviceStateRequested,
  REJECTED: CobrowseIONative.kCBIOFullDeviceStateRejected
}
console.log("🚀 ~ file: Session.js ~ line 13 ~ FULL_DEVICE_STATES", FULL_DEVICE_STATES)

export default class Session {
  constructor (session) {
    this._session = session || {}
    this._listen()
  }

  _update (session, onMatch) {
    if (this._session.id === undefined || (session && this._session.id === session.id)) {
      this._session = session
      if (typeof onMatch === 'function') onMatch()
    }
  }

  _listen () {
    const updates = emitter.addListener('session.updated', (session) => this._update(session))
    const ended = emitter.addListener('session.ended', (session) => {
      this._update(session, () => {
        updates.remove()
        ended.remove()
      })
    })
  }

  addListener (eventType, listener) {
    // TODO: this class should extend EventEmitter and forward
    //       on the non-namespaced versions of these events
    switch (eventType) {
      case 'ended':
        eventType = 'session.updated'
        break
      case 'updated':
        eventType = 'session.ended'
        break
    }

    return emitter.addListener(eventType, (session) => {
      this._update(session, () => {
        listener(this)
      })
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

  get full_device_state () {
    return this._session.full_device_state
  }

  get remote_control () {
    return this._session.remote_control
  }

  get agent () {
    return this._session.agent
  }

  activate () {
    return CobrowseIONative.activateSession().then(() => {})
  }

  end () {
    return CobrowseIONative.endSession()
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

  setFullDeviceState (state) {
    console.log("🚀 ~ file: Session.js ~ line 114 ~ Session ~ setFullDeviceState ~ state", state)
    // if (!Object.values(FULL_DEVICE_STATES).includes(state)) {
    //   throw new Error('Invalid state passed to `setFullDeviceState`. Please use values defined under `FULL_DEVICE_STATES`')
    // }
    return CobrowseIONative.updateSession({ full_device: "off" })
  }

  setRemoteControl (state) {
    return CobrowseIONative.updateSession({ remote_control: state })
  }
}
