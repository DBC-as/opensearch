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


package dk.dbc.opensearch.types;


/**
 * This interface specifies the type that uniquely identifies a {@link
 * TaskInfo} with the {@link
 * dk.dbc.opensearch.components.harvest.IHarvest} implementation.
 * Clients must not be allowed to read information from the
 * IIdentifier, i.e. the contents of the IIdentifier is private to the
 * corresponding harvester.
 */
public interface IIdentifier
{ 
    // This interface is intentionally left empty since 
    // the purpose of the interface is to have a way to distribute 
    // a "closed" identifier to clients of harvesters,
    // which can be given to harvesters in relation to getting jobs, etc.
}