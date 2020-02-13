CREATE TABLE patients (
  id char(32) NOT NULL,
  full_name varchar(128) NOT NULL,
  ssn varbinary(2000) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE physicians (
  id char(32) NOT NULL,
  full_name varchar(128) NOT NULL,
  license_no varbinary(2000) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE physician_assignments (
  patient_id char(32) NOT NULL,
  physician_id char(32) NOT NULL
);

CREATE TABLE prescriptions (
  id char(32) NOT NULL,
  patient_id char(32) NOT NULL,
  physician_id char(32) NOT NULL,
  notes varchar(1000),
  assign_date timestamp,
  release_date timestamp,
  PRIMARY KEY (id)
)

CREATE TABLE lab_tests (
  id char(32) NOT NULL,
  test_name varchar(128),
  patient_id char(32) NOT NULL,
  physician_id char(32) NOT NULL,
  test_date timestamp,
  results varbinary(2000),
  PRIMARY KEY (id)
);
