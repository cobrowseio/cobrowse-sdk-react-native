/* eslint-disable @typescript-eslint/no-extraneous-class,accessor-pairs */
import type { EmitterSubscription } from 'react-native'

export interface Agent {
  id: string
  name: string
}

export type SessionEvent = 'session_updated' | 'session_ended' | 'session_requested'

export type SessionState = 'active' | 'authorizing' | 'ended' | 'pending'

export type RemoteControlState = 'on' | 'requested' | 'rejected' | 'off'

export interface Session extends EventEmitter {
  get id (): string

  get code (): string

  get state (): SessionState

  get full_device (): boolean

  get remote_control (): RemoteControlState

  get agent (): Agent

  activate (): Promise<void>

  end (): Promise<void>

  hasAgent (): boolean

  isActive (): boolean

  isAuthorizing (): boolean

  isPending (): boolean

  isEnded (): boolean

  setFullDevice (state: boolean): Promise<void>

  setRemoteControl (state: RemoteControlState): Promise<void>
}

class CobrowseAccessibilityService {
  static showSetup (): Promise<void>

  static isRunning (): Promise<boolean>
}

export default class CobrowseIO {
  static get SESSION_UPDATED (): 'session_updated'

  static get SESSION_ENDED (): 'session_ended'

  static get SESSION_REQUESTED (): 'session_requested'

  static get accessibilityService (): CobrowseAccessibilityService

  static handleSessionRequest (session?: Session): void

  static addListener (eventType: SessionEvent, listener: (session: Session) => void): EmitterSubscription

  static start (): void

  static stop (): void

  static set api (api: string)

  static set license (license: string)

  static set customData (customData: Record<string, any>)

  static set deviceToken (token: string)

  static currentSession (): Promise<Session>

  static createSession (): Promise<Session>

  static getSession (codeOrId: string): Promise<Session>

  /** @deprecated */
  static activateSession (): Promise<Session>

  /** @deprecated */
  static endSession (): Promise<void>
}
