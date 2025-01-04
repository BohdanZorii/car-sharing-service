INSERT INTO payments (id, status, type, rental_id, session_url, session_id, amount_to_pay)
VALUES
    ('123e4567-e89b-12d3-a456-426614174200', 'PENDING', 'PAYMENT', '123e4567-e89b-12d3-a456-426614174100', 'http://example.com', 'session-123', 100.00),
    ('123e4567-e89b-12d3-a456-426614174201', 'PAID', 'PAYMENT', '123e4567-e89b-12d3-a456-426614174101', 'http://example.com', 'session-124', 50.00);
