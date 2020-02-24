INSERT INTO patients (id, full_name, ssn) VALUES ('780b28cb531c4e4fb1513529b09b8a34', 'patient1', '123456789012');

INSERT INTO physicians (id, full_name, license_no) VALUES ('0e1ddb5ff64941e382b36018f1ee8663', 'physician1', '01');

INSERT INTO laboratories (id, full_name) VALUES ('0e1ddb5ff64941e382b36018f1ee8664', 'laboratory1');

INSERT INTO prescriptions (id, patient_id, physician_id, notes, assign_date, release_date, created_at)
  VALUES ('1', '780b28cb531c4e4fb1513529b09b8a34', '0e1ddb5ff64941e382b36018f1ee8663', '50696c6c732031207461626c65742070657220646179', '2020-01-01', '2020-01-05', '2019-12-30');

INSERT INTO prescriptions (id, patient_id, physician_id, notes, assign_date, release_date, created_at)
  VALUES ('2', '780b28cb531c4e4fb1513529b09b8a34', '0e1ddb5ff64941e382b36018f1ee8663', '50696c6c732032207461626c65742070657220646179', '2020-01-01', '2020-01-07', '2019-12-31');
