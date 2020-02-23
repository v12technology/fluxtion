/*
 * Copyright (C) 2019 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.text.builder.csv;

import com.fluxtion.api.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.builder.generation.GenerationContext;
import static com.fluxtion.builder.generation.GenerationContext.SINGLETON;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.util.Pair;
import static com.fluxtion.ext.streaming.builder.factory.BooleanBuilder.and;
import static com.fluxtion.ext.streaming.builder.factory.BooleanBuilder.nand;
import static com.fluxtion.ext.streaming.builder.factory.BooleanBuilder.not;
import static com.fluxtion.ext.streaming.builder.factory.BooleanBuilder.or;
import static com.fluxtion.ext.streaming.builder.factory.FilterByNotificationBuilder.filter;
import com.fluxtion.ext.streaming.builder.stream.StreamFunctionCompiler;
import com.fluxtion.ext.text.api.csv.ColumnName;
import com.fluxtion.ext.text.api.csv.RowExceptionNotifier;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.ext.text.api.csv.RulesEvaluator;
import com.fluxtion.ext.text.api.csv.ValidationLogSink.LogNotifier;
import com.fluxtion.ext.text.api.util.EventPublsher;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A RulesEvaluator aggregates a set of rules and reports success if all rules
 * are valid.
 *
 * @author V12 Technology Ltd.
 */
public class RulesEvaluatorBuilder<T> {

    public static <T> BuilderWrapper<T> validator(Wrapper<T> bean) {
        return new BuilderWrapper<T>(bean);
    }

    public static <T> Builder<T> validator(T bean) {
        return new Builder<T>(bean);
    }

    public static <T> BuilderRowProcessor<T> validator(RowProcessor<T> bean) {
        return new BuilderRowProcessor<T>(bean);
    }

    public static class BuilderRowProcessor<T> {

        private final RowProcessor<T> monitoredWrapped;
        private final List<Pair<SerializableConsumer, SerializableFunction<T, ?>>> ruleList;

        public BuilderRowProcessor(RowProcessor<T> monitored) {
            this.monitoredWrapped = monitored;
            ruleList = new ArrayList<>();
        }

        public <R> BuilderRowProcessor<T> addRule(SerializableConsumer<? extends R> rule, SerializableFunction<T, R> supplier) {
            Object test = rule.captured()[0];
            if (test instanceof ColumnName) {
                Method accessorMethod = supplier.method();
                ((ColumnName) test).setName(accessorMethod.getName() + " ");
            }
            ruleList.add(new Pair(rule, supplier));
            return this;
        }

        public <R> RulesEvaluator<T> build() {
            //and all rules and pass through boolean filter
            RowExceptionNotifier notifier = SINGLETON.addOrUseExistingNode(
                    new RowExceptionNotifier(monitoredWrapped));
            RulesEvaluator<T> evaluator = null;
            if (ruleList.isEmpty()) {
                evaluator = new RulesEvaluator<>(
                        monitoredWrapped,
                        filter(monitoredWrapped, notifier)
                );
            } else {
                List testList = new ArrayList();
                for (Pair<SerializableConsumer, SerializableFunction<T, ?>> pair : ruleList) {
                    SerializableConsumer<? extends R> rule = pair.getKey();
                    SerializableFunction<T, R> supplier = (SerializableFunction<T, R>) pair.getValue();
                    testList.add(StreamFunctionCompiler.filter(rule.captured()[0], rule.method(), monitoredWrapped, supplier.method()).build()
                    );
                }

                evaluator = new RulesEvaluator<>(
                        filter(monitoredWrapped, and(testList.toArray())),
                        filter(monitoredWrapped, or(notifier, nand(testList.toArray()))
                        )
                );
            }

            EventPublsher publisher = new EventPublsher();
            publisher.addEventSource(monitoredWrapped);
            publisher = GenerationContext.SINGLETON.addOrUseExistingNode(publisher);
            publisher.addValidatedSource(evaluator.passedNotifier());

            SINGLETON.addOrUseExistingNode(new LogNotifier(evaluator.failedNotifier(), monitoredWrapped));
            return evaluator;
        }
    }

