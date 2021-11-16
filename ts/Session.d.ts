/* eslint-disable @typescript-eslint/no-extraneous-class,accessor-pairs */
import type { EmitterSubscription } from 'react-native'

export type SessionEvent = 'updated' | 'ended'

export type SessionState = 'active' | 'authorizing' | 'ended' | 'pending'

export type RemoteControlState = 'on' | 'requested' | 'rejected' | 'off'

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
}

export default class Session {
  constructor (sessionLike: SessionLike)

  addListener (eventType: SessionEvent, listener: (session: Session) => void): EmitterSubscription

  get id (): string

  get code (): string

  get state (): SessionState

  get full_device (): boolean

  get remote_control (): RemoteControlState

  get agent (): Agent

  activate (): Promise<void>

  end (): Promise<void>

  isActive (): boolean

  isAuthorizing (): boolean

  isPending (): boolean

  isEnded (): boolean

  setFullDevice (state: boolean): Promise<void>

  setRemoteControl (state: RemoteControlState): Promise<void>
}
