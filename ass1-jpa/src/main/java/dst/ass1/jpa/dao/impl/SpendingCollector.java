package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.model.IMoney;
import dst.ass1.jpa.model.IRider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class SpendingCollector implements Collector<RecentSpending, Map<IRider, Map<String, IMoney>>, Map<IRider, Map<String, IMoney>>> {

    public static SpendingCollector toMapOfMaps() {
        return new SpendingCollector();
    }

    @Override
    public Supplier<Map<IRider, Map<String, IMoney>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<IRider, Map<String, IMoney>>, RecentSpending> accumulator() {
        return (map, spending) -> {
            map.putIfAbsent(spending.getiRider(), new HashMap<>());
            Map<String, IMoney> money = map.get(spending.getiRider());
            money.put(spending.getCurrency(), spending.getiMoney());
        };
    }

    @Override
    public BinaryOperator<Map<IRider, Map<String, IMoney>>> combiner() {
        return (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        };
    }

    @Override
    public Function<Map<IRider, Map<String, IMoney>>, Map<IRider, Map<String, IMoney>>> finisher() {
        return Collections::unmodifiableMap;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }
}
