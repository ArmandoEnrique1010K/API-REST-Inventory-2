

# Las contraseñas tienen el mismo valor: 12345
INSERT INTO `db_inventory_app_backend`.`usuarios` (`dni`, `email`, `firstname`, `lastname`, `password`, `active`) VALUES 
('55726207', 'kcarillo0@hexun.com', 'Kaitlyn', 'Darker', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS', true), 
('65808829', 'dcasin1@diigo.com', 'Dodi', 'Tyas', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS', true),
('84561619', 'kguilder2@mysql.com', 'Kevyn', 'Gorce', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS', true),
('72707073', 'zolifard3@symantec.com', 'Zacharie', 'Cranton', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS', true),
('46849809', 'mcorkitt4@google.cn', 'Merle', 'Methringham', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS', true),
('74133641', 'arennels0@google.de', 'Adena', 'Vater', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS', true),
('42825647', 'laubrun1@smugmug.com', 'Levin', 'Noddles', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS', true),
('89835099', 'kbinion2@tiny.cc', 'Korie', 'Giovanni', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS', true),
('74586241', 'ggillow3@auda.org.au', 'Grazia', 'Allabarton', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS', true),
('78652879', 'ijako4@sfgate.com', 'Isidore', 'Filchakov', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS', true);

INSERT INTO `db_inventory_app_backend`.`usuarios_roles` (`user_id`, `role_id`) VALUES 
('2', '1'),
('2', '2'),
('3', '1'),
('3', '3'),
('4', '1'),
('4', '4'),
('5', '1'),
('6', '1'),
('6', '2'),
('6', '3'),
('7', '1'),
('8', '1'),
('8', '4'),
('9', '1'),
('9', '2'),
('9', '4'),
('10', '1'),
('10', '3'),
('10', '4'),
('11', '1'),
('11', '2'),
('11', '3'),
('11', '4');

INSERT INTO `db_inventory_app_backend`.`categorias` (`name`, `status`) VALUES 
('Fitness', true),
('Travel', true),
('Photography',true),
('Food - Breakfast',true),
('Outdoor',true),
('Clothing - Shirts', true),
('Food - Vegetables', true);




-- INSERT INTO `db_inventory_app_backend`.`productos` (`caducity_date`, `created_at`, `entry_date`, `image_url`, `length`, `name`, `status`, `total_quantity_available`, `updated_at`, `width`, `category_id`, `total_quantity_received`, `total_quantity_delivered`) VALUES 
-- ('2025-09-05','2025-04-25','2025-06-18','http://dummyimage.com/158x100.png/cc0000/ffffff',8.95,'Gaming Headset',true,1000,'2025-05-20',6.69,8,1000, 0),
-- ('2025-08-30','2025-06-24','2025-06-13','http://dummyimage.com/185x100.png/dddddd/000000',6.87,'Fashionable Fanny Pack',true,1000,'2025-02-11',27.27,4,1000, 0),
-- ('2025-07-26','2025-08-04','2025-09-21','http://dummyimage.com/222x100.png/5fa2dd/ffffff',22.82,'Blueberry Chia Jam',true,1000,'2025-11-02',2.67,1,1000, 0),
-- ('2025-12-22','2025-09-26','2025-07-25','http://dummyimage.com/179x100.png/ff4444/ffffff',13.96,'Blackberry Compote',true,1000,'2025-05-24',21.6,6,1000, 0),
-- ('2025-04-29','2025-01-09','2025-01-16','http://dummyimage.com/128x100.png/cc0000/ffffff',23.7,'Basic V-Neck T-Shirt',true,1000,'2025-11-14',13.68,4,1000, 0),
-- ('2025-03-11','2025-08-25','2025-07-03','http://dummyimage.com/239x100.png/dddddd/000000',10.46,'Balsamic Fig Dressing',true,1000,'2025-05-29',6.82,7,1000, 0),
-- ('2025-02-13','2025-03-03','2025-02-18','http://dummyimage.com/139x100.png/5fa2dd/ffffff',4.45,'Instant Read Meat Thermometer',true,1000,'2025-09-14',16.72,5,1000, 0),
-- ('2025-09-19','2025-02-21','2025-11-18','http://dummyimage.com/195x100.png/dddddd/000000',11.78,'Ground Turkey',true,1000,'2025-05-09',26.68,1,1000, 0),
-- ('2025-05-11','2025-02-08','2025-05-19','http://dummyimage.com/201x100.png/dddddd/000000',18.95,'Couscous Mix',true,1000,'2025-01-23',19.22,7,1000, 0),
-- ('2025-05-30','2025-06-22','2025-04-15','http://dummyimage.com/121x100.png/5fa2dd/ffffff',10.9,'Smart Fitness Scale',true,1000,'2025-06-17',29.81,5,1000, 0),
-- ('2025-10-02','2025-01-04','2025-12-08','http://dummyimage.com/200x100.png/ff4444/ffffff',16.35,'Pasta Maker Machine',true,1000,'2025-05-06',9.16,4,1000, 0),
-- ('2025-06-12','2025-09-29','2025-01-26','http://dummyimage.com/215x100.png/cc0000/ffffff',2.66,'Mediterranean Chickpea Bowl',true,1000,'2025-11-05',13.31,6,1000, 0),
-- ('2025-07-17','2025-12-03','2025-07-12','http://dummyimage.com/110x100.png/5fa2dd/ffffff',29.24,'Water Bottle with Built-in Fruit Infuser',true,1000,'2025-05-15',17.74,6,1000, 0),
-- ('2025-08-31','2025-08-29','2025-07-22','http://dummyimage.com/132x100.png/cc0000/ffffff',12.5,'Smart Plug',true,1000,'2025-06-10',5.84,4,1000, 0),
-- ('2025-10-14','2025-12-13','2025-11-01','http://dummyimage.com/157x100.png/cc0000/ffffff',25.95,"Jennifer's Amazing Lip Balm Kit",true,1000,'2025-11-09',26.76,2,1000, 0),
-- ('2025-07-08','2025-11-07','2025-12-25','http://dummyimage.com/206x100.png/5fa2dd/ffffff',25.65,'Comfy Slippers',true,1000,'2025-07-01',27.89,8,1000, 0),
-- ('2025-12-21','2025-07-25','2025-03-15','http://dummyimage.com/140x100.png/cc0000/ffffff',19.45,'Surimi Crab Sticks',false,1000,'2025-08-10',15.34,5,1000, 0),
-- ('2025-01-03','2025-05-05','2025-05-16','http://dummyimage.com/112x100.png/ff4444/ffffff',15.11,'Chocolate Dipped Fruit',true,1000,'2025-12-22',11.38,1,1000, 0),
-- ('2025-05-25','2025-04-27','2025-11-09','http://dummyimage.com/217x100.png/ff4444/ffffff',10.55,"Kids' Science Experiment Kit",true,1000,'2025-04-16',24.74,8,1000, 0),
-- ('2025-02-01','2025-05-21','2025-08-04','http://dummyimage.com/230x100.png/dddddd/000000',15.17,'Sriracha Hot Chili Sauce',true,1000,'2025-12-01',11.48,8,1000, 0);
INSERT INTO `db_inventory_app_backend`.`productos` (`name`, `length`, `width`, `height`, `status`, `quantityModels`, `category_id`, `type_id`) VALUES 
('Rechargeable Electric Toothbrush',27.46,82.69,2.56,true,3,2,2),
('Whole Grain Mustard',34.06,84.99,42.35,false,4,4,3),
('Herb Drying Rack',73.76,88.59,69.59,false,2,3,2),
('Water-Resistant Bluetooth Speaker',99.38,22.31,18.91,false,4,2,2),
('A-Line Skirt',26.9,42.52,41.64,true,2,1,1),
('Chic Ankle Strap Heels',42.8,88.25,67.56,false,2,1,4),
('Biodegradable Dog Waste Bags',40.92,49.64,30.67,false,4,1,1),
('Peanut Butter Granola',60.57,76.27,27.62,true,1,2,2),
('Non-Stick Crepe Pan',86.2,83.02,77.9,false,2,1,4),
('LED Strip Light Kit',7.52,88.01,37.32,true,2,1,1),
('Air Purifier',65.82,48.71,40.97,false,1,1,4),
('Wine Decanter',86.8,81.08,4.47,true,3,2,2),
('Fridge Magnet Set',99.31,84.43,42.4,false,4,1,1),
('Classic Pesto Sauce',4.18,21.72,50.26,true,1,1,1),
('Electric Griddle with Lid',17.71,22.66,64.92,true,3,1,3),
('Portable Water Filter',43.24,58.97,97.48,true,3,2,4),
('Maple Breakfast Sausage',36.38,9.69,62.25,false,3,3,4),
('Cat Scratching Post with Toys',56.05,15.61,27.23,true,1,3,2),
('Interactive Plush Toy',96.85,92.61,55.3,false,1,2,1),
('Fruit & Nut Trail Mix',51.27,60.62,12.69,true,1,1,2),
('Foam Building Blocks for Kids',13.26,17.88,95.25,false,4,4,3),
('Spicy Vegetable Sushi Rolls',96.38,88.81,16.26,false,1,4,1),
('Cilantro Lime Rice',3.64,75.93,96.66,true,1,4,1),
('Peanut Butter Protein Balls',57.2,92.38,72.58,false,2,1,4),
('Casual Sneakers',9.36,14.12,61.8,false,3,4,4),
('Mini Electric Kettle',80.41,72.38,34.83,false,1,4,3),
('Suction Cup Hooks',13.69,44.68,42.25,false,4,4,3),
('Cookbook',74.61,61.59,4.48,false,2,3,3),
('Pet Safety Belt for Car',68.98,67.64,51.25,true,4,4,2),
('Glass Food Containers',17.44,59.23,35.38,false,4,3,3)


('2025-09-05','2025-04-25','2025-06-18','http://dummyimage.com/158x100.png/cc0000/ffffff',8.95,'Gaming Headset',true,1000,'2025-05-20',6.69,8,1000, 0),
('2025-08-30','2025-06-24','2025-06-13','http://dummyimage.com/185x100.png/dddddd/000000',6.87,'Fashionable Fanny Pack',true,1000,'2025-02-11',27.27,4,1000, 0),
('2025-07-26','2025-08-04','2025-09-21','http://dummyimage.com/222x100.png/5fa2dd/ffffff',22.82,'Blueberry Chia Jam',true,1000,'2025-11-02',2.67,1,1000, 0),
('2025-12-22','2025-09-26','2025-07-25','http://dummyimage.com/179x100.png/ff4444/ffffff',13.96,'Blackberry Compote',true,1000,'2025-05-24',21.6,6,1000, 0),
('2025-04-29','2025-01-09','2025-01-16','http://dummyimage.com/128x100.png/cc0000/ffffff',23.7,'Basic V-Neck T-Shirt',true,1000,'2025-11-14',13.68,4,1000, 0),
('2025-03-11','2025-08-25','2025-07-03','http://dummyimage.com/239x100.png/dddddd/000000',10.46,'Balsamic Fig Dressing',true,1000,'2025-05-29',6.82,7,1000, 0),
('2025-02-13','2025-03-03','2025-02-18','http://dummyimage.com/139x100.png/5fa2dd/ffffff',4.45,'Instant Read Meat Thermometer',true,1000,'2025-09-14',16.72,5,1000, 0),
('2025-09-19','2025-02-21','2025-11-18','http://dummyimage.com/195x100.png/dddddd/000000',11.78,'Ground Turkey',true,1000,'2025-05-09',26.68,1,1000, 0),
('2025-05-11','2025-02-08','2025-05-19','http://dummyimage.com/201x100.png/dddddd/000000',18.95,'Couscous Mix',true,1000,'2025-01-23',19.22,7,1000, 0),
('2025-05-30','2025-06-22','2025-04-15','http://dummyimage.com/121x100.png/5fa2dd/ffffff',10.9,'Smart Fitness Scale',true,1000,'2025-06-17',29.81,5,1000, 0),
('2025-10-02','2025-01-04','2025-12-08','http://dummyimage.com/200x100.png/ff4444/ffffff',16.35,'Pasta Maker Machine',true,1000,'2025-05-06',9.16,4,1000, 0),
('2025-06-12','2025-09-29','2025-01-26','http://dummyimage.com/215x100.png/cc0000/ffffff',2.66,'Mediterranean Chickpea Bowl',true,1000,'2025-11-05',13.31,6,1000, 0),
('2025-07-17','2025-12-03','2025-07-12','http://dummyimage.com/110x100.png/5fa2dd/ffffff',29.24,'Water Bottle with Built-in Fruit Infuser',true,1000,'2025-05-15',17.74,6,1000, 0),
('2025-08-31','2025-08-29','2025-07-22','http://dummyimage.com/132x100.png/cc0000/ffffff',12.5,'Smart Plug',true,1000,'2025-06-10',5.84,4,1000, 0),
('2025-10-14','2025-12-13','2025-11-01','http://dummyimage.com/157x100.png/cc0000/ffffff',25.95,"Jennifer's Amazing Lip Balm Kit",true,1000,'2025-11-09',26.76,2,1000, 0),
('2025-07-08','2025-11-07','2025-12-25','http://dummyimage.com/206x100.png/5fa2dd/ffffff',25.65,'Comfy Slippers',true,1000,'2025-07-01',27.89,8,1000, 0),
('2025-12-21','2025-07-25','2025-03-15','http://dummyimage.com/140x100.png/cc0000/ffffff',19.45,'Surimi Crab Sticks',false,1000,'2025-08-10',15.34,5,1000, 0),
('2025-01-03','2025-05-05','2025-05-16','http://dummyimage.com/112x100.png/ff4444/ffffff',15.11,'Chocolate Dipped Fruit',true,1000,'2025-12-22',11.38,1,1000, 0),
('2025-05-25','2025-04-27','2025-11-09','http://dummyimage.com/217x100.png/ff4444/ffffff',10.55,"Kids' Science Experiment Kit",true,1000,'2025-04-16',24.74,8,1000, 0),
('2025-02-01','2025-05-21','2025-08-04','http://dummyimage.com/230x100.png/dddddd/000000',15.17,'Sriracha Hot Chili Sauce',true,1000,'2025-12-01',11.48,8,1000, 0);



INSERT INTO `db_inventory_app_backend`.`regiones` (`name`) VALUES 
('First region'), ('Second region'), ('Third region'), ('Quarter region');


INSERT INTO `db_inventory_app_backend`.`ubicaciones` (`name`, `status`, `region_id`) VALUES 
('Kosai-shi',true,4),
('Mentaras',true,2),
('Somanda',true,3),
('Đồng Mỏ',true,4),
('Dacun',true,2),
("Debark'",true,3),
('Atengmelang',true,4),
('Insrom',true,1),
('Boyu',true,1),
('Paraiso',true,2),
('Liliba',true,3),
('Tlatah',true,2),
('Sumberpitu',true,1),
('Alvide',true,4),
('Luruaco',true,1),
('Seredyna-Buda',true,2),
('Dakingari',true,1),
('Laoliangcang',true,2),
('Xiaogang',true,3),
("Jian’ou",true,4);


INSERT INTO `db_inventory_app_backend`.`empresas` (`name`) VALUES 
('First company'), ('Second company'), ('Third company'), ('Quarter company');



