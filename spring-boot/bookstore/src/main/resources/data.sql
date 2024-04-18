-- Populate Authors
INSERT INTO authors (name) VALUES ('J.K. Rowling'), ('George R.R. Martin'), ('J.R.R. Tolkien');

-- Populate Books
INSERT INTO books (title, author_id, isbn, price) VALUES 
('Harry Potter and the Sorcerer''s Stone', 1, '9780590353427', 15.99),
('A Game of Thrones', 2, '9780553381689', 20.00),
('The Lord of the Rings', 3, '9780544003415', 25.00);

-- Populate Users
INSERT INTO users (username, password, email) VALUES 
('user1', 'password1', 'user1@example.com'),
('user2', 'password2', 'user2@example.com');

-- Populate Customers
INSERT INTO customers (id, registration_date, loyalty_points) VALUES 
(1, '2022-01-01', 100),
(2, '2022-01-02', 50);

-- Populate Orders
INSERT INTO orders (customer_id, order_date, total_price) VALUES 
(1, '2022-04-10', 35.99),
(2, '2022-04-11', 45.00);

-- Populate Order Items
INSERT INTO order_items (order_id, book_id, quantity, price) VALUES 
(1, 1, 2, 31.98),
(1, 2, 1, 20.00),
(2, 3, 1, 25.00);
