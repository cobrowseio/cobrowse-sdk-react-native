import type { Component } from 'react'
import type { View, Text } from 'react-native'

export default class CobrowseView extends Component {
  componentDidMount (): Promise<void>

  componentWillUnmount (): void

  endSession (): Promise<void>

  renderError (): Text

  renderCode (): View

  renderManageSession (): View

  renderContent (): Text | View

  render (): View
}
