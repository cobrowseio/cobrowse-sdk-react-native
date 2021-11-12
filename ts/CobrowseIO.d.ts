/* eslint-disable @typescript-eslint/no-extraneous-class,accessor-pairs */
import type { EmitterSubscription } from 'react-native'

interface Agent {
  id: string
  name: string
}

export interface Session {
  id: string | null
  code: string | null
  state: string | null
  full_device: boolean
  agent: Agent | null
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

  static addListener (eventType: string, listener: (event: any) => void, context?: any): EmitterSubscription

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
