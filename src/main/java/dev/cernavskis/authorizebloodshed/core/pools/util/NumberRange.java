package dev.cernavskis.authorizebloodshed.core.pools.util;

import java.util.List;

import net.minecraft.util.RandomSource;

public class NumberRange<T extends Number> {
  public final T min;
  public final T max;

  public NumberRange(T min, T max) {
    this.min = min;
    this.max = max;
  }

  public NumberRange<Integer> toInt() {
    return new NumberRange<>(this.min.intValue(), this.max.intValue());
  }

  public NumberRange<Double> toDouble() {
    return new NumberRange<>(this.min.doubleValue(), this.max.doubleValue());
  }

  public boolean isInRange(T value) {
    return value.doubleValue() >= this.min.doubleValue() && value.doubleValue() <= this.max.doubleValue();
  }

  @SuppressWarnings("unchecked")
  public T getRandom(RandomSource random) {
    if (this.min.equals(this.max)) {
      return this.min;
    }
    
    if (this.min instanceof Integer) {
      return (T) Integer.valueOf(this.min.intValue() + random.nextInt(this.max.intValue() - this.min.intValue()));
    } else if (this.min instanceof Double) {
      return (T) Double.valueOf(this.min.doubleValue() + random.nextDouble() * (this.max.doubleValue() - this.min.doubleValue()));
    } else {
      throw new UnsupportedOperationException("Unsupported number type: " + this.min.getClass().getSimpleName());
    }
  }

  public static NumberRange<Integer> ofInt(int min, int max) {
    return new NumberRange<>(min, max);
  }

  public static NumberRange<Double> ofDouble(double min, double max) {
    return new NumberRange<>(min, max);
  }

  public static NumberRange<Double> deserialize(Object range) {
    if (range instanceof List<?> list) {
      return new NumberRange<>(Double.valueOf(list.get(0).toString()), Double.valueOf(list.get(1).toString()));
    } else if (range instanceof Number number) {
      return new NumberRange<Double>(number.doubleValue(), number.doubleValue());
    } else {
      throw new IllegalArgumentException("Expected List<Number> or Number, got " + range.getClass().getSimpleName());
    }
  }

  public static Object serialize(NumberRange<?> number) {
    if (number.min.equals(number.max)) {
      return number.min;
    } else {
      return List.of(number.min, number.max);
    }
  }
}
