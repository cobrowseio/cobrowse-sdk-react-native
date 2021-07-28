import React, { Component } from 'react';
import { TouchableOpacity, Platform, StyleSheet, Text, View, TextInput } from 'react-native';
import { CobrowseView, Redacted, SessionControl, redact } from 'cobrowse-sdk-react-native';

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
  android:
    'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

type Props = {};
export default class App extends Component<Props> {

    constructor() {
        super()
        this.state = { show: 1 };
    }

  render() {
    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={() => {
            this.setState({ show: (this.state.show + 1) })
        }} style={styles.welcome}><Text>Welcome to React Native!</Text></TouchableOpacity>
        { this.state.show % 3 ? <View style={styles.container}>
            <Redacted>
                <View><Text style={styles.instructions}>To get started, edit App.js</Text></View>
                { this.state.show % 2 ? <View><Text>This is a sibling!</Text></View> : null }
            </Redacted>
            <Text style={styles.welcome}>
                <Text>This is the parent text</Text>
                <Redacted style={{fontWeight: 'bold'}}><Text>This is the child text</Text></Redacted>
            </Text>
            <Redacted style={{
                position: 'absolute',
                top: 0,
                left: 20
            }}>
                <Text>Floating</Text>
            </Redacted>
            <TextInput defaultValue={'Hello!'} onChange={() => {}} />
            <Text style={styles.instructions}>{instructions}</Text>
            <SessionControl><Text>Session is active</Text></SessionControl>
            <SessionControl><View style={styles.floating}><Text>Overlay</Text></View></SessionControl>
            <CobrowseView onEnded={() => {}} />
        </View> : null }
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    paddingTop: 50,
    flex: 1,
    justifyContent: 'flex-start',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  floating: {
    zIndex: 500,
    bottom:100,
    position: 'absolute',
    left: 10,
    right: 10,
    height: 60,
    backgroundColor: '#00ff00',
  },
});
