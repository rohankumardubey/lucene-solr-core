/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.lucene.analysis.reverse;

import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;

public class TestReverseStringFilter extends BaseTokenStreamTestCase {
  public void testFilter() throws Exception {
    TokenStream stream = new WhitespaceTokenizer(TEST_VERSION_CURRENT, 
        new StringReader("Do have a nice day"));     // 1-4 length string
    ReverseStringFilter filter = new ReverseStringFilter(TEST_VERSION_CURRENT, stream);
    assertTokenStreamContents(filter, new String[] { "oD", "evah", "a", "ecin", "yad" });
  }
  
  public void testFilterWithMark() throws Exception {
    TokenStream stream = new WhitespaceTokenizer(TEST_VERSION_CURRENT, new StringReader(
        "Do have a nice day")); // 1-4 length string
    ReverseStringFilter filter = new ReverseStringFilter(TEST_VERSION_CURRENT, stream, '\u0001');
    assertTokenStreamContents(filter, 
        new String[] { "\u0001oD", "\u0001evah", "\u0001a", "\u0001ecin", "\u0001yad" });
  }

  public void testReverseString() throws Exception {
    assertEquals( "A", ReverseStringFilter.reverse(TEST_VERSION_CURRENT, "A" ) );
    assertEquals( "BA", ReverseStringFilter.reverse(TEST_VERSION_CURRENT, "AB" ) );
    assertEquals( "CBA", ReverseStringFilter.reverse(TEST_VERSION_CURRENT, "ABC" ) );
  }
  
  public void testReverseChar() throws Exception {
    char[] buffer = { 'A', 'B', 'C', 'D', 'E', 'F' };
    ReverseStringFilter.reverse(TEST_VERSION_CURRENT, buffer, 2, 3 );
    assertEquals( "ABEDCF", new String( buffer ) );
  }
  
  /**
   * Test the broken 3.0 behavior, for back compat
   */
  public void testBackCompat() throws Exception {
    assertEquals("\uDF05\uD866\uDF05\uD866", ReverseStringFilter.reverse("????????"));
  }
  
  public void testReverseSupplementary() throws Exception {
    // supplementary at end
    assertEquals("???????????????????", ReverseStringFilter.reverse(TEST_VERSION_CURRENT, "???????????????????"));
    // supplementary at end - 1
    assertEquals("a???????????????????", ReverseStringFilter.reverse(TEST_VERSION_CURRENT, "???????????????????a"));
    // supplementary at start
    assertEquals("fedcba????", ReverseStringFilter.reverse(TEST_VERSION_CURRENT, "????abcdef"));
    // supplementary at start + 1
    assertEquals("fedcba????z", ReverseStringFilter.reverse(TEST_VERSION_CURRENT, "z????abcdef"));
    // supplementary medial
    assertEquals("gfe????dcba", ReverseStringFilter.reverse(TEST_VERSION_CURRENT, "abcd????efg"));
  }

  public void testReverseSupplementaryChar() throws Exception {
    // supplementary at end
    char[] buffer = "abc???????????????????".toCharArray();
    ReverseStringFilter.reverse(TEST_VERSION_CURRENT, buffer, 3, 7);
    assertEquals("abc???????????????????", new String(buffer));
    // supplementary at end - 1
    buffer = "abc???????????????????d".toCharArray();
    ReverseStringFilter.reverse(TEST_VERSION_CURRENT, buffer, 3, 8);
    assertEquals("abcd???????????????????", new String(buffer));
    // supplementary at start
    buffer = "abc???????????????????".toCharArray();
    ReverseStringFilter.reverse(TEST_VERSION_CURRENT, buffer, 3, 7);
    assertEquals("abc???????????????????", new String(buffer));
    // supplementary at start + 1
    buffer = "abcd???????????????????".toCharArray();
    ReverseStringFilter.reverse(TEST_VERSION_CURRENT, buffer, 3, 8);
    assertEquals("abc???????????????????d", new String(buffer));
    // supplementary medial
    buffer = "abc??????????def".toCharArray();
    ReverseStringFilter.reverse(TEST_VERSION_CURRENT, buffer, 3, 7);
    assertEquals("abcfed??????????", new String(buffer));
  }
}
