/* eslint-disable @typescript-eslint/no-extraneous-class,accessor-pairs */
import type { EmitterSubscription } from 'react-native'
import Session from './Session'
import CobrowseAccessibilityService from './CobrowseAccessibilityService'

export type NativeSessionEvent = 'session.updated' | 'session.ended'

export default class CobrowseIO {
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
}
