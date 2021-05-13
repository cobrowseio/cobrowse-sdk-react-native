/* eslint-disable @typescript-eslint/no-extraneous-class,accessor-pairs */
import type { EmitterSubscription } from 'react-native'

// TODO: find out what this interface is in 'io.cobrowse' jar.
export type Session = any

export default class CobrowseIO {
  static get SESSION_UPDATED (): 'session_updated'

  static get SESSION_ENDED (): 'session_ended'

  static get SESSION_REQUESTED (): 'session_requested'

  static handleSessionRequest (session?: any): void

  static addListener (eventType: string, listener: (event: any) => void, context?: Object): EmitterSubscription

  static start (): void

  static stop (): void

  static set api (api: string)

  static set license (license: string)

  static set customData (customData: Record<string, any>)

  static set deviceToken (token: string)

  static currentSession (): Promise<Session>

  static createSession (): Promise<Session>

  static activateSession (): Promise<Session>

  static getSession (codeOrId: string): Promise<Session>

  static endSession (): Promise<void>
}
