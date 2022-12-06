import { useEffect, useState } from 'react'
import CobrowseIO from './CobrowseIO'

export function useSession () {
  const [session, setSession] = useState(null)

  // initial value
  useEffect(() => {
    let isMounted = true
    const getSession = async () => {
      const session = await CobrowseIO.currentSession()

      if (isMounted) {
        setSession(session)
      }
    }

    getSession()

    return () => {
      isMounted = false
    }
  }, [])

  // listen to session updates
  useEffect(() => {
    const subscription = CobrowseIO.addListener(CobrowseIO.SESSION_UPDATED, setSession)

    return () => subscription.remove()
  }, [])

  return session
}
