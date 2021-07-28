import React, { useContext } from 'react'
import { View, requireNativeComponent } from 'react-native'
const CBIOCobrowseRedacted = requireNativeComponent('CBIOCobrowseRedacted')

const RedactionContext = React.createContext(false)

module.exports = function (props) {
  const alreadyRedacted = useContext(RedactionContext)
  if (!alreadyRedacted) {
    return (
      <RedactionContext.Provider value>
        <CBIOCobrowseRedacted style={props.style}>
          <View>{props.children}</View>
        </CBIOCobrowseRedacted>
      </RedactionContext.Provider>
    )
  }
  return props.children
}
