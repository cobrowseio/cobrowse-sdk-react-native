import type { Provider } from 'react'

type Props = Readonly<{}> & Readonly<{
  children?: React.ReactNode
}>

export default function (props: Props): Provider<boolean>
