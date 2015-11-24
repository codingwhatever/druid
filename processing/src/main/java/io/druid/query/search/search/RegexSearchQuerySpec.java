/*
 * Druid - a distributed column store.
 * Copyright 2012 - 2015 Metamarkets Group Inc.
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

package io.druid.query.search.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.metamx.common.StringUtils;

import java.nio.ByteBuffer;
import java.util.regex.Pattern;

/**
 */
public class RegexSearchQuerySpec implements SearchQuerySpec
{

  private static final byte CACHE_TYPE_ID = 0x3;

  private final String pattern;
  private Pattern compiled;

  @JsonCreator
  public RegexSearchQuerySpec(
      @JsonProperty("pattern") String pattern
  )
  {
    this.pattern = pattern;
    compiled = Pattern.compile(pattern);
  }

  @JsonProperty
  public String getPattern()
  {
    return pattern;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RegexSearchQuerySpec)) {
      return false;
    }

    RegexSearchQuerySpec that = (RegexSearchQuerySpec) o;

    return !(pattern != null ? !pattern.equals(that.pattern) : that.pattern != null);

  }

  @Override
  public int hashCode()
  {
    return pattern != null ? pattern.hashCode() : 0;
  }

  @Override
  public boolean accept(String dimVal)
  {
    if (dimVal == null || pattern == null) {
      return false;
    }

    return compiled.matcher(dimVal).find();
  }

  @Override
  public byte[] getCacheKey()
  {
    if (pattern == null) {
      return ByteBuffer.allocate(1)
                       .put(CACHE_TYPE_ID).array();
    }

    byte[] patternBytes = StringUtils.toUtf8(pattern);

    return ByteBuffer.allocate(1 + patternBytes.length)
                     .put(CACHE_TYPE_ID)
                     .put(patternBytes)
                     .array();
  }

  @Override
  public String toString()
  {
    return "RegexSearchQuerySpec{" +
           "pattern=" + pattern + "}";
  }

}
