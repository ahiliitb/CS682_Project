**CS 682 Final Project : Stage 1 Report**

**Team Name:** Prismym

**Allocated Theme:** A website built for our IITB students. Its main purpose is to build a unified platform for IITB students to buy and sell items throughout the IITB campus, and register and claim lost items.

**Project Title:** CampusCart

**GitHub Repository Link:** [https://github.com/ahiliitb/CS682\_Project.git](https://github.com/ahiliitb/CS682_Project.git)

### **1\. Project Overview & Scope**

* **Project Theme Overview:** A centralized site for buying/selling of items, and claiming lost items and other additional features for IITB students.  
* **Problem Statement:** Fragmented platform ill equipped to handle buying and selling of items, or no mechanism for handling of lost items in the campus.  
* **Target Users:** Primary users \-\> IITB students,  secondary users-\> other IIT/NIT campus students.

### **2\. Committed Functionalities**

* **Core Functionalities (Commitment):**   
  * IITB email based user authentication.  
  * Buy/Sell feature  
  * Lost/Found feature  
  * Auction feature	  
  * Team making function (so students can form teams for different courses)  
  * Rating feature from buyer to rate seller  
  * Statistics of items bought and sold.  
* **Bonus Functionalities (Stage 1 Completed):**  
  * SSO based login with one-time access codes  
  * Role-based restrictions (Student/Admin roles)  
  * Admin moderation panel for report management  
  * In-app notifications system  
  * Visibility of items (Public/Private)  
  * Suggested price based on past items  
  * Pooling (connect students for pooling options such as car pool, ordering food or online item, travel etc)  
  * Library (so students can list their own books and exchange books and return after reading)  
* **Future Scale Functionalities:** Docker containerization and Kubernetes deployment for horizontal scaling  
  


### **3\. Technical Design & Framework**

* **Development Framework:**  
  ![][image1]  
* **Requirement Gathering & Architecture :**   
  * Requirements:  
    * The site is more usable as an app will not be downloaded.  
    * Should be iit exclusive.  
    * Should prevent fraud (bidding high in auction but then backing out )  
  * Architecture

               ![][image2]

### **4\. Project Status & Timeline**

**Stage 1 Completed Features:**

* Core features completed: User authentication, buy/sell, lost/found, auction system, team management, ratings, and statistics.
* Enhanced login system with SSO (Single Sign-On) using campus email-based one-time access codes.
* Role-based access control: Students can access all features, Admins can manage moderation.
* Admin moderation panel: Allows moderators to review, resolve, and reject user reports from any module.
* In-app real-time notification system: Users receive instant notifications for transactions, security events, team activities, library operations, and moderation updates.
* Library exchange module: Complete book listing, borrowing, return request, and return management workflow.
* Session persistence: User sessions are persisted in the database, surviving browser refresh and app restarts.
* Database: Migrated from MySQL setup to file-based H2 database for portability and ease of local deployment.

**What is Left (Bonus Features for Future Stages):**

* **Real OAuth2/OIDC Integration:** Replace current mock SSO with real IITB OAuth2 or SAML2 integration for enterprise-level security.
* **Advanced Search & Filtering:** Full-text search across listings with advanced filters (price range, condition, location, category).
* **Analytics Dashboard:** Comprehensive statistics and insights for admins (user growth, transaction volume, popular items, fraud patterns).
* **Mobile App:** Native or cross-platform mobile application for iOS and Android.
* **API Documentation:** RESTful API with OpenAPI/Swagger documentation for third-party integrations.
* **Real-time Notifications:** WebSocket-based real-time push notifications instead of polling-based in-app notifications.
* **Advanced Ratings & Reviews:** Multi-dimensional ratings (reliability, condition accuracy, communication), with photo uploads and helpful votes.
* **Recommendation Engine:** ML-based product recommendations based on browsing history and purchase behavior.
* **Multi-language Support:** Internationalization for Hindi, Marathi, and other languages.
* **Email Notifications:** SMTP-based email delivery for critical events (order confirmation, shipping updates, account security).
* **Payment Gateway Integration:** Secure payment processing (GPay, PhonePe, credit card) with PCI-DSS compliance.
* **Seller Dashboard:** Analytics for sellers (inventory, sales trends, customer reviews, earnings).
* **Dispute Resolution System:** Formal mechanism for buyer-seller disputes with evidence uploads and arbitration.
* **Seller Verification:** KYC/AML verification for sellers above a transaction threshold.
* **Blockchain Verification:** Optional blockchain-based verification for high-value items to prevent counterfeits.
* **Dark Mode:** User interface theme customization for better accessibility.
* **Accessibility Features:** WCAG 2.1 AA compliance for users with disabilities.


### **5\. Team Contribution & AI Disclosure**

* **Member Contributions:** Clearly state the specific tasks completed by each of the 3/4 members during Stage  
  * Mayank Mehar (24m0785)  
    * Initialized project in github.  
    * Initialized backend using springer, and finalized directory structure,, and added dependencies.  
    * Created database schema and database in MySQL.  
    * Created api endpoints.  
    * Created data structures and dto.

  * Nitesh Singh (22b0919)  
    * Removed the dashboard from the authentication flow so that only logged-in users can access it.  
    * Added webmail verification and ensured that only IIT Bombay email addresses are accepted.  
    * Set the default dashboard view to the Buy/Sell page. Additionally, made it extensible so contributors can add more features if they wish.  
    * Implemented SSO login system with one-time access codes (admin-issued).  
    * Implemented role-based access control (Student/Admin roles).  
    * Built admin moderation panel for reviewing and managing user reports.  
    * Built in-app notification system for transactional, security, and informational events.  
    * Completed library exchange module (add, borrow, request return, mark returned).  
    * Removed email notification system (kept in-app only as required).  
    * Integrated notifications across all modules (buy/sell, auction, lost/found, teams, pooling, library, moderation, SSO).  
  * Sukhmanjot  (22b0976)  
    * Designed the architecture and appropriate User Flow Diagram considering all features and functionalities.  
    * Report Making   
  * Ahil Khan (22b0911)  
    * Created front end page for login and registration page.  
    * Created controller for these pages.  
* **Generative AI Disclosure:** List the specific parts of the project where Gen-AI tools (ChatGPT, Gemini, etc.) were used or will be used (e.g., boilerplate code, documentation).  
  * Initialized project basics using springer.  
  * Used Github copilot to implement Get set features in data structures.  
  * Used GitHub Copilot for implementing service methods, notification integrations, and boilerplate code generation.

Mayank \-\>  
	  
Nitesh-\>  
	  
Sukhman-\>  
	  
Ahil-\>  
	  
