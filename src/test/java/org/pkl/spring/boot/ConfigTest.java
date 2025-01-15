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

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pkl.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ConfigTestApp.class})
public class ConfigTest {
  private static final AppConfig.Person pigeon = new AppConfig.Person("Pigeon", 42, List.of());
  private static final AppConfig.Person parrot = new AppConfig.Person("Parrot", 21, List.of());

  @Autowired
  @SuppressWarnings("unused")
  private Environment environment;

  @Autowired
  @SuppressWarnings("unused")
  private ApplicationContext appContext;

  @Autowired
  @SuppressWarnings("unused")
  private AppConfig appConfig;

  @Test
  public void consumeIntProperty() {
    assertThat(environment.getRequiredProperty("intProp")).isEqualTo("42");
    assertThat(environment.getRequiredProperty("intProp", long.class)).isEqualTo(42L);

    assertThat(environment.getRequiredProperty("int32")).isEqualTo("-42");
    assertThat(environment.getRequiredProperty("int32", int.class)).isEqualTo(-42);
  }

  @Test
  public void consumeFloatProperty() {
    assertThat(environment.getRequiredProperty("floatProp")).isEqualTo("1.23");
    assertThat(environment.getRequiredProperty("floatProp", double.class)).isEqualTo(1.23d);
  }

  @Test
  public void consumeBooleanProperty() {
    assertThat(environment.getRequiredProperty("booleanProp")).isEqualTo("true");
    assertThat(environment.getRequiredProperty("booleanProp", boolean.class)).isTrue();
  }

  @Test
  public void consumeStringProperty() {
    assertThat(environment.getRequiredProperty("string")).isEqualTo("string");
    assertThat(environment.getRequiredProperty("string", String.class)).isEqualTo("string");
  }

  @Test
  public void consumeDurationProperty() {
    assertThatThrownBy(() -> environment.getRequiredProperty("duration"))
        .isInstanceOf(ConverterNotFoundException.class);
    assertThat(environment.getRequiredProperty("duration", Duration.class))
        .isEqualTo(new Duration(3, DurationUnit.HOURS));
  }

  @Test
  public void consumeDataSizeProperty() {
    assertThatThrownBy(() -> environment.getRequiredProperty("dataSize"))
        .isInstanceOf(ConverterNotFoundException.class);
    assertThat(environment.getRequiredProperty("dataSize", DataSize.class))
        .isEqualTo(new DataSize(1.23, DataSizeUnit.GIGABYTES));
  }

  @Test
  public void consumePairProperty() {
    assertThatThrownBy(() -> environment.getRequiredProperty("pair"))
        .isInstanceOf(ConverterNotFoundException.class);
    assertThat(environment.getRequiredProperty("pair", Pair.class))
        .isEqualTo(new Pair<>("hello", true));
  }

