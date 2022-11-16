import React, { ReactElement, ForwardRefRenderFunction } from 'react'
import { View, ScrollView, Text, Image, FlatList, SectionList } from 'react-native'

export function useUnredaction<
  T = View | ScrollView | Text | Image | FlatList | SectionList
>(shouldWarnUnhandledRefs?: boolean, componentName?: string): (elem: T) => void

type ForwardedRefType<T, P> = typeof React.forwardRef

// HOC for adding unredaction to a whole component class
export function unredact(Component: ReactElement): ForwardedRefType<View, Record<string, unknown>>

// also expose a basic Component based on a View
declare const Unredacted: ForwardRefRenderFunction<View, Record<string, unknown>>
export default Unredacted
