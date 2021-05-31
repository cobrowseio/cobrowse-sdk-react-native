import type { Provider, ReactNode } from 'react'

type Props = Readonly<{}> & Readonly<{
  children?: ReactNode
}>

export default function (props: Props): Provider<boolean>
