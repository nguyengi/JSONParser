package edu.grinnell.nguyengi.json;

import java.io.BufferedReader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Tools for playing with JSON.  Right now, there is only minor
 * error checking and whitespace is not supported.  Constants have
 * to be in all lowercase.
 */
public class JSON3
{
  // +-----------+---------------------------------------------------------
  // | Utilities |
  // +-----------+

  /**
   * Peek at a character in a buffered reader.  Why isn't this
   * implemented as part of BufferedReader?
   */
  public static int peek(BufferedReader br)
    throws Exception
  {
    br.mark(1);
    int ch = br.read();
    br.reset();
    return ch;
  } // peek(BufferedReader)

  /**
   * Read digits until you run out of digits and append them to a
   * string.
   */
  public static void readDigits(BufferedReader br, StringBuilder str)
    throws Exception
  {
    int ch;
    while (Character.isDigit(ch = peek(br)))
      {
        str.append((char) br.read());
      } // while
  } // readDigits(BufferedReader, StringBuilder)

  // +---------+-----------------------------------------------------------
  // | Parsing |
  // +---------+

  /**
   * Read and parse JSON, read from a BufferedReader.
   */
  public static Object parse(BufferedReader json)
    throws Exception
  {
    switch (peek(json))
      {
        case '"':
          return parseString(json);
        case '[':
          return parseArray(json);
        case '{':
          return parseObject(json);
        case '-':
          return parseNumber(json);
        default:
          if (Character.isDigit(peek(json)))
            {
              return parseNumber(json);
            } // if it's a digit
          else
            {
              return parseConstant(json);
            } // if it's not a string or array or object or number
      } // switch
  } // parse(BufferedReader)

  /**
   * Parse a string.
   */
  public static Object parse(String str)
    throws Exception
  {
    return parse(new BufferedReader(new StringReader(str)));
  } // parse(String)

  // +-----------------+---------------------------------------------------
  // | Parsing Helpers |
  // +-----------------+

  /**
   * Read and parse a JSON array.
   */
  static ArrayList<Object> parseArray(BufferedReader json)
    throws Exception
  {
    int ch;
    ArrayList<Object> result = new ArrayList<Object>();
    if (json.read() != '[')
      throw new Exception("Not an array.");
    while ((ch = peek(json)) != ']')
      {
        result.add(parse(json));
        // MAKE ME MORE ELEGANT
        if (((ch = peek(json)) != ',') && (ch != ']'))
          throw new Exception("Invalid character in array: " + ch);
        if (ch == ',')
          json.read();
      } // while
    // Consume the right brace
    json.read();
    return result;
  } // parseArray

  /**
   * Read and parse a JSON constant.
   */
  static Object parseConstant(BufferedReader json)
    throws Exception
  {
    int ch = peek(json);
    switch (ch)
      {
        case 'n':
          if ((json.read() == 'n') && (json.read() == 'u')
              && (json.read() == 'l') && (json.read() == 'l'))
            return null;
          else
            throw new Exception("Invalid constant starting with n.");
        case 't':
          if ((json.read() == 't') && (json.read() == 'r')
              && (json.read() == 'u') && (json.read() == 'e'))
            return Boolean.TRUE;
          else
            throw new Exception("Invalid constant starting with t.");
        case 'f':
          if ((json.read() == 'f') && (json.read() == 'a')
              && (json.read() == 'l') && (json.read() == 's')
              && (json.read() == 'e'))
            return Boolean.FALSE;
          else
            throw new Exception("Invalid constant starting with f.");
        default:
          throw new Exception("Invalid constant");
      } // swith
  } // parseConstant(BufferedReader)

  /**
   * Read and parse a JSON number.
   */
  static Number parseNumber(BufferedReader json)
    throws Exception
  {
    int ch;
    boolean hasDot = false;
    boolean hasExponent = false;
    StringBuilder result = new StringBuilder();

    // Handle intro minus (no intro plus supported).
    if (peek(json) == '-')
      {
        result.append((char) json.read());
      } // if (peek == '-')

    // Read initial digits
    readDigits(json, result);

    // We can have a period then more digits.
    if (peek(json) == '.')
      {
        hasDot = true;
        result.append((char) json.read());
        readDigits(json, result);
      } // if it's a period

    // Read the exponent.
    if (((ch = peek(json)) == 'e') || (ch == 'E'))
      {
        hasExponent = true;
        result.append((char) json.read());
        // Read the optional sign
        if (((ch = peek(json)) == '+') || (ch == '-'))
          {
            result.append((char) json.read());
          } // if
        // Read the remaining digits.
        readDigits(json, result);
      } // if it's an exponent
    // Convert to the appropriate type.
    if (hasDot || hasExponent)
      return new Double(result.toString());
    else
      return new Double(result.toString());
  } // parseNumber

