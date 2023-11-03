package com.ipast.permission.utils.rom;

/**
 * author:gang.cheng
 * description:
 * date:2023/11/2
 */
public class RomInfo {
    private String name;
    private String version;

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取 ROM 名称
     *
     * @return ROM 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取 ROM 版本信息
     *
     * @return ROM 版本信息
     */
    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "RomInfo{name=" + name + ", version=" + version + "}";
    }
}
