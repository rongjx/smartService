package com.dotoyo.archivedb.smartDb;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.dotoyo.archivedb.smartDb.buildBean.BuildBeanAssist;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 覆写mybatiesPlus 基础实现方法
 * 增加实体泛型参数
 * 动态注入mapper和Service
 */
public class SmartServiceImpl implements ISmartService {

    /**
     * 动态获取service
     *
     * @param entityCls
     * @return
     */
    private static <T> IService<T> getService(Class<T> entityCls) {
        return BuildBeanAssist.getService(entityCls);
    }

    @Override
    public <T> boolean insert(T entity, Class<T> entityCls) {
        IService service=getService(entityCls);
        return service.insert(entity);
    }

    @Override
    public <T> boolean insertAllColumn(T entity, Class<T> entityCls) {
        return getService(entityCls).insertAllColumn((T) entity);
    }

    @Override
    public <T> boolean insertBatch(List<T> entityList, Class<T> entityCls) {
        return getService(entityCls).insertBatch(entityList);
    }

    @Override
    public <T> boolean insertBatch(List<T> entityList, int batchSize, Class<T> entityCls) {
        return getService(entityCls).insertBatch(entityList, batchSize);
    }

    @Override
    public <T> boolean insertOrUpdateBatch(List<T> entityList, Class<T> entityCls) {
        return getService(entityCls).insertOrUpdateBatch(entityList);
    }

    @Override
    public <T> boolean insertOrUpdateBatch(List<T> entityList, int batchSize, Class<T> entityCls) {
        return false;
    }

    @Override
    public <T> boolean insertOrUpdateAllColumnBatch(List<T> entityList, Class<T> entityCls) {
        return false;
    }

    @Override
    public <T> boolean insertOrUpdateAllColumnBatch(List<T> entityList, int batchSize, Class<T> entityCls) {
        return false;
    }

    @Override
    public <T> boolean deleteById(Serializable id, Class<T> entityCls) {
        return getService(entityCls).deleteById(id);
    }

    @Override
    public <T> boolean delete(Wrapper<T> wrapper, Class<T> entityCls) {
        return getService(entityCls).delete(wrapper);
    }

    @Override
    public <T> boolean deleteBatchIds(Collection<? extends Serializable> idList, Class<T> entityCls) {
        return getService(entityCls).deleteBatchIds(idList);
    }

    @Override
    public <T> boolean updateById(T entity, Class<T> entityCls) {
        return getService(entityCls).updateById(entity);
    }

    @Override
    public <T> boolean updateAllColumnById(T entity, Class<T> entityCls) {
        return getService(entityCls).updateAllColumnById(entity);
    }

    @Override
    public <T> boolean update(T entity, Wrapper<T> wrapper, Class<T> entityCls) {
        return getService(entityCls).update(entity, wrapper);
    }

    @Override
    public <T> boolean updateBatchById(List<T> entityList, Class<T> entityCls) {
        return getService(entityCls).updateBatchById(entityList);
    }

    @Override
    public <T> boolean updateBatchById(List<T> entityList, int batchSize, Class<T> entityCls) {
        return getService(entityCls).updateBatchById(entityList);
    }

    @Override
    public <T> boolean updateAllColumnBatchById(List<T> entityList, Class<T> entityCls) {
        return getService(entityCls).updateAllColumnBatchById(entityList);
    }

    @Override
    public <T> boolean updateAllColumnBatchById(List<T> entityList, int batchSize, Class<T> entityCls) {
        return getService(entityCls).updateAllColumnBatchById(entityList);
    }

    @Override
    public <T> boolean insertOrUpdate(T entity, Class<T> entityCls) {
        return getService(entityCls).insertOrUpdate(entity);
    }

    @Override
    public <T> boolean insertOrUpdateAllColumn(T entity, Class<T> entityCls) {
        return getService(entityCls).insertOrUpdateAllColumn(entity);
    }

    @Override
    public <T> T selectById(Serializable id, Class<T> entityCls) {
        return getService(entityCls).selectById(id);
    }

    @Override
    public <T> List<T> selectBatchIds(Collection<? extends Serializable> idList, Class<T> entityCls) {
        return getService(entityCls).selectBatchIds(idList);
    }

    @Override
    public <T> T selectOne(Wrapper<T> wrapper, Class<T> entityCls) {
        return getService(entityCls).selectOne(wrapper);
    }

    @Override
    public <T> int selectCount(Wrapper<T> wrapper, Class<T> entityCls) {
        return getService(entityCls).selectCount(wrapper);
    }

    @Override
    public <T> List<T> selectList(Wrapper<T> wrapper, Class<T> entityCls) {
        return getService(entityCls).selectList(wrapper);
    }

    @Override
    public <T> Page<T> selectPage(Page<T> page, Class<T> entityCls) {
        return getService(entityCls).selectPage(page);
    }

    @Override
    public <T> Page<T> selectPage(Page<T> page, Wrapper<T> wrapper, Class<T> entityCls) {
        return getService(entityCls).selectPage(page, wrapper);
    }
}
