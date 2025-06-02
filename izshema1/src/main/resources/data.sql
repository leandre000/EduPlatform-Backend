-- Insert an admin user with a BCrypt hashed password (e.g., 'adminPassword123')
INSERT INTO users (id, name, email, password, role, bio, created_at)
VALUES (
           'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
           'Admin User',  -- Combined name
           'admin@example.com',
           '$2a$10$XURPShQ5u3Z4D./Ioe1Y0.J8U2e2eHpfw4wPf2V/r4wV/7bSMLXn2',
           'ADMIN',
           'Platform Administrator',
           CURRENT_TIMESTAMP
       );