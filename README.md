# user-profile-read-service
This repository contains a read-heavy backend service for managing user profiles, built with Java and the Spring Boot framework. It demonstrates a common architecture pattern involving a primary database (PostgreSQL) and a caching layer (Redis) to optimize performance for frequent read operations.

## Architecture

The service follows a layered architecture to separate concerns:

-   **Controller (`UserProfileController`)**: Exposes RESTful API endpoints for interacting with user profiles.
-   **Service (`UserProfileService`)**: Implements the core business logic, orchestrating calls between the cache and the repository. It employs the **Cache-Aside** pattern.
-   **Cache (`UserCache` interface)**: An abstraction for the caching mechanism. The primary implementation, `RedisUserCache`, uses Redis for distributed caching.
-   **Repository (`UserRepository` interface)**: An abstraction for the data persistence layer. The primary implementation, `JdbcUserRepository`, uses Spring's `JdbcTemplate` to interact with a PostgreSQL database.
-   **Model (`User`)**: A simple Plain Old Java Object (POJO) representing the user entity.

### Read Flow (Cache-Aside Pattern)

When a request is made to `GET /users/{id}`:
1.  The service first checks the Redis cache for the user data.
2.  **Cache Hit**: If the user is found in the cache, it's returned immediately.
3.  **Negative Cache Hit**: If a "not found" entry exists for the user ID, the service immediately throws a `UserNotFoundException`, preventing a useless database query.
4.  **Cache Miss**: If the user is not in the cache, the service queries the PostgreSQL database.
    -   If the user is found in the database, the data is added to the Redis cache with a Time-To-Live (TTL) and then returned.
    -   If the user is not found in the database, a negative cache entry is created in Redis to prevent repeated lookups for the same non-existent ID. A `UserNotFoundException` is then thrown.

### Write Flow (Cache Invalidation)

When a request is made to `PUT /users/{id}`:
1.  The service updates the user's data in the PostgreSQL database.
2.  After the database update is successful, the corresponding entry (both positive and negative) for that user is invalidated (deleted) from the Redis cache. This ensures that the next read for this user will fetch the fresh data from the database and repopulate the cache.

## API Endpoints

-   `GET /users/{id}`
    -   **Description**: Retrieves the profile for a specific user.
    -   **Path Variable**: `id` (String) - The unique identifier of the user.
    -   **Success Response (200 OK)**:
        ```json
        {
          "id": "1",
          "name": "Alice"
        }
        ```
    -   **Error Response (404 Not Found)**:
        ```json
        {
          "message": "Could not find user with id: 99"
        }
        ```

-   `PUT /users/{id}`
    -   **Description**: Updates an existing user's profile.
    -   **Path Variable**: `id` (String) - The unique identifier of the user. The ID in the path must match the ID in the request body.
    -   **Request Body**: A JSON object representing the user.
        ```json
        {
          "id": "1",
          "name": "Alice Updated"
        }
        ```
    -   **Success Response**: `200 OK` (no body)
    -   **Error Response (400 Bad Request)**: If path `id` and body `id` do not match.
    -   **Error Response (404 Not Found)**: If the user to be updated does not exist in the database.

## Prerequisites

-   Java 17 or later
-   Maven 3.x
-   A running PostgreSQL instance
-   A running Redis instance

## Getting Started

### 1. Database Setup

Connect to your PostgreSQL instance and run the following commands to create the necessary database, user, and table.

```sql
-- Create a dedicated database (optional)
CREATE DATABASE user_profile_db;

-- Connect to the new database and create the users table
\c user_profile_db;

CREATE TABLE users (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Insert some sample data
INSERT INTO users (id, name) VALUES ('1', 'Alice');
INSERT INTO users (id, name) VALUES ('2', 'Bob');
```

### 2. Configuration

The application configuration is located in `src/main/resources/application.properties`. Update the PostgreSQL and Redis connection details if they differ from the defaults.

```properties
# PostgreSQL datasource
spring.datasource.url=jdbc:postgresql://localhost:5432/user_profile_db
spring.datasource.username=<username>
spring.datasource.password=<pass>

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### 3. Build and Run

1.  Clone the repository:
    ```sh
    git clone https://github.com/sushi0706/user-profile-read-service.git
    cd user-profile-read-service
    ```

2.  Build the project using Maven:
    ```sh
    mvn clean install
    ```

3.  Run the application:
    ```sh
    mvn spring-boot:run
    ```

The service will start on `http://localhost:8080`. You can now use a tool like `curl` or Postman to interact with the API.
