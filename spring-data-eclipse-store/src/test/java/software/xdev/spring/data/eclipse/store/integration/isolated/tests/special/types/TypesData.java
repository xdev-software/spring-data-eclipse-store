/*
 * Copyright © 2023 XDEV Software (https://xdev.software)
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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.special.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.serializer.collections.lazy.LazyArrayList;
import org.eclipse.serializer.collections.lazy.LazyHashMap;
import org.eclipse.serializer.collections.lazy.LazyHashSet;
import org.junit.jupiter.params.provider.Arguments;

import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.repository.lazy.SpringDataEclipseStoreLazy;


final class TypesData
{
	public record ListOfTestArguments(List<TestArguments<?>> testArguments)
	{
		public Stream<Arguments> toArguments()
		{
			return this.testArguments.stream().map(TestArguments::toArguments);
		}
	}
	
	
	public record TestArguments<T extends ComplexObject<?>>(
		Class<? extends EclipseStoreRepository<T, Integer>> repositoryClass,
		Function<Integer, T> objectCreator,
		Consumer<T> objectChanger)
	{
		public Arguments toArguments()
		{
			return Arguments.of(this.repositoryClass, this.objectCreator, this.objectChanger);
		}
	}
	
	@SuppressWarnings("checkstyle:MethodLength")
	public static Stream<Arguments> generateData()
	{
		// noinspection RedundantTypeArguments (explicit type arguments speedup compilation and analysis time)
		return new ListOfTestArguments(
			List.<TestArguments<?>>of(
				new TestArguments<>(
					SetRepository.class,
					id -> new SetDaoObject(id, Set.of()),
					set -> set.setValue(Set.of("1"))
				),
				new TestArguments<>(
					SetRepository.class,
					id -> new SetDaoObject(id, Set.of("1")),
					set -> set.setValue(Set.of("1", "2"))
				),
				new TestArguments<>(
					SetRepository.class,
					id -> new SetDaoObject(id, Set.of("1", "2")),
					set -> set.setValue(Set.of("1", "2", "3"))
				),
				new TestArguments<>(
					SetRepository.class,
					id -> new SetDaoObject(id, new HashSet<>()),
					set -> set.getValue().add("1")
				),
				new TestArguments<>(
					SetRepository.class,
					id -> new SetDaoObject(id, new HashSet<>(List.of("1"))),
					set -> set.getValue().add("2")
				),
				new TestArguments<>(
					SetRepository.class,
					id -> new SetDaoObject(id, new HashSet<>(List.of("1", "2"))),
					set -> set.getValue().add("3")
				),
				new TestArguments<>(
					SetRepository.class,
					id -> new SetDaoObject(id, new LazyHashSet<>()),
					set -> set.getValue().add("1")
				),
				new TestArguments<>(
					SetRepository.class,
					id ->
					{
						final LazyHashSet<String> set = new LazyHashSet<>();
						set.add("1");
						return new SetDaoObject(id, set);
					},
					set -> set.getValue().add("2")
				),
				new TestArguments<>(
					SetRepository.class,
					id ->
					{
						final LazyHashSet<String> set = new LazyHashSet<>();
						set.add("1");
						set.add("2");
						return new SetDaoObject(id, set);
					},
					set -> set.getValue().add("3")
				),
				new TestArguments<>(
					SetRepository.class,
					id -> new SetDaoObject(id, new TreeSet<>()),
					set -> set.getValue().add("1")
				),
				new TestArguments<>(
					SetRepository.class,
					id -> new SetDaoObject(id, new TreeSet<>(List.of("1"))),
					set -> set.getValue().add("2")
				),
				new TestArguments<>(
					SetRepository.class,
					id -> new SetDaoObject(id, new TreeSet<>(List.of("1", "2"))),
					set -> set.getValue().add("3")
				),
				new TestArguments<>(
					SetRepository.class,
					id -> new SetDaoObject(id, new LinkedHashSet<>()),
					set -> set.getValue().add("1")
				),
				new TestArguments<>(
					SetRepository.class,
					id -> new SetDaoObject(id, new LinkedHashSet<>(List.of("1"))),
					set -> set.getValue().add("2")
				),
				new TestArguments<>(
					SetRepository.class,
					id -> new SetDaoObject(id, new LinkedHashSet<>(List.of("1", "2"))),
					set -> set.getValue().add("3")
				),
				new TestArguments<>(
					BigDecimalRepository.class,
					id -> new BigDecimalDaoObject(id, BigDecimal.ONE),
					object -> object.setValue(object.getValue().add(BigDecimal.ONE))
				),
				new TestArguments<>(
					BigDecimalRepository.class,
					id -> new BigDecimalDaoObject(id, BigDecimal.ZERO),
					object -> object.setValue(object.getValue().add(BigDecimal.ONE))
				),
				new TestArguments<>(
					BigDecimalRepository.class,
					id -> new BigDecimalDaoObject(id, null),
					object -> object.setValue(BigDecimal.ONE)
				),
				new TestArguments<>(
					BigIntegerRepository.class,
					id -> new BigIntegerDaoObject(id, BigInteger.ONE),
					object -> object.setValue(object.getValue().add(BigInteger.ONE))
				),
				new TestArguments<>(
					BigIntegerRepository.class,
					id -> new BigIntegerDaoObject(id, BigInteger.ZERO),
					object -> object.setValue(object.getValue().add(BigInteger.ONE))
				),
				new TestArguments<>(
					BigIntegerRepository.class,
					id -> new BigIntegerDaoObject(id, null),
					object -> object.setValue(BigInteger.ONE)
				),
				new TestArguments<>(
					BigIntegerRepository.class,
					id -> new BigIntegerDaoObject(id, BigInteger.ONE),
					object -> object.setValue(null)
				),
				new TestArguments<>(
					DateRepository.class,
					id -> new DateDaoObject(id, null),
					object -> object.setValue(new Date(System.currentTimeMillis()))
				),
				new TestArguments<>(
					DateRepository.class,
					id -> new DateDaoObject(id, new Date(System.currentTimeMillis())),
					object -> object.getValue().setTime(System.currentTimeMillis() + 1)
				),
				new TestArguments<>(
					ListRepository.class,
					id -> new ListDaoObject(id, List.of()),
					object -> object.setValue(List.of("1"))
				),
				new TestArguments<>(
					ListRepository.class,
					id -> new ListDaoObject(id, List.of("1")),
					object -> object.setValue(List.of("1", "2"))
				),
				new TestArguments<>(
					ListRepository.class,
					id -> new ListDaoObject(id, List.of("1", "2")),
					object -> object.setValue(List.of("1", "2", "3"))
				),
				new TestArguments<>(
					ListRepository.class,
					id -> new ListDaoObject(id, null),
					object -> object.setValue(List.of("1"))
				),
				new TestArguments<>(
					ListRepository.class,
					id -> new ListDaoObject(id, new ArrayList<>()),
					object -> object.getValue().add("1")
				),
				new TestArguments<>(
					ListRepository.class,
					id -> new ListDaoObject(id, new ArrayList<>(Set.of("1"))),
					object -> object.getValue().add("2")
				),
				new TestArguments<>(
					ListRepository.class,
					id -> new ListDaoObject(id, new ArrayList<>(Set.of("1", "2"))),
					object -> object.getValue().add("3")
				),
				new TestArguments<>(
					ListRepository.class,
					id -> new ListDaoObject(id, new Stack<>()),
					object -> object.getValue().add("1")
				),
				new TestArguments<>(
					ListRepository.class,
					id ->
					{
						final Stack<String> stack = new Stack<>();
						stack.add("1");
						return new ListDaoObject(id, stack);
					},
					object -> object.getValue().add("2")
				),
				new TestArguments<>(
					ListRepository.class,
					id ->
					{
						final Stack<String> stack = new Stack<>();
						stack.push("1");
						stack.push("2");
						return new ListDaoObject(id, stack);
					},
					object -> object.getValue().add("3")
				),
				new TestArguments<>(
					ListRepository.class,
					id -> new ListDaoObject(id, new LazyArrayList<>()),
					object -> object.getValue().add("1")
				),
				new TestArguments<>(
					ListRepository.class,
					id ->
					{
						final LazyArrayList<String> list = new LazyArrayList<>();
						list.add("1");
						return new ListDaoObject(id, list);
					},
					object -> object.getValue().add("2")
				),
				new TestArguments<>(
					ListRepository.class,
					id ->
					{
						final LazyArrayList<String> list = new LazyArrayList<>();
						list.add("1");
						list.add("2");
						return new ListDaoObject(id, list);
					},
					object -> object.getValue().add("3")
				),
				new TestArguments<>(
					ListRepository.class,
					id -> new ListDaoObject(id, new ArrayList<>(Set.of("1"))),
					object -> object.getValue().remove("1")
				),
				new TestArguments<>(
					ListRepository.class,
					id -> new ListDaoObject(id, new Vector<>(Set.of("1"))),
					object -> object.getValue().remove("1")
				),
				new TestArguments<>(
					ListRepository.class,
					id -> new ListDaoObject(id, Collections.emptyList()),
					object -> object.setValue(List.of("1"))
				),
				new TestArguments<>(
					LocalDateRepository.class,
					id -> new LocalDateDaoObject(id, LocalDate.of(2000, 1, 1)),
					object -> object.setValue(object.getValue().plusDays(1))
				),
				new TestArguments<>(
					LocalDateTimeRepository.class,
					id -> new LocalDateTimeDaoObject(id, LocalDateTime.of(2000, 1, 1, 1, 1, 1)),
					object -> object.setValue(object.getValue().plusDays(1))
				),
				new TestArguments<>(
					LocalTimeRepository.class,
					id -> new LocalTimeDaoObject(id, LocalTime.of(1, 1, 1)),
					object -> object.setValue(object.getValue().plusHours(1))
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, Map.of()),
					set -> set.setValue(Map.of("1", "1"))
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, Map.of("1", "1")),
					set -> set.setValue(Map.of("1", "1", "2", "2"))
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, Map.of("1", "1", "2", "2")),
					set -> set.setValue(Map.of("1", "1", "2", "2", "3", "3"))
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new HashMap<>()),
					set -> set.getValue().put("1", "1")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new HashMap<>(Map.of("1", "1"))),
					set -> set.getValue().put("2", "2")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new HashMap<>(Map.of("1", "1", "2", "2"))),
					set -> set.getValue().put("3", "3")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new LinkedHashMap<>()),
					set -> set.getValue().put("1", "1")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new LinkedHashMap<>(Map.of("1", "1"))),
					set -> set.getValue().put("2", "2")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new LinkedHashMap<>(Map.of("1", "1", "2", "2"))),
					set -> set.getValue().put("3", "3")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new Hashtable<>()),
					set -> set.getValue().put("1", "1")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new Hashtable<>(Map.of("1", "1"))),
					set -> set.getValue().put("2", "2")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new Hashtable<>(Map.of("1", "1", "2", "2"))),
					set -> set.getValue().put("3", "3")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new TreeMap<>()),
					set -> set.getValue().put("1", "1")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new TreeMap<>(Map.of("1", "1"))),
					set -> set.getValue().put("2", "2")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new TreeMap<>(Map.of("1", "1", "2", "2"))),
					set -> set.getValue().put("3", "3")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new IdentityHashMap<>()),
					set -> set.getValue().put("1", "1")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new IdentityHashMap<>(Map.of("1", "1"))),
					set -> set.getValue().put("2", "2")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new IdentityHashMap<>(Map.of("1", "1", "2", "2"))),
					set -> set.getValue().put("3", "3")
				),
				new TestArguments<>(
					OptionalRepository.class,
					id -> new OptionalDaoObject(id, Optional.of("1")),
					set -> set.setValue(Optional.of("2"))
				),
				new TestArguments<>(
					OptionalRepository.class,
					id -> new OptionalDaoObject(id, Optional.empty()),
					set -> set.setValue(Optional.of("1"))
				),
				new TestArguments<>(
					OptionalRepository.class,
					id -> new OptionalDaoObject(id, null),
					set -> set.setValue(Optional.of("1"))
				),
				new TestArguments<>(
					LazyRepository.class,
					id -> new LazyDaoObject(id, SpringDataEclipseStoreLazy.build("1")),
					object -> object.setValue(SpringDataEclipseStoreLazy.build("2"))
				),
				new TestArguments<>(
					LazyRepository.class,
					id -> new LazyDaoObject(id, SpringDataEclipseStoreLazy.build("1")),
					object -> object.getValue().clear()
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new LazyHashMap<>()),
					set -> set.getValue().put("1", "1")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> {
						final LazyHashMap<String, String> lazyHashMap = new LazyHashMap<>();
						lazyHashMap.put("1", "1");
						return new MapDaoObject(id, lazyHashMap);
					},
					set -> set.getValue().put("2", "2")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> {
						final LazyHashMap<String, String> lazyHashMap = new LazyHashMap<>();
						lazyHashMap.put("1", "1");
						lazyHashMap.put("2", "2");
						return new MapDaoObject(id, lazyHashMap);
					},
					set -> set.getValue().put("3", "3")
				)
			)
		).toArguments();
	}
	
	/**
	 * Should be identical to
	 * {@link
	 * software.xdev.spring.data.eclipse.store.repository.SupportedChecker.Implementation#UNSUPPORTED_DATA_TYPES}
	 */
	public static Stream<Arguments> generateNotWorkingData()
	{
		return new ListOfTestArguments(
			List.of(
				new TestArguments<>(
					EnumMapRepository.class,
					id -> new EnumMapDaoObject(id, new EnumMap<>(EnumMapDaoObject.Album.class)),
					object -> object.getValue().put(EnumMapDaoObject.Album.RUMOURS, "1")
				),
				new TestArguments<>(
					EnumMapRepository.class,
					id -> new EnumMapDaoObject(id, new EnumMap<>(Map.of(EnumMapDaoObject.Album.RUMOURS, "1"))),
					object -> object.getValue().put(EnumMapDaoObject.Album.TUSK, "2")
				),
				new TestArguments<>(
					EnumMapRepository.class,
					id -> new EnumMapDaoObject(id, new EnumMap<>(Map.of(EnumMapDaoObject.Album.RUMOURS, "1"))),
					object -> object.getValue().remove(EnumMapDaoObject.Album.RUMOURS)
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new WeakHashMap<>()),
					set -> set.getValue().put("1", "1")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new WeakHashMap<>(Map.of("1", "1"))),
					set -> set.getValue().put("2", "2")
				),
				new TestArguments<>(
					MapRepository.class,
					id -> new MapDaoObject(id, new WeakHashMap<>(Map.of("1", "1", "2", "2"))),
					set -> set.getValue().put("3", "3")
				),
				new TestArguments<>(
					CalendarRepository.class,
					id -> new CalendarDaoObject(id, Calendar.getInstance()),
					object -> object.getValue().add(Calendar.DAY_OF_MONTH, 1)
				)
			)
		).toArguments();
	}
}
