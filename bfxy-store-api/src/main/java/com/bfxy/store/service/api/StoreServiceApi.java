package com.bfxy.store.service.api;


import java.util.Date;

/**
 *  库存服务对外dubbo接口
 */
public interface StoreServiceApi {
    
    /**
     * 获取版本号(乐观锁用)
     * @param supplierId
     * @param goodsId
     * @return
     */
    Integer selectVersion(String supplierId, String goodsId);
    
    /**
     * 查询库存剩余量
     * @param supplierId
     * @param goodsId
     * @return
     */
    Integer selectStoreCount(String supplierId, String goodsId);
    
    /**
     * 返回修改的记录条数
     * @param version
     * @param supplierId
     * @param goodsId
     * @param updateBy
     * @param updateDate
     * @return
     */
    Integer updateStoreCountByVersion(int version, String supplierId, String goodsId, String updateBy, Date updateDate);
}
