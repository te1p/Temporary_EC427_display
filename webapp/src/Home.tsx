import * as React from "react"
import { API_URL, FINNHUB_API_URL } from "./constants"

const BASE_POLL_INTERVAL_MS = 100
const MAX_POLL_INTERVAL_MS = 30000
//I have set the BASE_POLL_INTERVAL_MS to be 100 to make sure that the
// user stays up to date with information. Stocks change rapidly, so
// interval smaller than 1s is necessary

//The MAX_POLL_INTERVAL_MS is 30 seconds.

// Only back off when the previous poll came back empty; successful polls stay at the base cadence.
const getPollIntervalMs = (lastPollReturnedEmpty: boolean): number => {
  if (!lastPollReturnedEmpty) {
    return BASE_POLL_INTERVAL_MS
  }

  return Math.min(BASE_POLL_INTERVAL_MS * 2, MAX_POLL_INTERVAL_MS)
  //increase is times two for every empty poll, because I want to avoid increasing
  //poll interval too high in the case a stock update is received late
  //Stale information can be very bad for people trading stocks
}

const parseSubscribedSymbols = (text: string): string[] => {
  const cleaned = text.trim()

  if (!cleaned) {
    return []
  }

  const theyAreMatch = cleaned.match(/They are:\s*(.+?)\.?$/i)
  if (theyAreMatch?.[1]) {
    return theyAreMatch[1]
      .split(",")
      .map((symbol) => symbol.trim())
      .filter(Boolean)
  }

  return []
}

