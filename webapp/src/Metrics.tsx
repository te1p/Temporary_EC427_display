import * as React from "react"
import { METRICS_API_URL } from "./constants"

type MetricEntry = {
  symbol: string
  value: number
}

const parseMetricsText = (text: string): MetricEntry[] => {
  const lines = text
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter(Boolean)

  return lines.flatMap((line) => {
    const match = line.match(/^([^:]+):\s*(\d+(?:\.\d+)?)$/)
    if (!match) {
      return []
    }

    const [, symbol, valueText] = match
    const value = Number.parseFloat(valueText)

    return Number.isFinite(value) ? [{ symbol: symbol.trim(), value }] : []
  })
}

export const Metrics: React.FC = () => {
  const [updatesVolume, setUpdatesVolume] = React.useState<MetricEntry[]>([])
  const [priceVolume, setPriceVolume] = React.useState<MetricEntry[]>([])
  const [error, setError] = React.useState<string | null>(null)

  React.useEffect(() => {
    let isActive = true

    const loadMetrics = async () => {
      try {
        const [updatesResponse, priceResponse] = await Promise.all([
          fetch(`${METRICS_API_URL}/updates-volume`),
          fetch(`${METRICS_API_URL}/price-volume`),
        ])

        if (!updatesResponse.ok || !priceResponse.ok) {
          throw new Error("One or more metrics endpoints failed to load.")
        }

        const [updatesText, priceText] = await Promise.all([
          updatesResponse.text(),
          priceResponse.text(),
        ])

        if (!isActive) {
          return
        }

        setUpdatesVolume(parseMetricsText(updatesText))
        setPriceVolume(parseMetricsText(priceText))
        setError(null)
      } catch (loadError) {
        if (!isActive) {
          return
        }

        setError(
          loadError instanceof Error
            ? loadError.message
            : "Unable to load metrics right now.",
        )
      }
    }

    void loadMetrics()
    const intervalId = window.setInterval(() => {
      void loadMetrics()
    }, 5000)

    return () => {
      isActive = false
      window.clearInterval(intervalId)
    }
  }, [])

  const renderTable = (title: string, rows: MetricEntry[]) => (
    <section>
      <h3>{title}</h3>
      {rows.length === 0 ? (
        <p>No metric data available.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Symbol</th>
              <th>Count</th>
            </tr>
          </thead>
          <tbody>
            {rows.map(({ symbol, value }) => (
              <tr key={symbol}>
                <td>{symbol}</td>
                <td>{value}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )

  return (
    <article>
      <h2>Metrics</h2>
      {error ? <p role="alert">{error}</p> : null}

      {renderTable("Updates Volume", updatesVolume)}
      {renderTable("Price Volume", priceVolume)}
    </article>
  )
}
