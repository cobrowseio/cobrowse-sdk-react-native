import React from 'react'
import { Platform, requireNativeComponent } from 'react-native'

const CBIOBroadcastPickerView = requireNativeComponent(
  'CBIOBroadcastPickerView'
)

export default function (props) {
  // this native component is only available on iOS
  return Platform.OS === 'ios' ? <CBIOBroadcastPickerView {...props} /> : null
}
