import React from 'react'
import { requireNativeComponent, type ViewProps } from 'react-native'

const CBIOCobrowseRedacted = requireNativeComponent('CBIOCobrowseRedacted')

export default function Redacted (props: ViewProps): JSX.Element {
  return <CBIOCobrowseRedacted {...props}>{props.children}</CBIOCobrowseRedacted>
}
