-- Insert an admin user with a BCrypt hashed password (e.g., 'adminPassword123')
INSERT INTO users (id, first_name, last_name, email, password, role, bio)
VALUES ('admin-uuid-123', 'Admin', 'User', 'admin@gmail.com', '$2a$10$XURPShQ5u3Z4D./Ioe1Y0.J8U2e2eHpfw4wPf2V/r4wV/7bSMLXn2', 'ADMIN', 'Platform Administrator');