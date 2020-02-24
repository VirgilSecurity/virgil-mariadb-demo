CREATE TABLE patients (
  id char(32) NOT NULL PRIMARY KEY,
  full_name varchar(128) NOT NULL,
  ssn varbinary(2000)
);

CREATE TABLE physicians (
  id char(32) NOT NULL PRIMARY KEY,
  full_name varchar(128) NOT NULL,
  license_no varbinary(2000)
);

CREATE TABLE physician_assignments (
  patient_id char(32) NOT NULL,
  physician_id char(32) NOT NULL,
  FOREIGN KEY (patient_id)
    REFERENCES patients (id)
    ON DELETE CASCADE,
  FOREIGN KEY (physician_id)
    REFERENCES physicians (id)
    ON DELETE CASCADE
);
CREATE UNIQUE INDEX physician_assignments_index
  ON physician_assignments (patient_id, physician_id);

CREATE TABLE laboratories (
  id char(32) NOT NULL PRIMARY KEY,
  full_name varchar(128) NOT NULL
);

CREATE TABLE prescriptions (
  id char(32) NOT NULL PRIMARY KEY,
  patient_id char(32) NOT NULL,
  physician_id char(32) NOT NULL,
  notes varbinary(2000),
  assign_date timestamp,
  release_date timestamp,
  created_at timestamp NOT NULL,
  FOREIGN KEY (patient_id)
    REFERENCES patients (id)
    ON DELETE CASCADE,
  FOREIGN KEY (physician_id)
    REFERENCES physicians (id)
    ON DELETE CASCADE
);

CREATE TABLE lab_tests (
  id char(32) NOT NULL PRIMARY KEY,
  test_name varchar(128),
  patient_id char(32) NOT NULL,
  physician_id char(32) NOT NULL,
  test_date timestamp,
  results varbinary(2000),
  created_at timestamp NOT NULL,
  FOREIGN KEY (patient_id)
    REFERENCES patients (id)
    ON DELETE CASCADE,
  FOREIGN KEY (physician_id)
    REFERENCES physicians (id)
    ON DELETE CASCADE
);
