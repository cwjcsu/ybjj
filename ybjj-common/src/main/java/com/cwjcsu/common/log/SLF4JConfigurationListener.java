/**
 * 
 */
package com.cwjcsu.common.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.cwjcsu.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * SLF4J日志配置
 * @author gongdewei
 * @date 2012-9-26
 */
public class SLF4JConfigurationListener implements ServletContextListener {

	private static final String SLF4J_CONFIG_DIR = "SLF4JConfigDir";
	private static final String SLF4J_CONFIG_FILE = "SLF4JConfigFile";
	
	private static final Logger logger = LoggerFactory.getLogger(SLF4JConfigurationListener.class);

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		ServletContext servletContext = evt.getServletContext();
		String configurationFile = servletContext.getInitParameter(SLF4J_CONFIG_FILE);
		if(StringUtil.isEmpty(configurationFile)) {
			System.err.println("SLF4JConfig: configuration file not define, please set <context-param> 'SLF4JConfigFile' in web.xml. ");
			return;
		}
		String configurationDir = servletContext.getInitParameter(SLF4J_CONFIG_DIR);
		System.err.println("SLF4JConfig: try to load config: "+configurationFile+", preferred dir: "+configurationDir);
		
		//查找log配置文件 configurationDir -> classpath
		//1.从configurationDir查找
		URL configurationURL = null;
		if(!StringUtil.isEmpty(configurationDir)) {
			File file = new File(configurationDir+"/"+configurationFile);
			if(file.exists()) {
				try {
					configurationURL = file.toURI().toURL();
				} catch (MalformedURLException e) {
					logger.error("SLF4JConfig: load slf4j configuration from external dir failed: "+e.getMessage() , e);
				}
			}
		}
		//2.从classpath查找
		if(configurationURL == null) {
			configurationURL = SLF4JConfigurationListener.class.getClassLoader().getResource(configurationFile);
		}
		if(configurationURL == null) {
			System.err.println("SLF4JConfig: slf4j configuration file not found: "+configurationFile);
			return;
		}
		System.err.println("SLF4JConfig: use config "+configurationURL);
		
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
	    try {
	        JoranConfigurator configurator = new JoranConfigurator();
	        configurator.setContext(context);
	        // Call context.reset() to clear any previous configuration, e.g. default 
	        // configuration. For multi-step configuration, omit calling context.reset().
	        context.reset(); 
	        configurator.doConfigure(configurationURL);
	      } catch (JoranException je) {
	        // StatusPrinter will handle this
	      }
	      StatusPrinter.printInCaseOfErrorsOrWarnings(context);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}
}
