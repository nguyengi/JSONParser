JSONParser
==========

JSONParser is a Java library designed to deal with JSON and JSON values. For 
more details about JSON, please see www.json.org.

Use
---

The JSONUtils class contains all of the library's utility methods, most 
importantly parse(String) and toJSONString(Object). To use the methods, simply 
call, for example, JSONUtils.parse ("str"), as all methods are static. When 
invoking parse, know that parse will return a HashMap<String, Object> when it 
is given a string representing a JSON object, an ArrayList<Object> for a JSON 
array, a String for a JSON string, a Number for a JSON number, and equivalent 
values for true, false, and null. All these values (except for null) are boxed 
as Java Objects. In addition, two methods, parseFromFile(String) and 
removeWhiteSpace(String), have been added to demonstrate some uses for the 
library's features and make the use of the library easier.

Additional details are available in the Documentation. Examples of use cases 
are available in Tests.java.

Standard Compliance
-------------------

JSONParser supports JSON representation of Strings, Numbers, and true, false, 
and null, not just Objects and Arrays. The library also allows white spaces and 
Unicode characters (expressed in JSON as \uXXXX).

JSONUtilsTest.java provides some randomized testing should the implementation 
need to be changed.

