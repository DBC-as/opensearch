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

package dk.dbc.opensearch.helpers;

import dk.dbc.opensearch.fedora.FedoraObjectFields;
import dk.dbc.opensearch.fedora.OpenSearchCondition;
import dk.dbc.opensearch.types.ITargetField;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;
import mockit.Mocked;

public class ConvertCQLToFedoraConditionsTest {
    

    @Test
    public void convertStringToCondition()
    {
        String test = "title = hat";
        ArrayList< OpenSearchCondition > conditionList = ConvertCQLToFedoraConditions.transformConditionStringToConditionList( test );
        OpenSearchCondition condition = conditionList.get( 0 );
        assertTrue( condition.getField().fieldname().equals( "title" ) );
        assertTrue( condition.getOperator() == OpenSearchCondition.Operator.EQUALS );
        assertTrue( condition.getValue().equals( "hat" ) ); 
    }
    
    @Test
    public void convertEmptyStringToCondition()
    {
        String test = "";
        ArrayList< OpenSearchCondition > conditionList = ConvertCQLToFedoraConditions.transformConditionStringToConditionList( test );
        assertTrue( conditionList.isEmpty() );
    }

    @Test
    public void getSubstringwithoutEndParanthese() throws Exception
    {
        String test = "test ( betatest )";
        String[] testArray = test.split( " " );
        String[] result = ConvertCQLToFedoraConditions.getQueryInParantheses( 1, testArray );
        assertTrue( result.length == 1 );
        assertTrue( result[ 0 ].equals( "betatest" ) );

    }

    @Test
    public void getSubstringWithNestedParatheses() throws Exception
    {
        String test = "( test ( test ) )";
        String[] testArray = test.split( " " );
        String[] result = ConvertCQLToFedoraConditions.getQueryInParantheses( 0, testArray );
        assertTrue( result.length == 4 );
        assertTrue( result[ 0 ].equals( "test" ) );
        assertTrue( result[ 1 ].equals( "(" ) );
        assertTrue( result[ 2 ].equals( "test" ) );
        assertTrue( result[ 3 ].equals( ")" ) ); 
    }
    
    @Test
    public void getEmptyString() throws Exception
    {
        String test = "( )"; 
        String[] testArray = test.split( " " );
        String[] result = ConvertCQLToFedoraConditions.getQueryInParantheses( 0, testArray );
        assertTrue( result.length == 1 );
        assertTrue( result[ 0 ].isEmpty() );
    }
    
    @Test
    public void makeListPermutationsEmptyAndSingle()
    {
        String test = "title = test";
        OpenSearchCondition condition = ConvertCQLToFedoraConditions.transformConditionStringToConditionList( test ).get( 0 );
        ArrayList< OpenSearchCondition > emptyList = new ArrayList< OpenSearchCondition >();
        ArrayList< OpenSearchCondition > list1 = new ArrayList< OpenSearchCondition >();
        list1.add( condition );
        ArrayList< ArrayList< OpenSearchCondition > > listList1 = new ArrayList< ArrayList< OpenSearchCondition > >();
        ArrayList< ArrayList< OpenSearchCondition > > listList2 = new ArrayList< ArrayList< OpenSearchCondition > >();
        listList1.add( emptyList );
        listList2.add( list1 );
        
        ArrayList< ArrayList< OpenSearchCondition > > resultListList = ConvertCQLToFedoraConditions.makeListPermutations( listList1, listList2 );
        
        assertTrue( resultListList.size() == 1 );
        assertTrue( resultListList.get( 0 ).size() == 1 );

        OpenSearchCondition resultCondition = resultListList.get( 0 ).get( 0 );
        
        assertTrue( resultCondition.getField().fieldname().equals( "title" ) );
        assertTrue( resultCondition.getOperator() == OpenSearchCondition.Operator.EQUALS );
        assertTrue( resultCondition.getValue().equals( "test" ) ); 
    }

