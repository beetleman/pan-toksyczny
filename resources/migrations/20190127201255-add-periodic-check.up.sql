ALTER TABLE users ADD (
      check_enabled BOOLEAN DEFAULT TRUE NOT NULL,
      last_check TIMESTAMP
);
