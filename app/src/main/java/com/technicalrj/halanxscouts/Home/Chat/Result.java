
package com.technicalrj.halanxscouts.Home.Chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("is_read")
    @Expose
    private Boolean isRead;
    @SerializedName("read_at")
    @Expose
    private Object readAt;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("role")
    @Expose
    private String role;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Object getReadAt() {
        return readAt;
    }

    public void setReadAt(Object readAt) {
        this.readAt = readAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", createdAt='" + createdAt + '\'' +
                ", isRead=" + isRead +
                ", readAt=" + readAt +
                ", content='" + content + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
