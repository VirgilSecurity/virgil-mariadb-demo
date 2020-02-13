CREATE TABLE virgil_users (
  user_id CHAR(36) NOT NULL PRIMARY KEY,
  record_version INTEGER NOT NULL,
  protobuf VARBINARY(2048) NOT NULL
);
CREATE INDEX virgil_users_record_version_index
  ON virgil_users (record_version);
CREATE UNIQUE INDEX virgil_users_user_id_record_version_index
  ON virgil_users (user_id, record_version);

CREATE TABLE virgil_keys (
  user_id CHAR(36) NOT NULL,
  data_id VARCHAR(128) NOT NULL,
  protobuf VARBINARY(32768) NOT NULL,
  PRIMARY KEY(user_id, data_id),
  FOREIGN KEY (user_id)
    REFERENCES virgil_users(user_id)
    ON DELETE CASCADE
);
                        
CREATE TABLE virgil_roles (
  role_name VARCHAR(64) NOT NULL PRIMARY KEY,
  protobuf VARBINARY(196) NOT NULL
);
                        
CREATE TABLE virgil_role_assignments (
  role_name VARCHAR(64) NOT NULL,
  user_id CHAR(36) NOT NULL,
  protobuf VARBINARY(1024) NOT NULL,
  PRIMARY KEY(role_name, user_id),
  FOREIGN KEY (role_name)
    REFERENCES virgil_roles(role_name)
    ON DELETE CASCADE,
  FOREIGN KEY (user_id)
    REFERENCES virgil_users(user_id)
    ON DELETE CASCADE,
);
CREATE INDEX virgil_role_assignments_user_id_index
  ON virgil_role_assignments (user_id);
                        
CREATE TABLE virgil_grant_keys (
  record_version INTEGER NOT NULL,
  user_id CHAR(36) NOT NULL,
  key_id BINARY(64) NOT NULL,
  expiration_date TIMESTAMP NOT NULL,      
  protobuf VARBINARY(1024) NOT NULL,
  PRIMARY KEY(user_id, key_id),
  FOREIGN KEY (user_id)
    REFERENCES virgil_users(user_id)
    ON DELETE CASCADE
);
CREATE INDEX virgil_grant_keys_record_version_index
  ON virgil_grant_keys (record_version);
CREATE INDEX virgil_grant_keys_expiration_date_index
  ON virgil_grant_keys (expiration_date)
