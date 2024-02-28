package com.user.usercenter.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * 自定义swagger配置类
 *
 * @author zmj
 */
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //这里一定要标注配置位置
                .apis(RequestHandlerSelectors.basePackage("com.user.usercenter.controller"))  //添加ApiOperiation注解的被扫描
                .paths(PathSelectors.any())
                .build();

    }

    /**
     * API信息*
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("伙伴匹配系统")
                .description("伙伴匹配系统API文档")
                .version("1.0").build();
    }



}
