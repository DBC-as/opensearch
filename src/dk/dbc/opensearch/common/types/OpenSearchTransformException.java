/*
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

/**
 * \file 
 * \brief
 */


package dk.dbc.opensearch.common.types;

import dk.dbc.opensearch.common.types.*;


/**
 *
 */
public class OpenSearchTransformException extends OpenSearchException {

    /**
     * Constructs an instance of <code>OpenSearchTransformException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public OpenSearchTransformException(String msg) {
        super(msg);
    }

    public OpenSearchTransformException( String msg, Exception exception )
    {
        super( msg, exception );
    }

    public OpenSearchTransformException( Exception exception )
    {
        super( exception );
    }



}
