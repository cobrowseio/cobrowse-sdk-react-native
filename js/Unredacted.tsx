import React, {
  forwardRef,
  useMemo,
  useRef,
  useCallback,
  useEffect,
  Component as ReactComponentType,
  PropsWithoutRef,
  type ForwardRefExoticComponent,
  type RefAttributes,
  type ComponentClass,
  type Ref,
  type PropsWithChildren,
  type ForwardedRef
} from 'react'
import { ScrollView, Text, View, SectionList, FlatList, Image, findNodeHandle, NativeModules } from 'react-native'
import { throttle } from 'lodash'

const CobrowseIONative = NativeModules.CobrowseIO

type RefHandlingComponent<T, P> =
  | ComponentClass<P & { ref?: Ref<T> }, any>
  | ForwardRefExoticComponent<PropsWithChildren<P> & RefAttributes<T>>

function mergeRefs<T> (refs: Array<ForwardedRef<T>>): Ref<T> {
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

function removeUnredactedView (view: number | null): void {
  if (view != null) {
    unredactedTags.delete(view)
    sendUnredactionUpdates()
  }
}

export function useUnredaction<C extends ComponentClass | ReactComponentType = View | ScrollView | Text | Image | FlatList | SectionList> (shouldWarnUnhandledRefs = true, componentName = ''): ForwardedRef<C> {
  const ref = useRef<number | null>(null)

  const setRef = useCallback((node: C | null) => {
    let hasRemovedRef = false
    if (ref.current != null) {
      hasRemovedRef = true
      removeUnredactedView(ref.current)
    }

    if (node != null) {
      const view = findNodeHandle(node)

      if (view != null) {
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
    if (shouldWarnUnhandledRefs && ref.current == null) {
      console.warn(
        `Failed to apply unredact() to ${componentName} due to null node handle – make sure the setRef function is called with the ref`
      )
    }

    return () => removeUnredactedView(ref.current)
  }, [])

  return setRef
}

export function unredact<T, P extends {}> (
  Component: RefHandlingComponent<T, P>
): ForwardRefExoticComponent<PropsWithoutRef<P> & RefAttributes<T>> {
  return forwardRef<T, P>(
    function ComponentFromHOC (props, ref) {
      const localRef = useUnredaction(true, Component?.displayName) as ForwardedRef<T>
      const refs = useMemo(() => mergeRefs([localRef, ref]), [localRef, ref])

      return (
        <Component {...(props)} ref={refs} />
      )
    }
  )
}

const Unredacted = unredact(View)
export default Unredacted