  @Test
  public void consumeRegexProperty() {
    // Boot 3 no longer requires `getRequiredType("regex", Pattern.class)`
    assertThat(environment.getRequiredProperty("regex")).isEqualTo("regex");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void consumeNullableIntProperty() {
    assertThat(environment.getRequiredProperty("nullableInt1")).isEqualTo("42");
    assertThat(environment.getRequiredProperty("nullableInt1", long.class)).isEqualTo(42L);
    assertThat(environment.getRequiredProperty("nullableInt1", Optional.class)).contains(42L);

    assertThat(environment.containsProperty("nullableInt2")).isTrue();
    assertThat(environment.getProperty("nullableInt2")).isNull();
    assertThat(environment.getProperty("nullableInt2", Long.class)).isNull();
    assertThat(environment.getProperty("nullableInt2", Optional.class)).isEmpty();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void consumeNullableStringProperty() {
    assertThat(environment.getRequiredProperty("nullableString1")).isEqualTo("string");
    assertThat(environment.getRequiredProperty("nullableString1", String.class))
        .isEqualTo("string");
    assertThat(environment.getRequiredProperty("nullableString1", Optional.class))
        .contains("string");

    assertThat(environment.containsProperty("nullableString2")).isTrue();
    assertThat(environment.getProperty("nullableString2")).isNull();
    assertThat(environment.getProperty("nullableString2", String.class)).isNull();
    assertThat(environment.getProperty("nullableString2", Optional.class)).isEmpty();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void consumeNullableTypedProperties() {
    assertThat(environment.getRequiredProperty("nullablePigeon1.name")).isEqualTo("Pigeon");
    assertThat(environment.getRequiredProperty("nullablePigeon1.name", String.class))
        .isEqualTo("Pigeon");
    assertThat(environment.getRequiredProperty("nullablePigeon1.name", Optional.class))
        .contains("Pigeon");

    assertThat(environment.getRequiredProperty("nullablePigeon1.age")).isEqualTo("42");
    assertThat(environment.getRequiredProperty("nullablePigeon1.age", long.class)).isEqualTo(42L);
    assertThat(environment.getRequiredProperty("nullablePigeon1.age", Optional.class))
        .contains(42L);

    assertThat(environment.containsProperty("nullablePigeon2")).isTrue();
    assertThat(environment.getProperty("nullablePigeon2")).isNull();
    assertThat(environment.getProperty("nullablePigeon2", Optional.class)).isEmpty();
  }

  @Test
  public void consumeNullableListProperties() {
    doConsumeNullableListOrListingProperties("listWithNullElements");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void consumeNullableSetProperties() {
    assertThat(environment.getRequiredProperty("setWithNullElements[0]")).isEqualTo("elem1");
    assertThat(environment.getRequiredProperty("setWithNullElements[0]", String.class))
        .isEqualTo("elem1");
    assertThat(environment.getRequiredProperty("setWithNullElements[0]", Optional.class))
        .contains("elem1");

    assertThat(environment.getRequiredProperty("setWithNullElements[1]")).isEqualTo("elem2");
    assertThat(environment.getRequiredProperty("setWithNullElements[1]", String.class))
        .isEqualTo("elem2");
    assertThat(environment.getRequiredProperty("setWithNullElements[1]", Optional.class))
        .contains("elem2");
  }

  @Test
  public void consumeNullableMapProperties() {
    doConsumeNullableMapOrMappingProperties("mapWithNullElements");
  }

  @Test
  public void consumeNullableListingProperties() {
    doConsumeNullableListOrListingProperties("listingWithNullElements");
  }

  @Test
  public void consumeNullableMappingProperties() {
    doConsumeNullableMapOrMappingProperties("mappingWithNullElements");
  }

  @SuppressWarnings("unchecked")
  private void doConsumeNullableListOrListingProperties(String name) {
    assertThat(environment.getRequiredProperty(name + "[0]")).isEqualTo("elem1");
    assertThat(environment.getRequiredProperty(name + "[0]", String.class)).isEqualTo("elem1");
    assertThat(environment.getRequiredProperty(name + "[0]", Optional.class)).contains("elem1");

    assertThat(environment.getRequiredProperty(name + "[1]")).isEqualTo("elem2");
    assertThat(environment.getRequiredProperty(name + "[1]", String.class)).isEqualTo("elem2");
    assertThat(environment.getRequiredProperty(name + "[1]", Optional.class)).contains("elem2");

    assertThat(environment.containsProperty(name + "[2]")).isFalse();
  }

  @SuppressWarnings("unchecked")
  private void doConsumeNullableMapOrMappingProperties(String name) {
    assertThat(environment.getRequiredProperty(name + ".0")).isEqualTo("elem1");
    assertThat(environment.getRequiredProperty(name + ".0", String.class)).isEqualTo("elem1");
    assertThat(environment.getRequiredProperty(name + ".0", Optional.class)).contains("elem1");

    assertThat(environment.containsProperty(name + ".1")).isTrue();
    assertThat(environment.getProperty(name + ".1")).isNull();
    assertThat(environment.getProperty(name + ".1", String.class)).isNull();
    assertThat(environment.getProperty(name + ".1", Optional.class)).isEmpty();

    assertThat(environment.getRequiredProperty(name + ".2")).isEqualTo("elem2");
    assertThat(environment.getRequiredProperty(name + ".2", String.class)).isEqualTo("elem2");
    assertThat(environment.getRequiredProperty(name + ".2", Optional.class)).contains("elem2");

    assertThat(environment.containsProperty(name + ".3")).isTrue();
    assertThat(environment.getProperty(name + ".3")).isNull();
    assertThat(environment.getProperty(name + ".3", String.class)).isNull();
    assertThat(environment.getProperty(name + ".3", Optional.class)).isEmpty();

    assertThat(environment.containsProperty(name + ".4")).isFalse();
  }

  @Test
  public void consumeEnumProperty() {
    assertThat(environment.getRequiredProperty("size")).isEqualTo("SMALL");
    assertThat(environment.getRequiredProperty("size", AppConfig.Size.class))
        .isEqualTo(AppConfig.Size.SMALL);
  }

  @Test
  public void consumeListProperties() {
    assertThat(environment.getRequiredProperty("simpleList[0]")).isEqualTo("Pigeon");
    assertThat(environment.getRequiredProperty("simpleList[1]")).isEqualTo("Parrot");

    assertThat(environment.getRequiredProperty("simpleEmptyList")).isEqualTo("");
    assertThat(environment.getRequiredProperty("simpleEmptyList", List.class)).isEmpty();

    assertThat(environment.getRequiredProperty("complexList[0].name")).isEqualTo("Pigeon");
    assertThat(environment.getRequiredProperty("complexList[0].age")).isEqualTo("42");
    assertThat(environment.getRequiredProperty("complexList[1].name")).isEqualTo("Parrot");
    assertThat(environment.getRequiredProperty("complexList[1].age")).isEqualTo("21");

    assertThat(environment.getRequiredProperty("complexEmptyList")).isEqualTo("");
    assertThat(environment.getRequiredProperty("complexEmptyList", List.class)).isEmpty();
  }

  @Test
  public void consumeSetProperties() {
    assertThat(environment.getRequiredProperty("simpleSet[0]")).isEqualTo("Pigeon");
    assertThat(environment.getRequiredProperty("simpleSet[1]")).isEqualTo("Parrot");

    assertThat(environment.getRequiredProperty("simpleEmptySet")).isEqualTo("");
    assertThat(environment.getRequiredProperty("simpleEmptySet", Set.class)).isEmpty();

    assertThat(environment.getRequiredProperty("complexSet[0].name")).isEqualTo("Pigeon");
    assertThat(environment.getRequiredProperty("complexSet[0].age")).isEqualTo("42");
    assertThat(environment.getRequiredProperty("complexSet[1].name")).isEqualTo("Parrot");
    assertThat(environment.getRequiredProperty("complexSet[1].age")).isEqualTo("21");

    assertThat(environment.getRequiredProperty("complexEmptySet")).isEqualTo("");
    assertThat(environment.getRequiredProperty("complexEmptySet", Set.class)).isEmpty();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void consumeMapProperties() {
    assertThat(environment.getRequiredProperty("simpleMap.Pigeon")).isEqualTo("42");
    assertThat(environment.getRequiredProperty("simpleMap.Parrot")).isEqualTo("21");

    assertThatThrownBy(() -> environment.getRequiredProperty("simpleEmptyMap"))
        .isInstanceOf(ConverterNotFoundException.class);
    assertThat(environment.getRequiredProperty("simpleEmptyMap", Map.class)).isEmpty();

    assertThat(environment.getRequiredProperty("complexMap.Pigeon.name")).isEqualTo("Pigeon");
    assertThat(environment.getRequiredProperty("complexMap.Pigeon.age")).isEqualTo("42");
    assertThat(environment.getRequiredProperty("complexMap.Parrot.name")).isEqualTo("Parrot");
    assertThat(environment.getRequiredProperty("complexMap.Parrot.age")).isEqualTo("21");

    assertThatThrownBy(() -> environment.getRequiredProperty("complexEmptyMap"))
        .isInstanceOf(ConverterNotFoundException.class);
    assertThat(environment.getRequiredProperty("complexEmptyMap", Map.class)).isEmpty();
  }

  @Test
  public void consumeListingProperties() {
    assertThat(environment.getRequiredProperty("simpleListing[0]")).isEqualTo("Pigeon");
    assertThat(environment.getRequiredProperty("simpleListing[1]")).isEqualTo("Parrot");

    assertThat(environment.getRequiredProperty("simpleEmptyListing")).isEqualTo("");
    assertThat(environment.getRequiredProperty("simpleEmptyListing", List.class)).isEmpty();

    assertThat(environment.getRequiredProperty("complexListing[0].name")).isEqualTo("Pigeon");
    assertThat(environment.getRequiredProperty("complexListing[0].age")).isEqualTo("42");
    assertThat(environment.getRequiredProperty("complexListing[1].name")).isEqualTo("Parrot");
    assertThat(environment.getRequiredProperty("complexListing[1].age")).isEqualTo("21");

    assertThat(environment.getRequiredProperty("complexEmptyListing")).isEqualTo("");
    assertThat(environment.getRequiredProperty("complexEmptyListing", List.class)).isEmpty();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void consumeMappingProperties() {
    assertThat(environment.getRequiredProperty("simpleMapping.Pigeon")).isEqualTo("42");
    assertThat(environment.getRequiredProperty("simpleMapping.Parrot")).isEqualTo("21");

    assertThatThrownBy(() -> environment.getRequiredProperty("simpleEmptyMapping"))
        .isInstanceOf(ConverterNotFoundException.class);
    assertThat(environment.getRequiredProperty("simpleEmptyMapping", Map.class)).isEmpty();

    assertThat(environment.getRequiredProperty("complexMapping.Pigeon.name")).isEqualTo("Pigeon");
    assertThat(environment.getRequiredProperty("complexMapping.Pigeon.age")).isEqualTo("42");
    assertThat(environment.getRequiredProperty("complexMapping.Parrot.name")).isEqualTo("Parrot");
    assertThat(environment.getRequiredProperty("complexMapping.Parrot.age")).isEqualTo("21");

    assertThatThrownBy(() -> environment.getRequiredProperty("complexEmptyMapping"))
        .isInstanceOf(ConverterNotFoundException.class);
    assertThat(environment.getRequiredProperty("complexEmptyMapping", Map.class)).isEmpty();
  }

  @Test
  public void consumeDynamicProperties() {
    assertThat(environment.getRequiredProperty("dynamicPigeon.name")).isEqualTo("Pigeon");
    assertThat(environment.getRequiredProperty("dynamicPigeon.age")).isEqualTo("42");
    // `.0` instead of `[0]` because elements/entries of dynamic objects
    // are currently turned into PObject properties with String keys (see VmDynamic.export())
    assertThat(environment.getRequiredProperty("dynamicPigeon.addresses.0.street"))
        .isEqualTo("Wilmore St.");
    assertThat(environment.getRequiredProperty("dynamicPigeon.addresses.0.zip")).isEqualTo("94102");

    assertThat(environment.getRequiredProperty("dynamicParrot.name")).isEqualTo("Parrot");
    assertThat(environment.getRequiredProperty("dynamicParrot.age")).isEqualTo("21");
    // `.1` instead of `[1]` because elements/entries of dynamic objects
    // are currently turned into PObject properties with String keys (see VmDynamic.export())
    assertThat(environment.getRequiredProperty("dynamicParrot.addresses.1.street"))
        .isEqualTo("Leisure St.");
    assertThat(environment.getRequiredProperty("dynamicParrot.addresses.1.zip")).isEqualTo("93118");
  }

  @Test
  public void consumeTypedProperties() {
    assertThat(environment.getRequiredProperty("typedPigeon.name")).isEqualTo("Pigeon");
    assertThat(environment.getRequiredProperty("typedPigeon.age")).isEqualTo("42");
    assertThat(environment.getRequiredProperty("typedPigeon.addresses[0].street"))
        .isEqualTo("Wilmore St.");
    assertThat(environment.getRequiredProperty("typedPigeon.addresses[0].zip")).isEqualTo("94102");

    assertThat(environment.getRequiredProperty("typedParrot.name")).isEqualTo("Parrot");
    assertThat(environment.getRequiredProperty("typedParrot.age")).isEqualTo("21");
    assertThat(environment.getRequiredProperty("typedParrot.addresses[1].street"))
        .isEqualTo("Leisure St.");
    assertThat(environment.getRequiredProperty("typedParrot.addresses[1].zip")).isEqualTo("93118");
  }

  @Test
  public void consumeInt() {
    assertThat(appConfig.getIntProp()).isEqualTo(42L);
    assertThat(appConfig.getInt32()).isEqualTo(-42);
  }

  @Test
  public void consumeFloat() {
    assertThat(appConfig.getFloatProp()).isEqualTo(1.23d);
  }

  @Test
  public void consumeBoolean() {
    assertThat(appConfig.isBooleanProp()).isEqualTo(true);
  }

  @Test
  public void consumeString() {
    assertThat(appConfig.getString()).isEqualTo("string");
  }

  @Test
  public void consumeDuration() {
    assertThat(appConfig.getDuration()).isEqualTo(new Duration(3, DurationUnit.HOURS));
  }

  @Test
  public void consumeDataSize() {
    assertThat(appConfig.getDataSize()).isEqualTo(new DataSize(1.23, DataSizeUnit.GIGABYTES));
  }

  @Test
  public void consumePair() {
    assertThat(appConfig.getPair()).isEqualTo(new Pair<>("hello", true));
  }

  @Test
  public void consumeRegex() {
    assertThat(appConfig.getRegex().pattern()).isEqualTo("regex");
  }

  @Test
  public void consumeNullableInt() {
    assertThat(appConfig.getNullableInt1()).isEqualTo(42L);
    assertThat(appConfig.getNullableInt2()).isNull();
  }

  @Test
  public void consumeNullableString() {
    assertThat(appConfig.getNullableString1()).contains("string");
    assertThat(appConfig.getNullableString2()).isNull();
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  public void consumeNullableTyped() {
    assertThat(appConfig.getNullablePigeon2()).isNull();
    // doesn't work as desired;
    // apparently, Spring cannot both assemble an object from properties and lift that object to
    // `Optional`
    assertThat(appConfig.getNullablePigeon1()).isNotNull();
  }

  @Test
  public void consumeListWithNullElements() {
    assertThat(appConfig.getListWithNullElements()).containsExactly("elem1", "elem2");
  }

  @Test
  public void consumeSetWithNullElements() {
    assertThat(appConfig.getSetWithNullElements()).containsExactly("elem1", "elem2");
  }

  @Test
  public void consumeMapWithNullElements() {
    assertThat(appConfig.getMapWithNullElements())
        .containsExactly(entry(0L, "elem1"), entry(2L, "elem2"));
  }

  @Test
  public void consumeListingWithNullElements() {
    assertThat(appConfig.getListingWithNullElements()).containsExactly("elem1", "elem2");
  }

  @Test
  public void consumeMappingWithNullElements() {
    assertThat(appConfig.getMappingWithNullElements())
        .containsExactly(entry(0L, "elem1"), entry(2L, "elem2"));
  }

  @Test
  public void consumeEnum() {
    assertThat(appConfig.getSize()).isEqualTo(AppConfig.Size.SMALL);
  }

  @Test
  public void consumeList() {
    assertThat(appConfig.getSimpleList()).containsExactly("Pigeon", "Parrot");
    assertThat(appConfig.getSimpleEmptyList()).isEmpty();
    assertThat(appConfig.getComplexList()).containsExactly(pigeon, parrot);
    assertThat(appConfig.getComplexEmptyList()).isEmpty();
  }

  @Test
  public void consumeSet() {
    assertThat(appConfig.getSimpleSet()).containsExactly("Pigeon", "Parrot");
    assertThat(appConfig.getSimpleEmptySet()).isEmpty();
    assertThat(appConfig.getComplexSet()).containsExactly(pigeon, parrot);
    assertThat(appConfig.getComplexEmptySet()).isEmpty();
  }

  @Test
  public void consumeMap() {
    assertThat(appConfig.getSimpleMap())
        .containsExactly(entry("Pigeon", 42L), entry("Parrot", 21L));
    assertThat(appConfig.getSimpleEmptyMap()).isEmpty();
    assertThat(appConfig.getComplexMap())
        .containsExactly(entry("Pigeon", pigeon), entry("Parrot", parrot));
    assertThat(appConfig.getComplexEmptyMap()).isEmpty();
  }

  @Test
  public void consumeListing() {
    assertThat(appConfig.getSimpleListing()).containsExactly("Pigeon", "Parrot");
    assertThat(appConfig.getSimpleEmptyListing()).isEmpty();
    assertThat(appConfig.getComplexListing()).containsExactly(pigeon, parrot);
    assertThat(appConfig.getComplexEmptyListing()).isEmpty();
  }

  @Test
  public void consumeMapping() {
    assertThat(appConfig.getSimpleMapping())
        .containsExactly(entry("Pigeon", 42L), entry("Parrot", 21L));
    assertThat(appConfig.getSimpleEmptyMapping()).isEmpty();
    assertThat(appConfig.getComplexMapping())
        .containsExactly(entry("Pigeon", pigeon), entry("Parrot", parrot));
    assertThat(appConfig.getComplexEmptyMapping()).isEmpty();
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  public void consumeDynamic() {
    // not currently supported
    assertThat(appConfig.getDynamicPigeon()).isNull();
    assertThat(appConfig.getDynamicParrot()).isNull();
  }

  @Test
  public void consumeTyped() {
    var address1 = new AppConfig.Address("Wilmore St.", 94102);
    var address2 = new AppConfig.Address("Leisure St.", 93118);

    var pigeon = appConfig.getTypedPigeon();
    assertThat(pigeon).isNotNull();
    assertThat(pigeon.getName()).isEqualTo("Pigeon");
    assertThat(pigeon.getAge()).isEqualTo(42L);
    assertThat(pigeon.getAddresses()).containsExactly(address1, address2);

    var parrot = appConfig.getTypedParrot();
    assertThat(parrot).isNotNull();
    assertThat(parrot.getName()).isEqualTo("Parrot");
    assertThat(parrot.getAge()).isEqualTo(21L);
    assertThat(parrot.getAddresses()).containsExactly(address1, address2);
  }
}
