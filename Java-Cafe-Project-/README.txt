Hello sir, i hope this text finds you well. In order for our OOVP project to work, you must have a similar database to ours. That being said the following steps will ensure that the code runs as expected

1. Run the following query inside MYSQL
	create database cafe;
2. Create the tables inside the 'cafe' database
	CREATE TABLE menu (
		product_id int auto_increment ,
    		product_name varchar(255),
  		product_desc varchar(255),
  		product_price int,
  		product_stock int,
  		constraint pk_menu primary key (product_id)
		);
	CREATE TABLE customer (
    		customer_id int(11) AUTO_INCREMENT,
		customer_name varchar(50),
		PRIMARY KEY (`customer_id`)
  		);
	CREATE TABLE orders (
		order_id int(11) AUTO_INCREMENT,
		order_type varchar(50),
		customer_seat varchar(20),
		order_total int(11),
		PRIMARY KEY (`order_id`)
  		);
3. Insert the required data for menu, For the menu table there shouldn't be any entry before the following entry due to the nature of auto_increment
	INSERT INTO menu (product_name, product_desc, product_price, product_stock) VALUES
		('Americano', 'A classic espresso-based drink made with hot water and a shot of espresso.', 18000, 10),
		('Vanilla Latte', 'A creamy and aromatic latte infused with vanilla flavor.', 24000, 10),
		('Cappuccino', 'A delicious espresso-based drink topped with steamed milk foam.', 22000, 10),
		('Black Tea', 'A robust and flavorful black tea served hot or iced.', 20000, 10),
		('Matcha', 'A traditional Japanese green tea powder blended with hot water or milk.', 24000, 10),
		('Jasmine Tea', 'A fragrant and refreshing tea made from jasmine-scented green tea leaves.', 20000, 10),
		('Strawberry Cake', 'A decadent cake layered with fresh strawberries and whipped cream.', 20000, 10),
		('Macaron', 'A delightful French pastry consisting of two almond meringue cookies sandwiching a creamy filling.', 25000, 10),
		('Brownies', 'Rich and fudgy chocolate brownies made with premium cocoa and walnuts.', 15000, 10);
4. The menu program will now run smoothly