package json;

// Citation: iterating with HashMap from: http://stackoverflow.com/questions/46898/how-do-i-iterate-over-each-entry-in-a-map

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * For dealing with JSON data types
 * 
 * @author Giang
 * 
 */
public class JSON
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
   * Parses a JSON string and return an object that corresponds to the value
   * described in that string.
   * 
   * @throws ParsingException
   */
  static public Object parse(String str)
    throws ParsingException
  {
    try
      {
        return parseString(str);
      } // try
    catch (ParsingException stringE)
      {
        try
          {
            return parseArray(str);
          } // try
        catch (ParsingException arrayE)
          {
            try
              {
                return parseObject(str);
              } // try
            catch (ParsingException objectE)
              {
                try
                  {
                    return parseNumber(str);
                  } // try
                catch (Exception e)
                  {
                    return parseSpecial(str);
                  } // catch
              } // catch
          } // catch
      } // catch
  } // parse(String)

  /**
   * Tries to parse str as an array, returns array if successful
   * 
   * @param str
   *          a String
   * @throws ParsingException
   */
  static public ArrayList<Object> parseArray(String str)
    throws ParsingException
  {
    try
      {
        if (str.charAt(0) == '[' && str.charAt(str.length() - 1) == ']')
          {
            Stack<Character> s = new Stack<>();
            ArrayList<Object> result = new ArrayList<>();
            int commaPlace = 0;
            for (int i = 1; i < str.length() - 1; i++)
              {
                if (str.charAt(i) == ',' && s.isEmpty())
                  {
                    result.add(parse(str.substring(commaPlace + 1, i)));
                    commaPlace = i;
                  } // if
                else if (str.charAt(i) == '\"' && str.charAt(i - 1) == '\\')
                  {
                    continue;
                  } // else if
                trackBrackets(str.charAt(i), s);
              } // for
            if (commaPlace != 0)
              result.add(parse(str.substring(commaPlace + 1, str.length() - 1)));
            return result;
          } // if
        throw new ParsingException(str);
      } // try
    catch (Exception e)
      {
        throw new ParsingException(str);
      } // catch
  } // parseArray(String)

  /**
   * Tries to parse str as a Double, returns a Number of successful
   * 
   * @param str
   *          a String
   * @throws ParsingException
   */
  static public Number parseNumber(String str)
    throws ParsingException
  {
    try
      {
        return Double.parseDouble(str);
      } // try
    catch (Exception e)
      {
        throw new ParsingException(str);
      } // catch
  } // parseNumber(String)

  /**
   * Tries to parse str as the keywords true, false, or null, returns the values
   * if successful
   * 
   * @param str
   *          a String
   * @throws ParsingException
   */
  static public Object parseSpecial(String str)
    throws ParsingException
  {
    switch (str)
      {
        case "true":
          return Boolean.TRUE;
        case "false":
          return Boolean.FALSE;
        case "null":
          return null;
        default:
          throw new ParsingException(str);
      } // switch
  } // parseSpecial(String)

  /**
   * Tries to parse str as a JSON object, returns a HashMap representing the
   * object if successful
   * 
   * @param str
   *          a String
   * @throws ParsingException
   */
  static public HashMap<String, Object> parseObject(String str)
    throws ParsingException
  {
    try
      {
        if (str.charAt(0) == '{' && str.charAt(str.length() - 1) == '}')
          {
            HashMap<String, Object> result = new HashMap<>();
            Stack<Character> s = new Stack<>();
            String currentKey = null;
            int commaPlace = 0;
            int colonPlace = 0;
            for (int i = 1; i < str.length() - 1; i++)
              {
                if (str.charAt(i) == ',' && s.isEmpty())
                  {
                    Object value = parse(str.substring(colonPlace + 1, i));
                    result.put(currentKey, value);
                    commaPlace = i;
                  } // if
                else if (str.charAt(i) == ':' && s.isEmpty())
                  {
                    currentKey = parseString(str.substring(commaPlace + 1, i));
                    colonPlace = i;
                  } // else if
                else if (str.charAt(i) == '\"' && str.charAt(i - 1) == '\\')
                  {
                    continue;
                  } // else if
                trackBrackets(str.charAt(i), s);
              } // for
            if (colonPlace != 0)
              result.put(currentKey,
                         parse(str.substring(colonPlace + 1, str.length() - 1)));
            return result;
          } // if
        throw new ParsingException(str);
      } // try
    catch (StringIndexOutOfBoundsException e)
      {
        throw new ParsingException(str + ": string index out of bounds.");
      } // catch
  } // parseObject(String)

  /**
   * Keeps track of the brackets and quotes for the caller
   * 
   * @param c
   *          a char
   * @param s
   *          a Stack
   * @throws ParsingException
   */
  public static void trackBrackets(char c, Stack<Character> s)
    throws ParsingException
  {
    String open = "\"[{";
    String close = "\"]}";
    // if the character is within a quote, skip it
    if (c != '\"' && !s.isEmpty() && s.peek() == '\"')
      {
        return;
      } // if
    if (c == '\"' && !s.isEmpty() && s.peek() == '\"')
      {
        s.pop();
      } // if
    else if (open.indexOf(c) >= 0
             && (s.isEmpty() || (!s.isEmpty() && s.peek() != '\"')))
      {
        s.push(c);
      } // else if
    else if (close.indexOf(c) >= 0)
      {
        if (s.isEmpty() || (close.indexOf(c) != open.indexOf(s.peek())))
          {
            throw new ParsingException(c + "");
          } // if
        else
          {
            s.pop();
          } // else
      } // else if
  } // trackBrackets(char, Stack<Character>)

  /**
   * Tries to parse str as a JSON string, returns a string if successful
   * 
   * @param str
   *          a String
   * @throws ParsingException
   */
  static public String parseString(String str)
    throws ParsingException
  {
    try
      {
        if (str.charAt(0) == '\"' && str.charAt(str.length() - 1) == '\"')
          {
            str = str.substring(1, str.length() - 1);
            String[] sequence =
                { "\\\"", "\\\\", "\\/", "\\b", "\\f", "\\n", "\\r", "\\t" };
            String[] replacedBy =
                { "\"", "\\", "/", "\b", "\f", "\n", "\r", "\t" };
            for (int i = 0; i < replacedBy.length; i++)
              {
                str = str.replace(sequence[i], replacedBy[i]);
              } // for
            return str;
          } // if
        throw new ParsingException(str);
      } // try
    catch (Exception e)
      {
        throw new ParsingException(str);
      } // catch
  } // parseString(String)

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
      } // if
    else if (obj instanceof HashMap)
      {
        return objectToJSONString((HashMap<String, Object>) obj);
      } // else if
    else if (obj == null)
      {
        return "null";
      } // else if
    else if (obj instanceof String)
      {
        return stringToJSONString((String) obj);
      } // else if
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
   */
  public static String stringToJSONString(String str)
  {
    String[] replacedBy =
        { "\\\\", "\\\"", "\\/", "\\b", "\\f", "\\n", "\\r", "\\t" };
    String[] sequence = { "\\", "\"", "/", "\b", "\f", "\n", "\r", "\t" };
    for (int i = 0; i < replacedBy.length; i++)
      {
        str = str.replace(sequence[i], replacedBy[i]);
      } // for
    return "\"" + str + "\"";
  } // stringToJSONString(String)

  /**
   * From an ArrayList generated by parse, computes a corresponding JSON String
   * 
   * @param arr
   *          an ArrayList
   */
  static public String arrayToJSONString(ArrayList<Object> arr)
  {
    String result = "[";
    for (Object o : arr)
      {
        result += toJSONString(o) + ",";
      } // for
    if (result.length() > 1)
      result = result.substring(0, result.length() - 1);
    result += "]";
    return result;
  } // arrayToJSONString(ArrayList<Object>)

  /**
   * From an object (HashMap) generated by parse, computes a corresponding JSON
   * String
   * 
   * @param map
   *          a HashMap
   */
  static public String objectToJSONString(HashMap<String, Object> map)
  {
    String result = "{";
    for (Map.Entry<String, Object> entry : map.entrySet())
      {
        result +=
            toJSONString(entry.getKey()) + ":" + toJSONString(entry.getValue())
                + ",";
      } // for
    if (result.length() > 1)
      result = result.substring(0, result.length() - 1);
    result += "}";
    return result;
  } // objectToJSONString(HashMap<String, Object>)

  // +----------------+----------------------------------------------------------
  // | Helper Classes |
  // +----------------+

  /**
   * Helper class to recognize and handle parsing exceptions. Under
   * construction.
   * 
   * @author Giang
   * 
   */
  @SuppressWarnings("serial")
  static class ParsingException
      extends
        Exception
  {
    /**
     * Reports where error is
     */
    String location;

    public ParsingException(String location)
    {
      this.location = location;
    } // ParsingException(String)
    // STUB
  } // class ParsingException
} // class JSON
