package com.messagewindow;

public class test{

	public static void main(String[] args) {
		MessageWindow.getMessageWindow("请确认！！",new Message() {
			
			@Override
			public void Click(int ID) {
				switch(ID) {
				case 1:
					System.out.println("点击了确定");
					break;
				case 2:
					System.out.println("点击了取消");
					break;
				}
			}
		});
		
	}
}
