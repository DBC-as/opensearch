package dk.dbc.opensearch.common.types;

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

import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;

import java.io.IOException;

import java.util.List;

/**
 * \brief The CargoContainer interface defines the interactions that can be made
 * with a CargoContainer. The CargoContainer holds zero or more CargoObjects and
 * the methods defined in the CargoContainer aims to provide simple and uniform
 * access to the data contained in the CargoContainer.
 */

public interface ICargoContainer {

	/**
	 * Adds a 'datastream' to the CargoContainer; a datastream is any kind of
	 * data which, for the duration of the CargoContainer object to which it is
	 * attached will be treated as binary data. To ensure (and guarantee) that
	 * the program does not meddle with the data, it is added as a byte[] and
	 * returned as a byte[]. No attempts are made to interpret the contained
	 * data at any times.
	 * 
	 * The returned id uniquely identifies the data and makes it available as a
	 * CargoObject structure that encapsulates the information given to this
	 * method through the getCargoObject() and getCargoObjects().
	 * 
	 * 
	 * @param dataStreamName
	 *            defines the name (type, really) of the datastream which is
	 *            added to the CargoContainer
	 * @param format
	 *            specifies the type of material, which the datastream contains
	 * @param submitter
	 *            specifies the submitter (and legal owner) of the submitted
	 *            data
	 * @param language 
	 *            specifies the language of the datastream
	 * @param mimetype 
	 *            specifies the MIME (really the Internet Media Type) of the
	 *            datastream (see http://tools.ietf.org/html/rfc2388)
	 * @param indexingAlias 
	 *            specifies which alias should be used when indexing the
	 *            datastream
	 * @param data 
	 *            contains the datastream to be added to the cargocontainer. The
	 *            data is submitted as a byte[] and throughout the lifetime of
	 *            the CargoContainer, it is treated as binary data; ie. not
	 *            touched.
	 * 
	 * @return a unique id identifying the submitted data
	 */
	public int add( DataStreamType dataStreamName, 
					String format,
					String submitter, 
					String language, 
					String mimetype,
					IndexingAlias indexingAlias, 
					byte[] data) 
		throws IOException;

    public boolean hasCargo( int id );

    public boolean hasCargo( DataStreamType type );


    public CargoObject getCargoObject( int id );

    public CargoObject getCargoObject( DataStreamType type );

	/**
	 * Returns a List of CargoObjects that matches the DataStreamType. If you
	 * know that there are only one CargoObject matching the DataStreamType, use
	 * getCargoObject() instead. If no CargoObjects match the DataStreamType,
	 * this method returns null.
	 * 
	 * @param type The DataStreamType to find the CargoObject from
	 * 
	 * @return a List of CargoObjects or a null List if none were found
	 */
	public List<CargoObject> getCargoObjects( DataStreamType type );

	/**
	 * Returns a List of all the CargoObjects that are contained in the
	 * CargoContainer. If no CargoObjects are found, a null List object is
	 * returned
	 * 
	 * @return a List of all CargoObjects from the CargoContainer or a null List
	 *         object if none are found
	 */
	public List<CargoObject> getCargoObjects();

	public int getCargoObjectCount( DataStreamType type );

	public int getCargoObjectCount();

	/**
	 * Given an id of a CargoObject, this method returns the DataStreamType
	 * which the CargoObject was registered with
	 * 
	 * @param id the id to match the CargoObject with
	 * @return the DataStreamType of the CargoObject with the specified id
	 */
	public DataStreamType getDataStreamType( int id );

	
	/**
	 * Given an id of a CargoObject, this method returns the IndexingAlias 
	 * for the data in the CargoObject 
	 * 
	 * @param id the id to match the CargoObject with
	 * @return the alias that is used to index the data in the CargoObject with
	 */
	public IndexingAlias getIndexingAlias( int id );

	/**
	 * Given a DataStreamType of a CargoObject, this method returns the IndexingAlias 
	 * for the data in the CargoObject 
	 * 
	 * @param dataStreamType the DataStreamType to match the CargoObject with
	 * @return the alias that is used to index the data in the CargoObject with
	 */
	public IndexingAlias getIndexingAlias( DataStreamType dataStreamType );

}