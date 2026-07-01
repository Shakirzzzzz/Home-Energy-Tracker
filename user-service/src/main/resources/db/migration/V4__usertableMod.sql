ALTER TABLE `user`
ADD `username` VARCHAR(100) NOT NULL,
ADD `keycloak_id` VARCHAR(40) NOT NULL,
ADD UNIQUE KEY `uk_username` (`username`),
ADD UNIQUE KEY `uk_keycloak_id` (`keycloak_id`);