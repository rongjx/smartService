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