    public static class BuilderWrapper<T> {

        private final Wrapper<T> monitoredWrapped;
        private List<Pair<SerializableConsumer, SerializableFunction<T, ?>>> ruleList;

        public BuilderWrapper(Wrapper<T> monitored) {
            this.monitoredWrapped = monitored;
            ruleList = new ArrayList<>();
        }

        public <R> BuilderWrapper<T> addRule(SerializableConsumer<? extends R> rule, SerializableFunction<T, R> supplier) {
            Object test = rule.captured()[0];
            if (test instanceof ColumnName) {
                Method accessorMethod = supplier.method();
                ((ColumnName) test).setName(accessorMethod.getName() + " ");
            }
            ruleList.add(new Pair(rule, supplier));
            return this;
        }

        public <R> RulesEvaluator<T> build() {
            //TODO add logic for node validators
            //and all rules and pass through boolean filter
            RulesEvaluator<T> evaluator = null;
            if (ruleList.isEmpty()) {
                evaluator = new RulesEvaluator<>(
                        monitoredWrapped,
                        filter(monitoredWrapped, not(monitoredWrapped))
                );
            } else {
                List testList = new ArrayList();
                for (Pair<SerializableConsumer, SerializableFunction<T, ?>> pair : ruleList) {
                    SerializableConsumer<? extends R> rule = pair.getKey();
                    SerializableFunction<T, R> supplier = (SerializableFunction<T, R>) pair.getValue();
                    testList.add(StreamFunctionCompiler.filter(rule.captured()[0], rule.method(), monitoredWrapped, supplier.method()).build()
                    );
                    
                }
                evaluator = new RulesEvaluator<>(
                        filter(monitoredWrapped, and(testList.toArray())),
                        filter(monitoredWrapped, nand(testList.toArray()))
                );
            }

            EventPublsher publisher = new EventPublsher();
            publisher.addEventSource(monitoredWrapped);
            publisher = GenerationContext.SINGLETON.addOrUseExistingNode(publisher);
            publisher.addValidatedSource(evaluator.passedNotifier());

            SINGLETON.addOrUseExistingNode(new LogNotifier(evaluator.failedNotifier()));
            return evaluator;
        }
    }

    public static class Builder<T> {

        private final T monitored;
        private final List<Pair<SerializableConsumer, SerializableSupplier<?>>> ruleList;

        public Builder(T monitored) {
            this.monitored = monitored;
            ruleList = new ArrayList<>();
        }

        public <R> Builder<T> addRule(SerializableConsumer<? extends R> rule, SerializableSupplier< R> supplier) {
            Object test = rule.captured()[0];
            if (test instanceof ColumnName) {
                ((ColumnName) test).setName(supplier.method(SINGLETON.getClassLoader()).getName() + " ");
            }
            ruleList.add(new Pair(rule, supplier));
            return this;
        }

        public <R> RulesEvaluator<T> build() {
            //TODO add logic for node validators
            RulesEvaluator<T> evaluator = null;
            if (ruleList.isEmpty()) {
                evaluator = new RulesEvaluator<>(
                        filter(monitored, monitored),
                        filter(monitored, not(monitored))
                );
            } else {
                List testList = new ArrayList();
                for (Pair<SerializableConsumer, SerializableSupplier< ?>> pair : ruleList) {
                    SerializableConsumer<? extends R> rule = pair.getKey();
                    SerializableSupplier< R> supplier = (SerializableSupplier< R>) pair.getValue();
                    testList.add(StreamFunctionCompiler.filter(rule.captured()[0], rule.method(), monitored, supplier.method()).build()
                    );
                }
                evaluator = new RulesEvaluator<>(
                        filter(monitored, and(testList.toArray())),
                        filter(monitored, nand(testList.toArray()))
                );
            }
            EventPublsher publisher = new EventPublsher();
            publisher.addEventSource(monitored);
            publisher = GenerationContext.SINGLETON.addOrUseExistingNode(publisher);
            publisher.addValidatedSource(evaluator.passedNotifier());
            SINGLETON.addOrUseExistingNode(new LogNotifier(evaluator.failedNotifier()));
            return evaluator;
        }
    }

}
