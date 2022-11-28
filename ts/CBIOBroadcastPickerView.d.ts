import type { ReactElement } from 'react'
import type { ViewProps } from 'react-native'

type Props = Readonly<{
  buttonColor?: string
}> & ViewProps

export default function (props: Props): ReactElement | null
