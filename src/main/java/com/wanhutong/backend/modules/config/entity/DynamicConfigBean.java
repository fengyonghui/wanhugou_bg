package com.wanhutong.backend.modules.config.entity;

import java.util.Date;

public class DynamicConfigBean {

    private int id;

    private String confName;

    private String content;

	private Date created;

    private int status;

    private int version;

	private Date updated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConfName() {
        return confName;
    }

    public void setConfName(String confName) {
        this.confName = confName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@Override
    public String toString() {
        return "DynamicConfig [id=" + id + ", confName=" + confName + ", content=" + content + ", created=" + created
                + ", status=" + status + ", version=" + version + ", updated=" + updated + "]";
    }

}
