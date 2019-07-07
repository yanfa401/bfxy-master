package com.bfxy.bfxystore.service.dubbo.provider;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.bfxy.bfxystore.mapper.StoreMapper;
import com.bfxy.store.service.api.StoreServiceApi;

/**
 * 描述：库存服务 dubbo接口实现
 *
 * @author xielei
 * @date 2019/07/05
 */

@Service(
        version = "0.0.1",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
public class StoreServiceImpl implements StoreServiceApi {
    
    @Autowired
    private StoreMapper storeMapper;
    
    /**
     * 获取版本号(乐观锁用)
     *
     * @param supplierId
     * @param goodsId
     * @return
     */
    @Override
    public Integer selectVersion(String supplierId, String goodsId) {
        return storeMapper.selectVersion(supplierId, goodsId);
    }
    
    /**
     * 查询库存剩余量
     *
     * @param supplierId
     * @param goodsId
     * @return
     */
    @Override
    public Integer selectStoreCount(String supplierId, String goodsId) {
        return storeMapper.selectStoreCount(supplierId, goodsId);
    }
    
    /**
     * 返回修改的记录条数
     *
     * @param version
     * @param supplierId
     * @param goodsId
     * @param updateBy
     * @param updateDate
     * @return
     */
    @Override
    public Integer updateStoreCountByVersion(int version, String supplierId, String goodsId, String updateBy, Date updateDate) {
        return storeMapper.updateStoreCountByVersion(version, supplierId, goodsId, "admin", new Date());
    }
}
