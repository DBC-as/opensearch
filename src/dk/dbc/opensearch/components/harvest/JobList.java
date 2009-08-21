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

package dk.dbc.opensearch.components.harvest;

import java.util.ArrayList;
import java.util.Iterator;

public class JobList implements IJobList
{    
    private ArrayList<IJob> jobList;
    private Iterator<IJob> iter;

    public JobList()
    {
        jobList = new ArrayList<IJob>();
        iter = jobList.iterator();
    }

    public IJob getNext()
    {
        System.out.println( "getNext called" );
        if(! iter.hasNext() )
        {
            throw new IllegalStateException( "no further elements in the JobList" );
        }
        else
        {
            System.out.println( "iter has next..." );
            return (IJob)iter.next();
        }
    }

    public boolean hasNext()
    {
        return iter.hasNext();
    }

    public void add( IJob theJob )
    {
        jobList.add( theJob );
    }

    public int size()
    {
        return jobList.size();
    }
}