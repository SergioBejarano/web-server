# Non-Concurrent Web Server

This project implements a basic HTTP server in Java. It serves static files from the `www` directory and exposes two REST services:

- **GET /app/hello?name=YourName**  
  Returns a JSON greeting message.

- **POST /app/hello**  
  Returns a JSON echo of the message sent in the request body.

## Features

- Serves static files (HTML, CSS, JS, images) from the `resources` folder.
- Handles GET and POST requests for REST endpoints.
- Easily extensible for new REST services.
- No external dependencies required except Maven for build.

## How to Build and Run

1. **Compile and package the project using Maven:**
   ```sh
   mvn clean package
   ```

<img width="2879" height="1412" alt="image" src="https://github.com/user-attachments/assets/36a40b70-6de5-4122-9058-89e7645bd1c4" />


2. **Run the server:**
   ```sh
   mvn exec:java -Dexec.mainClass="co.edu.escuelaing.httpserver.HttpServer"
   ```

<img width="2878" height="483" alt="image" src="https://github.com/user-attachments/assets/1c20e186-5f5f-4a3f-ac03-27a91043f331" />


3. **Open browser and go to:**
   ```
   http://localhost:35000/
   ```
<img width="2879" height="1702" alt="image" src="https://github.com/user-attachments/assets/eebf5c40-da41-4e51-a3d4-3512f0cccf30" />

From console:
<img width="2879" height="861" alt="image" src="https://github.com/user-attachments/assets/68973092-b5e7-4a67-bc97-ebcfe5037a86" />

## Unit Tests

<img width="2174" height="437" alt="image" src="https://github.com/user-attachments/assets/96cd9f5f-c21a-4087-947e-87ee7be7ef32" />


## REST Endpoints - Evaluation with tests

### GET /app/hello

- **Description:** Returns a greeting message in JSON format.
- **Example:**
  ```
  GET http://localhost:35000/app/hello?name=Sergio
  ```
- **Response:**
  ```json
  {"mensaje": "Hola Sergio"}
  ```

<img width="2879" height="1205" alt="image" src="https://github.com/user-attachments/assets/da075283-7470-4735-9131-e7e9e5b055dd" />

<img width="1412" height="280" alt="image" src="https://github.com/user-attachments/assets/199f8b31-4e56-433b-9897-3a0cb35ba5a8" />

In case of not including any name to test GET:

<img width="2879" height="765" alt="image" src="https://github.com/user-attachments/assets/8e4a1bac-7b69-4418-9cb8-1f7cfcd59fc8" />

### POST /app/hello

- **Description:** Echoes the message sent in the request body.
- **Example using curl:**
  ```sh
  curl -X POST http://localhost:35000/app/hello -d "Hello from POST"
  ```
- **Response:**
  ```json
  {"echo": "Hello from POST"}
  ```

From console:
<img width="2346" height="424" alt="image" src="https://github.com/user-attachments/assets/e57235c8-c431-48f8-8802-592e7d4839d0" />

From web page:

<img width="2879" height="1208" alt="image" src="https://github.com/user-attachments/assets/7180281b-9b42-477e-88d8-a68c06453fdf" />

<img width="1002" height="294" alt="image" src="https://github.com/user-attachments/assets/43ac277f-7b30-418b-9c6c-41f6d0b57191" />

## Static Files

Place your HTML, CSS, JS, and image files in the `resources` directory.  
The server will serve them automatically.

<img width="2879" height="1681" alt="image" src="https://github.com/user-attachments/assets/4c1ed4ea-0e83-47f2-8b6b-eef3b5d8a7c6" />

<img width="2879" height="1706" alt="image" src="https://github.com/user-attachments/assets/0f6ccf66-4e79-426d-a77f-64707dbe6af2" />

<img width="2876" height="1707" alt="image" src="https://github.com/user-attachments/assets/e4876115-1515-4cb1-9b92-468ed1367706" />

When the file does not exist: 

<img width="2879" height="1177" alt="image" src="https://github.com/user-attachments/assets/d17e6413-72ce-4560-8b13-bb3eecd30924" />


## Prototype architecture

This means that the prototype flow is as follows:

The client sends a request (GET or POST).

HttpServer analyzes the route:

If it is a REST service → it calls RestServices.

If it is a file → it returns it.

If it does not exist → it sends a 404.

RestServices builds the HTTP response with JSON.

HttpServer sends it back to the client.
![My First Board (2)](https://github.com/user-attachments/assets/b6b81be5-0bac-4aee-8ddf-5c4bc15b7830)


## Author

Sergio Bejarano
