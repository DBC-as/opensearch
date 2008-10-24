/**
 * \file doxygen.java
 * \brief Dummy file to hold the main documentation page.
 */

/**
 * \defgroup components Components
 * \brief The components of opensearch
 * \defgroup tools Tools
 * \brief The tools classes for facilitating the components and testing.
 * 
 * \defgroup datadock DataDock
 * \ingroup components 
 * \brief The datadock receives incoming data and stores it in the repository.
 * \defgroup pti PTI
 * \ingroup components 
 * \brief The PTI component validates, processes and indexes the data referenced in the processqueue.
 *
 */

/**
 * \mainpage 
 * \section Overview 
 * The Opensearch project has 3 different main responsibilities: 
 * - Receiving and storing data.\n    
 *   This is done in the DataDock component. 
 *   The first responsibility of this component is to receive data, and validate
 *   the necessary metadata needed for later indexing.\n
 *   After validation, the recieved data is stored in an internal format (a DataDock.CargoContainer).
 *   Secondly the component must store the data in the fedora repository, 
 *   alert the pti component of the new task, and lastly return an estimate 
 *   of the time before the data object is effectively searchable.
 *   \see DataDock  
 * - Indexing data.\n
 *   This is done in the PTI component. 
 *   When alerted of a new data object to index, this component retrieves a copy 
 *   of the data from the repository and prepares it for indexing. Through the
 *   Compass framework the data gets indexed and saved in the database. When this process is finished, the 
 *   estimate table in the database gets updated.
 *   \see PTI  
 * - Facilitate search of indexed data.\n 
 *   Not yet implemented
 *
 * \section Components
 *
 * \subsection DataDock
 * The DataDock can roughly be split into these parts:
 * - Receive data.\n
 *   This part is somewhat hardcoded and currently only supports faktalink xml files. 
 *   Data is read from files in a directory given to the DataDock main method.
 *   The information needed for indexing which is mimetype, language, submitter 
 *   and format is also given to the main method. These arguments are validated, 
 *   and a CargoContainer with the data and meta data is constructed.
 * \see DataDockPoolAdmMain, DataDockPoolAdm, DataDockPool, CargoContainer 
 * - Store data in repository.\n
 *   When a CargoContainer is constructed the DataDockPool spawns a DataDock thread.
 *   The DataDock thread stores the data in the repository through a FedoraHandler.
 *   \see DataDockPool, DataDock, FedoraHandler
 * - Alert the PTI Component of a new data object
 *   After the data is stored, a repository pointer is pushed onto the Processqueue,
 *   which will be polled by the PTI component.
 *   \see DataDock, Processqueue
 * - Return an Estimate of the time before the data object is searchable.\n
 *   Reads an average process time for data with this mimetype from the statisticDB 
 *   table in the database, and returns a suitable estimate for how long time remains 
 *   before the data object is searchable by the Lantern component to the submitter.
 *   Currently the estimate is printed to the log
 *   \see DataDock, Estimate
 *
 * \subsection pti PTI (Processing, Transformation, BLABLA)
 * The PTI Component is thought as a daemon service that polls the Processqueue for 
 * new repository pointers. it can be split into these parts:
 * - Polling Processqueue and start processing/indexing.\n
 *   The PTIPoolAdm class polls the Processqueue, and when a new data entry is made 
 *   by the DataDock, it starts a new PTI thread through the PTIPool.
 *   \see PTIPoolAdmMain, PTIPoolAdm, PTIPool, Processqueue
 * - Process and transform data for indexing.\n
 *   This only works for faktalink xml at the moment. The Indexing is done with a 
 *   Compass instance, And the indexes in the faktalink instance re generated by using
 *   Compass XSEM mapping feature. Later other format can be index using XSEM or  the 
 *   mapping models provided by Compass. 
 *   \see PTIPool, PTI, Compass
 * - Indexing and Storing of data.\n
 *   The Indexing and storing of the indexes is done through a CompassSession. It stores 
 *   the indexes in an oracle database.
 *   \see PTI, Compass
 * - Update Estimate for this mimetype.\n
 *   After the indexes are stored in the database the data object is searchable. The 
 *   PTI thread exits, and return the actual processtime for this data object. The average 
 *   process-time is calculated and the StatisticDB table in the database is updated with 
 *   the new information. 
 *   \see PTI, Estimate
 *
 * \subsection Lantern
 * Functionality of this module is not yet implemented, a prototype of the funtionality is available through Solr.
 *
 * \section Tools
 * Here follows a description of the tools used to facilitate the components.
 *
 * \subsection FedoraHandler
 * The FedoraHandler handles communication with the Fedora repository. 
 * - Storing data.\n
 *   When used to store data, which it does in the DataDock, the FedoraHandler generates 
 *   Dublin Core data from the CargoContainer supplied in the submitDataStream method.  
 *   After wards the data and the Dublin Core is submitted to the repository.
 * - Retrieving data.\n
 *   The FedoraHandlers getDataStream method retrieves the data from the repository, and 
 *   returns it wrapped in a CargoContainer.
 *
 * \see FedoraHandler
 *
 * \subsection Processqueue
 * The Processqueue is used to register not yet indexed data objects. This class handles all 
 * communication to the Processqueue table in the database.When the DataDock has stored
 * the data in the repository i pointer to the data is pushed to the queue.
 * The PTI component polls the Processqueue and when a new data-element is present it is popped 
 * from the queue. After the PTI has ended indexing the pop is now committed to the queue. 
 * If something goes wrong i the indexing a rollback of the pop is possible.
 * \see Processqueue
 *
 * \subsection Estimate
 * The Estimate class handles all communication with the StatisticDB table in the database, 
 * and is used to return an estimate of the time before the data is searchable.
 * It provides methods for retrieving and updating estimates for the processing time of 
 * mimetypes.
 * \see Estimate
 * 
 * \subsection xsd2xsem
 * xsd2xsem is a python script used to generate XSEM mappings for for all xml documents 
 * validated by the given xsd file.
 *
 * \section Executables
 * 
 * At the moment there are 2 executables:
 * - DataDockPoolAdmMain\n
 *   starts The DataDock.\n
 *   example of usage:\n
 *   java -Dsubmitter="dbc" -Dfilepath="testdir/*.xml" -Dformat="faktalink" -Dmimetype="text/xml" -Dlang="" -cp `bin/run` dbc.opensearch.components.datadock.DataDockPoolAdm  
 * - PTIPoolAdmMain\n
 *   starts the PTI.\n
 *   example of usage:\n
 *   java -cp `bin/run` dbc.opensearch.components.pti.PTIPoolAdmMain
 *
 * \section Configuration
 * The configuration files are located in the 'opensearch/trunk/config' directory.
 * It contains the following files:
 * - compass.cfg.xml\n
     The compass configuration.
 * - config.xml\n
     Configuration file currently containing configuration for the database access for the 
     FedoraHandler, Processqueue and Estimate. 
 * - log4j.xml\n
     Configuration of the logging framework.
 * - xml.cpm.xlm\n
     Contains the XSEM mappings for known formats.
 * \section Administration
 * The Administration files are located in the 'opensearch/trunk/admin' directory.
 * The directory contains the following files:
 * - Processqueue_init.sql\n
 *   SQL script used to initialize the Processqueue table and a stored procedure.
 * - StatisticDB_init.sql\n
 *   SQL script used to initialize the StatisticDB table.
 * - runall.sql\n
 *   Runs Processqueue_init.sql and StatisticDB_init.sql
 * - query_Processqueue.sql\n
 *   dumps the Processqueue.
 */
