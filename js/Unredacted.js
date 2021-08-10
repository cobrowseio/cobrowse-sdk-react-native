import React, { useRef, useEffect } from 'react'
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

const unredactedTags = new Set()
function sendUnredactionUpdates () {
  return CobrowseIONative.setUnredactedTags([...unredactedTags])
}

// HOC for adding unredaction to a whole component class
export function unredact (Component) {
  return React.forwardRef(function Redacted (props, ref) {
    const localRef = useRef()
    useEffect(() => {
      const view = findNodeHandle(localRef.current)
      if (view) {
        unredactedTags.add(view)
        sendUnredactionUpdates()
      } else {
        console.warn(`Failed to apply unredact() to ${Component?.name} due to null node handle – make sure you are forwarding refs`)
      }
      return () => {
        if (view) {
          unredactedTags.delete(view)
          sendUnredactionUpdates()
        }
      }
    }, [])
    return <Component {...props} collapsable={false} ref={mergeRefs([localRef, ref])} />
  })
}

// also expose a basic Component based on a View
const Unredacted = unredact(View)
export default Unredacted
