import * as React from "react"
import { FINNHUB_API_URL } from "./constants"

const parseSubscribedSymbols = (text: string): string[] => {
  const cleaned = text.trim()
  if (!cleaned) {
    return []
  }

  const match = cleaned.match(/They are:\s*(.+?)\.?$/i)
  if (match?.[1]) {
    return match[1]
      .split(",")
      .map((symbol) => symbol.trim())
      .filter(Boolean)
  }

  return []
}

export const Symbols: React.FC = () => {
  const [symbolInput, setSymbolInput] = React.useState("")
  const [subscribedSymbols, setSubscribedSymbols] = React.useState<string[]>([])
  const [status, setStatus] = React.useState<string | null>(null)
  const [error, setError] = React.useState<string | null>(null)

  const loadSubscribedSymbols = React.useCallback(async () => {
    try {
      const response = await fetch(`${FINNHUB_API_URL}/subscribed-symbols`)
      if (!response.ok) {
        throw new Error(`Could not load subscriptions (${response.status})`)
      }

      const text = await response.text()
      setSubscribedSymbols(parseSubscribedSymbols(text))
      setError(null)
    } catch (loadError) {
      setError(
        loadError instanceof Error
          ? loadError.message
          : "Unable to load subscribed symbols.",
      )
    }
  }, [])

  React.useEffect(() => {
    void loadSubscribedSymbols()
  }, [loadSubscribedSymbols])

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()

    const trimmedSymbol = symbolInput.trim().toUpperCase()
    if (!trimmedSymbol) {
      setError("Please enter a stock symbol.")
      return
    }

    try {
      const response = await fetch(`${FINNHUB_API_URL}/subscribe/${encodeURIComponent(trimmedSymbol)}`)
      const message = await response.text()

      if (!response.ok) {
        throw new Error(message || `Subscription failed (${response.status})`)
      }

      setStatus(message)
      setError(null)
      setSymbolInput("")
      await loadSubscribedSymbols()
    } catch (submitError) {
      setStatus(null)
      setError(
        submitError instanceof Error
          ? submitError.message
          : "Unable to subscribe to that symbol.",
      )
    }
  }

  const handleUnsubscribe = async (symbol: string) => {
    try {
      const response = await fetch(`${FINNHUB_API_URL}/unsubscribe/${encodeURIComponent(symbol)}`, {
        method: "DELETE",
      })
      const message = await response.text()

      if (!response.ok) {
        throw new Error(message || `Unsubscribe failed (${response.status})`)
      }

      setStatus(message)
      setError(null)
      await loadSubscribedSymbols()
    } catch (unsubscribeError) {
      setStatus(null)
      setError(
        unsubscribeError instanceof Error
          ? unsubscribeError.message
          : "Unable to unsubscribe from that symbol.",
      )
    }
  }

  return (
    <section>
      <h2>Symbols</h2>

      <form onSubmit={handleSubmit}>
        <label htmlFor="symbol-input">Subscribe to a symbol</label>
        <div style={{ display: "flex", alignItems: "center", gap: "0.5rem", flexWrap: "wrap" }}>
          <input
            id="symbol-input"
            type="text"
            value={symbolInput}
            onChange={(event) => setSymbolInput(event.target.value)}
            placeholder="e.g. NVDA"
            style={{ minWidth: 0, flex: 1 }}
          />
          <button type="submit" style={{ padding: "0.4rem 0.75rem", fontSize: "0.875rem" }}>
            Subscribe
          </button>
        </div>
      </form>

      {status ? <p>{status}</p> : null}
      {error ? <p role="alert">{error}</p> : null}

      <h3>Subscribed symbols</h3>
      {subscribedSymbols.length === 0 ? (
        <p>No symbols are subscribed right now.</p>
      ) : (
        <ul style={{ listStyle: "none", padding: 0, margin: 0 }}>
          {subscribedSymbols.map((symbol) => (
            <li
              key={symbol}
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                gap: "0.75rem",
                padding: "0.35rem 0",
                borderBottom: "1px solid #e5e7eb",
              }}
            >
              <span>{symbol}</span>
              <button
                type="button"
                onClick={() => void handleUnsubscribe(symbol)}
                style={{ padding: "0.35rem 0.7rem", fontSize: "0.8rem" }}
              >
                Unsubscribe
              </button>
            </li>
          ))}
        </ul>
      )}
    </section>
  )
}
