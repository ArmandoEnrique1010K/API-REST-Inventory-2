

# Las contraseñas tienen el mismo valor: 12345
INSERT INTO `db_inventory_app_backend`.`users` (`dni`, `email`, `firstname`, `lastname`, `password`) VALUES 
('55726207', 'kcarillo0@hexun.com', 'Kaitlyn', 'Darker', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS'), 
('65808829', 'dcasin1@diigo.com', 'Dodi', 'Tyas', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS'),
('84561619', 'kguilder2@mysql.com', 'Kevyn', 'Gorce', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS'),
('72707073', 'zolifard3@symantec.com', 'Zacharie', 'Cranton', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS'),
('46849809', 'mcorkitt4@google.cn', 'Merle', 'Methringham', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS'),
('74133641', 'arennels0@google.de', 'Adena', 'Vater', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS'),
('42825647', 'laubrun1@smugmug.com', 'Levin', 'Noddles', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS'),
('89835099', 'kbinion2@tiny.cc', 'Korie', 'Giovanni', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS'),
('74586241', 'ggillow3@auda.org.au', 'Grazia', 'Allabarton', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS'),
('78652879', 'ijako4@sfgate.com', 'Isidore', 'Filchakov', '$2a$12$xTcQQboh9TC8yNiGo3WWXOTCwBJgaZzjJMAj7iiQwJneO09YI5lWS');

INSERT INTO `db_inventory_app_backend`.`users_roles` (`user_id`, `role_id`) VALUES 
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

INSERT INTO `db_inventory_app_backend`.`products` (`caducity_date`, `created_at`, `entry_date`, `image_url`, `length`, `name`, `status`, `stock`, `updated_at`, `width`, `category_id`) VALUES 
('2025-09-05','2025-04-25','2025-06-18','http://dummyimage.com/158x100.png/cc0000/ffffff',8.95,'Gaming Headset',true,980,'2025-05-20',6.69,8),
('2025-08-30','2025-06-24','2025-06-13','http://dummyimage.com/185x100.png/dddddd/000000',6.87,'Fashionable Fanny Pack',true,2730,'2025-02-11',27.27,4),
('2025-07-26','2025-08-04','2025-09-21','http://dummyimage.com/222x100.png/5fa2dd/ffffff',22.82,'Blueberry Chia Jam',true,7103,'2025-11-02',2.67,1),
('2025-12-22','2025-09-26','2025-07-25','http://dummyimage.com/179x100.png/ff4444/ffffff',13.96,'Blackberry Compote',true,4139,'2025-05-24',21.6,6),
('2025-04-29','2025-01-09','2025-01-16','http://dummyimage.com/128x100.png/cc0000/ffffff',23.7,'Basic V-Neck T-Shirt',true,5447,'2025-11-14',13.68,4),
('2025-03-11','2025-08-25','2025-07-03','http://dummyimage.com/239x100.png/dddddd/000000',10.46,'Balsamic Fig Dressing',true,381,'2025-05-29',6.82,7),
('2025-02-13','2025-03-03','2025-02-18','http://dummyimage.com/139x100.png/5fa2dd/ffffff',4.45,'Instant Read Meat Thermometer',true,9395,'2025-09-14',16.72,5),
('2025-09-19','2025-02-21','2025-11-18','http://dummyimage.com/195x100.png/dddddd/000000',11.78,'Ground Turkey',true,5130,'2025-05-09',26.68,1),
('2025-05-11','2025-02-08','2025-05-19','http://dummyimage.com/201x100.png/dddddd/000000',18.95,'Couscous Mix',true,7553,'2025-01-23',19.22,7),
('2025-05-30','2025-06-22','2025-04-15','http://dummyimage.com/121x100.png/5fa2dd/ffffff',10.9,'Smart Fitness Scale',true,7327,'2025-06-17',29.81,5),
('2025-10-02','2025-01-04','2025-12-08','http://dummyimage.com/200x100.png/ff4444/ffffff',16.35,'Pasta Maker Machine',true,1486,'2025-05-06',9.16,4),
('2025-06-12','2025-09-29','2025-01-26','http://dummyimage.com/215x100.png/cc0000/ffffff',2.66,'Mediterranean Chickpea Bowl',true,2190,'2025-11-05',13.31,6),
('2025-07-17','2025-12-03','2025-07-12','http://dummyimage.com/110x100.png/5fa2dd/ffffff',29.24,'Water Bottle with Built-in Fruit Infuser',true,5396,'2025-05-15',17.74,6),
('2025-08-31','2025-08-29','2025-07-22','http://dummyimage.com/132x100.png/cc0000/ffffff',12.5,'Smart Plug',true,9106,'2025-06-10',5.84,4),
('2025-10-14','2025-12-13','2025-11-01','http://dummyimage.com/157x100.png/cc0000/ffffff',25.95,"Jennifer's Amazing Lip Balm Kit",true,4358,'2025-11-09',26.76,2),
('2025-07-08','2025-11-07','2025-12-25','http://dummyimage.com/206x100.png/5fa2dd/ffffff',25.65,'Comfy Slippers',true,2678,'2025-07-01',27.89,8),
('2025-12-21','2025-07-25','2025-03-15','http://dummyimage.com/140x100.png/cc0000/ffffff',19.45,'Surimi Crab Sticks',false,3459,'2025-08-10',15.34,5),
('2025-01-03','2025-05-05','2025-05-16','http://dummyimage.com/112x100.png/ff4444/ffffff',15.11,'Chocolate Dipped Fruit',true,2904,'2025-12-22',11.38,1),
('2025-05-25','2025-04-27','2025-11-09','http://dummyimage.com/217x100.png/ff4444/ffffff',10.55,"Kids' Science Experiment Kit",true,1331,'2025-04-16',24.74,8),
('2025-02-01','2025-05-21','2025-08-04','http://dummyimage.com/230x100.png/dddddd/000000',15.17,'Sriracha Hot Chili Sauce',true,9571,'2025-12-01',11.48,8);


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



