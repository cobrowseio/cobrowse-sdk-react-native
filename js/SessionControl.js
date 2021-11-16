import React from 'react'
import CobrowseIO from './CobrowseIO'

export default class SessionControl extends React.Component {
  constructor () {
    super()
    this.state = { session: null }
  }

  async componentDidMount () {
    this.setState({ session: await CobrowseIO.currentSession() })
    this.updateListener = CobrowseIO.addListener(CobrowseIO.SESSION_UPDATED, (session) => {
      this.setState({ session })
    })
  }

  componentWillUnmount () {
    if (this.updateListener) this.updateListener.remove()
  }

  render () {
    const { session } = this.state
    if (session && session.isActive()) return this.props.children
    else return null
  }
}