  /**
   * Read and parse a JSON object.
   */
  static HashMap<String, Object> parseObject(BufferedReader json)
    throws Exception
  {
    int ch;
    HashMap<String, Object> result = new HashMap<String, Object>();
    if (json.read() != '{')
      throw new Exception("Not an object.");
    while ((ch = peek(json)) != '}')
      {
        String key = parseString(json);
        if ((ch = json.read()) != ':')
          throw new Exception("Missing colon in object.");
        Object value = parse(json);
        result.put(key, value);
        // MAKE ME MORE ELEGANT
        if (((ch = peek(json)) != ',') && (ch != '}'))
          throw new Exception("Invalid character in array: " + ch);
        if (ch == ',')
          json.read();
      } // while
    // Consume the right brace
    json.read();
    return result;
  } // parseObject(BufferedReader)

  /**
   * Read and parse a JSON string.
   */
  static String parseString(BufferedReader json)
    throws Exception
  {
    int ch;
    StringBuilder result = new StringBuilder();
    if (json.read() != '"')
      throw new Exception("Strings must start with double quotation marks.");
    while ((ch = json.read()) != '"')
      {
        if (ch == -1)
          throw new Exception("Ran out of input before hitting end of string.");
        // Special case: Backslash
        if (ch == '\\')
          {
            ch = json.read();
            switch (ch)
              {
                case '"':
                case '\\':
                case '/':
                  result.append((char) ch);
                  break;
                case 'b':
                  result.append('\b');
                  break;
                case 'f':
                  result.append('\f');
                  break;
                case 'n':
                  result.append('\n');
                  break;
                case 'r':
                  result.append('\r');
                  break;
                case 't':
                  result.append('\t');
                  break;
                case 'u':
                  throw new Exception("Unicode unsupported");
                case -1:
                  throw new Exception("Ran out of input");
                default:
                  throw new Exception("Invalid backslash character: \\" + ch);
              } // switch
          } // if it's a backslash
        // Normal case
        else
          {
            result.append((char) ch);
          } // if it's something else
      } // while
    return result.toString();
  } // parseString(BufferedReader)

  // +-----------------------+---------------------------------------------
  // | Converting to Strings |
  // +-----------------------+

  /**
   * Convert an object created by parse back to a JSON string.
   * Does not verify that the object was created by parse.
   */
  public static String toString(Object o)
  {
    // Constants
    if (o == null)
      return "null";
    else if (Boolean.TRUE.equals(o))
      return "true";
    else if (Boolean.FALSE.equals(o))
      return "false";
    // Strings
    else if (o instanceof String)
      return toString((String) o);
    // Arrays
    else if (o instanceof ArrayList)
      return toString((ArrayList) o);
    // Objects
    else if (o instanceof HashMap)
      return toString((HashMap) o);
    // Constants
    else
      return o.toString();
  } // toString

  // +--------------------+------------------------------------------------
  // | Conversion Helpers |
  // +--------------------+

  /**
   * Convert an ArrayList (representing a JSON array) to a JSON string.
   */
  static String toString(ArrayList lst)
  {
    StringBuilder result = new StringBuilder();
    boolean first = true; // Hack!
    result.append("[");
    for (Object obj : lst)
      {
        if (!first)
          result.append(",");
        else
          first = false;
        result.append(toString(obj));
      } //  for
    result.append("]");
    return result.toString();
  } // toString(ArrayList)

  /**
   * Convert a HashMap (representing a JSON object) to a JSON string.
   */
  static String toString(HashMap map)
  {
    StringBuilder result = new StringBuilder();
    boolean first = true; // Hack!
    result.append("{");
    for (Object key : map.keySet())
      {
        if (!first)
          result.append(",");
        else
          first = false;
        result.append(toString(key));
        result.append(":");
        result.append(toString(map.get(key)));
      } //  for
    result.append("}");
    return result.toString();
  } // toString(HashMap)

  /**
   * Convert a string back to a JSON description of the string.
   */
  static String toString(String str)
  {
    StringBuilder result = new StringBuilder();
    result.append('"');
    char ch;
    int len = str.length();
    for (int i = 0; i < len; i++)
      {
        switch (ch = str.charAt(i))
          {
            case '\b':
              result.append("\\b");
              break;
            case '\f':
              result.append("\\f");
              break;
            case '\n':
              result.append("\\n");
              break;
            case '\r':
              result.append("\\r");
              break;
            case '\t':
              result.append("\\t");
              break;
            case '\\':
            case '\"':
            case '/':
              result.append('\\');
              result.append(ch);
              break;
            default:
              result.append(ch);
          } // switch
      } // for
    result.append('"');
    return result.toString();
  } // toString(String)

} // class JSON