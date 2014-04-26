package edu.grinnell.nguyengi.json;

/*
 * Citation:
 * Text of FileSample.txt, FileSample2.txt, FileSample3.txt taken from http://json.org/example.html
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.PrintWriter;

import edu.grinnell.nguyengi.json.JSON.ParsingException;

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
    // Tests methods using specific inputs from a file
    BufferedReader eyes =
        new BufferedReader(new FileReader(new File("JSONsamples.txt")));
    String test = null;
    while ((test = eyes.readLine()) != null)
      {
        System.out.println("test: " + test);
        System.out.println(JSONUtils.parse(test));
        Object o1 = JSONUtils.parse(test);
        Object o2 = JSONUtils.parse(JSONUtils.toJSONString(JSONUtils.parse(test)));
        // Whether the results of repeated parsing still match
        System.out.println(o1.equals(o2));
      } // while
    eyes.close();

    // Removes all unnecessary whitespaces from the file
    JSONUtils.deleteWhitespace("FileSample.txt");

    Object obj2 = JSONUtils.parseFromFile("FileSample2.txt");
    System.out.println(obj2);

    Object obj3 = JSONUtils.parseFromFile("FileSample3.txt");
    System.out.println(obj3);
  } // main (String[])

} // class Tests
