const CobrowseIONative = require('react-native').NativeModules.CobrowseIO

export default class CobrowseAccessibilityService {
  static showSetup () {
    return CobrowseIONative.accessibilityServiceShowSetup()
  }

  static isRunning () {
    return CobrowseIONative.accessibilityServiceIsRunning()
  }
}
