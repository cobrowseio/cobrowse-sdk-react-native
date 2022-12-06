import React from 'react';
import {StyleSheet, Text, Button} from 'react-native';
import {
  Redacted,
  SessionControl,
  Unredacted,
  useSession,
} from 'cobrowse-sdk-react-native';
import {SafeAreaView} from 'react-native';

export function SessionIndicator() {
  const session = useSession();

  return (
    <SessionControl>
      <Unredacted style={styles.container}>
        <SafeAreaView style={styles.contentWrapper}>
          <Text style={styles.text}>Screen Sharing session is active</Text>
          <Redacted>
            <Button title="Stop" onPress={() => session?.end()} />
          </Redacted>
        </SafeAreaView>
      </Unredacted>
    </SessionControl>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: 'red',
    width: '100%',
    flexDirection: 'row',
    justifyContent: 'space-between',
    padding: 16,
  },
  contentWrapper: {
    flex: 1,
    alignItems: 'center',
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  text: {fontSize: 16},
});
