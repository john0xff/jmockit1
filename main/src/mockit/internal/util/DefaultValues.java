/*
 * Copyright (c) 2006-2015 Rogério Liesenfeld
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.util;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import static java.util.Collections.*;

import mockit.external.asm.Type;
import static mockit.Deencapsulation.*;
import static mockit.internal.util.Utilities.*;

import org.jetbrains.annotations.*;

/**
 * Provides default values for each type, typically used for returning default values according to
 * method return types.
 */
@SuppressWarnings("ZeroLengthArrayAllocation")
public final class DefaultValues
{
   private DefaultValues() {}

   private static final Integer ZERO_INT = 0;
   private static final Long ZERO_LONG = 0L;
   private static final Float ZERO_FLOAT = 0.0F;
   private static final Double ZERO_DOUBLE = 0.0;
   private static final Byte ZERO_BYTE = 0;
   private static final Short ZERO_SHORT = 0;
   private static final Character ZERO_CHAR = '\0';

   private static final Map<String, Object> TYPE_DESC_TO_VALUE_MAP = new HashMap<String, Object>();
   private static final Map<String, Object> ELEM_TYPE_TO_ONE_D_ARRAY = new HashMap<String, Object>();
   static {
      TYPE_DESC_TO_VALUE_MAP.put("Z", Boolean.FALSE);
      TYPE_DESC_TO_VALUE_MAP.put("C", ZERO_CHAR);
      TYPE_DESC_TO_VALUE_MAP.put("B", ZERO_BYTE);
      TYPE_DESC_TO_VALUE_MAP.put("S", ZERO_SHORT);
      TYPE_DESC_TO_VALUE_MAP.put("I", ZERO_INT);
      TYPE_DESC_TO_VALUE_MAP.put("F", ZERO_FLOAT);
      TYPE_DESC_TO_VALUE_MAP.put("J", ZERO_LONG);
      TYPE_DESC_TO_VALUE_MAP.put("D", ZERO_DOUBLE);
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/lang/Boolean;", Boolean.FALSE);
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/lang/Character;", ZERO_CHAR);
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/lang/Byte;", ZERO_BYTE);
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/lang/Short;", ZERO_SHORT);
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/lang/Integer;", ZERO_INT);
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/lang/Float;", ZERO_FLOAT);
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/lang/Long;", ZERO_LONG);
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/lang/Double;", ZERO_DOUBLE);
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/lang/Iterable;", emptyList());
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/Enumeration;", enumeration(emptyList()));
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/Collection;", emptyList());
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/List;", emptyList());
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/Set;", emptySet());
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/SortedSet;", unmodifiableSortedSet(new TreeSet<Object>()));
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/Map;", emptyMap());
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/SortedMap;", unmodifiableSortedMap(new TreeMap<Object, Object>()));
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/Iterator;", emptyList().iterator());
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/ListIterator;", emptyList().listIterator());

      ELEM_TYPE_TO_ONE_D_ARRAY.put("[Z", new boolean[0]);
      ELEM_TYPE_TO_ONE_D_ARRAY.put("[C", new char[0]);
      ELEM_TYPE_TO_ONE_D_ARRAY.put("[B", new byte[0]);
      ELEM_TYPE_TO_ONE_D_ARRAY.put("[S", new short[0]);
      ELEM_TYPE_TO_ONE_D_ARRAY.put("[I", new int[0]);
      ELEM_TYPE_TO_ONE_D_ARRAY.put("[F", new float[0]);
      ELEM_TYPE_TO_ONE_D_ARRAY.put("[J", new long[0]);
      ELEM_TYPE_TO_ONE_D_ARRAY.put("[D", new double[0]);
      ELEM_TYPE_TO_ONE_D_ARRAY.put("[Ljava/lang/Object;", new Object[0]);
      ELEM_TYPE_TO_ONE_D_ARRAY.put("[Ljava/lang/String;", new String[0]);

      if (JAVA8) {
         addJava8TypeMapEntries();
      }
   }

