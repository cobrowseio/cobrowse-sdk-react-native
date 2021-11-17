const CobrowseIONative = require('react-native').NativeModules.CobrowseIO
const NativeEventEmitter = require('react-native').NativeEventEmitter

const emitter = new NativeEventEmitter(CobrowseIONative)

export default class Session {
  constructor (session) {
    this._session = session || {}
    this._listen()
  }

  _update (session, onEnd) {
    if (this._session.id === undefined || (session && this._session.id === session.id)) {
      this._session = session
      if (typeof onEnd === 'function') onEnd()
    }
  }

  _listen () {
    const updates = emitter.addListener(CobrowseIONative.SESSION_UPDATED, (session) => this._update(session))
    const ended = emitter.addListener(CobrowseIONative.SESSION_ENDED, (session) => {
      this._update(session, () => {
        updates.remove()
        ended.remove()
      })
    })
  }

  addListener (eventType, listener) {
    switch (eventType) {
      case 'ended':
        eventType = CobrowseIONative.SESSION_ENDED
      case 'updated':
        eventType = CobrowseIONative.SESSION_UPDATED
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

  setRemoteControl (state) {
    return CobrowseIONative.updateSession({ remote_control: state })
  }
}
