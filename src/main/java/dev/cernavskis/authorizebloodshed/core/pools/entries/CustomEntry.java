package dev.cernavskis.authorizebloodshed.core.pools.entries;

public abstract class CustomEntry extends WeightedEntry {
  public CustomEntry(int weight) {
    super(weight);
  }

  public abstract String getType();
}