    @Test
    public void makeListPermutationsOneAndTwo()
    {
        String test1 = "title = testBase";
        String test2 = "title = test2";
        String test3 = "title = test3";
        ArrayList< OpenSearchCondition > baseList = ConvertCQLToFedoraConditions.transformConditionStringToConditionList( test1 );
        ArrayList< OpenSearchCondition > list2 = ConvertCQLToFedoraConditions.transformConditionStringToConditionList( test2 );
        ArrayList< OpenSearchCondition > list3 = ConvertCQLToFedoraConditions.transformConditionStringToConditionList( test3 ); 
        ArrayList< ArrayList< OpenSearchCondition > > listList1 = new ArrayList< ArrayList< OpenSearchCondition > >();
        ArrayList< ArrayList< OpenSearchCondition > > listList2 = new ArrayList< ArrayList< OpenSearchCondition > >();
        listList1.add( baseList );
        listList2.add( list2 );
        listList2.add( list3 );
        
        ArrayList< ArrayList< OpenSearchCondition > > resultListList = ConvertCQLToFedoraConditions.makeListPermutations( listList1, listList2 );

        assertTrue( resultListList.size() == 2 );
        assertTrue( resultListList.get( 0 ).size() == 2 );
        assertTrue( resultListList.get( 0 ).get( 0 ).getValue().equals( "testBase" ) );
        assertTrue( resultListList.get( 0 ).get( 1 ).getValue().equals( "test2" ) );
        assertTrue( resultListList.get( 1 ).size() == 2 );
        assertTrue( resultListList.get( 1 ).get( 0 ).getValue().equals( "testBase" ) );
        assertTrue( resultListList.get( 1 ).get( 1 ).getValue().equals( "test3" ) );
    }

    @Test
    public void makeListPermutationsOneAndOneTwo()
    {
        String test1 = "title = testBase";
        String test2 = "title = test2";
        String test3 = "title = test3";
        ArrayList< OpenSearchCondition > baseList = ConvertCQLToFedoraConditions.transformConditionStringToConditionList( test1 );
        ArrayList< OpenSearchCondition > list2 = ConvertCQLToFedoraConditions.transformConditionStringToConditionList( test2 );
        ArrayList< OpenSearchCondition > list3 = ConvertCQLToFedoraConditions.transformConditionStringToConditionList( test3 ); 
        ArrayList< ArrayList< OpenSearchCondition > > listList1 = new ArrayList< ArrayList< OpenSearchCondition > >();
        ArrayList< ArrayList< OpenSearchCondition > > listList2 = new ArrayList< ArrayList< OpenSearchCondition > >();
        listList1.add( baseList );
        listList2.add( list2 );
        listList2.add( list3 );
        listList2.get( 1 ).add( listList2.get( 1 ).get( 0 ) );
        
        ArrayList< ArrayList< OpenSearchCondition > > resultListList = ConvertCQLToFedoraConditions.makeListPermutations( listList1, listList2 );

        assertTrue( resultListList.size() == 2 );
        assertTrue( resultListList.get( 0 ).size() == 2 );
        assertTrue( resultListList.get( 0 ).get( 0 ).getValue().equals( "testBase" ) );
        assertTrue( resultListList.get( 0 ).get( 1 ).getValue().equals( "test2" ) );
        assertTrue( resultListList.get( 1 ).size() == 3 );
        assertTrue( resultListList.get( 1 ).get( 0 ).getValue().equals( "testBase" ) );
        assertTrue( resultListList.get( 1 ).get( 1 ).getValue().equals( "test3" ) );
        assertTrue( resultListList.get( 1 ).get( 2 ).getValue().equals( "test3" ) );
    }
    
    @Test
    public void makeListPermutationsEmptyBaseList()
    {
        String test2 = "title = test2";
        ArrayList< OpenSearchCondition > list2 = ConvertCQLToFedoraConditions.transformConditionStringToConditionList( test2 );
        ArrayList< ArrayList< OpenSearchCondition > > listList1 = new ArrayList< ArrayList< OpenSearchCondition > >();
        ArrayList< ArrayList< OpenSearchCondition > > listList2 = new ArrayList< ArrayList< OpenSearchCondition > >(); 
        listList2.add( list2 );

        ArrayList< ArrayList< OpenSearchCondition > > resultListList = ConvertCQLToFedoraConditions.makeListPermutations( listList1, listList2 );

        assertTrue( resultListList.size() == 1 );
        assertTrue( resultListList.get( 0 ).get( 0 ).getValue().equals( "test2" ) );
    } 

    @Test
    public void makeListPermutationsEmptyAppendList()
    {
        String test1 = "title = testBase";
        ArrayList< OpenSearchCondition > list1 = ConvertCQLToFedoraConditions.transformConditionStringToConditionList( test1 );
        ArrayList< ArrayList< OpenSearchCondition > > listList1 = new ArrayList< ArrayList< OpenSearchCondition > >();
        ArrayList< ArrayList< OpenSearchCondition > > listList2 = new ArrayList< ArrayList< OpenSearchCondition > >(); 
        listList1.add( list1 );

        ArrayList< ArrayList< OpenSearchCondition > > resultListList = ConvertCQLToFedoraConditions.makeListPermutations( listList1, listList2 );

        assertTrue( resultListList.size() == 1 );
        assertTrue( resultListList.get( 0 ).get( 0 ).getValue().equals( "testBase" ) );
    }

}