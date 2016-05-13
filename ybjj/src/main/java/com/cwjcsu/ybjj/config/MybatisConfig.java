package com.cwjcsu.ybjj.config;

import com.cwjcsu.ybjj.domain.enums.IdEnumTypeHandler;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Set;

/**
 * @author ye
 */
@Configuration
@MapperScan("com.cwjcsu.ybjj.mapper")
public class MybatisConfig {

    @SuppressWarnings("unchecked")
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();

        // 需要用到外部XML配置时可取消此行注释
//        String resources = "mybatis-config.xml";
//        sqlSessionFactory.setConfigLocation(new ClassPathResource(resources));

        // 设置数据源 快速失败 类型别名 类型处理器
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setFailFast(true);
        sqlSessionFactory.setTypeAliasesPackage("com.cwjcsu.ybjj.domain");
//        sqlSessionFactory.setTypeHandlersPackage("com.cloudbility.domain.typehandler");

//        sqlSessionFactory.setTypeHandlers(new TypeHandler[]{typeEnumHandler, sizeEnumHandler});
        org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getObject().getConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setUseGeneratedKeys(true);

        // 遍历domain中的枚举类, 用EnumOrdinalTypeHandler作为数据库的数字和枚举的处理器
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
        resolverUtil.find(new ResolverUtil.IsA(Enum.class), "com.cwjcsu.ybjj.domain.enums");
        Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
        for (Class<?> type : typeSet) {
            typeHandlerRegistry.register(type, new IdEnumTypeHandler(type));
        }

        return sqlSessionFactory.getObject();
    }

}
