import React, { Component } from 'react'
import {
  View,
  Text,
  ActivityIndicator,
  TouchableOpacity,
  StyleSheet,
  EmitterSubscription
} from 'react-native'
import CobrowseIO from './CobrowseIO'
import type Session from './Session'

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'flex-end'
  },
  text: {
    textAlign: 'center',
    margin: 15,
    fontSize: 15,
    lineHeight: 20
  },
  code: {
    fontSize: 29,
    padding: 20,
    fontWeight: 'bold',
    textAlign: 'center'
  },
  button: {
    color: 'rgb(0, 122, 255)',
    fontSize: 18,
    margin: 10
  }
})

interface CobrowseViewProps {
  // @deprecated
  license?: string
  onEnded?: () => void
}

interface CobrowseViewState {
  error: Error | null
  session: Session | null
}

export default class CobrowseView extends Component<CobrowseViewProps, CobrowseViewState> {
  private _updateListener: EmitterSubscription | null = null
  private _endListener: EmitterSubscription | null = null

  constructor (props: CobrowseViewProps) {
    super(props)
    this.state = { error: null, session: null }
  }

  async componentDidMount (): Promise<void> {
    if (this.props.license != null) {
      console.warn('Passing license to view is deprecated. Use CobrowseIO.license = "..." instead')
      CobrowseIO.license = this.props.license
    }

    try {
      const current = await CobrowseIO.currentSession()
      if (current != null) this.setState({ session: current })
      else {
        const session = await CobrowseIO.createSession()
        this.setState({ session })
      }
    } catch (error) {
      this.setState({ error: error as Error })
    }

    this._updateListener = CobrowseIO.addListener('session.updated', (session) => {
      this.setState({ session })
    })
    this._endListener = CobrowseIO.addListener('session.ended', () => {
      if (this.props.onEnded != null) this.props.onEnded()
      this.setState({ session: null })
    })
  }

  componentWillUnmount (): void {
    if (this._updateListener != null) this._updateListener.remove()
    if (this._endListener != null) this._endListener.remove()
  }

  async endSession (): Promise<void> {
    try {
      const { session } = this.state

      if (session == null) {
        throw new Error('No session to end')
      }

      await session.end()
      this.setState({ session: null })
    } catch (error) {
      this.setState({ error: error as Error })
    }
  }

  renderError (error: Error): JSX.Element {
    return (
      <Text style={[styles.text]}>{error.message}</Text>
    )
  }

  renderCode (): JSX.Element {
    let code = this.state.session?.code
    if (code != null) code = code.substr(0, 3) + '-' + code.substr(3)
    return (
      <View>
        <Text style={[styles.code, { opacity: code != null ? 1 : 0.2 }]}>{code ?? '000-000'}</Text>
        <Text style={[styles.text]}>Provide this code to your support agent to begin screen sharing.</Text>
        <ActivityIndicator />
      </View>
    )
  }

  renderManageSession (): JSX.Element {
    return (
      <View>
        <Text style={[styles.text]}>You're sharing screens from this app with a support agent.</Text>
        <TouchableOpacity onPress={() => this.endSession}>
          <Text style={[styles.text, styles.button]}>End Session</Text>
        </TouchableOpacity>
      </View>
    )
  }

  renderContent (): JSX.Element {
    const { error, session } = this.state
    if (error != null) {
      return this.renderError(error)
    } else if ((session == null) || (session.state === 'pending' || session.state === 'authorizing')) {
      return this.renderCode()
    } else {
      return this.renderManageSession()
    }
  }

  render (): JSX.Element {
    return (
      <View style={styles.container}>
        {this.renderContent()}
      </View>
    )
  }
}
