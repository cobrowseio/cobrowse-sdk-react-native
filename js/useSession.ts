import { useEffect, useState } from 'react'
import CobrowseIO from './CobrowseIO'
import type Session from './Session'

export function useSession (): Session | null {
  const [session, setSession] = useState<Session | null>(null)

  // initial value
  useEffect(() => {
    let isMounted = true
    async function getSession (): Promise<void> {
      const session = await CobrowseIO.currentSession()

      if (isMounted) {
        setSession(session)
      }
    }

    void getSession()

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
