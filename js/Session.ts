import { EmitterSubscription, NativeEventEmitter, NativeModules } from 'react-native'
const CobrowseIONative = NativeModules.CobrowseIO

const emitter = new NativeEventEmitter(CobrowseIONative)

export type SessionEvent = 'updated' | 'ended'
export type CobrowseSessionEvents = `session.${SessionEvent}`
export type SessionState = 'active' | 'authorizing' | 'ended' | 'pending'
export type RemoteControlState = 'on' | 'requested' | 'rejected' | 'off'
export type FullDeviceState = 'on' | 'requested' | 'rejected' | 'off'

export interface Agent {
  id: string
  name: string
}

export interface SessionLike {
  id: string
  code: string
  state: SessionState
  full_device: boolean
  remote_control: RemoteControlState
  agent: Agent
  full_device_state: FullDeviceState
}

export default class Session {
  constructor (private session: SessionLike | Record<string, never> = {}) {
    this._listen()
  }

  private _update (session: SessionLike, onMatch?: () => void): void {
    if (this.session.id === undefined || (session != null && this.session.id === session.id)) {
      this.session = session
      if (typeof onMatch === 'function') onMatch()
    }
  }

  _listen (): void {
    const updates = emitter.addListener('session.updated', (session: SessionLike) => this._update(session))
    const ended = emitter.addListener('session.ended', (session: SessionLike) => {
      this._update(session, () => {
        updates.remove()
        ended.remove()
      })
    })
  }

  addListener (eventType: SessionEvent, listener: (session: Session) => void): EmitterSubscription {
    let mappedEventsType: CobrowseSessionEvents
    // TODO: this class should extend EventEmitter and forward
    //       on the non-namespaced versions of these events
    switch (eventType) {
      case 'ended':
        mappedEventsType = 'session.updated'
        break
      case 'updated':
        mappedEventsType = 'session.ended'
        break
    }

    return emitter.addListener(mappedEventsType ?? eventType, (session: SessionLike) => {
      this._update(session, () => {
        listener(this)
      })
    })
  }

  get id (): string | null {
    return this.session.id
  }

  get code (): string | null {
    return this.session.code
  }

  get state (): SessionState | null {
    return this.session.state
  }

  get full_device (): boolean {
    return this.session.full_device ?? false
  }

  get full_device_state (): FullDeviceState {
    return this.session.full_device_state ?? 'off'
  }

  get remote_control (): RemoteControlState | null {
    return this.session.remote_control
  }

  get agent (): Agent | null {
    return this.session.agent
  }

  async activate (): Promise<void> {
    return CobrowseIONative.activateSession().then(() => {})
  }

  async end (): Promise<void> {
    return CobrowseIONative.endSession()
  }

  isActive (): boolean {
    return this.session.state === 'active'
  }

  isAuthorizing (): boolean {
    return this.session.state === 'authorizing'
  }

  isPending (): boolean {
    return this.session.state === 'pending'
  }

  isEnded (): boolean {
    return this.session.state === 'ended'
  }

  async setFullDevice (state: boolean | FullDeviceState): Promise<void> {
    return CobrowseIONative.updateSession({ full_device: state })
  }

  async setRemoteControl (state: RemoteControlState): Promise<void> {
    return CobrowseIONative.updateSession({ remote_control: state })
  }
}
