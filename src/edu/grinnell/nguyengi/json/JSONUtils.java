package edu.grinnell.nguyengi.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* Citation:
 * 
 *  http://stackoverflow.com/questions/3326112/java-best-way-to-pass-int-by-reference
 *  http://stackoverflow.com/questions/3834468/java-reading-hex-values-into-an-array-of-type-int
 *  http://stackoverflow.com/questions/1515597/how-should-escaped-unicode-be-handled-by-json-parsers-and-encoders
 *  inspired by http://www.cs.grinnell.edu/~rebelsky/Courses/CSC207/2014S/examples/sample-json-parser/src/JSON.java
 */

/**
 * For dealing with JSON data types
 * 
 * @author Giang
 * 
 */
public class JSONUtils
{

  // +-------+-----------------------------------------------------------
  // | Notes |
  // +-------+

  /*
   * This library recognizes more primitive JSON types such as strings, numbers,
   * and special values such as true, false, and null. As such, it will parse
   * and return them even when they are not wrapped in an array or an object.
   */

  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+

  // +--------------+----------------------------------------------------
  // | Constructors |
  // +--------------+

  // +---------+---------------------------------------------------
  // | Methods |
  // +---------+

  // +----------------+-----------------------------------------------
  // | Parser Methods |
  // +----------------+

  /**
   * Parses a JSON string and returns an object that corresponds to the value
   * described in that string.
   * 
   * @param str
   *          a String
   * @throws Exception
   */
  static public Object parse(String str)
    throws Exception
  {
    // the (first member of the) integer array will be used to keep track of the
    // parsing process
    return parse(str, new int[] { 0 });
  } // parse (String)

  /**
   * Parses a JSON string from a specified index and returns a corresponding
   * object.
   * 
   * @param str
   *          a String
   * @param index
   *          an int[]
   * @throws Exception
   */
  static Object parse(String str, int[] index)
    throws Exception
  {
    // White spaces either precede or follow JSON values
    // Removing here takes care of the white spaces in front in most cases
    deleteWhitespace(str, index);
    char first = str.charAt(index[0]);
    // I1: Object and array parse methods can only call parse when index is at
    // the first character of new value
    // I2: All parse methods keeps index at the separator tokens or the end when
    // they finish
    if (first == ('{'))
      {
        return parseObject(str, index);
      } // if object
    else if (first == '[')
      {
        return parseArray(str, index);
      } // else if array
    else if (first == '\"')
      {
        return parseString(str, index);
      } // else if String
    else if (Character.isDigit(first) || first == '.' || first == '+'
             || first == '-')
      {
        return parseNumber(str, index);
      } // else if Number
    else
      {
        return parseSpecialValue(str, index);
      } // else must be special value

  } // parse(String)

  /**
   * Parses single JSON object from the file specified by fileName.
   * 
   * @param fileName
   *          a String
   * @throws Exception
   */
  static public Object parseFromFile(String fileName)
    throws Exception
  {
    BufferedReader eyes =
        new BufferedReader(new FileReader(new File(fileName)));
    String test = null;
    StringBuilder obj = new StringBuilder();
    // Method treats all lines as single object
    while ((test = eyes.readLine()) != null)
      {
        obj.append(test);
      } // while
    eyes.close();
    return parse(obj.toString());
  } // parseFromFile(String)

  /**
   * Parses single JSON object from the file specified by fileName. Writes back
   * into file the JSON string without white spaces.
   * 
   * @param fileName
   *          a String
   * @throws Exception
   */
  static public void deleteWhitespace(String fileName)
    throws Exception
  {
    Object obj = parseFromFile(fileName);
    PrintWriter pen = new PrintWriter(new FileOutputStream(new File(fileName)));
    pen.write(toJSONString(obj));
    pen.flush();
    pen.close();
  } // deleteWhitespace(String)

  /**
   * Skips over consecutive whitespace characters starting from index specified
   * by index[0].
   * 
   * @param str
   *          a String
   * @param index
   *          an int[]
   */
  static void deleteWhitespace(String str, int[] index)
  {
    while (!isEnd(str, index[0])
           && Character.isWhitespace(str.charAt(index[0])))
      {
        index[0]++;
      } // while
  } // deleteWhitespace(String, int[])

