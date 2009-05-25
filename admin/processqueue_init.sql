/**
This file is part of opensearch.
Copyright © 2009, Dansk Bibliotekscenter a/s, 
Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

opensearch is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

opensearch is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/


-- The processqueue table

CREATE TABLE processqueue(
       queueID INTEGER PRIMARY KEY,
       fedorahandle VARCHAR(100),
       processing CHAR(1) CHECK (processing IN ( 'Y', 'N' ))
);

CREATE SEQUENCE processqueue_sequence
       MAXVALUE 1000000000
       NO CYCLE;

-- Returns a resultset of non processing posts, which where updated to
-- processing = 'Y' during retrieval.

CREATE OR REPLACE FUNCTION get_all_posts() RETURNS SETOF processqueue AS
$BODY$
DECLARE
    r processqueue%rowtype;
BEGIN
    FOR r IN SELECT * FROM processqueue
    WHERE processing = 'N' ORDER BY queueid LOOP
        UPDATE processqueue
        SET processing = 'Y'
        WHERE queueid = r.queueid;
        RETURN NEXT r;
    END LOOP;
    RETURN;
END
$BODY$
LANGUAGE 'plpgsql';


-- Returns a resultset of non processing posts, which where updated to
-- processing = 'Y' during retrieval. the integer argument is the max
-- resultsize 
-- 
-- @param max  the maximum resultsetsize

CREATE OR REPLACE FUNCTION get_posts( integer ) RETURNS SETOF processqueue AS
$BODY$
DECLARE
    max ALIAS FOR $1;
    r processqueue%rowtype;
BEGIN
    FOR r IN SELECT * FROM processqueue
    WHERE processing = 'N' ORDER BY queueid LIMIT max LOOP
        UPDATE processqueue
        SET processing = 'Y'
        WHERE queueid = r.queueid;
        RETURN NEXT r;
    END LOOP;
    RETURN;
END
$BODY$
LANGUAGE 'plpgsql';


