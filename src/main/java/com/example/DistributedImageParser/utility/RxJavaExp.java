package com.example.DistributedImageParser.utility;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/*
 * by u.cavusoglu
 * */


/*
 * Let us assume MultipleThreading.
 *  A thread enters the synchronized block to get an instance, on the other hand the other is blocked.
 *  Hence the other thread should wait till the accompanied thread's job would have been done.
 * */

@Service  //DoubleLock method is used for SingletonPattern
public class RxJavaExp<T, U> {

    private static RxJavaExp SINGLE_INSTANCE = null;

    private RxJavaExp() {
    }

    public static RxJavaExp getInstance() {
        if (SINGLE_INSTANCE == null) {
            synchronized (RxJavaExp.class) {
                if (SINGLE_INSTANCE == null) {
                    SINGLE_INSTANCE = new RxJavaExp();
                }
            }
        }
        return SINGLE_INSTANCE;
    }

    public Boolean responsive(Function<U, T> function) { // make you wait for respond
        AtomicReference<Boolean> status = new AtomicReference<>();
        Observable.fromArray(new LinkedBlockingQueue<Boolean>() {{
            add(true);
        }}.toArray()).doOnNext(emitter -> function.apply((U) emitter)).doFinally(()-> {
            status.set(Boolean.TRUE);
        }).observeOn(Schedulers.newThread()).observeOn(Schedulers.newThread())
                .subscribe();
        return status.get();
    }

    public synchronized void doSilently(Function<U, T> function) { // does not make you wait for respond, it works on the background
        BlockingQueue<Boolean> list = new LinkedBlockingQueue<>();
        list.add(Boolean.TRUE);
        Observable.create(emitter -> {
            list.forEach(emitter::onNext);
        }).observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe((element -> function.apply((U) element)), Throwable::printStackTrace);
    }

}
