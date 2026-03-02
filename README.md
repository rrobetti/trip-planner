# trip-planner

A Java CLI application that searches for flights and hotels across a range of dates using [Skyscanner](https://www.skyscanner.ie) and [Booking.com](https://www.booking.com) URLs, then reports the results.

## Features

- **Flight search**: generates Skyscanner search URLs for every date in a specified range
  - One-way or return flights
  - Multiple adults and children (with ages)
  - Cabin class (economy, business, etc.)
  - Baggage options (checked-bag, cabin-bag)
  - Stop preferences
  - Sortable by cheapest, fastest, etc.
- **Hotel search**: generates Booking.com search URLs for every check-in date in a range
  - Location, number of rooms, stay duration
  - Optional hotel name filter (finds the specific hotel in results)
  - Adults + children with ages
- **Price fetching** (optional `--fetch` flag): attempts to extract prices via HTTP/HTML parsing
  - Note: Skyscanner is heavily JavaScript-rendered so prices may not be available without a headless browser
- All results are reported at the end with URLs for manual browsing

## Build

Requires Java 17+ and Maven 3.6+.

```bash
mvn package
```

The fat JAR will be at `target/trip-planner-1.0.0-jar-with-dependencies.jar`.

## Usage

```bash
# URL generation only (no HTTP requests)
java -jar target/trip-planner-1.0.0-jar-with-dependencies.jar config.json

# With price fetching (best-effort HTTP requests)
java -jar target/trip-planner-1.0.0-jar-with-dependencies.jar config.json --fetch
```

## Configuration

Create a JSON config file. See `src/main/resources/config-example.json` for a full example.

```json
{
  "passengers": {
    "adults": 2,
    "childrenAges": [4, 6]
  },
  "flights": [
    {
      "origin": "DUB",
      "destination": "SAOA",
      "returnFlight": false,
      "dateRangeStart": "2026-07-15",
      "dateRangeEnd": "2026-07-20"
    }
  ],
  "hotels": [
    {
      "location": "Cabo de Santo Agostinho",
      "hotelName": "Suites Quatro Rodas",
      "rooms": 1,
      "stayNights": 7,
      "checkinRangeStart": "2026-07-15",
      "checkinRangeEnd": "2026-07-20"
    }
  ],
  "flightOptions": {
    "cabinClass": "economy",
    "stops": ["!direct", "!twoPlusStops"],
    "fareAttributes": ["checked-bag", "cabin-bag"],
    "preferDirects": false,
    "sortBy": "cheapest"
  },
  "extraFlightParams": {},
  "extraHotelParams": {}
}
```

### Configuration fields

| Field | Description |
|---|---|
| `passengers.adults` | Number of adult passengers |
| `passengers.childrenAges` | Array of children's ages |
| `flights[].origin` | IATA airport code (e.g. `DUB`) |
| `flights[].destination` | IATA airport code (e.g. `SAOA`) |
| `flights[].returnFlight` | `true` for return, `false` for one-way |
| `flights[].dateRangeStart` | First departure date to search (ISO-8601) |
| `flights[].dateRangeEnd` | Last departure date to search (ISO-8601) |
| `flights[].maxDurationDays` | Maximum trip duration in days (informational) |
| `hotels[].location` | Destination city/area name |
| `hotels[].hotelName` | Optional: specific hotel name to look for in results |
| `hotels[].rooms` | Number of rooms |
| `hotels[].stayNights` | Length of stay in nights |
| `hotels[].checkinRangeStart` | First check-in date to search (ISO-8601) |
| `hotels[].checkinRangeEnd` | Last check-in date to search (ISO-8601) |
| `flightOptions.cabinClass` | `economy`, `business`, `first`, `premiumeconomy` |
| `flightOptions.stops` | Stop filters, e.g. `["!direct", "!twoPlusStops"]` |
| `flightOptions.fareAttributes` | `checked-bag`, `cabin-bag` |
| `flightOptions.sortBy` | `cheapest`, `fastest`, `best` |
| `extraFlightParams` | Extra query parameters added to every Skyscanner URL |
| `extraHotelParams` | Extra query parameters added to every Booking.com URL |

## Running tests

```bash
mvn test
```