  /**
   * Tries to parse str as true, false, or null, returns them if successful
   * 
   * @param str
   *          s String
   * @param index
   *          an int array
   * @throws Exception
   */
  static Object parseSpecialValue(String str, int[] index)
    throws Exception
  {
    // An obviously false value to compare later
    Object result = "";
    // Moves index as long as the first characters match
    if (str.startsWith("true", index[0]))
      {
        index[0] += 4;
        result = Boolean.TRUE;
      } // if
    else if (str.startsWith("false", index[0]))
      {
        index[0] += 5;
        result = Boolean.FALSE;
      } // else if
    else if (str.startsWith("null", index[0]))
      {
        index[0] += 4;
        result = null;
      } // else if
    // Takes care of all white spaces after value
    deleteWhitespace(str, index);
    // We're safe if there was actually a value or if token ends here
    if (result == null || !result.equals(""))
      if (isEnd(str, index[0]) || isEndOfSV(str.charAt(index[0])))
        return result;
    throw new Exception("Parser does not recognize value: "
                        + str.charAt(index[0]));
  } // parseSpecialValue(String, int[])

  /**
   * Helper predicate, determines whether c signals end of a special value
   * 
   * @param c
   *          a char
   */
  static boolean isEndOfSV(char c)
  {
    return c == ',' || c == ']' || c == '}';
  } // isEndOfSV(char)

  /**
   * Tries to parse str as a JSON object, returns a HashMap representing the
   * object if successful
   * 
   * @param str
   *          a String
   * @param index
   *          an int[]
   * @throws Exception
   */
  static HashMap<String, Object> parseObject(String str, int[] index)
    throws Exception
  {
    HashMap<String, Object> obj = new HashMap<>();
    index[0]++;
    deleteWhitespace(str, index);
    while (str.charAt(index[0]) != '}')
      {
        // Removes whitespace before parsing String, because call does not go
        // through parse()
        deleteWhitespace(str, index);
        if (str.charAt(index[0]) != '\"')
          throw new Exception("Invalid key type.");
        String key = parseString(str, index);
        if (str.charAt(index[0]) != ':')
          throw new Exception("Invalid object format.");
        index[0]++;
        obj.put(key, parse(str, index));
        // check for appropriate object characters
        if (str.charAt(index[0]) != ',' && str.charAt(index[0]) != '}')
          throw new Exception("Illegal object character: "
                              + str.charAt(index[0]));
        // skip a character only if it's a comma, not a }
        if (str.charAt(index[0]) == ',')
          index[0]++;
      } // while
    if (str.charAt(index[0]) != '}')
      throw new Exception("Illegal object character: " + str.charAt(index[0]));
    index[0]++;
    // Takes care of all white spaces after object
    deleteWhitespace(str, index);
    return obj;
  } // parseObject(String, int[])

  /**
   * Tries to parse str as a String, returns a corresponding String if
   * successful
   * 
   * @param str
   *          a String
   * @param index
   *          an int[]
   * @throws Exception
   */
  static String parseString(String str, int[] index)
    throws Exception
  {
    char current = 'a';
    StringBuilder temp = new StringBuilder();
    // Moves past "
    index[0]++;
    while (!isEnd(str, index[0])
           && ((current = str.charAt(index[0]++)) != '\"'))
      {
        // Special cases
        if (current == '\\')
          {
            switch (str.charAt(index[0]))
              {
                case '\\':
                  temp.append('\\');
                  break;
                case '\"':
                  temp.append('\"');
                  break;
                case '/':
                  temp.append('/');
                  break;
                case 'n':
                  temp.append('\n');
                  break;
                case 't':
                  temp.append('\t');
                  break;
                case 'b':
                  temp.append('\b');
                  break;
                case 'f':
                  temp.append('\f');
                  break;
                case 'r':
                  temp.append('\r');
                  break;
                case 'u':
                  // hex String -parse-> int -cast-> char = Unicode char
                  temp.append((char) (Integer.parseInt(str.substring(++index[0],
                                                                     index[0] + 4),
                                                       16)));
                  index[0] += 3;
                  break;
                default:
                  throw new Exception("Illegal character after \\.");
              } // switch
            index[0]++;
          } // if
        else
          {
            temp.append(current);
          }
      } // while
    if (current == '\"')
      {
        // Remove spaces after Strings
        deleteWhitespace(str, index);
        return temp.toString();
      } // if
    throw new Exception("Missing quote.");
  } // parseString(String, int[])

  /**
   * Tries to parse str as a JSON Number, returns a corresponding Number if
   * successful
   * 
   * @param str
   *          a String
   * @param index
   *          an int[]
   */
  static Number parseNumber(String str, int[] index)
  {
    char current;
    StringBuilder temp = new StringBuilder();
    // Keeps adding to string until token ends, as parseDouble will do the error
    // checking
    while (!isEnd(str, index[0])
           && !isEndOfNumber(current = str.charAt(index[0])))
      {
        temp.append(current);
        index[0]++;
      } // while
    return Double.parseDouble(temp.toString());
  } // parseNumber(String, int[])

  /**
   * Helper predicate
   * 
   * @param c
   *          a char
   */
  static boolean isEndOfNumber(char c)
  {
    return c == ',' || c == ']' || c == '}';
  } // isEndOfNumber(char)

