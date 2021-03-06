/*
 * Copyright 2018 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package io.hyperfoil.core.parser;

import java.util.Arrays;
import java.util.function.BiConsumer;

import org.yaml.snakeyaml.events.ScalarEvent;

class PropertyParser {
   private PropertyParser() {}

   static class String<T> implements Parser<T> {
      private final BiConsumer<T, java.lang.String> consumer;

      String(BiConsumer<T, java.lang.String> consumer) {
         this.consumer = consumer;
      }

      @Override
      public void parse(Context ctx, T target) throws ParserException {
         ScalarEvent event = ctx.expectEvent(ScalarEvent.class);
         consumer.accept(target, event.getValue());
      }
   }

   static class Int<T> implements Parser<T> {
      private final BiConsumer<T, Integer> consumer;

      Int(BiConsumer<T, Integer> consumer) {
         this.consumer = consumer;
      }

      @Override
      public void parse(Context ctx, T target) throws ParserException {
         ScalarEvent event = ctx.expectEvent(ScalarEvent.class);
         try {
            consumer.accept(target, Integer.parseInt(event.getValue()));
         } catch (NumberFormatException e) {
            throw new ParserException(event, "Failed to parse as integer: " + event.getValue());
         }
      }
   }

   static class Long<T> implements Parser<T> {
      private final BiConsumer<T, java.lang.Long> consumer;

      Long(BiConsumer<T, java.lang.Long> consumer) {
         this.consumer = consumer;
      }

      @Override
      public void parse(Context ctx, T target) throws ParserException {
         ScalarEvent event = ctx.expectEvent(ScalarEvent.class);
         try {
            consumer.accept(target, java.lang.Long.parseLong(event.getValue()));
         } catch (NumberFormatException e) {
            throw new ParserException(event, "Failed to parse as long: " + event.getValue());
         }
      }
   }

   static class Double<T> implements Parser<T> {
      private final BiConsumer<T, java.lang.Double> consumer;

      Double(BiConsumer<T, java.lang.Double> consumer) {
         this.consumer = consumer;
      }

      @Override
      public void parse(Context ctx, T target) throws ParserException {
         ScalarEvent event = ctx.expectEvent(ScalarEvent.class);
         try {
            consumer.accept(target, java.lang.Double.parseDouble(event.getValue()));
         } catch (NumberFormatException e) {
            throw new ParserException(event, "Failed to parse as long: " + event.getValue());
         }
      }
   }

   static class Boolean<T> implements Parser<T> {
      private final BiConsumer<T, java.lang.Boolean> consumer;

      Boolean(BiConsumer<T, java.lang.Boolean> consumer) {
         this.consumer = consumer;
      }

      @Override
      public void parse(Context ctx, T target) throws ParserException {
         ScalarEvent event = ctx.expectEvent(ScalarEvent.class);
         boolean value;
         if (event.getValue().equalsIgnoreCase("true")) {
            value = true;
         } else if (event.getValue().equalsIgnoreCase("false")) {
            value = false;
         } else {
            throw new ParserException("Failed to parse as boolean: " + event.getValue());
         }
         consumer.accept(target, value);
      }
   }

   public static class Enum<E extends java.lang.Enum<E>, T> implements Parser<T> {
      private final E[] values;
      private final BiConsumer<T, E> consumer;

      public Enum(E[] values, BiConsumer<T, E> consumer) {
         this.values = values;
         this.consumer = consumer;
      }

      @Override
      public void parse(Context ctx, T target) throws ParserException {
         ScalarEvent event = ctx.expectEvent(ScalarEvent.class);
         for (E value : values) {
            if (value.name().equalsIgnoreCase(event.getValue())) {
               consumer.accept(target, value);
               return;
            }
         }
         throw new ParserException("No match for enum value '" + event.getValue() + "', options are: " + Arrays.toString(values));
      }
   }
}
