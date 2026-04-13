📦 CampusCart – IITB Marketplace & Community Platform

CampusCart is a full-stack web application built for the IIT Bombay ecosystem that centralizes student activities like:

🛒 Buying & selling items
🔨 Auctions
🔍 Lost & found
📚 Library/book exchange
👥 Team formation & pooling
🔔 Notifications & moderation

Instead of scattered WhatsApp groups and random posts, this provides a secure, campus-only platform with structured workflows and trust mechanisms.

🚀 Features
Secure Authentication
IITB email-based registration & verification
One-time code (SSO-style) login
Role-based access (Student/Admin)
Marketplace
Buy/sell listings with images
Auction system with bidding
Suggested pricing based on history
Community Modules
Lost & found tracking
Library book exchange
Team formation with size limits
Pooling (travel/order coordination)
Governance & Trust
Transaction-based ratings
Admin moderation system
In-app notifications
Admin analytics dashboard
🛠️ Tech Stack

Backend

Java 21
Spring Boot
Spring MVC
Spring Security
Spring Data JPA (Hibernate)

Frontend

Thymeleaf (server-side rendering)

Database

H2 (file-based, no external setup needed)

Build & Tools

Maven
Selenium (for UI testing)
📂 Project Structure
src/main/java/com/SE/final_project/
│── config/        # Security & configuration
│── controller/    # API endpoints
│── service/       # Business logic
│── repository/    # Database access (JPA)
│── model/         # Entity classes

src/main/resources/
│── templates/     # Thymeleaf UI
│── static/        # Static assets
│── application.properties
⚙️ Setup & Installation
1. Clone the repository
git clone https://github.com/ahiliitb/CS682_Project.git
cd CS682_Project
2. Build the project
./mvnw clean compile
3. Run the application
./mvnw spring-boot:run
4. Open in browser
http://localhost:8080
🧪 Running Tests (Optional)
./mvnw test

Selenium-based UI tests are included for key user flows.

🔐 Default Notes
Uses H2 file-based database → no setup required
Data persists locally during runs
Only IITB email format is accepted for registration (can be modified in code)
📸 Demo

👉 (Add your video/demo link here)

📈 Future Improvements
OAuth2 / Google SSO integration
Advanced search & recommendation engine
Production-grade database (PostgreSQL/MySQL)
Analytics & monitoring dashboard
👨‍💻 Contributors
Mayank Mehar
Nitesh Singh
Ahil Khan
Sukhmanjot
