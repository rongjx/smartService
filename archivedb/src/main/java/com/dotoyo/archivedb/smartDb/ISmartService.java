package com.dotoyo.archivedb.smartDb;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface ISmartService {

    /**
     * <p>
     * 插入一条记录（选择字段，策略插入）
     * </p>
     *
     * @param entity 实体对象
     * @return boolean
     */
    <T> boolean insert(T entity, Class<T> entityCls);

    /**
     * <p>
     * 插入一条记录（全部字段）
     * </p>
     *
     * @param entity 实体对象
     * @return boolean
     */
    <T> boolean insertAllColumn(T entity, Class<T> entityCls);

    /**
     * <p>
     * 插入（批量），该方法不适合 Oracle
     * </p>
     *
     * @param entityList 实体对象列表
     * @return boolean
     */
    <T> boolean insertBatch(List<T> entityList, Class<T> entityCls);

    /**
     * <p>
     * 插入（批量）
     * </p>
     *
     * @param entityList 实体对象列表
     * @param batchSize  插入批次数量
     * @return boolean
     */
    <T> boolean insertBatch(List<T> entityList, int batchSize, Class<T> entityCls);

    /**
     * <p>
     * 批量修改插入
     * </p>
     *
     * @param entityList 实体对象列表
     * @return boolean
     */
    <T> boolean insertOrUpdateBatch(List<T> entityList, Class<T> entityCls);

    /**
     * <p>
     * 批量修改插入
     * </p>
     *
     * @param entityList 实体对象列表
     * @param batchSize
     * @return boolean
     */
    <T> boolean insertOrUpdateBatch(List<T> entityList, int batchSize, Class<T> entityCls);

    /**
     * <p>
     * 批量修改或插入全部字段
     * </p>
     *
     * @param entityList 实体对象列表
     * @return boolean
     */
    <T> boolean insertOrUpdateAllColumnBatch(List<T> entityList, Class<T> entityCls);

    /**
     * 批量修改或插入全部字段
     *
     * @param entityList 实体对象列表
     * @param batchSize
     * @return boolean
     */
    <T> boolean insertOrUpdateAllColumnBatch(List<T> entityList, int batchSize, Class<T> entityCls);

    /**
     * <p>
     * 根据 ID 删除
     * </p>
     *
     * @param id 主键ID
     * @return boolean
     */
    <T> boolean deleteById(Serializable id, Class<T> entityCls);


    /**
     * <p>
     * 根据 entity 条件，删除记录
     * </p>
     *
     * @param wrapper 实体包装类 {@link Wrapper}
     * @return boolean
     */
    <T> boolean delete(Wrapper<T> wrapper, Class<T> entityCls);

    /**
     * <p>
     * 删除（根据ID 批量删除）
     * </p>
     *
     * @param idList 主键ID列表
     * @return boolean
     */
    <T> boolean deleteBatchIds(Collection<? extends Serializable> idList, Class<T> entityCls);

    /**
     * <p>
     * 根据 ID 选择修改
     * </p>
     *
     * @param entity 实体对象
     * @return boolean
     */
    <T> boolean updateById(T entity, Class<T> entityCls);

    /**
     * <p>
     * 根据 ID 修改全部字段
     * </p>
     *
     * @param entity 实体对象
     * @return boolean
     */
    <T> boolean updateAllColumnById(T entity, Class<T> entityCls);

    /**
     * <p>
     * 根据 whereEntity 条件，更新记录
     * </p>
     *
     * @param entity  实体对象
     * @param wrapper 实体包装类 {@link Wrapper}
     * @return boolean
     */
    <T> boolean update(T entity, Wrapper<T> wrapper, Class<T> entityCls);


    /**
     * <p>
     * 根据ID 批量更新
     * </p>
     *
     * @param entityList 实体对象列表
     * @return boolean
     */
    <T> boolean updateBatchById(List<T> entityList, Class<T> entityCls);

    /**
     * <p>
     * 根据ID 批量更新
     * </p>
     *
     * @param entityList 实体对象列表
     * @param batchSize  更新批次数量
     * @return boolean
     */
    <T> boolean updateBatchById(List<T> entityList, int batchSize, Class<T> entityCls);

    /**
     * <p>
     * 根据ID 批量更新全部字段
     * </p>
     *
     * @param entityList 实体对象列表
     * @return boolean
     */
    <T> boolean updateAllColumnBatchById(List<T> entityList, Class<T> entityCls);

    /**
     * <p>
     * 根据ID 批量更新全部字段
     * </p>
     *
     * @param entityList 实体对象列表
     * @param batchSize  更新批次数量
     * @return boolean
     */
    <T> boolean updateAllColumnBatchById(List<T> entityList, int batchSize, Class<T> entityCls);

    /**
     * <p>
     * TableId 注解存在更新记录，否插入一条记录
     * </p>
     *
     * @param entity 实体对象
     * @return boolean
     */
    <T> boolean insertOrUpdate(T entity, Class<T> entityCls);

    /**
     * 插入或修改一条记录的全部字段
     *
     * @param entity 实体对象
     * @return boolean
     */
    <T> boolean insertOrUpdateAllColumn(T entity, Class<T> entityCls);

    /**
     * <p>
     * 根据 ID 查询
     * </p>
     *
     * @param id 主键ID
     * @return T
     */
    <T> T selectById(Serializable id, Class<T> entityCls);

    /**
     * <p>
     * 查询（根据ID 批量查询）
     * </p>
     *
     * @param idList 主键ID列表
     * @return List<T>
     */
    <T> List<T> selectBatchIds(Collection<? extends Serializable> idList, Class<T> entityCls);


    /**
     * <p>
     * 根据 Wrapper，查询一条记录
     * </p>
     *
     * @param wrapper 实体对象
     * @return T
     */
    <T> T selectOne(Wrapper<T> wrapper, Class<T> entityCls);


    /**
     * <p>
     * 根据 Wrapper 条件，查询总记录数
     * </p>
     *
     * @param wrapper 实体对象
     * @return int
     */
    <T> int selectCount(Wrapper<T> wrapper, Class<T> entityCls);

    /**
     * <p>
     * 查询列表
     * </p>
     *
     * @param wrapper 实体包装类 {@link Wrapper}
     * @return
     */
    <T> List<T> selectList(Wrapper<T> wrapper, Class<T> entityCls);

    /**
     * <p>
     * 翻页查询
     * </p>
     *
     * @param page 翻页对象
     * @return
     */
    <T> Page<T> selectPage(Page<T> page, Class<T> entityCls);


    /**
     * <p>
     * 翻页查询
     * </p>
     *
     * @param page    翻页对象
     * @param wrapper 实体包装类 {@link Wrapper}
     * @return
     */
    <T> Page<T> selectPage(Page<T> page, Wrapper<T> wrapper, Class<T> entityCls);
}
