package com.vartool.web.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;

/**
 * 타일즈 설정.
* 
* @fileName	: AppTilesConfigurer.java
* @author	: ytkim
 */
public class AppTilesConfigurer {

	/**
	 * Configure TilesConfigurer.
	 */
	@Bean
	public TilesConfigurer tilesConfigurer() {
		TilesConfigurer tilesConfigurer = new TilesConfigurer();
		tilesConfigurer.setDefinitions(new String[] { "classpath:/vartool-tiles.xml" });
		tilesConfigurer.setCheckRefresh(true);
		return tilesConfigurer;
	}

	@Bean
    public UrlBasedViewResolver tilesviewResolvers() {
        UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
        viewResolver.setOrder(1);
        viewResolver.setViewClass(AppTilesView.class);
        return viewResolver;
    }

}