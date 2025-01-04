INSERT INTO users (id, email, first_name, last_name, password, role)
VALUES
    ('123e4567-e89b-12d3-a456-426614174000', 'customer1@example.com', 'John', 'Doe', '$2a$10$udca5nGoMauJw80y7OgVf.pZL4PqlKWb/7ztH1SFFWqp3DlLzF4w.', 'CUSTOMER'),
    ('123e4567-e89b-12d3-a456-426614174001', 'customer2@example.com', 'Jane', 'Smith', '$2a$10$udca5nGoMauJw80y7OgVf.pZL4PqlKWb/7ztH1SFFWqp3DlLzF4w.', 'CUSTOMER'),
    ('123e4567-e89b-12d3-a456-426614174002', 'customer3@example.com', 'Bob', 'Brown', '$2a$10$udca5nGoMauJw80y7OgVf.pZL4PqlKWb/7ztH1SFFWqp3DlLzF4w.', 'CUSTOMER'),
    ('123e4567-e89b-12d3-a456-426614174003', 'customer4@example.com', 'Alice', 'Johnson', '$2a$10$udca5nGoMauJw80y7OgVf.pZL4PqlKWb/7ztH1SFFWqp3DlLzF4w.', 'CUSTOMER'),
    ('123e4567-e89b-12d3-a456-426614174004', 'customer5@example.com', 'Charlie', 'Williams', '$2a$10$udca5nGoMauJw80y7OgVf.pZL4PqlKWb/7ztH1SFFWqp3DlLzF4w.', 'CUSTOMER');


INSERT INTO cars (id, model, brand, type, inventory, daily_fee)
VALUES
    ('b3a29d93-8e21-4d53-92f3-d7a7d3d4a13d', 'Camry', 'Toyota', 'SEDAN', 10, 50.00),
    ('b3a29d93-8e21-4d53-92f3-d7a7d3d4a13e', 'Civic', 'Honda', 'SEDAN', 8, 45.00),
    ('b3a29d93-8e21-4d53-92f3-d7a7d3d4a13f', 'Focus', 'Ford', 'HATCHBACK', 12, 40.00),
    ('b3a29d93-8e21-4d53-92f3-d7a7d3d4a140', 'Model X', 'Tesla', 'SUV', 5, 120.00),
    ('b3a29d93-8e21-4d53-92f3-d7a7d3d4a141', 'Outback', 'Subaru', 'UNIVERSAL', 6, 70.00);

INSERT INTO rentals (id, car_id, user_id, rental_date, return_date, actual_return_date)
VALUES
    ('123e4567-e89b-12d3-a456-426614174100', 'b3a29d93-8e21-4d53-92f3-d7a7d3d4a13d', '123e4567-e89b-12d3-a456-426614174000', '2025-01-05', '2025-01-10', NULL),
    ('123e4567-e89b-12d3-a456-426614174101', 'b3a29d93-8e21-4d53-92f3-d7a7d3d4a13e', '123e4567-e89b-12d3-a456-426614174001', '2025-01-06', '2025-01-12', NULL),
    ('123e4567-e89b-12d3-a456-426614174102', 'b3a29d93-8e21-4d53-92f3-d7a7d3d4a13f', '123e4567-e89b-12d3-a456-426614174002', '2025-01-07', '2025-01-14', '2025-01-18'),
    ('123e4567-e89b-12d3-a456-426614174103', 'b3a29d93-8e21-4d53-92f3-d7a7d3d4a140', '123e4567-e89b-12d3-a456-426614174003', '2025-01-08', '2025-01-13', NULL),
    ('123e4567-e89b-12d3-a456-426614174104', 'b3a29d93-8e21-4d53-92f3-d7a7d3d4a141', '123e4567-e89b-12d3-a456-426614174002', '2025-01-09', '2025-01-15', '2025-01-14');
