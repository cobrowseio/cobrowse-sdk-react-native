import React from 'react'
import { requireNativeComponent, type ViewProps } from 'react-native'

const CBIOCobrowseRedacted = requireNativeComponent<ViewProps>('CBIOCobrowseRedacted')

export default function Redacted(props: ViewProps): JSX.Element {
  return <CBIOCobrowseRedacted {...props} collapsable={false}>{props.children}</CBIOCobrowseRedacted>
}
