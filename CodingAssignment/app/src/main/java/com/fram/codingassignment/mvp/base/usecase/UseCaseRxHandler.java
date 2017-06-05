/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fram.codingassignment.mvp.base.usecase;


/**
 * Runs {@link UseCase}s using a {@link UseCaseScheduler}.
 */
public class UseCaseRxHandler {

    private static UseCaseRxHandler INSTANCE;

    private final UseCaseScheduler mUseCaseScheduler;

    public UseCaseRxHandler(UseCaseScheduler useCaseScheduler) {
        mUseCaseScheduler = useCaseScheduler;
    }

    public <T extends RequestValues, R extends ResponseValue> void execute(
            final UseCase<T, R> useCase, T values, UseCase.UseCaseCallback<R> callback) {
        useCase.setRequestValues(values);
        useCase.setUseCaseCallback(callback);

        mUseCaseScheduler.subscribe(useCase, useCase.getUseCaseCallback());
    }

    public <T extends RequestValues, R extends ResponseValue> void executeRetrofit(
            final UseCase<T, R> useCase, T values, UseCase.UseCaseCallback<R> callback) {
        useCase.setRequestValues(values);
        useCase.setUseCaseCallback(callback);

        mUseCaseScheduler.subscribeRetrofit(useCase, useCase.getUseCaseCallback());
    }

    public void clear(){
        mUseCaseScheduler.clear();
    }

    public void release(){
        mUseCaseScheduler.unsubscribe();
    }

    public void resume(){
        mUseCaseScheduler.subscribe();
    }

    public static UseCaseRxHandler getInstance() {
        return new UseCaseRxHandler(new UseCaseRxScheduler());
    }
}
