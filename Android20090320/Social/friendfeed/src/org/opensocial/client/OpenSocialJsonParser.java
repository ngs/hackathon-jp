/* Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.opensocial.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensocial.data.OpenSocialActivity;
import org.opensocial.data.OpenSocialAppData;
import org.opensocial.data.OpenSocialField;
import org.opensocial.data.OpenSocialObject;
import org.opensocial.data.OpenSocialPerson;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * An object which exposes a number of static methods for parsing JSON strings
 * returned from RESTful or JSON-RPC requests into appropriate objects.
 *
 * @author Jason Cooper
 */
public class OpenSocialJsonParser {

  /**
   * Parses the passed JSON string into an OpenSocialResponse object -- if the
   * passed string represents a JSON array, each object in the array is added
   * to the returned object keyed on its "id" property.
   *
   * @param  in The complete JSON string returned from an OpenSocial container
   *            in response to a request for data
   */
  public static OpenSocialResponse getResponse(String in)
      throws OpenSocialRequestException {

    if (!isJsonArray(in)) {
      if (isJsonObject(in)) {
        JSONObject errorObject = null;

        try {
          errorObject = new JSONObject(in);
        } catch (JSONException e) {
          throw new OpenSocialRequestException(
              "Invalid JSON object string " + in);
        }

        String errorCode = null;
        String errorMessage = null;

        try {
          errorCode = errorObject.getString("code");
          errorMessage = errorObject.getString("message");
        } catch (JSONException e) {
          // Ignore exception thrown if code and/or message keys aren't found
        }
        
        if (errorCode != null && errorMessage != null) {
          throw new OpenSocialRequestException(
              "Container returned error " + errorCode + " " + errorMessage);          
        } else {
          throw new OpenSocialRequestException(
              "Container returned error response " + in);
        }
      }
      
      return null;
    }

    JSONArray responseArray = null;

    try {
      responseArray = new JSONArray(in);
    } catch (JSONException e) {
      throw new OpenSocialRequestException(
          "Invalid JSON array string " + in);
    }

    OpenSocialResponse r = new OpenSocialResponse();

    for (int i = 0; i < responseArray.length(); i++) {
      try {
        JSONObject o = responseArray.getJSONObject(i);

        if (o.has("id")) {
          String id = o.getString("id");
          r.addItem(id, escape(o.toString()));
        }
      } catch (JSONException e) {
        // Ignore exception thrown if object or ID string can't be parsed
        continue;
      }
    }

    return r;
  }

  /**
   * Parses the passed JSON string into an OpenSocialResponse object -- if the
   * passed string represents a JSON object, it is added to the returned
   * object keyed on the passed ID.
   *
   * @param  in The complete JSON string returned from an OpenSocial container
   *            in response to a request for data
   * @param  id The string ID to tag the JSON object string with as it is added
   *            to the OpenSocialResponse object
   */
  public static OpenSocialResponse getResponse(String in, String id)
      throws OpenSocialRequestException {

    OpenSocialResponse r = null;

    if (isJsonObject(in)) {
      r = new OpenSocialResponse();
      r.addItem(id, escape(in));
    } else if (isJsonArray(in)) {
      return getResponse(in);
    }

    return r;
  }

  /**
   * Transforms a raw JSON object string containing profile information for a
   * single user into an OpenSocialPerson instance with all profile details
   * abstracted as OpenSocialField objects associated with the instance.
   *
   * @param  in The JSON object string to parse as an OpenSocialPerson object
   * @throws OpenSocialRequestException
   */
  public static OpenSocialPerson parseAsPerson(String in)
      throws OpenSocialRequestException {

    if (in == null) {
      throw new OpenSocialRequestException(
          "Response item with given key not found");
    }

    JSONObject root = null;

    try {
      root = new JSONObject(in);
    } catch (JSONException e) {
      throw new OpenSocialRequestException(
          "Invalid JSON object string " + in);
    }

    JSONObject entry = getEntryObject(root);

    return (OpenSocialPerson) parseAsObject(entry, OpenSocialPerson.class);
  }

  /**
   * Transforms a raw JSON object string containing profile information for a
   * group of users into a list of OpenSocialPerson instances with all profile
   * details abstracted as OpenSocialField objects associated with the
   * instances. These instances are then added to a Java List which
   * gets returned.
   *
   * @param  in The JSON object string to parse as a List of OpenSocialPerson
   *         objects
   * @throws OpenSocialRequestException
   */
  public static List<OpenSocialPerson> parseAsPersonCollection(String in)
      throws OpenSocialRequestException {

    if (in == null) {
      throw new OpenSocialRequestException(
          "Response item with given key not found");
    }

    JSONObject root = null;

    try {
      root = new JSONObject(in);
    } catch (JSONException e) {
      throw new OpenSocialRequestException(
          "Invalid JSON object string " + in);
    }

    JSONArray entries = getEntryArray(root);
    List<OpenSocialPerson> l = new Vector<OpenSocialPerson>(entries.length());

    for (int i = 0; i < entries.length(); i++) {
      try {
        JSONObject entry = entries.getJSONObject(i);
        l.add((OpenSocialPerson) parseAsObject(entry, OpenSocialPerson.class));
      } catch (JSONException e) {
        // Ignore exception thrown if object can't be parsed
        continue;
      }
    }

    return l;
  }

