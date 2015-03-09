package com.example.model;

import java.io.Serializable;

public class AttitudeDesign implements Serializable {
	private static final long serialVersionUID = -4716339454232405881L;

	private String dsg_name;//姓名
	private String dsg_title;//标题
	private int dsg_tel;//电话
	private String dsg_email;//邮箱
	private int dsg_sex;//性别
	private int dsg_level;//等级
	private String dsg_photo;//头像照片
	private String dsg_code;//二维码图片
	private String group_photo;//背景照片
	private int group_id;//作品编号
	private String group_name;//作品名称
	private String group_theme;//作品简介
	private int group_photo_num;//作品图片数量
	private boolean  showFlag;//是否显示,冗余
	public String getDsg_name() {
		return dsg_name;
	}
	public void setDsg_name(String dsg_name) {
		this.dsg_name = dsg_name;
	}
	public String getDsg_title() {
		return dsg_title;
	}
	public void setDsg_title(String dsg_title) {
		this.dsg_title = dsg_title;
	}
	public int getDsg_tel() {
		return dsg_tel;
	}
	public void setDsg_tel(int dsg_tel) {
		this.dsg_tel = dsg_tel;
	}
	public String getDsg_email() {
		return dsg_email;
	}
	public void setDsg_email(String dsg_email) {
		this.dsg_email = dsg_email;
	}
	public int getDsg_sex() {
		return dsg_sex;
	}
	public void setDsg_sex(int dsg_sex) {
		this.dsg_sex = dsg_sex;
	}
	public int getDsg_level() {
		return dsg_level;
	}
	public void setDsg_level(int dsg_level) {
		this.dsg_level = dsg_level;
	}
	public String getDsg_photo() {
		return dsg_photo;
	}
	public void setDsg_photo(String dsg_photo) {
		this.dsg_photo = dsg_photo;
	}
	public String getDsg_code() {
		return dsg_code;
	}
	public void setDsg_code(String dsg_code) {
		this.dsg_code = dsg_code;
	}
	public String getGroup_photo() {
		return group_photo;
	}
	public void setGroup_photo(String group_photo) {
		this.group_photo = group_photo;
	}
	public int getGroup_id() {
		return group_id;
	}
	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	public String getGroup_theme() {
		return group_theme;
	}
	public void setGroup_theme(String group_theme) {
		this.group_theme = group_theme;
	}
	public int getGroup_photo_num() {
		return group_photo_num;
	}
	public void setGroup_photo_num(int group_photo_num) {
		this.group_photo_num = group_photo_num;
	}
	public boolean isShowFlag() {
		return showFlag;
	}
	public void setShowFlag(boolean showFlag) {
		this.showFlag = showFlag;
	}
}
