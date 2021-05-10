/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View, TextInput } from 'react-native';
import { CobrowseView, Redacted, SessionControl } from 'cobrowse-sdk-react-native';

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
  android:
    'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

type Props = {};
export default class App extends Component<Props> {
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>Welcome to React Native!</Text>
        <Redacted><Text style={styles.instructions}>To get started, edit App.js</Text></Redacted>
        <TextInput value={'Hello!'} onChange={() => {}} />
        <Text style={styles.instructions}>{instructions}</Text>
        <SessionControl><Text>Session is active</Text></SessionControl>
        <SessionControl><View style={styles.floating}><Text>Overlay</Text></View></SessionControl>
        <CobrowseView onEnded={() => {}} />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
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
    top: 155,
    position: 'absolute',
    left: 10,
    right: 10,
    height: 60,
    backgroundColor: '#00ff00',
  },
});
