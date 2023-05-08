import React, { Component } from 'react'
import { View, Text } from 'react-native'
import { unredact } from 'cobrowse-sdk-react-native'

interface MyComponentProps {
  children?: React.ReactNode
}

class MyComponent extends Component<MyComponentProps> {
  render (): JSX.Element {
    return (
      <View style={{ margin: 10, padding: 40, backgroundColor: 'green' }}>
        <Text>This is a custom component</Text>
        {this.props.children}
      </View>
    )
  }
}

export default unredact(MyComponent)
