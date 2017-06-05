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

import com.fram.codingassignment.rx.SchedulerProvider;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Executes asynchronous tasks using a {@link ThreadPoolExecutor}.
 * <p>
 * See also {@link Executors} for a list of factory methods to create common
 * {@link java.util.concurrent.ExecutorService}s for different scenarios.
 */
public class UseCaseRxScheduler implements UseCaseScheduler {

    private CompositeDisposable mSubscription;

    public UseCaseRxScheduler() {
        mSubscription = new CompositeDisposable();
    }

    @Override
    public <Q extends RequestValues, P extends ResponseValue> void subscribe(final UseCase<Q, P> useCase, final UseCase.UseCaseCallback<P> useCaseCallback) {
        // The network request might be handled in a different thread so make sure
        // Espresso knows
        // that the app is busy until the response is handled.
//        EspressoIdlingResource.increment(); // App is busy until further notice

        subscribe();

        Observable<P> observable = Observable.create(new ObservableOnSubscribe<P>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<P> e) throws Exception {
                e.onNext(doAction(useCase));
                e.onComplete();
            }
        });
        mSubscription.add(observable
                .subscribeOn(SchedulerProvider.getInstance().io())
                .observeOn(SchedulerProvider.getInstance().ui())
                .subscribeWith(new DisposableObserver<P>() {

                    @Override
                    public void onNext(@NonNull P p) {
                        useCaseCallback.onSuccess(p);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        useCaseCallback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    @Override
    public void unsubscribe() {
        mSubscription.dispose();
    }

    @Override
    public void subscribe() {
        if(mSubscription == null || mSubscription.isDisposed()){
            mSubscription = new CompositeDisposable();
        }
    }

    @Override
    public void clear() {
        if(mSubscription != null){
            mSubscription.clear();
        }
    }

    @Override
    public <Q extends RequestValues, P extends ResponseValue> P doAction(UseCase<Q, P> useCase) {
        return useCase.run();
    }

    @Override
    public <Q extends RequestValues, P extends ResponseValue> void subscribeRetrofit(UseCase<Q, P> useCase, final UseCase.UseCaseCallback<P> useCaseCallback) {
        // The network request might be handled in a different thread so make sure
        // Espresso knows
        // that the app is busy until the response is handled.
//        EspressoIdlingResource.increment(); // App is busy until further notice

        subscribe();

//        mSubscription.clear();

        mSubscription.add(useCase.getObservable(useCase.getRequestValues())
                .subscribeOn(SchedulerProvider.getInstance().io())
                .observeOn(SchedulerProvider.getInstance().ui())
                .subscribeWith(new DisposableObserver<P>() {

                    @Override
                    public void onNext(@NonNull P p) {
                        useCaseCallback.onSuccess(p);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        useCaseCallback.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

}
