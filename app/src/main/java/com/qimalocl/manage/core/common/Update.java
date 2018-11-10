package com.qimalocl.manage.core.common;

public class Update {
	private String appName;
	private String appVersion;
	private String force;
	private String link;
	private String updateDesc;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String isForce() {
		return force;
	}

	public void setForce(String force) {
		this.force = force;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getUpdateDesc() {
		return updateDesc;
	}

	public void setUpdateDesc(String updateDesc) {
		this.updateDesc = updateDesc;
	}
}
