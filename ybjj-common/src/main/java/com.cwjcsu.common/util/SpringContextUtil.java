package com.cwjcsu.common.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component(value = "springContextUtil")
public class SpringContextUtil implements ApplicationContextAware {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		context = ctx;
		bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	public static <T> T getBean(String name) throws BeansException {
		ApplicationContext ctx = context;
		T t = null;
		while (ctx != null && (t = (T) ctx.getBean(name)) == null)
			ctx = ctx.getParent();
		return (T) t;
	}

	public static <T> T getBean(Class<T> type) throws BeansException {
		ApplicationContext ctx = context;
		Map map = null;
		while (ctx != null && (map = ctx.getBeansOfType(type)) != null
				&& map.size() == 0) {
			ctx = ctx.getParent();
		}
		if(map == null){
			return null;
		}
		if (map.size() > 1) {
			throw new RuntimeException("Got more then 1 pojo of type " + type
					+ ",they are " + map);
		}
		if (map.size() == 0)
			throw new RuntimeException("No pojo of type  " + type
					+ " is defined.");
		return (T) map.values().iterator().next();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T getBean(ApplicationContext context, Class<T> type)
			throws BeansException {
		Map map = context.getBeansOfType(type);
		if (map.size() > 1) {
			throw new NoSuchBeanDefinitionException(
					"Got more then 1 pojo of type " + type + ",they are " + map);
		}
		if (map.size() == 0)
			throw new NoSuchBeanDefinitionException("No pojo of type  " + type
					+ " is defined.");
		return (T) map.values().iterator().next();
	}

	// autowire support

	public static WebApplicationContext getWebApplicationContext(
			ServletContext sc) {
		return WebApplicationContextUtils.getWebApplicationContext(sc);
	}

	public static void processInjectionBasedOnCurrentContext(
			List<? extends Object> targets) {
		for (Object target : targets) {
			processInjectionBasedOnCurrentContext(target);
		}
	}

	public static void autowire(Collection targets) {
		for (Object target : targets) {
			if (target != null)
				processInjectionBasedOnCurrentContext(target);
		}
	}

	public static void autowire(Object... targets) {
		for (Object target : targets) {
			if (target != null)
				processInjectionBasedOnCurrentContext(target);
		}
	}

	private static AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();

	public static void processInjectionBasedOnCurrentContext(Object target) {
		if (context == null || bpp == null) {
			throw new IllegalStateException(
					"Current WebApplicationContext is not available for processing of "
							+ ClassUtils.getShortName(target.getClass())
							+ ": "
							+ "Make sure this class gets constructed in a Spring web application. Proceeding without injection.");
		}
		bpp.processInjection(target);
	}

}
