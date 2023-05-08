import { NativeModules } from 'react-native'
const CobrowseIONative = NativeModules.CobrowseIO

export function showSetup (): void {
  return CobrowseIONative.accessibilityServiceShowSetup()
}

export function isRunning (): boolean {
  return CobrowseIONative.accessibilityServiceIsRunning()
}

export default {
  showSetup,
  isRunning
}
