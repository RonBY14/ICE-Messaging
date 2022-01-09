package com.rchat.core;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;

public abstract class Initializer {

	private static AbstractApplicationContext context;

	public static void main(String[] args) {
		JFrame window = new JFrame("Server");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(300, 0);
		window.setResizable(false);
		window.setVisible(true);

		context = new ClassPathXmlApplicationContext("Beans.xml");
	}

	public static Object getBean(String name) {
		return context.getBean(name);
	}

}
