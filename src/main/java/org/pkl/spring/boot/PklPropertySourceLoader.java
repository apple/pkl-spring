/**
 * Copyright © 2024 Apple Inc. and the Pkl project authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pkl.spring.boot;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.pkl.core.*;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

public class PklPropertySourceLoader implements PropertySourceLoader {
  @Override
  public String[] getFileExtensions() {
    return new String[] {"pkl", "pcf"};
  }

  @Override
  public List<PropertySource<?>> load(String propertySourceName, Resource resource)
      throws IOException {
    var text = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

    PModule module;
    try (var evaluator = EvaluatorBuilder.preconfigured().build()) {
      module = evaluator.evaluate(ModuleSource.create(resource.getURI(), text));
    }

    var result = new LinkedHashMap<String, Object>();
    module.getProperties().forEach((name, value) -> flatten(name, value, result));
    return List.of(new MapPropertySource(propertySourceName, result));
  }

  private static void flatten(
      String propertyName, Object propertyValue, Map<String, Object> result) {
    if (propertyValue instanceof Composite) {
      flatten(propertyName, ((Composite) propertyValue).getProperties(), result);
    } else if (propertyValue instanceof Map<?, ?>) {
      var map = (Map<?, ?>) propertyValue;
      if (map.isEmpty()) {
        result.put(propertyName, Collections.emptyMap());
      } else {
        map.forEach((name, value) -> flatten(propertyName + '.' + name, value, result));
      }
    } else if (propertyValue instanceof Collection) {
      var collection = (Collection<?>) propertyValue;
      if (collection.isEmpty()) {
        result.put(
            propertyName,
            propertyValue instanceof Set ? Collections.emptySet() : Collections.emptyList());
      } else {
        var index = 0;
        for (var element : collection) {
          flatten(propertyName + '[' + index++ + ']', element, result);
        }
      }
    } else {
      result.put(propertyName, propertyValue);
    }
  }
}
