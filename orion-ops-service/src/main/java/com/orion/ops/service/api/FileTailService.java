package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.entity.request.FileTailRequest;
import com.orion.ops.entity.vo.FileTailConfigVO;
import com.orion.ops.entity.vo.FileTailVO;

/**
 * 文件 tail service
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/8/1 23:33
 */
public interface FileTailService {

    /**
     * tail文件 检查文件是否存在
     *
     * @param request request
     * @return FileTailVO
     */
    HttpWrapper<FileTailVO> getTailToken(FileTailRequest request);

    /**
     * 添加 tail文件
     *
     * @param request request
     * @return id
     */
    Long insertTailFile(FileTailRequest request);

    /**
     * 修改 tail文件
     *
     * @param request request
     * @return effect
     */
    Integer updateTailFile(FileTailRequest request);

    /**
     * tail 列表
     *
     * @param request request
     * @return dataGrid
     */
    DataGrid<FileTailVO> tailFileList(FileTailRequest request);

    /**
     * tail 详情
     *
     * @param id id
     * @return vo
     */
    FileTailVO getTailDetail(Long id);

    /**
     * 更新 更新时间
     *
     * @param id id
     * @return effect
     */
    Integer updateFileUpdateTime(Long id);

    /**
     * 获取机器配置
     *
     * @param machineId machineId
     * @return config
     */
    FileTailConfigVO getMachineConfig(Long machineId);

}
