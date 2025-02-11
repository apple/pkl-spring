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

import java.util.*;
import org.pkl.core.PNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@AutoConfiguration
public class PklAutoConfiguration {
  public PklAutoConfiguration(ConfigurableEnvironment env) {
    // otherwise `Environment.getProperty("pklPropertyWithNullValue")` fails with
    // `ConverterNotFoundException`
    env.getConversionService().addConverter(new PNullConverter());
  }

  @Component
  @SuppressWarnings("unused")
  @ConfigurationPropertiesBinding
  public static class PNullConverter implements GenericConverter {
    @Override
    public @Nullable Set<ConvertiblePair> getConvertibleTypes() {
      return Set.of(new ConvertiblePair(PNull.class, Object.class));
    }

    @Override
    public @Nullable Object convert(
        @Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
      assert source == PNull.getInstance();
      return targetType.getType() == Optional.class ? Optional.empty() : null;
    }
  }
}
