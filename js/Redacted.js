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
  CobrowseIONative.setRedactedTags([...redactedTags])
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
        setTimeout(sendRedactionUpdates, 5000)
      }
    }, [])
    return <Component {...props} collapsable={false} ref={mergeRefs([localRef, ref])} />
  })
}

// also expose a basic Component based on a View
const Redacted = redact(View)
export default Redacted
