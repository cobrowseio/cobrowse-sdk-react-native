import React, { Component } from 'react'
import { View, Text } from 'react-native'
import { redact } from 'cobrowse-sdk-react-native'

class MyComponent extends Component {

  constructor() {
    super()
  }

  render() {
    return (
      <View style={{ margin: 10, padding: 40, backgroundColor: 'green' }}>
        <Text>This is a custom component</Text>
        { this.props.children }
      </View>
    )
  }
}

export default redact(MyComponent)
