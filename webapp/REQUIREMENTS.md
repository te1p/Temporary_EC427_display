The application is for people interested in keeping track of stocks. The StockApp needs to be
able to  display metrics concerning subscribed stock data.

The webapp needs to have screens for requesting information, managing subscriptions, and displaying metrics of the StockApp

Requesting Information
This screen is supposed to implement UI for /symbols, /price/<symbol>, /mostactive endpoints.
These endpoints are used by monolith-service on port 8000.

Displaying Metrics
This screen is supposed to implement UI for /symbols, /price/<symbol>, /mostactive endpoints.
These endpoints are used by monolith-service (Metrics Server) on port 8001.

Managing Subscriptions
This screen is supposed to implement UI for /subscribe/<symbol>, /unsubscribe/<symbol>, /subscribed-symbols endpoints.
These endpoints are used by finnhub-service	on port 8004

When queue-service is down the webapp should display that there is no communication between monolith and finnhub.

Out of scope:
