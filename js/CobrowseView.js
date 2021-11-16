import React, { Component } from 'react'
import {
  View,
  Text,
  ActivityIndicator,
  TouchableOpacity,
  StyleSheet
} from 'react-native'
import CobrowseIO from './CobrowseIO'

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

export default class CobrowseView extends Component {
  constructor () {
    super()
    this.state = { error: null, session: null }
  }

  async componentDidMount () {
    if (this.props.license) {
      console.warn('Passing license to view is deprecated. Use CobrowseIO.license = "..." instead')
      CobrowseIO.license = this.props.license
    }

    try {
      const current = await CobrowseIO.currentSession()
      if (current) this.setState({ session: current })
      else {
        const session = await CobrowseIO.createSession()
        this.setState({ session })
      }
    } catch (error) {
      this.setState({ error })
    }

    this.updateListener = CobrowseIO.addListener(CobrowseIO.SESSION_UPDATED, (session) => {
      this.setState({ session })
    })
    this.endListener = CobrowseIO.addListener(CobrowseIO.SESSION_ENDED, (session) => {
      if (this.props.onEnded) this.props.onEnded()
    })
  }

  componentWillUnmount () {
    if (this.updateListener) this.updateListener.remove()
    if (this.endListener) this.endListener.remove()
  }

  async endSession () {
    try {
      const { session } = this.state
      await session.end()
      this.setState({ session: null })
    } catch (error) {
      this.setState({ error })
    }
  }

  renderError () {
    return (
      <Text style={[styles.text]}>{this.state.error.message}</Text>
    )
  }

  renderCode () {
    let code = this.state.session && this.state.session.code
    if (code) code = code.substr(0, 3) + '-' + code.substr(3)
    return (
      <View>
        <Text style={[styles.code, { opacity: code ? 1 : 0.2 }]}>{code || '000-000'}</Text>
        <Text style={[styles.text]}>Provide this code to your support agent to begin screen sharing.</Text>
        <ActivityIndicator />
      </View>
    )
  }

  renderManageSession () {
    return (
      <View>
        <Text style={[styles.text]}>You{"'"}re sharing screens from this app with a support agent.</Text>
        <TouchableOpacity onPress={() => this.endSession()}>
          <Text style={[styles.text, styles.button]}>End Session</Text>
        </TouchableOpacity>
      </View>
    )
  }

  renderContent () {
    const { error, session } = this.state
    if (error) {
      return this.renderError()
    } else if ((!session) || (session.state === 'pending' || session.state === 'authorizing')) {
      return this.renderCode()
    } else {
      return this.renderManageSession()
    }
  }

  render () {
    return (
      <View style={styles.container}>
        {this.renderContent()}
      </View>
    )
  }
}
