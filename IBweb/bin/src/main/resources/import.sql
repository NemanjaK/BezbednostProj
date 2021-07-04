

INSERT INTO authority(name)VALUES('ROLE_ADMIN');
INSERT INTO authority(name)VALUES('ROLE_REGULAR');

INSERT INTO users(email,password,certificate,active)VALUES('nemanjak@example.com','$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra',NULL,true);
INSERT INTO users(email,password,certificate,active)VALUES('nikola@example.com','$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra',NULL,true);

INSERT INTO user_authority(user_id,authority_id)VALUES(1,1);
INSERT INTO user_authority(user_id,authority_id)VALUES(2,2)
