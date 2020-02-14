INSERT INTO patients (id, full_name, ssn) VALUES ('780b28cb531c4e4fb1513529b09b8a34', 'patient1', '123456789012');

INSERT INTO physicians (id, full_name, license_no) VALUES ('0e1ddb5ff64941e382b36018f1ee8663', 'physician1', '01');

INSERT INTO prescriptions (id, patient_id, physician_id, notes, assign_date, release_date)
  VALUES ('1', '780b28cb531c4e4fb1513529b09b8a34', '0e1ddb5ff64941e382b36018f1ee8663', 'Pills 1 tablet per day', '2020-01-01', '2020-01-05');

INSERT INTO prescriptions (id, patient_id, physician_id, notes, assign_date, release_date)
  VALUES ('2', '780b28cb531c4e4fb1513529b09b8a34', '0e1ddb5ff64941e382b36018f1ee8663', 'Pills 2 tablet per day', '2020-01-01', '2020-01-07');
