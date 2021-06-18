package com.orion.ops.handler.exec.impl;

import com.orion.constant.Letters;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.ops.handler.exec.AbstractExecHandler;
import com.orion.ops.handler.exec.ExecHint;
import com.orion.remote.channel.ssh.BaseRemoteExecutor;
import com.orion.utils.Strings;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Date;

/**
 * 普通命令执行器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/7 17:17
 */
@Slf4j
public class CommandExecHandler extends AbstractExecHandler {

    protected String logPathSuffix;

    protected String logPath;

    protected OutputStream logOutputStream;

    public CommandExecHandler(ExecHint hint) {
        super(hint);
        this.logPathSuffix = "/command";
    }

    @Override
    protected void openComputed() {
        this.getLogPath();
        log.info("execHandler-打开日志流 {} {}", execId, logPath);
        File logFile = new File(MachineEnvAttr.LOG_PATH.getValue() + logPath);
        this.logOutputStream = Files1.openOutputStreamFastSafe(logFile);
        this.logOpenComputed();
    }

    /**
     * 日志初始化完毕 写入数据
     */
    protected void logOpenComputed() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("# 准备执行命令\n")
                    .append("执行用户: ").append(hint.getUsername()).append(Letters.LF)
                    .append("任务id: ").append(execId).append(Letters.LF)
                    .append("任务类型: ").append(hint.getExecType().name()).append(Letters.LF)
                    .append("机器id: ").append(hint.getMachineId()).append(Letters.LF)
                    .append("机器host: ").append(machine.getMachineHost()).append(Letters.LF)
                    .append("机器user: ").append(machine.getUsername()).append(Letters.LF)
                    .append("机器name: ").append(machine.getMachineName()).append(Letters.LF)
                    .append("开始时间: ").append(Dates.format(hint.getStartDate(), Dates.YMDHMSS)).append(Letters.LF);
            Long relId = hint.getRelId();
            if (relId != null) {
                sb.append("relId: ").append(relId).append(Letters.LF);
            }
            String description = hint.getDescription();
            if (!Strings.isBlank(description)) {
                sb.append("描述: ").append(description).append(Letters.LF);
            }
            sb.append(Letters.LF);

            if (env != null) {
                sb.append("# 机器环境变量\n");
                env.forEach((k, v) -> sb.append(k).append(" = ").append(v).append(Letters.LF));
                sb.append(Letters.LF);
            }

            sb.append("# 执行命令开始\n")
                    .append(hint.getRealCommand())
                    .append("\n\n--------------------------------------------------\n\n");
            logOutputStream.write(Strings.bytes(sb.toString()));
            logOutputStream.flush();
        } catch (Exception e) {
            log.error("execHandler-写入日志失败 {} {}", execId, e);
            e.printStackTrace();
        }
        this.env = null;
    }

    /**
     * 获取日志目录
     */
    protected void getLogPath() {
        this.logPath = Const.EXEC_LOG_PATH + logPathSuffix + "/" + execId
                + "_" + hint.getMachineId()
                + "_" + Dates.current(Dates.YMDHMS2) + ".log";
        CommandExecDO update = new CommandExecDO();
        update.setId(execId);
        update.setLogPath(logPath);
        commandExecDAO.updateById(update);
    }

    @Override
    protected void processStandardOutputStream(BaseRemoteExecutor executor, InputStream in) {
        try {
            Streams.transfer(in, logOutputStream);
        } catch (IOException ex) {
            log.error("execHandler-执行命令处理流失败 {} {}", execId, ex);
            ex.printStackTrace();
        }
    }

    @Override
    protected void callback(BaseRemoteExecutor executor) {
        super.callback(executor);
        Date endDate = new Date();
        StringBuilder sb = new StringBuilder()
                .append("\n--------------------------------------------------\n")
                .append("# 命令执行完毕\n")
                .append("exit code: ").append(hint.getExitCode()).append(Letters.LF)
                .append("结束时间: ").append(Dates.format(endDate, Dates.YMDHMSS))
                .append("; used ").append(endDate.getTime() - hint.getStartDate().getTime()).append(" ms\n");
        try {
            logOutputStream.write(Strings.bytes(sb.toString()));
            logOutputStream.flush();
        } catch (Exception e) {
            log.error("execHandler-写入日志失败 {} {}", execId, e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onException(Exception e) {
        super.onException(e);
        StringBuilder sb = new StringBuilder()
                .append("\n--------------------------------------------------\n")
                .append("# 命令执行异常\n");
        try {
            logOutputStream.write(Strings.bytes(sb.toString()));
            e.printStackTrace(new PrintStream(logOutputStream));
            logOutputStream.flush();
        } catch (Exception ex) {
            log.error("execHandler-写入日志失败 {} {}", execId, ex);
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        super.close();
        Streams.close(logOutputStream);
    }

}