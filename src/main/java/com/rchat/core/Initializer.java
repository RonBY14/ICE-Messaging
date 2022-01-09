package com.rchat.core;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class Initializer {

	private static AbstractApplicationContext context;

	public static void main(String[] args) {
		context = new ClassPathXmlApplicationContext("Beans.xml");
	}

	public static Object getBean(String name) {
		return context.getBean(name);
	}

}
