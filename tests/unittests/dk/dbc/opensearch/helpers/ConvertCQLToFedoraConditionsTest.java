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


import dk.dbc.opensearch.fedora.OpenSearchCondition;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;


public class ConvertCQLToFedoraConditionsTest
{
    /*
     * Tests that transformConditionStringToConditionList can convert 
     * a cql query part into an OpensearchCondition contained in an ArrayList
     */
    @Test
    public void convertStringToConditionList()
    {
        String test = "title = hat";
        ArrayList< OpenSearchCondition > conditionList = ConvertCQLToFedoraConditions.transformConditionStringToConditionList( test );
        OpenSearchCondition condition = conditionList.get( 0 );
        assertTrue( condition.getField().fieldname().equals( "title" ) );
        assertTrue( condition.getOperator() == OpenSearchCondition.Operator.EQUALS );
        assertTrue( condition.getValue().equals( "hat" ) ); 
    }
    

    /*
     *The conversion of the empty String 
     */
    @Test
    public void convertEmptyStringToCondition()
    {
        String test = "";
        ArrayList< OpenSearchCondition > conditionList = ConvertCQLToFedoraConditions.transformConditionStringToConditionList( test );
        assertTrue( conditionList.isEmpty() );
    }


    /*
     * tests that the subquery contained in the parantheses is 
     * returned without the endning right paranthese
     */
    @Test
    public void getSubstringwithoutEndParanthese() throws Exception
    {
        String test = "test ( betatest )";
        String[] testArray = test.split( " " );
        String[] result = ConvertCQLToFedoraConditions.getQueryInParantheses( 1, testArray );
        assertTrue( result.length == 1 );
        assertTrue( result[ 0 ].equals( "betatest" ) );

    }


    /*
     * tests that a subquery nested in parantheses are treated correctly 
     * and not changed by the method
     */
    @Test
    public void getSubstringWithNestedParatheses() throws Exception
    {
        String test = "( test ( test ) ) hest";
        String[] testArray = test.split( " " );
        String[] result = ConvertCQLToFedoraConditions.getQueryInParantheses( 0, testArray );
        assertTrue( result.length == 4 );
        assertTrue( result[ 0 ].equals( "test" ) );
        assertTrue( result[ 1 ].equals( "(" ) );
        assertTrue( result[ 2 ].equals( "test" ) );
        assertTrue( result[ 3 ].equals( ")" ) ); 
    }
    

    /*
     * test that and empty string in parantheses returns an empty array
     */
    @Test
    public void getEmptyString() throws Exception
    {
        String test = "( )"; 
        String[] testArray = test.split( " " );
        String[] result = ConvertCQLToFedoraConditions.getQueryInParantheses( 0, testArray );
        assertTrue( result.length == 1 );
        assertTrue( result[ 0 ].isEmpty() );
    }
    
    /*
     * tests that we can append a baselist containing an empty list to a non-empty list
     */
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

    
    /*
     * test that the base list gets gets copied and gets 
     * both the lists in the appendlist appended
     */
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


    /*
     * tests that the baselist gets the appendlists appended correct 
     * when they are of different length
     */
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
    

    /*
     * tests that we return the appendlist when the baselist is empty
     */
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


    /*
     * tests that we return the baselist if the appendlist is empty
     */
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
    

    /*
     * tests that we can transform a string containing a legal cql query 
     * of field, operator and value, where the operator is EQUALS into a list of list 
     * of OpenSearchCondition
     */
    @Test
    public void searchTransformerSimpleSearch()
    {
        String query = "title = test";
        String[] queryParts = query.split( " " );
        ArrayList< ArrayList< OpenSearchCondition > > resultList = ConvertCQLToFedoraConditions.searchTransformer( queryParts );
        
        assertTrue( resultList.size() == 1 );
        assertTrue( resultList.get( 0 ).size() == 1 );
        OpenSearchCondition resultCondition = resultList.get( 0 ).get( 0 );
        assertTrue( resultCondition.getField().fieldname().equals( "title" ) );
        assertTrue( resultCondition.getValue().equals( "test" ) ); 
    
    
    } 
    

