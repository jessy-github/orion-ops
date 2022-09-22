package com.orion.ops.handler.exporter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.ExportType;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.dao.UserEventLogDAO;
import com.orion.ops.entity.domain.UserEventLogDO;
import com.orion.ops.entity.exporter.EventLogExportDTO;
import com.orion.ops.entity.request.data.DataExportRequest;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.spring.SpringHolder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * 用户操作日志 数据导出器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/8 18:54
 */
public class UserEventLogDataExporter extends AbstractDataExporter<EventLogExportDTO> {

    private static final UserEventLogDAO userEventLogDAO = SpringHolder.getBean(UserEventLogDAO.class);

    public UserEventLogDataExporter(DataExportRequest request, HttpServletResponse response) {
        super(ExportType.USER_EVENT_LOG, request, response);
        // 设置用户id
        if (Currents.isAdministrator()) {
            if (Const.ENABLE.equals(request.getOnlyMyself())) {
                request.setUserId(Currents.getUserId());
            }
        } else {
            request.setUserId(Currents.getUserId());
        }
    }

    @Override
    protected List<EventLogExportDTO> queryData() {
        // 查询数据
        Long userId = request.getUserId();
        Integer classify = request.getClassify();
        LambdaQueryWrapper<UserEventLogDO> wrapper = new LambdaQueryWrapper<UserEventLogDO>()
                .eq(UserEventLogDO::getExecResult, Const.ENABLE)
                .eq(Objects.nonNull(userId), UserEventLogDO::getUserId, userId)
                .eq(Objects.nonNull(classify), UserEventLogDO::getEventClassify, classify)
                .orderByDesc(UserEventLogDO::getCreateTime);
        List<UserEventLogDO> logList = userEventLogDAO.selectList(wrapper);
        return Converts.toList(logList, EventLogExportDTO.class);
    }

    @Override
    protected void setEventParams() {
        super.setEventParams();
        EventParamsHolder.addParam(EventKeys.USER_ID, request.getUserId());
        EventParamsHolder.addParam(EventKeys.CLASSIFY, request.getClassify());
    }

}
