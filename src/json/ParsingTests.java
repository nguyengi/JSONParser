package json;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import json.JSON.ParsingException;

import org.junit.Test;

/**
 * Some tests using randomized inputs
 * 
 * @author Giang
 * 
 */
public class ParsingTests
{
  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+
  /**
   * To use in permutation
   */
  Random r = new Random();
  
  /**
   * Keeps some sample Number values
   */
  Pair<Number>[] sampleNumbers;
  
  /**
   * Keeps some sample ArrayList values
   */
  Pair<ArrayList<Object>>[] sampleArrays;
  
  /**
   * Keeps some sample HashMap (JSON object) values
   */
  Pair<HashMap<String, Object>>[] sampleObjects;
  
  /**
   * Keeps some sample String values
   */
  Pair<String>[] sampleStrings;
  
  /**
   * Keeps the two true, false values. Cannot use null but null works by tests
   * in Tests.java
   */
  Pair<Object>[] specialValues;

  // +---------+---------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Initializes samples to a variety of values
   */
  @SuppressWarnings("unchecked")
  void initialize()
  {
    sampleNumbers = new Pair[5];
    sampleNumbers[0] = new Pair<Number>("0.0", 0.0);
    sampleNumbers[1] = new Pair<Number>("12E-5", Double.parseDouble("1.2E-4"));
    sampleNumbers[2] = new Pair<Number>("912381238.0", 912381238.0);
    sampleNumbers[3] =
        new Pair<Number>("-56.712E11", Double.parseDouble("-56.712E11"));
    sampleNumbers[4] = new Pair<Number>("-0.5", -0.5);

    sampleStrings = new Pair[5];
    sampleStrings[0] = new Pair<String>("\"a,b,c,d\"", "a,b,c,d");
    sampleStrings[1] = new Pair<String>("\"\\\"ABC\\\"\"", "\"ABC\"");
    sampleStrings[2] =
        new Pair<String>("\"[]+=-!@#$%^&*(){]\"", "[]+=-!@#$%^&*(){]");
    sampleStrings[3] = new Pair<String>("\"{::}:,\t\n\"", "{::}:,\t\n");
    sampleStrings[4] = new Pair<String>("\"\"", "");

    specialValues = new Pair[2];
    specialValues[0] = new Pair<Object>("true", true);
    specialValues[1] = new Pair<Object>("false", false);
    // specialValues[2] = new Pair<Object>("null", null);

    sampleArrays = new Pair[5];
    for (int i = 0; i < sampleArrays.length; i++)
      {
        sampleArrays[i] = new Pair<ArrayList<Object>>("[]", new ArrayList<>());
      }

    sampleObjects = new Pair[5];
    for (int i = 0; i < sampleObjects.length; i++)
      {
        sampleObjects[i] =
            new Pair<HashMap<String, Object>>("{}",
                                              new HashMap<String, Object>());
      }
  }

  /**
   * Checks all samples
   * 
   * @throws ParsingException
   */
  void genericTests()
    throws ParsingException
  {
    eachGenericTest(sampleNumbers);
    eachGenericTest(sampleStrings);
    eachGenericTest(specialValues);
    eachGenericTest(sampleObjects);
    eachGenericTest(sampleArrays);
  }

  /**
   * Check a specific sample
   * 
   * @param pr
   *          a Pair
   * @throws ParsingException
   */
  @SuppressWarnings("rawtypes")
  void eachGenericTest(Pair[] pr)
    throws ParsingException
  {
    for (int i = 0; i < pr.length; i++)
      {
        try
          {
            if (!testPair(pr[i]))
              {
                System.err.println("Failed on input " + pr[i].unparsed
                                   + ". Expected " + pr[i].parsed);
                fail();
              }
          }
        catch (Exception e)
          {
            System.err.println("Parsing error. Failed on input "
                               + pr[i].unparsed + ". Expected " + pr[i].parsed);
            fail();
          }
      }
  }

  /**
   * Permutates all sample arrays by adding random values a random number of
   * times
   */
  void permutateArrays()
  {
    for (int i = 0; i < sampleArrays.length; i++)
      {
        for (int i1 = 0; i1 < (r.nextInt() % 7); i1++)
          {
            addToArray(sampleArrays[i], sampleStrings[r.nextInt(5)]);
            addToArray(sampleArrays[i], sampleNumbers[r.nextInt(5)]);
            addToArray(sampleArrays[i], sampleObjects[r.nextInt(5)]);
            addToArray(sampleArrays[i], specialValues[r.nextInt(2)]);
          }
      }
  }