    /*
     *tests that we can treat the AND operator in the cql statement 
     */
    @Test
    public void searchTransformerANDSearch()
    {
        String query = "title = test AND creator = hat";
        String[] queryParts = query.split( " " );
        ArrayList< ArrayList< OpenSearchCondition > > resultList = ConvertCQLToFedoraConditions.searchTransformer( queryParts );
        
        assertTrue( resultList.size() == 1 );
        assertTrue( resultList.get( 0 ).size() == 2 );
        OpenSearchCondition resultCondition1 = resultList.get( 0 ).get( 0 );
        assertTrue( resultCondition1.getField().fieldname().equals( "title" ) );
        assertTrue( resultCondition1.getValue().equals( "test" ) ); 
    
        OpenSearchCondition resultCondition2 = resultList.get( 0 ).get( 1 );
        assertTrue( resultCondition2.getField().fieldname().equals( "creator" ) );
        assertTrue( resultCondition2.getValue().equals( "hat" ) ); 
    }    
    

    /*
     *tests that we can treat the OR operator   
     */
    @Test
    public void searchTransformerORSearch()
    {
        String query = "title = test OR creator = hat";
        String[] queryParts = query.split( " " );
        ArrayList< ArrayList< OpenSearchCondition > > resultList = ConvertCQLToFedoraConditions.searchTransformer( queryParts );
        
        assertTrue( resultList.size() == 2 );
        assertTrue( resultList.get( 0 ).size() == 1 );
        assertTrue( resultList.get( 1 ).size() == 1 );
        OpenSearchCondition resultCondition1 = resultList.get( 0 ).get( 0 );
        assertTrue( resultCondition1.getField().fieldname().equals( "title" ) );        
        assertTrue( resultCondition1.getValue().equals( "test" ) ); 
    
        OpenSearchCondition resultCondition2 = resultList.get( 1 ).get( 0 );
        assertTrue( resultCondition2.getField().fieldname().equals( "creator" ) );
        assertTrue( resultCondition2.getValue().equals( "hat" ) ); 
    }


    /*
     * tests the treatment of a query with both AND and OR
     */
    @Test
    public void searchTransformerANDORSearch()
    {
        String query = "title = test AND creator = hat OR creator = ged";
        String[] queryParts = query.split( " " );
        ArrayList< ArrayList< OpenSearchCondition > > resultList = ConvertCQLToFedoraConditions.searchTransformer( queryParts );
        
        assertTrue( resultList.size() == 2 );
        assertTrue( resultList.get( 0 ).size() == 2 );
        assertTrue( resultList.get( 1 ).size() == 2 );
        
        OpenSearchCondition resultCondition00 = resultList.get( 0 ).get( 0 );
        assertTrue( resultCondition00.getField().fieldname().equals( "title" ) );
        assertTrue( resultCondition00.getValue().equals( "test" ) ); 
    
        OpenSearchCondition resultCondition01 = resultList.get( 0 ).get( 1 );
        assertTrue( resultCondition01.getField().fieldname().equals( "creator" ) );
        assertTrue( resultCondition01.getValue().equals( "hat" ) ); 

        OpenSearchCondition resultCondition10 = resultList.get( 1 ).get( 0 );
        assertTrue( resultCondition10.getField().fieldname().equals( "title" ) );
        assertTrue( resultCondition10.getValue().equals( "test" ) ); 
    
        OpenSearchCondition resultCondition11 = resultList.get( 1 ).get( 1 );
        assertTrue( resultCondition11.getField().fieldname().equals( "creator" ) );
        assertTrue( resultCondition11.getValue().equals( "ged" ) ); 
    }


