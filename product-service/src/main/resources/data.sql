-- Create the 'category' table
CREATE TABLE category (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL
);

-- Create the 'product' table
CREATE TABLE product (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price VARCHAR(255) NOT NULL,
    stock VARCHAR(255) NOT NULL,
    category_id UUID NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(id)
);


-- Insert sample categories
INSERT INTO category (name) VALUES
('Smartphones'),
('Tablets'),
('Laptops'),
('Accessories'),
('Wearables');


-- Insert 100 products with references to the categories
INSERT INTO product (name, description, price, stock, category_id) VALUES
('iPhone-15', 'A mobile with stunning photography lenses', '7466', '12', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('iPhone-16', 'Next generation mobile with improved performance', '7999', '15', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('iPhone-15 Plus', 'A larger version with better battery life', '8499', '10', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Samsung Galaxy S23', 'Samsung’s flagship phone with a powerful camera', '8499', '18', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Samsung Galaxy Z Flip 5', 'Compact foldable with unique design', '12999', '7', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Google Pixel 8', 'Pure Android experience with excellent camera', '7999', '14', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('iPhone 14 Pro Max', 'Top-tier iPhone with the best features', '12499', '5', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Xiaomi Redmi Note 12', 'Budget-friendly phone with decent specs', '2999', '40', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Motorola Edge+ 2023', 'Premium phone with unique features', '9299', '9', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('OnePlus 11', 'Performance powerhouse with great design', '7499', '16', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('iPhone SE 3', 'Compact and budget-friendly with fast chip', '3999', '25', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Realme GT 2 Pro', 'Performance beast at a lower price', '6999', '15', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Oppo Reno 9 Pro', 'Mid-range phone with impressive specs', '5499', '19', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Vivo X90 Pro', 'Pro-grade camera system with flagship performance', '9999', '9', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Sony Xperia 5 IV', 'Compact version with pro-grade camera features', '8999', '10', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Samsung Galaxy Z Fold 5', 'Foldable phone with amazing multitasking features', '17999', '5', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Google Pixel 7a', 'Affordable Pixel with flagship features', '4999', '30', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Asus ROG Phone 7', 'Gaming phone with top-tier specs', '13999', '4', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('OnePlus Nord 3', 'Mid-range OnePlus phone with great value', '6499', '22', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Xiaomi Mi 13 Pro', 'Great camera phone with fast charging', '6999', '12', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('iPhone 13 Pro', 'Pro-level features with powerful performance', '8499', '9', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Oppo Find X6 Pro', 'Flagship with innovative design and features', '7999', '8', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Realme 11 Pro', 'Affordable 5G phone with great cameras', '4499', '28', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Motorola Moto G73', 'Mid-range phone with smooth performance', '3699', '30', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Huawei Mate 50', 'Another excellent camera-centric smartphone', '9499', '6', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('Nothing Phone (2)', 'Unique design with solid performance', '7999', '14', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
('iPhone XR', 'Mid-range iPhone with excellent performance', '6499', '17', (SELECT id FROM category WHERE name = 'Smartphones' LIMIT 1)),
