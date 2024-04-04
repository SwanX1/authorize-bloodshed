package dev.cernavskis.authorizebloodshed.core.pools.entries;

public abstract class WeightedEntry {
  public final int weight;

  public WeightedEntry(int weight) {
    this.weight = weight;
  }
}