  /**
   * Transforms a raw JSON object string containing key-value pairs (i.e. App
   * Data) for one or more users into a specialized OpenSocialObject instance
   * with each key-value pair abstracted as OpenSocialField objects associated
   * with the instance.
   *
   * @param  in The JSON object string to parse as an OpenSocialAppData object
   * @throws OpenSocialRequestException
   */
  public static OpenSocialAppData parseAsAppData(String in)
      throws OpenSocialRequestException {

    if (in == null) {
      throw new OpenSocialRequestException(
          "Response item with given key not found");
    }

    JSONObject root = null;

    try {
      root = new JSONObject(in);
    } catch (JSONException e) {
      throw new OpenSocialRequestException(
          "Invalid JSON object string " + in);
    }

    JSONObject entry = getEntryObject(root);

    return (OpenSocialAppData) parseAsObject(entry, OpenSocialAppData.class);
  }
  
  /**
   * Method to parse the input JSON string, build OpenSocialActivity Objects with
   * the entry values and return a list of OpenSocialActivity objects
   * 
   * @param in
   * @return
   * @throws OpenSocialRequestException
   */
  public static List<OpenSocialActivity> parseAsActivityCollection(String in) 
    throws OpenSocialRequestException {

    if (in == null) {
      throw new OpenSocialRequestException(
        "Response item with given key not found");
    }

    JSONObject root = null;

    try {
      root = new JSONObject(in);
    } catch (JSONException e) {
      throw new OpenSocialRequestException(
          "Invalid JSON object string " + in);
    }

    JSONArray entries = getEntryArray(root);
    List<OpenSocialActivity> l = new Vector<OpenSocialActivity>(entries.length());

    for (int i = 0; i < entries.length(); i++) {
      try {
        JSONObject entry = entries.getJSONObject(i);
        l.add((OpenSocialActivity) parseAsObject(entry, OpenSocialActivity.class));
      } catch (JSONException e) {
        // Ignore exception thrown if object can't be parsed
        continue;
      }
    }

    return l;
  }

  /**
   * Inspects the passed object for one of several specific properties and, if
   * found, returns that property as a JSONArray object. All valid response
   * objects which contain a data collection (e.g. a collection of people)
   * must have this property.
   *
   * @param  root JSONObject to query for the presence of the specific property
   * @throws OpenSocialRequestException if property is not found in the passed
   *         object
   */
  private static JSONArray getEntryArray(JSONObject root)
      throws OpenSocialRequestException {

    String property = "";

    try {
      if (root.has("entry")) {
        property = "entry";
        return root.getJSONArray("entry");
      } else if (root.has("data")) {
        property = "list";
        return root.getJSONObject("data").getJSONArray("list");
      }
    } catch (JSONException e) {
      throw new OpenSocialRequestException(
          "\"" + property + "\" property could not be parsed as a JSON array in " +
          root.toString());
    }

    throw new OpenSocialRequestException(
        "No \"entry\" or \"list\" property in " + root.toString());
  }

  /**
   * Inspects the passed object for one of several specific properties and, if
   * found, returns that property as a JSONObject object. All valid response
   * objects which encapsulate a single data item (e.g. a person) must have
   * this property.
   *
   * @param  root JSONObject to query for the presence of the specific property
   * @throws OpenSocialRequestException if property is not found in the passed
   *         object
   */
  private static JSONObject getEntryObject(JSONObject root)
      throws OpenSocialRequestException {

    String property = "";

    try {
      if (root.has("data")) {
        property = "data";
        return root.getJSONObject(property);
      } else if (root.has("entry")) {
        property = "entry";
        return root.getJSONObject(property);
      }
    } catch (JSONException e) {
      throw new OpenSocialRequestException(
          "\"" + property + "\" property could not be parsed as a JSON object in " +
          root.toString());
    }

    throw new OpenSocialRequestException(
        "No \"entry\" or \"data\" property in " + root.toString());
  }

