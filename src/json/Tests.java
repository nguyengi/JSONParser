package json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import json.JSON.ParsingException;
/**
 * Some tests using specific inputs
 * 
 * @author Giang
 *
 */
public class Tests
{
  public static void main(String[] args)
    throws ParsingException,
      Exception
  {
    BufferedReader eyes =
        new BufferedReader(new FileReader(new File("JSONsamples.txt")));
    String test = null;
    while ((test = eyes.readLine()) != null)
      {
        System.out.println("test: " + test);
        System.out.println(JSON.toJSONString(JSON.parse(test)));
        System.out.println(JSON.toJSONString(JSON.parse(test)).equals(JSON.toJSONString(JSON.parse(JSON.toJSONString(JSON.parse(test))))));
      } // while
    eyes.close();
    System.out.println(JSON.parse(JSON.toJSONString("\n\t\t\b\f\r")));
  } // main (String[])
} // class Tests
