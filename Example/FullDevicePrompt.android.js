import React, { useEffect, useState } from 'react'
import {
  StyleSheet,
  Text,
  View,
  useColorScheme,
  Modal,
  Button,
  TouchableNativeFeedback
} from 'react-native'
import CobrowseIO from 'cobrowse-sdk-react-native'

// tell cobrowse SDK that the app will be handling the full device prompt
CobrowseIO.handleFullDeviceRequest = () => {}

export function FullDevicePrompt () {
  const [session, setSession] = useState(null)
  const isDarkMode = useColorScheme() === 'dark'

  useEffect(() => {
    const storeSession = session => setSession(session)

    CobrowseIO.addListener('session.updated', storeSession)

    return () => {
      CobrowseIO.removeListener('session.updated', storeSession)
    }
  }, [])

  const isVisible = session?.full_device_state === 'requested'

  const reject = () => session?.setFullDevice('off')
  const displaySystemPrompt = () => session?.setFullDevice('on')

  return (
    <Modal animationType='slide' visible={isVisible} onRequestClose={reject}>
      <View
        style={StyleSheet.flatten([
          styles.container,
          isDarkMode ? styles.darkBg : styles.lightBg
        ])}
      >
        <View style={styles.contentWrapper}>
          <Text
            style={StyleSheet.flatten([
              styles.description,
              isDarkMode ? styles.lightText : styles.darkText
            ])}
          >
            An agent has requested to screen share your full device.
          </Text>

          <Text
            style={StyleSheet.flatten([
              styles.description,
              isDarkMode ? styles.lightText : styles.darkText
            ])}
          >
            If you wish to accept please press "Continue" and "Accept" the
            system prompt. Otherwise press "Cancel".
          </Text>
        </View>
        {/* </View> */}
        <View style={styles.actionWrapper}>
          <Button title='Continue' onPress={displaySystemPrompt} />
          <TouchableNativeFeedback
            onPress={reject}
            background={TouchableNativeFeedback.Ripple('white', true)}
          >
            <View style={styles.cancelButton}>
              <Text style={isDarkMode ? styles.lightText : styles.darkText}>
                Cancel
              </Text>
            </View>
          </TouchableNativeFeedback>
        </View>
      </View>
    </Modal>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'space-between'
  },
  darkBg: {
    backgroundColor: '#1C1C1E'
  },
  lightBg: {
    backgroundColor: '#F2F2F7'
  },
  contentWrapper: {
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center'
  },
  description: {
    padding: 18,
    fontSize: 18,
    textAlign: 'center'
  },
  lightText: {
    color: '#F2F2F7'
  },
  darkText: {
    color: '#1C1C1E'
  },
  actionWrapper: { marginBottom: 25 },
  cancelButton: {
    width: '100%',
    marginTop: 8,
    alignItems: 'center',
    padding: 12
  }
})
