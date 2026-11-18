import type { RouteObject } from "react-router-dom"
import { StockApp } from "./StockApp"
import { Symbols } from "./Symbols"
import { Metrics } from "./Metrics"
import { Home } from "./Home"

// This object describes the structure of the app: its possible screens, parameters,
// and data loading operations.
export const routes: RouteObject[] = [
  {
    path: "",
    element: <StockApp />,
    // Child routes are given a place to render within the parent via the <Outlet /> component
    children: [
      {
        path: "",
        element: <Home />,
      },
      {
        path: "symbols",
        element: <Symbols />,
        loader: async () => {
          // Fetch the most active symbol from the API
          // Convert the response to JSON
          // Return the most active symbol
          return null
        },
        // Provide JSX to show if the data fails to load
        errorElement: <div>Failed to load symbols</div>,
        // Child routes can continue to be nested
        children: [],
      },
      {
        path: "metrics",
        element: <Metrics />,
      },
    ],
  },
]
