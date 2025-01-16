# Car Rental Application

## Overview

The Car Rental Application is a Compose Multiplatform project designed to manage car rentals with features such as user authentication, car browsing, filtering, and rental management. It is built using modern Kotlin-based technologies, providing cross-platform support, including for web applications. This project is structured to integrate seamlessly with an ASP.NET backend.

---

## Features

### Core Functionalities

1. **User Authentication**
   - Google OAuth integration for secure sign-in.
   - JWT-based token authentication.
   - Persistent login state using `localStorage`.

2. **Car Browsing**
   - Display a paginated list of available cars.
   - Dynamic filtering based on brand, model, year, type, and location.
   - Detailed car view with essential information.

3. **Rental Management**
   - View user's rental history.
   - Active rentals with return functionality.
   - Backend communication for managing rentals.

4. **Notification System**
   - Displays notifications for key events such as rental updates or errors.
   - Dismissable notification UI for user feedback.

5. **Responsive Design**
   - Designed for web compatibility with an intuitive user interface.
   - Adaptive layouts for different screen sizes.

---

## Technologies Used

### Frontend
- **Compose Multiplatform**: Used to develop cross-platform UI.
- **Kotlin**: Primary programming language.
- **Ktor Client**: For network communication with the ASP.NET backend.
- **Material Design**: Implements Material Design principles for UI components.

### Backend
- **ASP.NET**: Backend framework for handling authentication and rental management.
- **Entity Framework**: ORM for database interactions.

---

## Project Structure

### `CarRentalAppViewModel`
- Centralized state management using `uiState`.
- Handles user actions and updates UI accordingly.
- Key Methods:
  - `initializeAuthState`: Manages user authentication state on app startup.
  - `initializeDataLayer`: Loads backend configurations and initializes API services.
  - `loadInitialData`: Fetches the first page of available cars.
  - `getRentedCars`: Retrieves the user's rental history.

### UI Components
1. **`StandardCarsView`**
   - Displays a paginated list of available cars fetched from the backend.
   - Integrates `PaginationControls` for navigating pages.

2. **`FilteredCarsView`**
   - Displays filtered car results fetched from the backend.
   - Implements frontend-based pagination.

3. **`UserScreen`**
   - Displays user details with the ability to update profile information.
   - Shows rental history and active rentals.

4. **`CarDetailsScreen`**
   - Provides a detailed view of a selected car.
   - Includes options for rental valuation.

5. **`RentalCard`**
   - Displays details of a specific rental in a card layout.
   - Supports ordering by status (`planned`, `inProgress`, `pendingReturn`, `ended`).

---

## API Integration

### Endpoints

#### User Management
- **POST `/api/auth/google`**
  - Handles Google OAuth authentication and returns a JWT token.
- **GET `/api/users/{id}`**
  - Fetches user details by ID.

#### Car Management
- **GET `/api/cars`**
  - Retrieves paginated car data.
- **GET `/api/cars/filter`**
  - Fetches cars based on filter parameters.

#### Rental Management
- **POST `/api/rentals`**
  - Submits a new rental request.
- **POST `/api/rentals/return`**
  - Marks a rental as returned.

---

## Installation and Setup

### Prerequisites
- **Kotlin**: Ensure Kotlin is installed on your machine.
- **Node.js**: Required for running the web server.
- **ASP.NET Backend**: The backend must be running for full functionality.

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo-url/car-rental-app.git
   cd car-rental-app
