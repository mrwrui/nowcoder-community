package com.nowcoder.community.community;

import com.nowcoder.community.community.dao.AlphaDao;
import com.nowcoder.community.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext() {
		System.out.println(applicationContext);

		AlphaDao alphtDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphtDao.select());

		alphtDao = applicationContext.getBean("alphaHibernate", AlphaDao.class);
		System.out.println(alphtDao.select());
	}

	@Test
	public void testBeanManagement() {
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);

		alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}

	@Test
	public void testBeanConfig() {
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format((new Date())));
	}

	@Autowired
	//这个注释表示希望spring将AlphaDao注入到这个属性中
	@Qualifier("alphaHibernate")
	//希望注入的是Hibernate
	private AlphaDao alphtDao;

	@Autowired
	private AlphaService alphaService;

	@Autowired
	private SimpleDateFormat simpleDateFormat;

	@Test
	//依赖注入的例子（Dependency Injection）
	public void testDI() {
		System.out.println(alphtDao);
		System.out.println(alphaService);
		System.out.println(simpleDateFormat);
	}
}
