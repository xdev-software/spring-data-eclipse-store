package software.xdev.spring.data.eclipse.store.integration.isolatet.tests.special.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;


public final class TypesData
{
	public record ListOfTestArguments(List<TestArguments> testArguments)
	{
		public Stream<Arguments> toArguments()
		{
			return this.testArguments.stream().map(TestArguments::toArguments);
		}
	}
	
	
	public record TestArguments<T extends ComplexObject>(
		Class<? extends EclipseStoreRepository<T, Integer>> repositoryClass,
		Function<Integer, T> objectCreator,
		Consumer<T> objectChanger)
	{
		public Arguments toArguments()
		{
			return Arguments.of(this.repositoryClass, this.objectCreator, this.objectChanger);
		}
	}
	
	public static Stream<Arguments> generateData()
	{
		return new ListOfTestArguments(
			List.of(
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
					object -> object.getValue().add(BigDecimal.ONE)
				),
				new TestArguments<>(
					BigDecimalRepository.class,
					id -> new BigDecimalDaoObject(id, BigDecimal.ZERO),
					object -> object.getValue().add(BigDecimal.ONE)
				),
				new TestArguments<>(
					BigDecimalRepository.class,
					id -> new BigDecimalDaoObject(id, null),
					object -> object.setValue(BigDecimal.ONE)
				),
				new TestArguments<>(
					BigIntegerRepository.class,
					id -> new BigIntegerDaoObject(id, BigInteger.ONE),
					object -> object.getValue().add(BigInteger.ONE)
				),
				new TestArguments<>(
					BigIntegerRepository.class,
					id -> new BigIntegerDaoObject(id, BigInteger.ZERO),
					object -> object.getValue().add(BigInteger.ONE)
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
					CalendarRepository.class,
					id -> new CalendarDaoObject(id, null),
					object -> object.setValue(Calendar.getInstance())
				),
				new TestArguments<>(
					CalendarRepository.class,
					id -> new CalendarDaoObject(id, Calendar.getInstance()),
					object -> object.getValue().add(Calendar.DAY_OF_MONTH, 1)
				),
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
					EnumMapRepository.class,
					id -> new EnumMapDaoObject(id, null),
					object -> object.setValue(new EnumMap<>(Map.of(EnumMapDaoObject.Album.RUMOURS, "1")))
				),
				new TestArguments<>(
					LazyRepository.class,
					id -> new LazyDaoObject(id, org.eclipse.serializer.reference.Lazy.Reference("1")),
					object -> object.setValue(org.eclipse.serializer.reference.Lazy.Reference("2"))
				),
				new TestArguments<>(
					LazyRepository.class,
					id -> new LazyDaoObject(id, null),
					object -> object.setValue(org.eclipse.serializer.reference.Lazy.Reference("1"))
				),
				new TestArguments<>(
					LazyRepository.class,
					id -> new LazyDaoObject(id, org.eclipse.serializer.reference.Lazy.Reference("1")),
					object -> object.getValue().clear()
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
					id -> new ListDaoObject(id, new ArrayList<>(Set.of("1"))),
					object -> object.getValue().remove("1")
				),
				new TestArguments<>(
					LocalDateRepository.class,
					id -> new LocalDateDaoObject(id, LocalDate.of(2000, 1, 1)),
					object -> object.getValue().plusDays(1)
				),
				new TestArguments<>(
					LocalDateTimeRepository.class,
					id -> new LocalDateTimeDaoObject(id, LocalDateTime.of(2000, 1, 1, 1, 1, 1)),
					object -> object.getValue().plusDays(1)
				),
				new TestArguments<>(
					LocalTimeRepository.class,
					id -> new LocalTimeDaoObject(id, LocalTime.of(1, 1, 1)),
					object -> object.getValue().plusHours(1)
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
				)
			)
		).toArguments();
	}
}
