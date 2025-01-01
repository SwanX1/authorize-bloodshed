package dev.cernavskis.authorizebloodshed.core.pools.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;

import dev.cernavskis.authorizebloodshed.core.pools.entries.WeightedEntry;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.util.RandomSource;

public class Entries<T extends WeightedEntry> implements Collection<T> {
  // entry hash -> entry
  private Map<Integer, T> entriesMap = new Int2ObjectArrayMap<T>();
  // entry hash -> entry weight
  private Map<Integer, Integer> weights = new Int2IntArrayMap();
  private int totalWeight = 0;

  public T getRandomWeighted(RandomSource source) {
    if (this.totalWeight == 0) {
      return null;
    }

    int random = source.nextInt(this.totalWeight);
    int current = 0;

    for (Entry<Integer, Integer> entry : this.weights.entrySet()) {
      current += entry.getValue();

      if (random < current) {
        return this.entriesMap.get(entry.getKey());
      }
    }

    return null;
  }

  @Override
  public boolean add(T entry) {
    int hash = entry.hashCode();
    int weight = entry.weight;

    this.weights.put(hash, weight);
    this.entriesMap.put(hash, entry);
    this.totalWeight += weight;

    return true;
  }

  @Override
  public boolean addAll(Collection<? extends T> entries) {
    for (T entry : entries) {
      this.add(entry);
    }

    return true;
  }

  @Override
  public void clear() {
    this.weights.clear();
    this.entriesMap.clear();
    this.totalWeight = 0;
  }

  @Override
  public boolean contains(Object entry) {
    return this.entriesMap.containsKey(entry.hashCode());
  }

  @Override
  public boolean containsAll(Collection<?> entries) {
    for (Object entry : entries) {
      if (!this.contains(entry)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean isEmpty() {
    return this.entriesMap.isEmpty();
  }

  @Override
  public Iterator<T> iterator() {
    return this.entriesMap.values().iterator();
  }

  @Override
  public boolean remove(Object entry) {
    int hash = entry.hashCode();
    int weight = this.weights.get(hash);

    this.weights.remove(hash);
    this.entriesMap.remove(hash);
    this.totalWeight -= weight;

    return true;
  }

  @Override
  public boolean removeAll(Collection<?> entry) {
    for (Object e : entry) {
      this.remove(e);
    }

    return true;
  }

  @Override
  public boolean retainAll(Collection<?> entry) {
    for (Entry<Integer, T> e : this.entriesMap.entrySet()) {
      if (!entry.contains(e.getValue())) {
        this.remove(e.getValue());
      }
    }

    return true;
  }

  @Override
  public int size() {
    return this.entriesMap.size();
  }

  @SuppressWarnings("unchecked")
  @Override
  public T[] toArray() {
    return (T[]) this.entriesMap.values().toArray();
  }

  @SuppressWarnings("hiding")
  @Override
  public <T> T[] toArray(T[] entries) {
    return this.entriesMap.values().toArray(entries);
  }

  public static <T extends WeightedEntry> Collector<T, Entries<T>, Entries<T>> toEntries() {
    return Collector.of(Entries::new, Entries<T>::add, (entries, entry) -> {
      entries.addAll(entry);
      return entries;
    });
  }
}
