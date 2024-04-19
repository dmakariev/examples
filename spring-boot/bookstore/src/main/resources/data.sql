-- Populate Authors if table is empty
INSERT INTO authors (name)
SELECT * FROM (VALUES 
    ('J.K. Rowling'),
    ('George R.R. Martin'),
    ('J.R.R. Tolkien')
) AS tmp(name)
WHERE NOT EXISTS (SELECT 1 FROM authors);

-- Populate Books if table is empty
INSERT INTO books (title, author_id, isbn, price)
SELECT * FROM (VALUES 
    ('Harry Potter and the Sorcerer''s Stone', 1, '9780590353427', 15.99),
    ('A Game of Thrones', 2, '9780553381689', 20.00),
    ('The Lord of the Rings', 3, '9780544003415', 25.00)
) AS tmp(title, author_id, isbn, price)
WHERE NOT EXISTS (SELECT 1 FROM books);

-- Populate Users if table is empty
INSERT INTO users (username, password, email)
SELECT * FROM (VALUES 
    ('user1', 'password1', 'user1@example.com'),
    ('user2', 'password2', 'user2@example.com')
) AS tmp(username, password, email)
WHERE NOT EXISTS (SELECT 1 FROM users);

-- Populate Customers if table is empty
INSERT INTO customers (id, registration_date, loyalty_points)
SELECT * FROM (VALUES 
    (1, '2022-01-01'::date, 100),
    (2, '2022-01-02'::date, 50)
) AS tmp(id, registration_date, loyalty_points)
WHERE NOT EXISTS (SELECT 1 FROM customers);

-- Populate Orders if table is empty
INSERT INTO orders (customer_id, order_date, total_price)
SELECT * FROM (VALUES 
    (1, '2022-04-10'::timestamp without time zone, 35.99),
    (2, '2022-04-11'::timestamp without time zone, 45.00)
) AS tmp(customer_id, order_date, total_price)
WHERE NOT EXISTS (SELECT 1 FROM orders);

-- Populate Order Items if table is empty
INSERT INTO order_items (order_id, book_id, quantity, price)
SELECT * FROM (VALUES 
    (1, 1, 2, 31.98),
    (1, 2, 1, 20.00),
    (2, 3, 1, 25.00)
) AS tmp(order_id, book_id, quantity, price)
WHERE NOT EXISTS (SELECT 1 FROM order_items);

-- Populate Inventory if table is empty
INSERT INTO inventory (book_id, quantity)
SELECT * FROM (VALUES 
    (1, 120),  -- Assuming book_id 1 is 'Harry Potter and the Sorcerer's Stone'
    (2, 85),   -- Assuming book_id 2 is 'A Game of Thrones'
    (3, 150)   -- Assuming book_id 3 is 'The Lord of the Rings'
) AS tmp(book_id, quantity)
WHERE NOT EXISTS (SELECT 1 FROM inventory);
