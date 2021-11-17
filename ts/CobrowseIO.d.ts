/* eslint-disable @typescript-eslint/no-extraneous-class,accessor-pairs */
import type { EmitterSubscription } from 'react-native'
import Session from './Session'
import CobrowseAccessibilityService from './CobrowseAccessibilityService'

export type NativeSessionEvent = 'session_updated' | 'session_ended' | 'session_requested'

export default class CobrowseIO {
  static get SESSION_UPDATED (): 'session_updated'

  static get SESSION_ENDED (): 'session_ended'

  static get SESSION_REQUESTED (): 'session_requested'

  static get accessibilityService (): CobrowseAccessibilityService

  static handleSessionRequest (session?: Session): void

  static addListener (eventType: NativeSessionEvent, listener: (session: Session) => void): EmitterSubscription

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
