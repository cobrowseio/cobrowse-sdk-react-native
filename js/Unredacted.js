import React, { useRef, useEffect, useCallback, useMemo } from 'react'
import { View, findNodeHandle } from 'react-native'
import { throttle } from 'lodash'
const CobrowseIONative = require('react-native').NativeModules.CobrowseIO

function mergeRefs (refs) {
  return (value) => {
    refs.forEach((ref) => {
      if (typeof ref === 'function') {
        ref(value)
      } else if (ref != null) {
        ref.current = value
      }
    })
  }
}

const unredactedTags = new Set()
const sendUnredactionUpdates = throttle(() => {
  CobrowseIONative.setUnredactedTags([...unredactedTags])
}, 50, { leading: false })

const removeUnredactedView = (view) => {
  if (view) {
    unredactedTags.delete(view)
    sendUnredactionUpdates()
  }
}

export const useUnredaction = (shouldWarnUnhandledRefs = true, componentName = '') => {
  const ref = useRef(null)

  const setRef = useCallback((node) => {
    let hasRemovedRef = false
    if (ref.current) {
      hasRemovedRef = true
      removeUnredactedView(ref.current)
    }

    if (node) {
      const view = findNodeHandle(node)

      if (view) {
        unredactedTags.add(view)
        sendUnredactionUpdates()

        ref.current = view
      } else {
        console.warn(`Failed to apply unredact() to ${componentName} due to view not found`)
      }
    } else if (!hasRemovedRef) {
      console.warn(
        `Failed to apply unredact() to ${componentName} due to null node handle – make sure you are forwarding refs`
      )
    }
  }, [])

  useEffect(() => {
    if (shouldWarnUnhandledRefs && !ref.current) {
      console.warn(
        `Failed to apply unredact() to ${componentName} due to null node handle – make sure the setRef function is called with the ref`
      )
    }

    return () => removeUnredactedView(ref.current)
  }, [])

  return setRef
}

// HOC for adding unredaction to a whole component class
export function unredact (Component) {
  return React.forwardRef(function Redacted (props, ref) {
    const localRef = useUnredaction(true, Component?.name)
    const refs = useMemo(() => mergeRefs([localRef, ref]), [localRef, ref])
    return <Component {...props} collapsable={false} ref={refs} />
  })
}

// also expose a basic Component based on a View
const Unredacted = unredact(View)
export default Unredacted
