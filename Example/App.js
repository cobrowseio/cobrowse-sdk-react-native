import React, {Component} from 'react';
import {
  TouchableOpacity,
  Platform,
  StyleSheet,
  Text,
  View,
  TextInput,
  ScrollView,
} from 'react-native';
import CobrowseIO, {
  CobrowseView,
  Redacted,
  SessionControl,
  Unredacted,
  unredact,
} from 'cobrowse-sdk-react-native';
import MyComponent from './MyComponent';
import {FullDevicePrompt} from './FullDevicePrompt';
import {SessionIndicator} from './SessionIndicator';

CobrowseIO.handleSessionControls = () => {};

CobrowseIO.license = 'bOoR9JN8R3x1FA';
CobrowseIO.start();

const UnredactedText = unredact(Text);

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
  android:
    'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

type Props = {};
export default class App extends Component<Props> {
  constructor() {
    super();
    this.state = {show: 1};
  }

  render() {
    return (
      <>
        <SessionIndicator />
        <View style={{flexShrink: 1}}>
          <ScrollView>
            <View style={styles.container}>
              <TouchableOpacity
                onPress={() => {
                  this.setState({show: this.state.show + 1});
                }}
                style={styles.welcome}>
                <Text>Welcome to React Native!</Text>
              </TouchableOpacity>
              {this.state.show % 3 ? (
                <View style={styles.container}>
                  <Redacted>
                    <View>
                      <Text style={styles.instructions}>
                        To get started, edit App.js
                      </Text>
                    </View>
                    {this.state.show % 2 ? (
                      <View>
                        <Text>This is a sibling!</Text>
                      </View>
                    ) : null}
                  </Redacted>
                  <Text style={styles.welcome}>
                    <Text>This is the parent text</Text>
                    <View>
                      <Redacted style={{fontWeight: 'bold'}}>
                        <Text>This is the child text</Text>
                      </Redacted>
                    </View>
                  </Text>
                  <MyComponent>
                    <Redacted>
                      <Text>Redacted inner text</Text>
                    </Redacted>
                    <Text>Some children</Text>
                  </MyComponent>
                  <Redacted
                    style={{
                      position: 'absolute',
                      top: 0,
                      left: 20,
                    }}>
                    <Text>Floating</Text>
                  </Redacted>
                  <UnredactedText>An unredacted text component</UnredactedText>
                  <ScrollView horizontal={true} style={{height: 50}}>
                    <Text>
                      123456789012345678901§23456789012345678902345678901234567890123456789012345678901234567890
                    </Text>
                  </ScrollView>
                  <ScrollView style={{height: 50}}>
                    <Text>1</Text>
                    <Text>2</Text>
                    <Text>3</Text>
                    <Text>4</Text>
                    <Text>5</Text>
                    <Text>6</Text>
                    <Text>7</Text>
                    <Text>8</Text>
                    <Text>9</Text>
                    <Text>10</Text>
                    <Text>11</Text>
                    <Text>12</Text>
                    <Text>13</Text>
                    <Text>14</Text>
                    <Text>15</Text>
                    <Text>16</Text>
                    <Text>17</Text>
                    <Text>18</Text>
                    <Text>19</Text>
                    <Text>20</Text>
                    <Text>21</Text>
                    <Text>22</Text>
                    <Text>23</Text>
                    <Text>24</Text>
                    <Text>25</Text>
                  </ScrollView>
                  <TextInput
                    style={{width: 100}}
                    defaultValue={'Hello!'}
                    onChange={() => {}}
                  />
                  <Text style={styles.instructions}>{instructions}</Text>
                  <SessionControl>
                    <Text>Session is active</Text>
                  </SessionControl>
                  <SessionControl>
                    <View style={styles.floating}>
                      <Text>Overlay</Text>
                    </View>
                  </SessionControl>
                  <Redacted style={styles.container}>
                    <View>
                      <Text>No redaction</Text>
                    </View>
                    <Unredacted>
                      <Text>Unredacted</Text>
                    </Unredacted>
                    <Redacted>
                      <Text>No redaction</Text>
                    </Redacted>
                    <Redacted>
                      <View>
                        <Text>Nested no redaction</Text>
                      </View>
                      <Unredacted>
                        <Text>Nested unredacted</Text>
                      </Unredacted>
                      <Redacted>
                        <Text>Nested redaction</Text>
                      </Redacted>
                    </Redacted>
                  </Redacted>
                  <Redacted>
                    <Unredacted>
                      <Text>Unredacted 1</Text>
                      <Unredacted>
                        <Text>Unredacted 2</Text>
                      </Unredacted>
                    </Unredacted>
                    <Text>Redacted 1</Text>
                  </Redacted>
                  <CobrowseView onEnded={() => {}} />
                </View>
              ) : null}
            </View>
          </ScrollView>
        </View>

        <FullDevicePrompt />
      </>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'flex-start',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
    paddingTop: 50,
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
    bottom: 10,
    position: 'absolute',
    left: 10,
    right: 10,
    height: 30,
    backgroundColor: '#00ff00',
  },
});
