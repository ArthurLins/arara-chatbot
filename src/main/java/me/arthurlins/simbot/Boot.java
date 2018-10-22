package me.arthurlins.simbot;

import me.arthurlins.simbot.core.answers.AnswersManager;
import me.arthurlins.simbot.storage.Database;
import me.arthurlins.simbot.ws.EmbedServer;

import java.time.Instant;
import java.util.Date;
import java.util.Scanner;


public class Boot {

    public static void main(String[] args){
        AnswersManager.initialize();
        //Todo:: Organize.
        EmbedServer.initialize();
//        ModuleLoader ml = new ModuleLoader();
//        new Thread(()->{
//            String command;
//            do {
//                Scanner in = new Scanner(System.in);
//                command = in.nextLine();
//                switch (command){
//                    case "unload":
//                        ml.unloadAll();
//                       break;
//                    case "load":
//                        ml.loadAll();
//                        break;
//                    case "reload":
//                        ml.refresh();
//                        break;
//                    default:
//                        System.out.println("Invalid command.");
//                }
//            } while (!command.equals("quit"));
//        }).start();
    }

}
