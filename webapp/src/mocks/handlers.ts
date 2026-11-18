import { http, HttpResponse } from "msw"
import { API_URL, FINNHUB_API_URL, METRICS_API_URL } from "../constants"

export const handlers = [
  http.get(`${API_URL}/symbols`, () => {
    return HttpResponse.json({
      symbols: ["AAPL"],
    })
  }),

  http.get(`${METRICS_API_URL}/updates-volume`, () => {
    return HttpResponse.text("AAPL: 6\nMSFT: 4\nNVDA: 2")
  }),

  http.get(`${METRICS_API_URL}/price-volume`, () => {
    return HttpResponse.text("AAPL: 5\nMSFT: 3\nNVDA: 1")
  }),

  http.get(`${API_URL}/mostactive`, () => {
    return HttpResponse.text("AAPL")
  }),

  http.get(`${API_URL}/totalvolume/:symbol`, ({ params }) => {
    const symbol = String(params.symbol)
    const totalVolume = {
      AAPL: 123456,
      MSFT: 987654,
    }[symbol]

    return HttpResponse.text(String(totalVolume ?? 0))
  }),

  http.get(`${FINNHUB_API_URL}/subscribed-symbols`, () => {
    return HttpResponse.text("StockApp is subscribed to 2 symbols. They are: AAPL, MSFT.")
  }),

  http.get(`${FINNHUB_API_URL}/subscribe/:symbol`, ({ params }) => {
    const symbol = String(params.symbol)
    return HttpResponse.text(`StockApp is now subscribed to updates for ${symbol}`)
  }),

  http.delete(`${FINNHUB_API_URL}/unsubscribe/:symbol`, ({ params }) => {
    const symbol = String(params.symbol)
    return HttpResponse.text(`StockApp is now unsubscribed from updates for ${symbol}`)
  }),

  http.get(`${API_URL}/price/:symbol`, ({ params }) => {
    const symbol = String(params.symbol)
    const basePrice = {
      AAPL: 185.42,
      MSFT: 421.9,
    }[symbol]

    return HttpResponse.text(String(basePrice ?? 100))
  }),

  http.get(`${API_URL}/averagevolume/:symbol`, ({ params }) => {
    const symbol = String(params.symbol)
    const averageVolume = {
      AAPL: 123456,
      MSFT: 987654,
    }[symbol]

    return HttpResponse.text(String(averageVolume ?? 50000))
  }),
]
