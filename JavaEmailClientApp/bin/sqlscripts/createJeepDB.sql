-- Creating JEEP Database
--DROP database jeep;
--CREATE database jeep;
--USE jeep;

-- Dropping all Tables.
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS folder;
DROP TABLE IF EXISTS contact;

-- Creating Contact Table
CREATE TABLE contact(
	contact_id INT unsigned PRIMARY KEY auto_increment,
	first_name TEXT(50),
	last_name TEXT(50),
	email TEXT(50) NOT NULL,
	phone_number TEXT(20),
	address TEXT(50),
	comments TEXT(500)
);

-- Creating Folder Table
CREATE TABLE folder(
	folder_id INT unsigned PRIMARY KEY auto_increment,
	name VARCHAR(20) NOT NULL UNIQUE
);

-- Creating Message Table
CREATE TABLE message(
	message_id INT unsigned PRIMARY KEY auto_increment,
	sender_email TEXT(50) NOT NULL,
	receiver_email TEXT(50) NOT NULL,
	bcc TEXT(500),
	cc TEXT(500),
	subject TEXT(100),
	message TEXT(65535),
	message_date DATETIME NOT NULL,
	folder_id INT unsigned NOT NULL,
	CONSTRAINT fk_message_folder_id FOREIGN KEY (folder_id) REFERENCES folder(folder_id)
);

INSERT INTO folder(folder_id, name) VALUES (NULL, 'Inbox');
INSERT INTO folder(folder_id, name) VALUES (NULL, 'Sent');
INSERT INTO folder(folder_id, name) VALUES (NULL, 'ToSend');