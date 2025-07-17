# Micro-Blog-Platform

A full-stack micro-blogging platform designed to demonstrate scalable backend architecture, secure user authentication, and robust data management. Built with Java, Spring Boot, and MySQL, this project showcases enterprise-level backend development skills relevant for software engineering roles in distributed systems and fintech.

## Project Overview

This platform enables users to:

- Register, log in, and manage secure sessions
- Create, view, and interact with posts (like, bookmark, comment)
- Follow/unfollow other users and view personalized feeds
- Search posts by hashtags
- Experience a responsive UI with Mustache templates and custom CSS

## Key Backend Features

- **Spring Boot Service Layer**: Modular services for users, posts, comments, likes, bookmarks, follows, and hashtags
- **Secure Authentication**: BCrypt password hashing, session-based authentication, and request interception
- **SQL Injection Prevention**: All database access uses prepared statements
- **Transaction Management**: Ensures data consistency for post creation and engagement actions
- **Personalized Feeds**: Efficient SQL queries for user-specific content
- **RESTful MVC Controllers**: Clean separation of concerns and robust error handling
- **Comprehensive Logging**: Error and event logging for maintainability

## Architecture Overview

- **Backend**: Java 17, Spring Boot 3.1.4, Spring Security, JDBC
- **Database**: MySQL 8.0.33, relational schema for users, posts, comments, relationships, and engagement
- **Frontend**: Mustache templates, HTML5, CSS3, JavaScript
- **Build/Run**: Maven

## Setup Instructions

1. **Start MySQL** (Docker or local install)
2. **Initialize Database**
   - Run `database_setup.sql` to create tables and seed essential data
3. **Build & Run Backend**
   - Navigate to the project directory with `pom.xml`
   - Run:
     ```sh
     mvn spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port=8081'
     ```
4. **Access the App**
   - Open [http://localhost:8081/](http://localhost:8081/) in your browser

## Tech Stack

- **Java 17**
- **Spring Boot 3.1.4**
- **Spring Security**
- **MySQL 8.0.33**
- **Maven**
- **BCrypt**
- **Mustache Templates**
- **HTML5, CSS3, JavaScript**

## Example Backend Skills Demonstrated

- Service-oriented architecture
- Dependency injection and session management
- Secure authentication and password management
- SQL best practices and transaction safety
- RESTful API design and MVC pattern
- Error handling and logging

---

This project is designed for demonstration and learning purposes, and can be extended with additional features such as API endpoints, containerization, or cloud deployment for further backend engineering practice.