  /**
   * Calls a function to recursively iterates through the the properties of the
   * passed JSONObject object and returns an equivalent OpenSocialObject with
   * each property of the original object mapped to fields in the returned
   * object.
   *
   * @param  entryObject Object-oriented representation of JSON response
   *         string which is transformed into and returned as an
   *         OpenSocialObject
   * @param  clientClass Class of object to return, either OpenSocialObject
   *         or a subclass
   */
  private static OpenSocialObject parseAsObject(
      JSONObject entryObject, Class<? extends OpenSocialObject> clientClass) {

    OpenSocialObject o = null;
    try {
      o = clientClass.newInstance();
    } catch (InstantiationException e) {
      throw new RuntimeException("Class not found " + clientClass);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Class not accessible " + clientClass);
    }

    Map<String, OpenSocialField> entryRepresentation =
        createObjectRepresentation(entryObject);

    for (Map.Entry<String,OpenSocialField> e : entryRepresentation.entrySet()) {
      o.setField(e.getKey(), e.getValue());
    }

    return o;
  }

  /**
   * Recursively iterates through the properties of the passed JSONObject
   * object and returns a Map of OpenSocialField objects keyed on Strings
   * representing the property values and names respectively.
   *
   * @param  o Object-oriented representation of a JSON object which is
   *         transformed into and returned as a Map of OpenSocialField
   *         objects keyed on Strings
   */
  private static Map<String, OpenSocialField> createObjectRepresentation(JSONObject o) {
    HashMap<String, OpenSocialField> r = new HashMap<String, OpenSocialField>();

    Iterator<?> keys = o.keys();

    while (keys.hasNext()) {
      String key = (String) keys.next();
      String property;
      try {
        property = o.getString(key);
      } catch (JSONException e) {
        // If we can't get the object at i, ignore it
        continue;
      }

      if (property.length() < 0) {
        continue;
      }

      if (isJsonObject(property)) {
        try {
          OpenSocialField field = new OpenSocialField(true);
          field.addValue(new OpenSocialObject(createObjectRepresentation(o.getJSONObject(key))));
          r.put(key, field);
          continue;
        } catch (JSONException e) {
          // If this isn't an object, try to parse it as something else
        }
      }

      if (isJsonArray(property)) {
        try {
          JSONArray p = o.getJSONArray(key);
          Collection<Object> values = createArrayRepresentation(p);
          OpenSocialField field = new OpenSocialField(true);

          for (Object v : values) {
            field.addValue(v);
          }

          r.put(key, field);
          continue;
        } catch (JSONException e) {
          // If this isn't an array, try to parse it as something else
        }
      }

      // As a last resort, add in the value as a string
      OpenSocialField field = new OpenSocialField(false);
      field.addValue(unescape(property));
      r.put(key, field);
    }

    return r;
  }

  /**
   * Iterates through the objects in the passed JSONArray object, recursively
   * transforms each as needed, and returns a List of Java objects.
   *
   * @param  a Object-oriented representation of a JSON array which is iterated
   *         through and returned as a List of Java objects
   */
  private static List<Object> createArrayRepresentation(JSONArray a) {
    Vector<Object> r = new Vector<Object>(a.length());

    for (int i = 0; i < a.length(); i++) {
      String member;
      try {
        member = a.getString(i);
      } catch (JSONException e) {
        // If we can't get the object at i we will just ignore it
        continue;
      }

      if (member.length() < 0) {
        continue;
      }

      if (isJsonObject(member)) {
        try {
          r.add(new OpenSocialObject(createObjectRepresentation(a.getJSONObject(i))));
          continue;
        } catch (JSONException e) {
          // If this isn't an object, try to parse it as something else
        }
      }

      if (isJsonArray(member)) {
        try {
          JSONArray p = a.getJSONArray(i);
          for (Object v : createArrayRepresentation(p)) {
            r.add(v);
          }
          continue;
        } catch (JSONException e) {
          // If this isn't an array, try to parse it as something else
        }
      }

      // As a last resort, add in the value as a string
      r.add(member);
    }

    return r;
  }

  private static boolean isJsonArray(String str) {
    if (str != null && str.length() > 0) {
      return str.charAt(0) == '[';      
    }
    
    return false;
  }

  private static boolean isJsonObject(String str) {
    if (str != null && str.length() > 0) {
      return str.charAt(0) == '{';
    }
    
    return false;
  }

  /**
   * Escapes "{ and }" as "%7B and "%7D respectively to prevent parsing errors
   * when property values begin with { or } tokens.
   *
   * @param  in String to escape
   * @return escaped String
   */
  private static String escape(String in) {
    String out = in;

    out = out.replaceAll("\"\\{", "\"%7B");
    out = out.replaceAll("\\}\"", "%7D\"");

    return out;
  }

  /**
   * Unescapes String objects previously returned from the escape method by
   * substituting { and } for %7B and %7D respectively. Called after
   * parsing to restore property values.
   *
   * @param  in String to unescape
   * @return unescaped String
   */
  private static String unescape(String in) {
    String out = in;

    out = out.replaceAll("%7B", "{");
    out = out.replaceAll("%7D", "}");

    return out;
  }
}
