import React, { useContext } from 'react'
import { requireNativeComponent } from 'react-native'
const CBIOCobrowseRedacted = requireNativeComponent('CBIOCobrowseRedacted')

const RedactionContext = React.createContext(false)

module.exports = function (props) {
  return <CBIOCobrowseRedacted {...props}>{props.children}</CBIOCobrowseRedacted>
}
