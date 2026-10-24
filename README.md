# Smart Parking System

A Spring Boot REST API for managing a multi-floor parking lot. The application supports vehicle registration, automatic parking spot allocation, parking tickets, fee calculation, and spot availability tracking.

## Features

- Multi-floor parking lot management
- Parking spots for motorcycles, cars, and buses
- Automatic allocation of compatible parking spots
- Vehicle check-in and check-out
- Parking fee calculation based on vehicle type and parking duration
- Active parking ticket tracking
- Duplicate check-in prevention
- Transactional database operations and pessimistic locking for spot allocation

## Technology Stack

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- H2 in-memory database
- Maven
- Postman

## Prerequisites

- Java 21 or later
- Maven, or the included Maven wrapper
- IntelliJ IDEA or another Java IDE
- Postman for API testing

## Running the Application

1. Clone or download the repository.
2. Open the project in IntelliJ IDEA.
3. Ensure Java 21 is configured as the project SDK.
4. Run `SmartParkingSystemApplication.java`.

The application starts on port `8081`.

Base URL:

`http://localhost:8081`

The application uses an in-memory H2 database. Data is reset when the application restarts.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/parking/floors` | Create a parking floor |
| POST | `/api/parking/spots` | Create a parking spot |
| GET | `/api/parking/spots` | List parking spots |
| POST | `/api/parking/check-in` | Check in a vehicle |
| POST | `/api/parking/check-out/{ticketId}` | Check out a vehicle |
| GET | `/api/parking/active-tickets` | List active parking tickets |

## Sample Requests

### Create a Floor

`POST /api/parking/floors`

```json
{
  "floorNumber": 0
}
```

### Create a Parking Spot

`POST /api/parking/spots`

```json
{
  "floorNumber": 0,
  "spotNumber": "C-01",
  "spotType": "COMPACT"
}
```

Supported spot types: `MOTORCYCLE`, `COMPACT`, `LARGE`.

### Check In a Vehicle

`POST /api/parking/check-in`

```json
{
  "registrationNumber": "KA01AB1234",
  "vehicleType": "CAR"
}
```

Supported vehicle types: `MOTORCYCLE`, `CAR`, `BUS`.

### Check Out a Vehicle

`POST /api/parking/check-out/{ticketId}`

Replace `{ticketId}` with the ID returned by check-in. No request body is required.

## Parking Fee Rules

| Vehicle Type | Fee per Started Hour |
|---|---:|
| Motorcycle | ₹20 |
| Car | ₹50 |
| Bus | ₹100 |

The fee is calculated using the number of started hours, with a minimum charge of one hour.

## Error Handling

The API returns appropriate HTTP error responses for invalid requests and conflicts, including duplicate check-ins, unavailable compatible spots, and attempts to close an already-closed ticket.

## Database

The application uses H2 with Spring Data JPA. Entities are persisted while the application is running, but the in-memory database is recreated on restart.

## License

Add a license if you intend to distribute this project publicly.