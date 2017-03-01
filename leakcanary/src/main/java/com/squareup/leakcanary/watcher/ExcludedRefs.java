/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.leakcanary.watcher;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Prevents specific references from being taken into account when computing the shortest strong
 * reference path from a suspected leaking instance to the GC roots.
 *
 * This class lets you ignore known memory leaks that you known about. If the shortest path
 * matches {@link ExcludedRefs}, than the heap analyzer should look for a longer path with nothing
 * matching in {@link ExcludedRefs}.
 */
public final class ExcludedRefs implements Serializable {

  public final Map<String, Set<String>> excludeFieldMap;
  public final Map<String, Set<String>> excludeStaticFieldMap;
  public final Set<String> excludedThreads;

  private ExcludedRefs(Map<String, Set<String>> excludeFieldMap,
      Map<String, Set<String>> excludeStaticFieldMap, Set<String> excludedThreads) {
    // Copy + unmodifiable.
    this.excludeFieldMap = unmodifiableMap(new LinkedHashMap<String, Set<String>>(excludeFieldMap));
    this.excludeStaticFieldMap = unmodifiableMap(new LinkedHashMap<String, Set<String>>(excludeStaticFieldMap));
    this.excludedThreads = unmodifiableSet(new LinkedHashSet<String>(excludedThreads));
  }

  public static final class Builder {
    private final Map<String, Set<String>> excludeFieldMap = new LinkedHashMap<String, Set<String>>();
    private final Map<String, Set<String>> excludeStaticFieldMap = new LinkedHashMap<String, Set<String>>();
    private final Set<String> excludedThreads = new LinkedHashSet<String>();

    public Builder instanceField(String className, String fieldName) {
    	Preconditions.checkNotNull(className, "className");
      Preconditions.checkNotNull(fieldName, "fieldName");
      Set<String> excludedFields = excludeFieldMap.get(className);
      if (excludedFields == null) {
        excludedFields = new LinkedHashSet<String>();
        excludeFieldMap.put(className, excludedFields);
      }
      excludedFields.add(fieldName);
      return this;
    }

    public Builder staticField(String className, String fieldName) {
    	Preconditions.checkNotNull(className, "className");
    	Preconditions.checkNotNull(fieldName, "fieldName");
      Set<String> excludedFields = excludeStaticFieldMap.get(className);
      if (excludedFields == null) {
        excludedFields = new LinkedHashSet<String>();
        excludeStaticFieldMap.put(className, excludedFields);
      }
      excludedFields.add(fieldName);
      return this;
    }

    public Builder thread(String threadName) {
    	Preconditions.checkNotNull(threadName, "threadName");
      excludedThreads.add(threadName);
      return this;
    }

    public ExcludedRefs build() {
      return new ExcludedRefs(excludeFieldMap, excludeStaticFieldMap, excludedThreads);
    }
  }
}
