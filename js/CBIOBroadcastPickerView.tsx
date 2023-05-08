import React from 'react'
import { Platform, requireNativeComponent, type ViewProps } from 'react-native'

const CBIOBroadcastPickerViewNativeView = requireNativeComponent(
  'CBIOBroadcastPickerView'
)

type CBIOBroadcastPickerViewProps = {
  buttonColor: string
} & ViewProps

export default function CBIOBroadcastPickerView (props: CBIOBroadcastPickerViewProps): JSX.Element | null {
  // this native component is only available on iOS
  return Platform.OS === 'ios' ? <CBIOBroadcastPickerViewNativeView {...props} /> : null
}
