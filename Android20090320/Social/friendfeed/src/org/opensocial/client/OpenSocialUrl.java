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

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * An object which represents a simple URL. Once an object is instantiated,
 * path components and parameters can be added. Once all of the elements
 * are in place, the object can be serialized into a string. This class
 * is used by internal classes and not by clients directly.
 *
 * @author Jason Cooper
 */
public class OpenSocialUrl {

  private String base;
  private List<String> components;
  private Map<String, String> queryStringParameters;

  public OpenSocialUrl(String base) {
    this.base = base;
    this.components = new Vector<String>();
    this.queryStringParameters = new HashMap<String, String>();
  }

  /**
   * Adds passed String to the path component queue.
   * 
   * @param  component Path component to add
   */
  public void addPathComponent(String component) {
    components.add(component);
  }

  /**
   * Creates a new entry in queryStringParameters Map with the passed key and
   * value; used for adding URL parameters such as oauth_signature and the
   * various other OAuth parameters that are required in order to submit a
   * signed request.
   * 
   * @param  key Parameter name
   * @param  value Parameter value
   */
  public void addQueryStringParameter(String key, String value) {
    queryStringParameters.put(key, value);
  }

  /**
   * Returns a String representing the serialized URL including the base
   * followed by any path components added to the path component queue
   * and, last but not least, appending any query string parameters as
   * name-value pairs after the full path.
   */
  public String toString() {
    StringBuilder s = new StringBuilder(this.base);

    for (String pathComponent : this.components) {
      if (s.charAt(s.length() - 1) != '/') {
        s.append("/");
      }
      s.append(pathComponent);
    }

    String connector = "?";
    for (Map.Entry<String, String> e : this.queryStringParameters.entrySet()) {
      s.append(connector);
      s.append(e.getKey());
      s.append("=");
      s.append(e.getValue());
      connector = "&";
    }

    return s.toString();
  }
  
  public URI toUri() throws java.net.URISyntaxException {
    return new URI(this.toString());
  }
}