  /**
   * Helper predicate
   * 
   * @param str
   *          a String
   * @param i
   *          an int
   */
  static boolean isEnd(String str, int i)
  {
    return (str.length() == i);
  } // isEnd(String, int)

  /**
   * Tries to parse str as a JSON array, returns an ArrayList representing the
   * object if successful
   * 
   * @param str
   *          a String
   * @param index
   *          an int[]
   * @throws Exception
   */
  static ArrayList<Object> parseArray(String str, int[] index)
    throws Exception
  {
    ArrayList<Object> arr = new ArrayList<>();
    // Moves past [
    index[0]++;
    deleteWhitespace(str, index);
    while (str.charAt(index[0]) != ']')
      {
        arr.add(parse(str, index));
        if (isIllegalArray(str.charAt(index[0])))
          throw new Exception("Illegal array character: "
                              + str.charAt(index[0]));
        // skips a space only if character is comma, not ]
        if (str.charAt(index[0]) == ',')
          index[0]++;
      } // while
    if (str.charAt(index[0]) != ']')
      throw new Exception("Illegal array character: " + str.charAt(index[0]));
    // for ]
    index[0]++;
    // Remove white spaces after array
    deleteWhitespace(str, index);
    return arr;
  } // parseArray (String, int[])

  /**
   * Helper predicate, returns whether char specified by c is legal.
   * 
   * @param c
   *          a char
   */
  static boolean isIllegalArray(char c)
  {
    return c != ',' && c != ']';
  } // isIllegalArray(char)

  // +------------------+-----------------------------------------------
  // | toString Methods |
  // +------------------+

  /**
   * Given an object created by parse, generate the JSON string that corresponds
   * to the object.
   * 
   * @exception Exception
   *              If the object cannot be converted, e.g., if it does not
   *              correspond to something created by parse.
   */
  @SuppressWarnings("unchecked")
  static public String toJSONString(Object obj)
  {
    if (obj instanceof ArrayList)
      {
        return arrayToJSONString((ArrayList<Object>) obj);
      } // if array
    else if (obj instanceof HashMap)
      {
        return objectToJSONString((HashMap<String, Object>) obj);
      } // else if object
    else if (obj == null)
      {
        return "null";
      } // else if null
    else if (obj instanceof String)
      {
        return stringToJSONString((String) obj);
      } // else if String
    else
      {
        return obj.toString();
      } // else
  } // toJSONString(Object)

  /**
   * From a String generated by parse, computes a corresponding JSON String
   * 
   * @param str
   *          a String
   * @throws Exception
   */
  static String stringToJSONString(String str)
  {
    StringBuilder temp = new StringBuilder();
    int index = 0;
    while (index < str.length())
      {
        switch (str.charAt(index))
          {
            case '\\':
              temp.append("\\\\");
              break;
            case '\"':
              temp.append("\\\"");
              break;
            case '/':
              temp.append("\\/");
              break;
            case '\n':
              temp.append("\\n");
              break;
            case '\t':
              temp.append("\\t");
              break;
            case '\b':
              temp.append("\\b");
              break;
            case 'f':
              temp.append("\\f");
              break;
            case '\r':
              temp.append("\\r");
              break;
            default:
              temp.append(str.charAt(index));
          } // switch
        index++;
      } // while
    return temp.insert(0, "\"").append("\"").toString();
  } // stringToJSONString(String)

  /**
   * From an ArrayList generated by parse, computes a corresponding JSON String
   * 
   * @param arr
   *          an ArrayList
   */
  static String arrayToJSONString(ArrayList<Object> arr)
  {
    StringBuilder result = new StringBuilder();
    result.append('[');
    for (Object o : arr)
      {
        result.append(toJSONString(o)).append(',');
      } // for
    // if array is not empty, get rid of ending comma
    if (result.length() > 1)
      result.deleteCharAt(result.length() - 1);
    return result.append(']').toString();
  } // arrayToJSONString(ArrayList<Object>)

  /**
   * From an object (HashMap) generated by parse, computes a corresponding JSON
   * String
   * 
   * @param map
   *          a HashMap
   */
  static String objectToJSONString(HashMap<String, Object> map)
  {
    StringBuilder result = new StringBuilder();
    result.append('{');
    // Go through HashMap, adding key & value in correct syntax
    for (Map.Entry<String, Object> entry : map.entrySet())
      {
        result.append(toJSONString(entry.getKey())).append(':')
              .append(toJSONString(entry.getValue())).append(',');
      } // for
    // if array is not empty, get rid of ending comma
    if (result.length() > 1)
      result.deleteCharAt(result.length() - 1);
    return result.append('}').toString();
  } // objectToJSONString(HashMap<String, Object>)
} // class JSONUtils