const CHART_COLORS = ["#2563eb", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6", "#14b8a6"]

export const Home: React.FC = () => {
  const [subscribedSymbols, setSubscribedSymbols] = React.useState<string[]>([])
  const [prices, setPrices] = React.useState<Record<string, number | null>>({})
  const [averageVolumes, setAverageVolumes] = React.useState<Record<string, number | null>>({})
  const [mostActive, setMostActive] = React.useState<{ symbol: string; volume: number } | null>(null)
  const [priceHistory, setPriceHistory] = React.useState<Record<string, Array<{ msSinceEpoch: number; price: number }>>>({})
  const [error, setError] = React.useState<string | null>(null)

  React.useEffect(() => {
    let isActive = true

    const loadSubscribedSymbols = async () => {
      try {
        const response = await fetch(`${FINNHUB_API_URL}/subscribed-symbols`)
        if (!response.ok) {
          throw new Error(`Failed to load subscribed symbols: ${response.status}`)
        }

        const text = await response.text()
        if (!isActive) {
          return
        }

        const symbols = parseSubscribedSymbols(text)
        setSubscribedSymbols(symbols)
        setError(null)
      } catch (loadError) {
        if (!isActive) {
          return
        }

        setError(
          loadError instanceof Error
            ? loadError.message
            : "Unable to load subscribed symbols.",
        )
      }
    }

    void loadSubscribedSymbols()

    return () => {
      isActive = false
    }
  }, [])

  React.useEffect(() => {
    if (subscribedSymbols.length === 0) {
      setPrices({})
      return
    }

    let isActive = true
    let timeoutId: number | undefined

    const scheduleNextPoll = (lastPollReturnedEmpty: boolean) => {
      if (!isActive) {
        return
      }

      timeoutId = window.setTimeout(() => {
        void loadPrices()
      }, getPollIntervalMs(lastPollReturnedEmpty))
    }

    const loadPrices = async () => {
      let lastPollReturnedEmpty = false

      try {
        const nextPrices = await Promise.all(
          subscribedSymbols.map(async (symbol) => {
            const response = await fetch(`${API_URL}/price/${encodeURIComponent(symbol)}`)

            if (!response.ok) {
              throw new Error(`Failed to load ${symbol}: ${response.status}`)
            }

            const text = await response.text()
            const numericValue = Number.parseFloat(text.trim())

            return [symbol, Number.isFinite(numericValue) ? numericValue : null] as const
          }),
        )

        if (!isActive) {
          return
        }

        const priceMap = Object.fromEntries(nextPrices)
        const hasAnyPrice = Object.values(priceMap).some((value) => typeof value === "number")
        lastPollReturnedEmpty = !hasAnyPrice

        setPrices(priceMap)

        setPriceHistory((current) => {
          const nextHistory: Record<string, Array<{ msSinceEpoch: number; price: number }>> = { ...current }

          subscribedSymbols.forEach((symbol) => {
            const currentPrice = priceMap[symbol]
            if (typeof currentPrice !== "number") {
              return
            }

            const history = nextHistory[symbol] ?? []
            nextHistory[symbol] = [...history, { msSinceEpoch: Date.now(), price: currentPrice }].slice(-12)
          })

          return nextHistory
        })

        setError(null)
      } catch (loadError) {
        if (!isActive) {
          return
        }

        lastPollReturnedEmpty = true
        setError(
          loadError instanceof Error
            ? loadError.message
            : "Unable to load prices for subscribed symbols.",
        )
      } finally {
        if (isActive) {
          scheduleNextPoll(lastPollReturnedEmpty)
        }
      }
    }

    void loadPrices()

    return () => {
      isActive = false
      if (timeoutId !== undefined) {
        window.clearTimeout(timeoutId)
      }
    }
  }, [subscribedSymbols])

  React.useEffect(() => {
    if (subscribedSymbols.length === 0) {
      setAverageVolumes({})
      return
    }

    let isActive = true
    let timeoutId: number | undefined

    const scheduleNextPoll = (lastPollReturnedEmpty: boolean) => {
      if (!isActive) {
        return
      }

      timeoutId = window.setTimeout(() => {
        void loadAverageVolumes()
      }, getPollIntervalMs(lastPollReturnedEmpty))
    }

    const loadAverageVolumes = async () => {
      let lastPollReturnedEmpty = false

      try {
        const nextAverageVolumes = await Promise.all(
          subscribedSymbols.map(async (symbol) => {
            const response = await fetch(`${API_URL}/averagevolume/${encodeURIComponent(symbol)}`)

            if (!response.ok) {
              throw new Error(`Failed to load average volume for ${symbol}: ${response.status}`)
            }

            const text = await response.text()
            const numericValue = Number.parseFloat(text.trim())

            return [symbol, Number.isFinite(numericValue) ? numericValue : null] as const
          }),
        )

        if (!isActive) {
          return
        }

        const hasAnyVolume = Object.values(Object.fromEntries(nextAverageVolumes)).some(
          (value) => typeof value === "number",
        )
        lastPollReturnedEmpty = !hasAnyVolume

        setAverageVolumes(Object.fromEntries(nextAverageVolumes))
        setError(null)
      } catch (loadError) {
        if (!isActive) {
          return
        }

        lastPollReturnedEmpty = true
        setError(
          loadError instanceof Error
            ? loadError.message
            : "Unable to load average volume for subscribed symbols.",
        )
      } finally {
        if (isActive) {
          scheduleNextPoll(lastPollReturnedEmpty)
        }
      }
    }

    void loadAverageVolumes()

    return () => {
      isActive = false
      if (timeoutId !== undefined) {
        window.clearTimeout(timeoutId)
      }
    }
  }, [subscribedSymbols])

  React.useEffect(() => {
    let isActive = true
    let timeoutId: number | undefined

    const scheduleNextPoll = (lastPollReturnedEmpty: boolean) => {
      if (!isActive) {
        return
      }

      timeoutId = window.setTimeout(() => {
        void loadMostActive()
      }, getPollIntervalMs(lastPollReturnedEmpty))
    }

    const loadMostActive = async () => {
      let lastPollReturnedEmpty = false

      try {
        const mostActiveResponse = await fetch(`${API_URL}/mostactive`)
        if (!mostActiveResponse.ok) {
          throw new Error(`Failed to load most active stock: ${mostActiveResponse.status}`)
        }

        const mostActiveText = await mostActiveResponse.text()
        const symbol = mostActiveText.trim().split(/[\s:]+/)[0] || "N/A"

        if (!isActive) {
          return
        }

        let totalVolume = 0

        try {
          const volumeResponse = await fetch(`${API_URL}/totalvolume/${encodeURIComponent(symbol)}`)
          if (volumeResponse.ok) {
            const volumeText = await volumeResponse.text()
            const numericVolume = Number.parseFloat(volumeText.trim())
            totalVolume = Number.isFinite(numericVolume) ? numericVolume : 0
          }
        } catch {
          totalVolume = 0
        }

        if (!isActive) {
          return
        }

        lastPollReturnedEmpty = totalVolume === 0 || symbol === "N/A"
        setMostActive({ symbol, volume: totalVolume })
      } catch (loadError) {
        if (!isActive) {
          return
        }

        lastPollReturnedEmpty = true
        setMostActive(null)
      } finally {
        if (isActive) {
          scheduleNextPoll(lastPollReturnedEmpty)
        }
      }
    }

    void loadMostActive()

    return () => {
      isActive = false
      if (timeoutId !== undefined) {
        window.clearTimeout(timeoutId)
      }
    }
  }, [])

  const allHistoryValues = subscribedSymbols.flatMap((symbol) => (priceHistory[symbol] ?? []).map((point) => point.price))
  const chartMin = allHistoryValues.length > 0 ? Math.min(...allHistoryValues) * 0.98 : 0
  const chartMax = allHistoryValues.length > 0 ? Math.max(...allHistoryValues) * 1.02 : 1

  const renderChart = () => {
    if (subscribedSymbols.length === 0) {
      return null
    }

    const width = 760
    const height = 220
    const padding = 36
    const legendOffset = 18

    const allTimestamps = subscribedSymbols.flatMap((symbol) => (priceHistory[symbol] ?? []).map((point) => point.msSinceEpoch))
    const firstTime = allTimestamps.length > 0 ? Math.min(...allTimestamps) : Date.now()
    const lastTime = allTimestamps.length > 0 ? Math.max(...allTimestamps) : Date.now()
    const range = Math.max(chartMax - chartMin, 1)
    const plotTop = padding + 8
    const plotBottom = height - padding - 12
    const dynamicYTicks = Array.from({ length: 5 }, (_, index) => {
      const ratio = index / 4
      const value = chartMin + ratio * range
      const y = plotBottom - ratio * (plotBottom - plotTop)
      return { value, y }
    })
    const toY = (price: number) => {
      const relative = (price - chartMin) / range
      return plotBottom - relative * (plotBottom - plotTop)
    }

    return (
      <div style={{ marginTop: "2rem", background: "#f9fafb", border: "1px solid #e5e7eb", borderRadius: 8, padding: "1rem" }}>
        <h3 style={{ marginTop: 0 }}>Recent Price History</h3>

        <svg viewBox={`0 0 ${width} ${height}`} width="100%" height={220} role="img" aria-label="Recent price history chart">
          {dynamicYTicks.map(({ value, y }) => (
            <g key={`${value}-${y}`}>
              <line x1={padding} x2={width - padding} y1={y} y2={y} stroke="#e5e7eb" strokeDasharray="3 3" />
              <text x={8} y={y + 4} fontSize="10" fill="#4b5563">
                {Number.isFinite(value) ? value.toFixed(2) : "0.00"}
              </text>
            </g>
          ))}

          <line x1={padding} x2={width - padding} y1={plotBottom} y2={plotBottom} stroke="#9ca3af" />
          <line x1={padding} x2={padding} y1={plotTop} y2={plotBottom} stroke="#9ca3af" />

          {subscribedSymbols.map((symbol, index) => {
            const points = priceHistory[symbol] ?? []
            if (points.length < 2) {
              return null
            }

            const linePath = points
              .map((point, pointIndex) => {
                const x =
                 padding +
                 ((point.msSinceEpoch - firstTime) / Math.max(lastTime - firstTime, 1)) * (width - padding * 2)
                const y = toY(point.price)
                return `${pointIndex === 0 ? "M" : "L"}${x},${y}`
              })
              .join(" ")

            return (
              <g key={symbol}>
                <path d={linePath} fill="none" stroke={CHART_COLORS[index % CHART_COLORS.length]} strokeWidth={2.5} />
                <text x={width - padding} y={padding + index * 14 + legendOffset} fill={CHART_COLORS[index % CHART_COLORS.length]} fontSize="11">
                  {symbol}
                </text>
              </g>
            )
          })}

          {[0, 0.5, 1].map((tickRatio) => {
            const x = padding + tickRatio * (width - padding * 2)
            const timeValue = firstTime + tickRatio * (lastTime - firstTime)
            return (
              <g key={tickRatio}>
                <text x={x} y={height - 10} textAnchor="middle" fontSize="10" fill="#4b5563">
                  {new Date(timeValue).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                </text>
              </g>
            )
          })}

          <text x={width / 2} y={height - 2} textAnchor="middle" fontSize="11" fill="#4b5563">
            Time
          </text>
          <text x={18} y={14} fontSize="11" fill="#4b5563">
            Price
          </text>
        </svg>
      </div>
    )
  }

  return (
    <article style={{ maxWidth: 900, margin: "0 auto" }}>
      <h2>Home</h2>
      <p>Watching prices for your active subscriptions.</p>

      {error ? <p role="alert">{error}</p> : null}

      {subscribedSymbols.length === 0 ? (
        <p>No symbols are currently subscribed.</p>
      ) : (
        <>
          <table
            style={{
              width: "100%",
              borderCollapse: "collapse",
              background: "#ffffff",
              border: "1px solid #d1d5db",
              borderRadius: 8,
              overflow: "hidden",
              boxShadow: "0 1px 3px rgba(0,0,0,0.08)",
            }}
          >
            <thead>
              <tr style={{ background: "#f3f4f6" }}>
                <th style={{ textAlign: "left", padding: "0.75rem 1rem" }}>Symbol</th>
                <th style={{ textAlign: "left", padding: "0.75rem 1rem" }}>Price</th>
                <th style={{ textAlign: "left", padding: "0.75rem 1rem" }}>Avg Volume</th>
              </tr>
            </thead>
            <tbody>
              {subscribedSymbols.map((symbol) => (
                <tr key={symbol} style={{ borderTop: "1px solid #e5e7eb" }}>
                  <td style={{ padding: "0.75rem 1rem", fontWeight: 600 }}>{symbol}</td>
                  <td style={{ padding: "0.75rem 1rem" }}>
                    {prices[symbol] ?? "Loading..."}
                  </td>
                  <td style={{ padding: "0.75rem 1rem" }}>
                    {averageVolumes[symbol] ?? "Loading..."}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <div style={{ marginTop: "2rem" }}>
            <h3>Most Active Stock</h3>
            <table
              style={{
                width: "100%",
                borderCollapse: "collapse",
                background: "#ffffff",
                border: "1px solid #d1d5db",
                borderRadius: 8,
                overflow: "hidden",
              }}
            >
              <thead>
                <tr style={{ background: "#f3f4f6" }}>
                  <th style={{ textAlign: "left", padding: "0.75rem 1rem" }}>Symbol</th>
                  <th style={{ textAlign: "left", padding: "0.75rem 1rem" }}>Volume</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td style={{ padding: "0.75rem 1rem", fontWeight: 600 }}>
                    {mostActive?.symbol ?? "Loading..."}
                  </td>
                  <td style={{ padding: "0.75rem 1rem" }}>
                    {mostActive ? mostActive.volume : "Loading..."}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          {renderChart()}
        </>
      )}
    </article>
  )
}
