import type { ReactNode, ReactElement } from 'react'

type Props = Readonly<{}> &
Readonly<{
  children?: ReactNode | undefined
}>

export default function (props: Props): ReactElement | null
