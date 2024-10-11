# Tech Test Library Application

This is a Spring Boot application designed for managing a library. It provides functionality to add, remove, borrow, and return books, as well as search for books by ISBN or author.


## Prerequisites

Before you can run the application, ensure you have the following installed:

- **Java 17 or later**
- **Maven 3.8+**
- An IDE (Optional, e.g., IntelliJ, Eclipse)

---

## Setup

1. **Clone the Repository:**

```bash
   git clone https://github.com/your-username/your-repo-n
```

1. **Run the below command to install dependencies:**

```bash
   mvn clean install
```
---
## Running the Application,

The application can be started using the following command: 

```bash
   mvn spring-boot:run
```
The application url is http://localhost:8080. Port number can be changed later on

---
## API Documentation

Below is the base URL: 
```bash
http://localhost:8080/api/library 
```

Request Methods: 

Add Book
Endpoint: ```POST /book/add ```
Request Body:
 ```json
{
  "isbn": "4561617292545",
  "title": "Spring Two",
  "author": "John Doe",
  "publicationYear": "2024",
  "availableCopies": 2
}
 ```

Remove Book
Endpoint: ```DELETE /book/remove/{isbn} ```
Path Parameter:
```@param isbn ```

Remove Book
Endpoint: ```DELETE /book/remove/{isbn} ```
Path Parameter:
```@param isbn ```

Find Book by ISBN
Endpoint: ```GET /findBookByIsbn/{isbn} ```
Path Parameter:
```@param isbn ```

Find Book by ISBN
Endpoint: ```GET /findBookByAuthor/{author} ```
Path Parameter:
```@param author ```

Borrow Book
Endpoint: ```POST /borrowBook/{isbn} ```
Path Parameter:
```@param isbn ```

Return Book
Endpoint: ```POST /returnBook/{isbn} ```
Path Parameter:
```@param isbn ```

---
## Running Tests

Run Tests
```bash 
mvn clean test
```
Generate Coverage Report
```bash 
mvn jacoco:report
```
The coverage report is found at:
```target/site/jacoco/index.html```

---
## Assumptions and Design Decisions

This application without README was completed in 90 mins. This was due to the TDD approach, IDE performance issue and focus more towards quality than quantity.

### Security Features
If there was more time given, security feature such as auth would have been added

### Circuit Breaker
This application would benefit from Circuit Breaker to handle any failure in the future. Assuming high number of requests coming in.

### Data Storage
This application uses an in-memory data structure (ConcurrentHashMap) to store book information. In a production environment, consider using a relational database such as MySQL or PostgreSQL.

### Error Handling
Detailed error messages can be enhanced by implementing a global exception handler such as AOP

### Input Validation
Additional validator can be implemented to make this application more robust. If more time given, DTOs can be added to serialise/de serialise json objects

