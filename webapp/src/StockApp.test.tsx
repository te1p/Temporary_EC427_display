import { test, expect, describe } from "vitest"
import { fireEvent, render, screen } from "@testing-library/react"
import "@testing-library/jest-dom"
import { createMemoryRouter, RouterProvider } from "react-router-dom"
import { http, HttpResponse } from "msw"
import { server } from "./tests/setup"
import { API_URL, FINNHUB_API_URL } from "./constants"
import { routes } from "./routes"

describe("StockApp", () => {
  test("loads the app root", async () => {
    const router = createMemoryRouter(routes, {
      initialEntries: ["/"],
    })

    render(<RouterProvider router={router} />)

    await screen.findByText("Stock App")

    expect(screen.getByText("Symbols")).toBeVisible()
    expect(screen.getByText("Metrics")).toBeVisible()
  })

  test("renders Home page data fetched from the configured endpoints", async () => {
    const router = createMemoryRouter(routes, {
      initialEntries: ["/"],
    })

    render(<RouterProvider router={router} />)

    expect(await screen.findAllByText("AAPL")).toHaveLength(2)
    expect(await screen.findAllByText("MSFT")).toHaveLength(1)
    expect(await screen.findAllByText("123456")).toHaveLength(2)
    expect(await screen.findByText("Most Active Stock")).toBeVisible()
  })

  test("handles an empty subscribed-symbols state", async () => {
    server.use(
      http.get(`${FINNHUB_API_URL}/subscribed-symbols`, () => {
        return HttpResponse.text("StockApp is subscribed to 0 symbols. They are: ")
      }),
      http.get(`${API_URL}/price/:symbol`, () => {
        return HttpResponse.text("0")
      }),
      http.get(`${API_URL}/averagevolume/:symbol`, () => {
        return HttpResponse.text("0")
      }),
    )

    const router = createMemoryRouter(routes, {
      initialEntries: ["/"],
    })

    render(<RouterProvider router={router} />)

    expect(await screen.findByText("No symbols are currently subscribed.")).toBeVisible()
  })

  test("shows an error message when a fetch fails", async () => {
    server.use(
      http.get(`${FINNHUB_API_URL}/subscribed-symbols`, () => {
        return HttpResponse.json({ message: "Service unavailable" }, { status: 500 })
      }),
    )

    const router = createMemoryRouter(routes, {
      initialEntries: ["/"],
    })

    render(<RouterProvider router={router} />)

    expect(await screen.findByRole("alert")).toHaveTextContent("Failed to load subscribed symbols: 500")
  })

  test("surfaces a 409 conflict when subscribing a duplicate or over-limit symbol", async () => {
    server.use(
      http.get(`${FINNHUB_API_URL}/subscribed-symbols`, () => {
        return HttpResponse.text("StockApp is subscribed to 2 symbols. They are: AAPL, MSFT.")
      }),
      http.get(`${FINNHUB_API_URL}/subscribe/:symbol`, ({ params }) => {
        const symbol = String(params.symbol)
        if (symbol === "AAPL" || symbol === "NVDA") {
          return HttpResponse.text(`Symbol ${symbol} is already subscribed or the limit has been reached.`, {
            status: 409,
          })
        }

        return HttpResponse.text(`StockApp is now subscribed to updates for ${symbol}`)
      }),
    )

    const router = createMemoryRouter(routes, {
      initialEntries: ["/symbols"],
    })

    render(<RouterProvider router={router} />)

    await screen.findByRole("heading", { name: "Symbols" })
    const input = screen.getByLabelText("Subscribe to a symbol")
    fireEvent.change(input, { target: { value: "AAPL" } })
    fireEvent.click(screen.getByRole("button", { name: "Subscribe" }))

    expect(await screen.findByRole("alert")).toHaveTextContent("Symbol AAPL is already subscribed or the limit has been reached.")
  })
})
