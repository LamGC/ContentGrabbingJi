package net.lamgc.cgj.bot.framework.message;

/**
 * 自定义 BotCode 方法.
 * @author LamGC
 */
public class CustomBotCodeFunction implements BotCodeFunction {

    private final String name;
    private final boolean headerFunction;


    public CustomBotCodeFunction(String name, boolean isHeaderFunction) {
        this.name = name;
        headerFunction = isHeaderFunction;
    }

    @Override
    public String getFunctionName() {
        return name;
    }

    @Override
    public boolean headerFunction() {
        return headerFunction;
    }
}
