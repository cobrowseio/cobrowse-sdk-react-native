import React, { useEffect, useState } from 'react'
import {
  StyleSheet,
  Text,
  View,
  useColorScheme,
  Modal,
  Button
} from 'react-native'
import CobrowseIO, { CBIOBroadcastPickerView } from 'cobrowse-sdk-react-native'

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

  return (
    <Modal animationType='slide' visible={isVisible} onRequestClose={reject}>
      <View
        style={StyleSheet.flatten([
          styles.container,
          isDarkMode ? styles.darkBg : styles.lightBg
        ])}
      >
        <View style={styles.contentWrapper}>
          <CBIOBroadcastPickerView
            style={styles.picker}
            buttonColor={isDarkMode ? '#0A84FF' : '#007AFF'}
          />
          <Text
            style={StyleSheet.flatten([
              styles.description,
              isDarkMode ? styles.lightText : styles.darkText
            ])}
          >
            Tap the record icon to manage full device screen sharing.
          </Text>
        </View>
        <View style={styles.actionWrapper}>
          <Button title='Cancel' onPress={reject} />
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
  picker: { width: 50, height: 50 },
  description: {
    fontSize: 18,
    textAlign: 'center',
    padding: 32,
    marginTop: 80
  },
  lightText: {
    color: '#F2F2F7'
  },
  darkText: {
    color: '#1C1C1E'
  },
  actionWrapper: { marginBottom: 50 }
})
