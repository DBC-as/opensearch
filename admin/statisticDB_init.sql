CREATE TABLE statisticDB(  
  processtime INTEGER,
  dataamount INTEGER,
  mimetype VARCHAR(100) PRIMARY KEY
);

/* Initialize table ... should be handled ind the datadock add handler class */

INSERT INTO statisticDB
VALUES (0,0,'text/xml');

COMMIT;