  /**
   * Permutates all sample objects by adding random values a random number of
   * times
   */
  void permutateObjects()
  {
    for (int i = 0; i < sampleObjects.length; i++)
      {
        for (int i1 = 0; i1 < (r.nextInt() % 7); i1++)
          {
            addToObject(sampleObjects[i], sampleStrings[r.nextInt(5)],
                        sampleStrings[r.nextInt(5)]);
            addToObject(sampleObjects[i], sampleStrings[r.nextInt(5)],
                        sampleNumbers[r.nextInt(5)]);
            addToObject(sampleObjects[i], sampleStrings[r.nextInt(5)],
                        specialValues[r.nextInt(2)]);
            addToObject(sampleObjects[i], sampleStrings[r.nextInt(5)],
                        sampleArrays[r.nextInt(5)]);
          }
      }
  }

  /**
   * Carries out tests after permuting the arrays
   * 
   * @throws ParsingException
   */
  @Test
  public void testArray()
    throws ParsingException
  {
    initialize();
    genericTests();
    for (int i = 0; i < 10; i++)
      {
        permutateArrays();
        genericTests();
        initialize();
        // permutateObjects();
        genericTests();
      }
  }

  /**
   * Carries out tests after permuting the objects
   * 
   * @throws ParsingException
   */
  @Test
  public void testObject()
    throws ParsingException
  {
    initialize();
    genericTests();
    for (int i = 0; i < 10; i++)
      {
        permutateObjects();
        genericTests();
      } // for
  } // testObject()

  /**
   * Adds specified element to specified array sample by adding both an element
   * to the stored ArrayList and a String to the JSON String
   * 
   * @param arr
   *          a Pair<ArrayList<Object>>
   * @param add
   *          a Pair
   */
  @SuppressWarnings("rawtypes")
  void addToArray(Pair<ArrayList<Object>> arr, Pair add)
  {
    arr.parsed.add(add.parsed);
    arr.unparsed = arr.unparsed.substring(0, arr.unparsed.length() - 1);
    if (arr.unparsed.length() > 1)
      arr.unparsed += ",";
    arr.unparsed += add.unparsed + "]";
  } // addToArray(Pair<ArrayList<Object>>, Pair)

  /**
   * Adds specified key value pair to specified HashMap (object) sample by
   * adding both an element to the stored HashMap and a String to the JSON
   * String
   * 
   * @param obj
   *          a Pair<HashMap<String, Object>>
   * @param key
   *          a Pair<String>
   * @param add
   *          a Pair
   */
  @SuppressWarnings("rawtypes")
  void
    addToObject(Pair<HashMap<String, Object>> obj, Pair<String> key, Pair add)
  {
    if (!obj.parsed.containsKey(key.parsed))
      {
        obj.parsed.put(key.parsed, add.parsed);
        obj.unparsed = obj.unparsed.substring(0, obj.unparsed.length() - 1);
        if (obj.unparsed.length() > 1)
          obj.unparsed += ",";
        obj.unparsed += (key.unparsed + ":" + add.unparsed + "}");
      } // if
  } // addToObject(Pair<HashMap<String, Object>>, Pair<String>, Pair)

  /**
   * Compares the Pair's stored object against the parsed result of the JSON
   * class
   * 
   * @param pr
   *          a Pair
   * @throws ParsingException
   */
  @SuppressWarnings("rawtypes")
  boolean testPair(Pair pr)
    throws ParsingException
  {
    return pr.parsed.equals(JSON.parse(JSON.toJSONString(JSON.parse(pr.unparsed))));
  } // testPair(Pair)

  /**
   * Helper class to help keep track of and check a JSON String and its
   * corresponding JSON object
   * 
   * @author Giang
   */

  class Pair<K>
  {
    // +--------+----------------------------------------------------------
    // | Fields |
    // +--------+

    /**
     * The JSON String that will be parsed to check against the object
     */
    String unparsed;

    /**
     * Keeps track of a stored JSON object
     */
    K parsed;

    // +--------------+----------------------------------------------------
    // | Constructors |
    // +--------------+
    public Pair(String unparsed, K parsed)
    {
      this.unparsed = unparsed;
      this.parsed = parsed;
    } // Pair(String, K)
  } // class Pair<K>
} // class ParsingTests
