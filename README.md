📝 Task Management Application (Spring Boot Backend)

A robust backend service for managing tasks with user authentication, authorization, and security features built using Spring Boot. Designed for real-world applications, this API supports JWT-based authentication, role-based access control, and forgot-password functionality.

🚀 Features Implemented

✅ User Management

User Signup & Login
Password hashing using BCrypt
Role-based access control (USER, ADMIN)
Last login & registration timestamps

✅ Task Management
Create, update, delete, and view tasks
Task ownership and filtering (priority, deadline, status)
Alert system for upcoming task deadlines

✅ Security (Spring Security)

JWT Authentication
Role-based authorization
Secure endpoints for /user/**, /admin/**, and /tasks/**
Stateless session management

✅ Forgot Password Flow (Email-based)

Generate secure token (6-digit code)
Send reset token via email (using JavaMailSender)
Reset password using the received token
Token expiration and validation logic

🔐 JWT Authentication Flow

Login → User sends username & password to /auth.
Authentication → Spring authenticates using AuthenticationManager & CustomUserDetailsService.
Token Generation → JWT is created using JwtUtils and returned to the client.
Request with Token → Client sends JWT in the Authorization header.
Token Validation → JwtAuthenticationFilter validates the token.
Set Security Context → Authenticated user is set in SecurityContextHolder.
Authorization Check → Roles are checked, controller is accessed.

🔁 Forgot Password Flow

User hits /auth/forgot-password with registered email.
System generates a random 6-digit token and sets expiry.
Sends token via email using JavaMailSender.
User submits the new password via /auth/reset-password?token=xxxxxx.
Backend validates the token and updates password.
Token and expiry fields are cleared.

🧾 Tech Stack

Spring Boot 3+

Spring Security

JWT (io.jsonwebtoken)

MongoDB with Spring Data

JavaMailSender

Project Lombok
