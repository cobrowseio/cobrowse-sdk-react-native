import type { Component, ReactElement } from 'react'
import type { ViewProps, TextProps } from 'react-native'

export default class CobrowseView extends Component {
  componentDidMount (): Promise<void>

  componentWillUnmount (): void

  endSession (): Promise<void>

  renderError (): ReactElement<TextProps>

  renderCode (): ReactElement<ViewProps>

  renderManageSession (): ReactElement<ViewProps>

  renderContent (): ReactElement<TextProps | ViewProps>

  render (): ReactElement<ViewProps>
}
