package net.lamgc.cgj;

import com.google.common.base.Strings;
import net.lamgc.cgj.util.DateParser;
import net.lamgc.cgj.util.PagesQualityParser;
import net.lamgc.utils.base.runner.ArgumentsRunner;
import net.lamgc.utils.base.runner.ArgumentsRunnerConfig;
import net.lamgc.utils.base.runner.exception.DeveloperRunnerException;
import net.lamgc.utils.base.runner.exception.NoSuchCommandException;
import net.lamgc.utils.base.runner.exception.ParameterNoFoundException;
import net.lz1998.cq.event.message.CQDiscussMessageEvent;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CQPluginMain extends CQPlugin {

    private final static String COMMAND_PREFIX = ".cgj";
    private final Logger log = LoggerFactory.getLogger("CQPluginMain@" + Integer.toHexString(this.hashCode()));
    private final ArgumentsRunnerConfig runnerConfig = new ArgumentsRunnerConfig();
    public final static Properties globalProp = new Properties();

    public CQPluginMain() {
        runnerConfig.setUseDefaultValueInsteadOfException(true);
        runnerConfig.setCommandIgnoreCase(true);
        runnerConfig.addStringParameterParser(new DateParser(new SimpleDateFormat("yyyy-MM-dd")));
        runnerConfig.addStringParameterParser(new PagesQualityParser());

        File globalPropFile = new File("./global.properties");
        if(globalPropFile.exists() && globalPropFile.isFile()) {
            log.info("正在加载全局配置文件...");
            try {
                globalProp.load(new FileInputStream(globalPropFile));
                log.info("全局配置文件加载完成.");
            } catch (IOException e) {
                log.error("加载全局配置文件时发生异常", e);
            }
        } else {
            log.info("未找到全局配置文件，跳过加载.");
        }
    }

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event) {
        //log.info("私聊消息到达: 发送者[{}], 消息内容: {}", event.getSender().getUserId(), event.getMessage());
        return processMessage(cq, event);
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event) {
        //log.info("群消息到达: 群[{}], 发送者[{}], 消息内容: {}", event.getGroupId(), event.getSender().getUserId(), event.getMessage());
        return processMessage(cq, event);
    }

    @Override
    public int onDiscussMessage(CoolQ cq, CQDiscussMessageEvent event) {
        //log.info("讨论组消息到达: 群[{}], 发送者[{}], 消息内容: {}", event.getDiscussId(), event.getSender().getUserId(), event.getMessage());
        return processMessage(cq, event);
    }

    public int processMessage(CoolQ cq, CQMessageEvent event) {
        String msg = event.getMessage();
        if(!msg.startsWith(COMMAND_PREFIX)) {
            return MESSAGE_IGNORE;
        }

        Pattern pattern = Pattern.compile("/\\s*(\".+?\"|[^:\\s])+((\\s*:\\s*(\".+?\"|[^\\s])+)|)|(\".+?\"|[^\"\\s])+");
        Matcher matcher = pattern.matcher(Strings.nullToEmpty(msg));
        ArrayList<String> argsList = new ArrayList<>();
        while (matcher.find()) {
            String arg = matcher.group();
            int startIndex = 0;
            int endIndex = arg.length();
            if(arg.startsWith("\"")) {
                while(arg.indexOf("\"") == startIndex) {
                    startIndex++;
                }
            }

            if(arg.endsWith("\"")) {
                while(arg.lastIndexOf("\"") == endIndex - 1) {
                    endIndex--;
                }
            }

            argsList.add(arg.substring(startIndex, endIndex));
        }
        String[] args = new String[argsList.size()];
        argsList.toArray(args);
        log.debug("传入参数: {}", Arrays.toString(args));

        log.warn("正在处理命令...");
        long time = System.currentTimeMillis();
        Object result;
        try {
            if(msg.toLowerCase().startsWith(COMMAND_PREFIX + "admin")) {
                if(!String.valueOf(event.getUserId()).equals(globalProp.getProperty("admin.adminId"))) {
                    sendMessage(cq, event, "你没有执行该命令的权限！", false);
                    return MESSAGE_BLOCK;
                } else {
                    result = new ArgumentsRunner(CQBotAdminProcess.class, runnerConfig)
                            .run(new CQBotAdminProcess(), args.length <= 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));
                }
            } else {
                result = new ArgumentsRunner(CQProcess.class, runnerConfig).run(args.length <= 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));
            }
        } catch(NoSuchCommandException e) {
            result = "没有这个命令！请使用“.cgj”查看帮助说明！";
        } catch(ParameterNoFoundException e) {
            result = "命令缺少参数: " + e.getParameterName();
        } catch(DeveloperRunnerException e) {
            log.error("执行命令时发生异常", e);
            result = "命令执行时发生错误，无法完成！";
        }
        log.warn("命令处理完成(耗时: {}ms)", System.currentTimeMillis() - time);
        if(Objects.requireNonNull(result) instanceof String) {
            try {
                sendMessage(cq, event, (String) result, false);
            } catch (Throwable e) {
                log.error("发送消息时发生异常", e);
            }
        }
        return MESSAGE_BLOCK;
    }

    private final static Logger msgLog = LoggerFactory.getLogger("SendMsg");

    /**
     * 发送消息
     * @param cq CoolQ对象
     * @param event 消息事件对象
     * @param message 消息内容
     * @param auto_escape 消息内容是否作为纯文本发送（即不解析 CQ 码），只在 message 字段是字符串时有效.
     */
    public static void sendMessage(CoolQ cq, CQMessageEvent event, String message, boolean auto_escape) {
        msgLog.debug("发送消息：{}", message);
        if(event instanceof CQPrivateMessageEvent) {
            CQPrivateMessageEvent _event = (CQPrivateMessageEvent) event;
            cq.sendPrivateMsg(_event.getSender().getUserId(), message, auto_escape);
        } else if(event instanceof CQGroupMessageEvent) {
            CQGroupMessageEvent _event = (CQGroupMessageEvent) event;
            cq.sendGroupMsg(_event.getGroupId(), message, auto_escape);
        } else if(event instanceof CQDiscussMessageEvent) {
            CQDiscussMessageEvent _event = (CQDiscussMessageEvent) event;
            cq.sendGroupMsg(_event.getDiscussId(), message, auto_escape).getData().getMessageId();
        }
    }


}
