# 背景
使用 mybaties 或者 hibernate 等 DB框架时经常需要基于每个实体表创建一个 service ,但是实际业务开发中,有很多中间表仅仅只是执行一些基础的增删改查操作,也就是更多的是一个模块需要一个 service 就好,但是这些 DB 框架的使用 service 层都需要指定实体类型,为此我们考虑使用 ClassPool 类来动态创建实际调用的 service 

# service 动态生成

```
package com.dotoyo.archivedb.smartDb.buildBean;

import com.baomidou.mybatisplus.service.IService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute.ClassSignature;
import javassist.bytecode.SignatureAttribute.ClassType;
import javassist.bytecode.SignatureAttribute.TypeArgument;
import javassist.bytecode.annotation.Annotation;
import lombok.extern.slf4j.Slf4j;

import java.security.ProtectionDomain;

/**
 * 为实体动态生成service
 *
 * @author rjx
 * <br>2019-12-06
 */
@Slf4j
public class ServiceGenerator {


    public static String getPackagePatch(Class<?> cls) {
        return cls.getPackage().getName().replaceAll("package ", "");
    }

    public static Class<?> buildClass(Class<?> entityCls, String packagePath, String serviceImplName, String mapperName)
            throws Exception {

        ClassPool pool = ClassPool.getDefault();
        String incls = packagePath + "." + serviceImplName;
        CtClass cc = pool.makeClass(incls);

        //父类
        CtClass superServiceImpl = pool.getCtClass(ServiceImpl.class.getName());
        cc.setSuperclass(superServiceImpl);

        //实现接口
        CtClass implInterface = pool.get(IService.class.getName());
        cc.setInterfaces(new CtClass[]{implInterface});

        //填充泛型
        buildServiceGerType(cc, ServiceImpl.class.getTypeName(), entityCls.getTypeName(), mapperName, IService.class.getTypeName());
        addClassAnnotation(cc);

        ClassLoader cl = entityCls.getClassLoader();
        ProtectionDomain pd = entityCls.getProtectionDomain();
        cc.toClass(cl, pd);

        cc.detach();
        Class<?> serviceBeanCls = Class.forName(packagePath + "." + serviceImplName);

        if (log.isInfoEnabled()) {
            log.info("Javassit成功生成[{}.class]并加载至JVM内存; {}", serviceImplName, cl);

            String path = "D://AssisService";
            //写入文件 调试时候使用用于测试查看生成的文件是否正确
            cc.writeFile(path);
            log.info("{}.class 写入文件path={}", serviceImplName, path);
        }
        return serviceBeanCls;
    }

    /**
     * 填充泛型参数
     *
     * @param ctClass
     * @param superServiceImplName
     * @param entityName
     * @param mapperName
     * @param interfaceClass
     */
    public static void buildServiceGerType(CtClass ctClass, String superServiceImplName, String entityName, String mapperName, String interfaceClass) {

        TypeArgument entityTypeArg = new TypeArgument(new ClassType(entityName));
        TypeArgument mapperTypeArg = new TypeArgument(new ClassType(mapperName));

        ClassType interfaceClassType = new ClassType(interfaceClass, new TypeArgument[]{entityTypeArg});
        ClassType superClassType = new ClassType(superServiceImplName, new TypeArgument[]{mapperTypeArg, entityTypeArg});

        // 实现类的泛型描述 实现类的泛型组成 自身泛型,父类泛型,接口泛型[], 因此这里均没有,则实际传入null
        ClassSignature signature = new ClassSignature(null, superClassType, new ClassType[]{interfaceClassType});
        ctClass.setGenericSignature(signature.encode());
    }

    /**
     * 添加Servcie注解
     *
     * @param cc
     */
    public static void addClassAnnotation(CtClass cc) {
        ConstPool constPool = cc.getClassFile().getConstPool();
        AnnotationsAttribute bodyAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation bodyAnnot = new Annotation("org.springframework.stereotype.Service", constPool);
        bodyAttr.addAnnotation(bodyAnnot);
        cc.getClassFile().addAttribute(bodyAttr);
    }
}

```
## service 使用流程
首先从 spring 容器中根据 beanName 获取 service,第一次获取是没有的,因为还没有动态创建,获取到为空后我们开始使用 classPool 构造一个,并将其注入 spring 容器中，这样后续就可以直接从 spring 中获取使用了

```
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
```
## spring上下文使用
要想动态从 spring 上下文中获取 bean,并能动态注册 bean,需要定义springUtil工具类在在启动时候装载上下文对象

```
package com.dotoyo.archivedb.smartDb.springContext;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author rjx
 * @date 2019-12-08
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtils.applicationContext == null) {
            SpringUtils.applicationContext = applicationContext;
        }
        System.out.println("---------------------------------------------------------------------");
        System.out.println("---------------------------------------------------------------------");
        System.out.println("---------------SpringUtil------------------------------------------------------");
        System.out.println("========ApplicationContext配置成功,在普通类可以通过调用SpringUtils.getAppContext()获取applicationContext对象,applicationContext=" + SpringUtils.applicationContext + "========");
        System.out.println("---------------------------------------------------------------------");
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
```

# mybatisplus 动态 service 封装
做完了 service 动态获取,我们开始对 mybatisplus 进行封装,思路为定义好基础  service,并查看 mybatisplus 基础 service 常用方法,对我们常用的进行定义封装,增加实体泛型参数,在基础 serviceImpl 的实现上,我们调用动态获取 service然后调用 mybatisplus自己的 service来实现

```
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
```



