package com.hce.paymentgateway.service;

import com.google.common.collect.Maps;
import com.hce.paymentgateway.api.hce.TradeRequest;
import com.hce.paymentgateway.util.ServiceParameter;
import com.hce.paymentgateway.util.ServiceWrapper;
import org.apache.commons.lang.StringUtils;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author Heling.Yao
 * @Date 10:17 2018/5/25
 */
@Service("scanService")
public class ScanService implements ApplicationContextAware {

    private static final String PACKAGE_PATH = "com.hce.paymentgateway.service.impl";

    private static final ConcurrentMap<String, ServiceWrapper> MAPPER = Maps.newConcurrentMap();

    private ApplicationContext applicationContext;

    public ServiceWrapper getTransactionService(String productType) {
        if(StringUtils.isBlank(productType)) {
            return null;
        }
        ServiceWrapper serviceWrapper = MAPPER.get(productType);
        return serviceWrapper;
    }

    private void scanFile() {
        TypeAnnotationsScanner scanner = new TypeAnnotationsScanner();
        SubTypesScanner subTypesScanner = new SubTypesScanner();
        Configuration configuration = ConfigurationBuilder.build();
        scanner.setConfiguration(configuration);
        subTypesScanner.setConfiguration(configuration);
        Reflections reflections = new Reflections(PACKAGE_PATH, scanner, subTypesScanner);
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(ServiceParameter.class);

        for(Class<?> clazz: classSet) {
            if(!TransactionService.class.isAssignableFrom(clazz)) continue;

            String productType = extractProductType(clazz);
            if(StringUtils.isBlank(productType)) continue;

            String springBeanName = extractBeanName(clazz);
            TransactionService service = applicationContext.getBean(springBeanName, TransactionService.class);
            if(service == null) continue;

            Type type = clazz.getGenericSuperclass();
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Class<? extends TradeRequest> argumentClass = (Class) parameterizedType.getActualTypeArguments()[0];

            ServiceWrapper serviceWrapper = new ServiceWrapper(service, argumentClass);
            ServiceWrapper old = MAPPER.putIfAbsent(productType, serviceWrapper);
            if(old != null) throw new RuntimeException("ProductType类型" + productType + "重复!");
        }
    }

    private String extractProductType(Class<?> clazz) {
        ServiceParameter annotation = clazz.getAnnotation(ServiceParameter.class);
        if(annotation != null) {
            return annotation.productType();
        }
        return null;
    }

    private String extractBeanName(Class<?> clazz) {
        String springBeanName = null;
        Service service = clazz.getAnnotation(Service.class);
        if(service != null && StringUtils.isNotBlank(service.value())) {
            springBeanName = service.value();
        }
        Component component = clazz.getAnnotation(Component.class);
        if(component != null && StringUtils.isNotBlank(component.value())) {
            springBeanName = component.value();
        }
        if(StringUtils.isBlank(springBeanName)) {
            String simpleName = clazz.getSimpleName();
            springBeanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        }
        return springBeanName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        scanFile();
    }

}
