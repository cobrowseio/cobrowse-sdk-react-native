import React from 'react'
import { requireNativeComponent } from 'react-native'
const CBIOCobrowseRedacted = requireNativeComponent('CBIOCobrowseRedacted')

module.exports = function (props) {
  return <CBIOCobrowseRedacted {...props}>{props.children}</CBIOCobrowseRedacted>
}
