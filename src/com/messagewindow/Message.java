package com.messagewindow;

public interface Message {
	/**
	 * 消息框点击按钮后触发
	 * @param id [1.确认；2.取消]
	 * */
	void Click(int id);
}