    /*
     * tests treatment of parantheses in a query where AND is 
     * right in front of the paranthese
     */
    @Test
    public void searchTransformerANDparORparSearch()
    {
        String query = "title = test AND ( creator = hat OR creator = ged )";
        String[] queryParts = query.split( " " );
        ArrayList< ArrayList< OpenSearchCondition > > resultList = ConvertCQLToFedoraConditions.searchTransformer( queryParts );
        
        assertTrue( resultList.size() == 2 );
        assertTrue( resultList.get( 0 ).size() == 2 );
        assertTrue( resultList.get( 1 ).size() == 2 );
        
        OpenSearchCondition resultCondition00 = resultList.get( 0 ).get( 0 );
        assertTrue( resultCondition00.getField().fieldname().equals( "title" ) );
        assertTrue( resultCondition00.getValue().equals( "test" ) ); 
    
        OpenSearchCondition resultCondition01 = resultList.get( 0 ).get( 1 );
        assertTrue( resultCondition01.getField().fieldname().equals( "creator" ) );
        assertTrue( resultCondition01.getValue().equals( "hat" ) ); 

        OpenSearchCondition resultCondition10 = resultList.get( 1 ).get( 0 );
        assertTrue( resultCondition10.getField().fieldname().equals( "title" ) );
        assertTrue( resultCondition10.getValue().equals( "test" ) ); 
    
        OpenSearchCondition resultCondition11 = resultList.get( 1 ).get( 1 );
        assertTrue( resultCondition11.getField().fieldname().equals( "creator" ) );
        assertTrue( resultCondition11.getValue().equals( "ged" ) ); 
    }

    
    /*
     * tests the treatment of parantheses in the query when OR is the operator 
     * in front of it
     */
    @Test
    public void searchTransformerORparANDparSearch()
    {
        String query = "title = test OR ( creator = hat AND creator = ged )";
        String[] queryParts = query.split( " " );
        ArrayList< ArrayList< OpenSearchCondition > > resultList = ConvertCQLToFedoraConditions.searchTransformer( queryParts );
        
        assertTrue( resultList.size() == 2 );
        assertTrue( resultList.get( 0 ).size() == 1 );
        assertTrue( resultList.get( 1 ).size() == 2 );
        
        OpenSearchCondition resultCondition00 = resultList.get( 0 ).get( 0 );
        assertTrue( resultCondition00.getField().fieldname().equals( "title" ) );
        assertTrue( resultCondition00.getValue().equals( "test" ) ); 
    
        OpenSearchCondition resultCondition10 = resultList.get( 1 ).get( 0 );
        assertTrue( resultCondition10.getField().fieldname().equals( "creator" ) );
        assertTrue( resultCondition10.getValue().equals( "hat" ) ); 
    
        OpenSearchCondition resultCondition11 = resultList.get( 1 ).get( 1 );
        assertTrue( resultCondition11.getField().fieldname().equals( "creator" ) );
        assertTrue( resultCondition11.getValue().equals( "ged" ) ); 
    } 
    
    
    /*
     * tests that we treat queries where there is more conditions after a subquery in parantheses
     */
    @Test
    public void searchTransformerCheckParentheses()
    {
        String query = "title = test OR ( creator = hat AND creator = ged ) AND title = hat";
        String[] queryParts = query.split( " " );
        ArrayList< ArrayList< OpenSearchCondition > > resultList = ConvertCQLToFedoraConditions.searchTransformer( queryParts );
        
        assertTrue( resultList.size() == 2 );
        assertTrue( resultList.get( 0 ).size() == 2 );
        assertTrue( resultList.get( 1 ).size() == 3 );
        
        OpenSearchCondition resultCondition00 = resultList.get( 0 ).get( 0 );
        assertTrue( resultCondition00.getField().fieldname().equals( "title" ) );
        assertTrue( resultCondition00.getValue().equals( "test" ) );   
        
        OpenSearchCondition resultCondition01 = resultList.get( 0 ).get( 1 );
        assertTrue( resultCondition01.getField().fieldname().equals( "title" ) );
        assertTrue( resultCondition01.getValue().equals( "hat" ) ); 
    
        OpenSearchCondition resultCondition10 = resultList.get( 1 ).get( 0 );
        assertTrue( resultCondition10.getField().fieldname().equals( "creator" ) );
        assertTrue( resultCondition10.getValue().equals( "hat" ) ); 
    
        OpenSearchCondition resultCondition11 = resultList.get( 1 ).get( 1 );
        assertTrue( resultCondition11.getField().fieldname().equals( "creator" ) );
        assertTrue( resultCondition11.getValue().equals( "ged" ) ); 
        
        OpenSearchCondition resultCondition12 = resultList.get( 1 ).get( 2 );
        assertTrue( resultCondition12.getField().fieldname().equals( "title" ) );
        assertTrue( resultCondition12.getValue().equals( "hat" ) ); 
    }

}