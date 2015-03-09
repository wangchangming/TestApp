package com.example.model;

import java.io.Serializable;

/**
 * 作品集，包装类
 * @author wching
 *
 */
public class DesignDetail implements Serializable {
	private static final long serialVersionUID = -2168889039765131008L;

	private String photo_url;//图片网址
	private String photo_infor;//图片描述
	private int aid;//所属图集
	public String getPhoto_url() {
		return photo_url;
	}
	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}
	public String getPhoto_infor() {
		return photo_infor;
	}
	public void setPhoto_infor(String photo_infor) {
		this.photo_infor = photo_infor;
	}
	public int getAid() {
		return aid;
	}
	public void setAid(int aid) {
		this.aid = aid;
	}

}
