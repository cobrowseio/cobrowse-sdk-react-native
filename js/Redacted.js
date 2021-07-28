import React, { useRef, useLayoutEffect, useState } from 'react'
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
  if (isDebuggingEnabled) return CobrowseIONative.setRedactedTags([...redactedTags])
  else return CobrowseIONative.setRedactedTagsSync([...redactedTags])
}

// HOC for adding redaction to a whole component class
export function redact (Component) {
  return React.forwardRef(function Redacted (props, ref) {
    const localRef = useRef()
    const [redactionReady, setRedactionReady] = useState(false)
    useLayoutEffect(() => {
      const view = findNodeHandle(localRef.current)
      redactedTags.add(view)
      ;(async () => {
        await sendRedactionUpdates()
        setRedactionReady(true)
      })()
      return () => {
        redactedTags.delete(view)
        setRedactionReady(false)
        // delay removal of redaction as a basic debounce
        setTimeout(sendRedactionUpdates, 1000)
      }
    }, [])
    const newProps = redactionReady ? props : { ...props, style: { ...props.style, opacity: 0 } }
    return <Component redactionReady={redactionReady} {...newProps} collapsable={false} ref={mergeRefs([localRef, ref])} />
  })
}

// also expose a basic Component based on a View
const Redacted = redact(View)
export default Redacted
