# Inventory Management System

The Inventory Management System is a web application designed to manage products, customers, and billing for a small business. It allows users to create bills, add new customers, and track inventory with real-time updates. The application is built using Spring Boot, Thymeleaf, Alpine.js, and Bootstrap, with WebSocket integration for real-time notifications.

## Features
- **Product Management**: Add, view, and manage products in the inventory.
- **Customer Management**: Add new customers or select existing ones during billing.
- **Billing**: Create bills with a step-by-step wizard, including customer selection, product selection, and review.
- **Real-Time Updates**: Receive instant notifications of new bills via WebSocket.
- **Low Stock Alerts**: Get warnings when product stock is low.
- **Responsive UI**: Built with Bootstrap for a modern, mobile-friendly interface.
- **Dynamic Frontend**: Uses Alpine.js for interactive features like product search and form toggling.

## Tech Stack
- **Backend**: Spring Boot (Java)
- **Frontend**: Thymeleaf, Alpine.js, Bootstrap
- **Real-Time Communication**: 
  - **Used**: WebSocket (SockJS, STOMP) - Enables real-time bill notifications.
    - **Server-Sent Events (SSE)**: A lightweight alternative for streaming updates from server to client.
    - **GraphQL Subscriptions**: For real-time data queries and updates, offering a more flexible alternative to REST APIs.
    - **Apache Kafka**: A distributed streaming platform for handling large-scale, real-time data feeds.
    - **RabbitMQ**: A message broker that supports real-time messaging with high reliability.
    - **Firebase Realtime Database**: A NoSQL cloud database for real-time data synchronization across clients.
- **Notifications**: Toastify.js
- **Database**: H2 (in-memory database, can be swapped with MySQL/PostgreSQL)
- **Build Tool**: Maven

## Prerequisites
Before setting up the project, ensure you have the following installed:
- **Java**: JDK 17 or later
- **Maven**: For dependency management and building the project
- **Git**: To clone the repository
- **Node.js**: Optional, only if you need to install additional frontend dependencies
- **Browser**: A modern browser (e.g., Chrome, Firefox) to run the application

## Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/inventory-management-system.git
   cd inventory-management-system
