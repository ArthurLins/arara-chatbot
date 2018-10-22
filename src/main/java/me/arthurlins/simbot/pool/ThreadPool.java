package me.arthurlins.simbot.pool;

import java.sql.Time;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ThreadPool {



    private static ThreadPoolExecutor executor;


    private static void init(){
        if (executor == null){
            executor = new ThreadPoolExecutor(1,
                    10,
                    5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        }
    }

    public static void async(Runnable tr){
        if (executor == null){
            init();
        }
        executor.execute(tr);
    }


}
