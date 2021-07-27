import React, { useRef, useLayoutEffect } from 'react'
import { View, findNodeHandle } from 'react-native'
const CobrowseIONative = require('react-native').NativeModules.CobrowseIO

function mergeRefs (refs) {
  return value => {
    refs.forEach(ref => {
      if (typeof ref === 'function') {
        ref(value)
      } else if (ref != null) {
        ref.current = value
      }
    })
  }
}

const redactedTags = new Set()
function sendRedactionUpdates () {
  // We provide both a sync and async method to set the redacted
  // tags as when RN is running in a debugger (e.g. Chrome) calling
  // sync methods is not supported. You know... why not encourage
  // people to use different logic when running in debug mode through
  // poor design decisions? Well done RN devs...
  // For discussion of a similar issue:
  // https://github.com/react-native-device-info/react-native-device-info/issues/776#issuecomment-533410011
  const isDebuggingEnabled = (typeof atob !== 'undefined')
  if (isDebuggingEnabled) CobrowseIONative.setRedactedTags([...redactedTags])
  else CobrowseIONative.setRedactedTagsSync([...redactedTags])
}

// HOC for adding redaction to a whole component class
export function redact (Component) {
  return React.forwardRef(function Redacted (props, ref) {
    const localRef = useRef()
    useLayoutEffect(() => {
      const view = findNodeHandle(localRef.current)
      redactedTags.add(view)
      sendRedactionUpdates()
      return () => {
        redactedTags.delete(view)
        // delay removal of redaction as a basic debounce
        setTimeout(sendRedactionUpdates, 1000)
      }
    }, [])
    return <Component nativeID='cobrowse-redacted' {...props} collapsable={false} ref={mergeRefs([localRef, ref])} />
  })
}

// also expose a basic Component based on a View
const Redacted = redact(View)
export default Redacted
