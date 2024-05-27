-- Insert into accommodation table
INSERT INTO accommodation (owner_id, name, location, min_guests, max_guests, price_per_day, automatic_reservation) VALUES
                                                                                                                           ( 101, 'Seaside Villa', 'Beach City', 2, 5, 100.0, TRUE),
                                                                                                                           ( 102, 'Mountain Retreat', 'Highland Valley', 1, 3, 75.0, FALSE),
                                                                                                                           ( 103, 'Urban Apartment', 'City Center', 1, 2, 90.0, TRUE);

-- Insert into availability_period table
INSERT INTO availability_period ( start_date, end_date, accommodation_id) VALUES
                                                                                 ( '2024-06-01', '2024-06-30', 1),
                                                                                 ( '2024-07-01', '2024-07-31', 1),
                                                                                 ( '2024-06-15', '2024-07-15', 2),
                                                                                 ( '2024-08-01', '2024-08-31', 3);

-- Insert into special_price table
INSERT INTO special_price ( start_date, end_date, price, accommodation_id) VALUES
                                                                                  ( '2024-07-01', '2024-07-31', 150.0, 1),
                                                                                  ( '2024-06-15', '2024-06-30', 80.0, 2),
                                                                                  ( '2024-08-01', '2024-08-15', 120.0, 3);
