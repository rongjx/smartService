package com.dotoyo.archivedb.smartDb.buildBean;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.service.IService;
import com.dotoyo.archivedb.smartDb.springContext.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * mapper 和 service 获取帮助类
 */
@Slf4j
public class BuildBeanAssist {

    /**
     * 首字母小写
     */
    public static String firstLower(String content) {
        return content.substring(0, 1).toLowerCase() + content.substring(1, content.length());
    }

    /**
     * 首字母大写
     */
    public static String firstUpper(String content) {
        return content.substring(0, 1).toUpperCase() + content.substring(1, content.length());
    }


    /**
     * 根据实体类获取对应的mapper文件 (从spring容器中获取没有则构造生成放入容器)
     *
     * @param cls
     * @return
     */
    public static <T> BaseMapper<T> getMapper(Class<T> cls) {
        String mapperName = firstLower(cls.getSimpleName()) + "Mapper";
        BaseMapper<T> mapper = (BaseMapper<T>) SpringUtils.getBean(mapperName);
        if (mapper == null) {
            log.error("找不到{}对应的mapper", cls);
        }
        return mapper;
    }


    /**
     * 根据实体类获取对应的mapper文件 (从spring容器中获取没有则构造生成放入容器)
     *
     * @param cls
     * @return
     */
    public static <T> IService<T> getService(Class<T> cls) {
        String serviceImplName = firstLower(cls.getSimpleName()) + "_Javassit_ServiceImpl";
        IService<T> service = null;
        try {
            service = (IService<T>) SpringUtils.getBean(serviceImplName);
        } catch (BeansException e) {
            log.info("第一次从spring容器中没有获取到 {}", serviceImplName);
        }

        if (service == null) {
            BaseMapper<T> mapper = null;
            try {
                mapper = getMapper(cls);
            } catch (BeansException e) {
                log.error("找不到{}对应的mapper", cls);
                throw e;
            }
            String packagePath = ServiceGenerator.getPackagePatch(cls);
            try {
                Class serviceBeanCls = ServiceGenerator.buildClass(cls, packagePath, firstUpper(serviceImplName), mapper.getClass().getGenericInterfaces()[0].getTypeName());
                registerBeanDefinition(serviceBeanCls, serviceImplName);
                log.info("======================================= 提示 =======================================");
                log.info("使用Javassit动态生成 {}<{}> 并加载至SpringContext中", serviceBeanCls.getName(), cls.getSimpleName());
                service = (IService<T>) SpringUtils.getBean(serviceImplName);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        if (service == null) {
            log.error("找不到{}对应的service", cls);
            throw new RuntimeException("找不到" + cls + "对应的service");
        }
        return service;
    }


    /**
     * 动态注册bean
     *
     * @param beanCls
     */
    public static void registerBeanDefinition(Class<?> beanCls, String beanName) {
        DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) ((ConfigurableApplicationContext) SpringUtils.getApplicationContext()).getBeanFactory();
        BeanDefinitionBuilder beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(beanCls);
        // 将实例注册spring容器中 bs 等同于 id配置
        dbf.registerBeanDefinition(beanName, beanDefinition.getBeanDefinition());
    }
}
