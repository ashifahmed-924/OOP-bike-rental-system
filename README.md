# Bike Rental System

Java JSP/Servlet web application for managing bikes, users, and ride bookings.

## Features

- Landing page with hero section, navigation bar, and bike fleet listing
- Rider registration and login
- Operator bike management
- Rider bike booking flow
- Admin user management
- File-based persistence using text files

## Project Structure

- `src/main/java` - DAOs, models, and servlets
- `src/main/webapp` - JSP pages and web resources
- `src/main/webapp/data` - text files used for users, bikes, and ride bookings

## Requirements

- Java 8 or newer
- Maven 3.8+ if you want to build from command line
- Tomcat 9 or another servlet container compatible with `javax.servlet` 4.x

## Running On Another PC

1. Clone the repository:

```bash
git clone https://github.com/ashifahmed-924/OOP-bike-rental-system.git
```

2. Build the WAR file:

```bash
mvn clean package
```

3. Deploy the generated WAR:

- File: `target/BikeRentalSystem.war`
- Deploy it to Tomcat 9

4. Open the app in a browser:

```text
http://localhost:8080/BikeRentalSystem/
```

## Running From IntelliJ IDEA

1. Open the project folder.
2. Make sure a JDK is configured.
3. Add a Tomcat 9 local run configuration.
4. Deploy the exploded artifact or WAR artifact.
5. Start the server and open `http://localhost:8080/BikeRentalSystem/`.

## Default Data

The project currently includes these sample accounts in `src/main/webapp/data/users.txt`:

- `admin / admin123` as `ADMIN`
- `test / 123456` as `RIDER`
- `mufmoh / 123456` as `OPERATOR`

Sample bike data exists in `src/main/webapp/data/bikes.txt`.

## Data Storage Note

This project uses text files for persistence. The DAO layer is configured to prefer `src/main/webapp/data` when the project root is available, which makes it easier to move the project between PCs without losing the checked-in data files.

## Notes

- If `mvn` is not installed, install Maven and ensure it is available in your `PATH`.
- If Tomcat shows old data or pages, clean/redeploy the application.
