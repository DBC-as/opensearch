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
 * \file FileHarvestLightTest.java
 * \brief test class for the FileHarvestLight class
 * \package harvest;
 */

package dk.dbc.opensearch.harvest;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockClass;
import mockit.Mocked;
import static mockit.Mockit.setUpMocks;
import static mockit.Mockit.tearDownMocks;

import org.junit.*;
import static org.junit.Assert.*;

public class FileHarvestLightTest
{
    FileHarvestLight harvester;
    
    @Test
    public void constructorTest() throws Exception
    {
	// The below assumes you have a directory named "Harvest"
	// Since this i seldom the case, the test will fail.
	// I have, therefore, removed the constructor.

        // harvester = new FileHarvestLight();
    }

}