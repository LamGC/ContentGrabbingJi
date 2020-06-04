package net.lamgc.cgj.bot.framework.cli;

import net.lamgc.cgj.bot.boot.ApplicationBoot;
import net.lamgc.cgj.bot.event.BotEventHandler;
import net.lamgc.cgj.bot.message.MessageSenderBuilder;

import java.util.Scanner;

public class ConsoleMain {

    public static void start() {
        MessageSenderBuilder.setCurrentMessageSenderFactory(new ConsoleMessageSenderFactory());
        ApplicationBoot.initialBot();
        Scanner scanner = new Scanner(System.in);
        boolean isGroup = false;
        do {
            String input = scanner.nextLine();
            if(input.equalsIgnoreCase("#exit")) {
                System.out.println("退出应用...");
                break;
            } else if(input.equalsIgnoreCase("#setgroup")) {
                isGroup = !isGroup;
                System.out.println("System: 群模式状态已变更: " + isGroup);
                continue;
            }
            BotEventHandler.executeMessageEvent(new ConsoleMessageEvent(input, isGroup));
        } while(true);
    }

}
