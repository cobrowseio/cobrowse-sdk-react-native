import React, { Component } from 'react'
import CobrowseIO from './CobrowseIO'
import type { EmitterSubscription } from 'react-native'
import type Session from './Session'

interface SessionControlProps {
  children: React.ReactNode
}

interface SessionControlState {
  session: Session | null
}

export default class SessionControl extends Component<SessionControlProps, SessionControlState> {
  private _updateListener: EmitterSubscription | null = null

  constructor (props: SessionControlProps) {
    super(props)
    this.state = { session: null }
  }

  async componentDidMount (): Promise<void> {
    this.setState({ session: await CobrowseIO.currentSession() })
    this._updateListener = CobrowseIO.addListener(CobrowseIO.SESSION_UPDATED, (session) => {
      this.setState({ session })
    })
  }

  componentWillUnmount (): void {
    if (this._updateListener != null) this._updateListener.remove()
  }

  render (): React.ReactNode | null {
    const { session } = this.state
    if (session?.isActive() === true) return this.props.children
    else return null
  }
}
