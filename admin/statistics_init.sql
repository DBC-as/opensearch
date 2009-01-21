CREATE TABLE statistics(  
  processtime INTEGER,
  dataamount INTEGER,
  mimetype VARCHAR(100) PRIMARY KEY
);

/* Initialize table ... should be handled ind the datadock add handler class */

INSERT INTO statistics
VALUES (0,0,'text/xml');