   @SuppressWarnings("Since15")
   private static void addJava8TypeMapEntries()
   {
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/Optional;", Optional.empty());
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/OptionalInt;", OptionalInt.empty());
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/OptionalLong;", OptionalLong.empty());
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/OptionalDouble;", OptionalDouble.empty());
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/Spliterator;", Spliterators.emptySpliterator());
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/Spliterator$OfInt;", Spliterators.emptyIntSpliterator());
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/Spliterator$OfLong;", Spliterators.emptyLongSpliterator());
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/Spliterator$OfDouble;", Spliterators.emptyDoubleSpliterator());

      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/PrimitiveIterator$OfInt;", new PrimitiveIterator.OfInt() {
         @Override public int nextInt() { throw new NoSuchElementException(); }
         @Override public Integer next() { throw new NoSuchElementException(); }
         @Override public boolean hasNext() { return false; }
         @Override public void forEachRemaining(IntConsumer action) {}
      });
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/PrimitiveIterator$OfLong;", new PrimitiveIterator.OfLong() {
         @Override public long nextLong() { throw new NoSuchElementException(); }
         @Override public Long next() { throw new NoSuchElementException(); }
         @Override public boolean hasNext() { return false; }
         @Override public void forEachRemaining(LongConsumer action) {}
      });
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/PrimitiveIterator$OfDouble;", new PrimitiveIterator.OfDouble() {
         @Override public double nextDouble() { throw new NoSuchElementException(); }
         @Override public Double next() { throw new NoSuchElementException(); }
         @Override public boolean hasNext() { return false; }
         @Override public void forEachRemaining(DoubleConsumer action) {}
      });

      // These are static interface methods, which can't be compiled on "-source 1.6".
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/stream/Stream;", invoke(Stream.class, "empty"));
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/stream/IntStream;", invoke(IntStream.class, "empty"));
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/stream/LongStream;", invoke(LongStream.class, "empty"));
      TYPE_DESC_TO_VALUE_MAP.put("Ljava/util/stream/DoubleStream;", invoke(DoubleStream.class, "empty"));
   }

   @Nullable
   public static Object computeForReturnType(@NotNull String methodNameAndDesc)
   {
      String typeDesc = getReturnTypeDesc(methodNameAndDesc);
      return computeForType(typeDesc);
   }

   @NotNull
   public static String getReturnTypeDesc(@NotNull String methodNameAndDesc)
   {
      int rightParen = methodNameAndDesc.indexOf(')') + 1;
      return methodNameAndDesc.substring(rightParen);
   }

   @Nullable
   public static Object computeForType(@NotNull String typeDesc)
   {
      char typeDescChar = typeDesc.charAt(0);

      if (typeDescChar == 'V') {
         return null;
      }

      Object defaultValue = TYPE_DESC_TO_VALUE_MAP.get(typeDesc);

      if (defaultValue != null) {
         return defaultValue;
      }

      if (typeDescChar == 'L') {
         return null;
      }

      // It's an array.
      return computeForArrayType(typeDesc);
   }

   @NotNull
   public static Object computeForArrayType(@NotNull String typeDesc)
   {
      Object emptyArray = ELEM_TYPE_TO_ONE_D_ARRAY.get(typeDesc);

      if (emptyArray == null) {
         emptyArray = newEmptyArray(typeDesc);
      }

      return emptyArray;
   }

   @NotNull
   private static Object newEmptyArray(@NotNull String typeDesc)
   {
      Type type = Type.getType(typeDesc);
      Class<?> elementType = TypeDescriptor.getClassForType(type.getElementType());

      return Array.newInstance(elementType, new int[type.getDimensions()]);
   }

   @Nullable
   public static Object computeForType(@NotNull Class<?> type)
   {
      if (type.isArray()) {
         return Array.newInstance(type.getComponentType(), 0);
      }
      else if (type != void.class && type.isPrimitive()) {
         return defaultValueForPrimitiveType(type);
      }

      return computeForWrapperType(type);
   }

   @NotNull
   public static Object defaultValueForPrimitiveType(@NotNull Class<?> type)
   {
      if (type == int.class) {
         return ZERO_INT;
      }
      else if (type == boolean.class) {
         return Boolean.FALSE;
      }
      else if (type == long.class) {
         return ZERO_LONG;
      }
      else if (type == double.class) {
         return ZERO_DOUBLE;
      }
      else if (type == float.class) {
         return ZERO_FLOAT;
      }
      else if (type == char.class) {
         return ZERO_CHAR;
      }
      else if (type == byte.class) {
         return ZERO_BYTE;
      }
      else {
         return ZERO_SHORT;
      }
   }

   @SuppressWarnings("unchecked")
   @Nullable
   public static <T> T computeForWrapperType(@NotNull java.lang.reflect.Type type)
   {
      if (type == Integer.class) {
         return (T) ZERO_INT;
      }
      else if (type == Boolean.class) {
         return (T) Boolean.FALSE;
      }
      else if (type == Long.class) {
         return (T) ZERO_LONG;
      }
      else if (type == Double.class) {
         return (T) ZERO_DOUBLE;
      }
      else if (type == Float.class) {
         return (T) ZERO_FLOAT;
      }
      else if (type == Character.class) {
         return (T) ZERO_CHAR;
      }
      else if (type == Byte.class) {
         return (T) ZERO_BYTE;
      }
      else if (type == Short.class) {
         return (T) ZERO_SHORT;
      }

      return null;
   }
}
