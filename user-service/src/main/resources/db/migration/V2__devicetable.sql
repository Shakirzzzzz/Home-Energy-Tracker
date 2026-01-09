CREATE TABLE `device` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(255),
  `type` VARCHAR(50),
  `location` VARCHAR(255),
  `user_id` BIGINT,
    KEY `idx_device_user_id` (`user_id`),
  CONSTRAINT `fk_device_user`
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
    ON DELETE CASCADE
);