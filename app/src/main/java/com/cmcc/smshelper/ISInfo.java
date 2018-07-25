package com.cmcc.smshelper;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ISInfo {

    @Id(autoincrement = true)
    private Long id;


    private String qyjc;
    private String qydm;
    private String fwdm;
    private String splx;
    private String ywmc;

    private String ywdm;
    private String zf;
    private String dgzl;
    private String sxjrm;
    
    @Generated(hash = 1098589863)
    public ISInfo(Long id, String qyjc, String qydm, String fwdm, String splx,
            String ywmc, String ywdm, String zf, String dgzl, String sxjrm) {
        this.id = id;
        this.qyjc = qyjc;
        this.qydm = qydm;
        this.fwdm = fwdm;
        this.splx = splx;
        this.ywmc = ywmc;
        this.ywdm = ywdm;
        this.zf = zf;
        this.dgzl = dgzl;
        this.sxjrm = sxjrm;
    }
    @Generated(hash = 1851859501)
    public ISInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getQyjc() {
        return this.qyjc;
    }
    public void setQyjc(String qyjc) {
        this.qyjc = qyjc;
    }
    public String getQydm() {
        return this.qydm;
    }
    public void setQydm(String qydm) {
        this.qydm = qydm;
    }
    public String getFwdm() {
        return this.fwdm;
    }
    public void setFwdm(String fwdm) {
        this.fwdm = fwdm;
    }
    public String getSplx() {
        return this.splx;
    }
    public void setSplx(String splx) {
        this.splx = splx;
    }
    public String getYwmc() {
        return this.ywmc;
    }
    public void setYwmc(String ywmc) {
        this.ywmc = ywmc;
    }
    public String getYwdm() {
        return this.ywdm;
    }
    public void setYwdm(String ywdm) {
        this.ywdm = ywdm;
    }
    public String getZf() {
        return this.zf;
    }
    public void setZf(String zf) {
        this.zf = zf;
    }
    public String getDgzl() {
        return this.dgzl;
    }
    public void setDgzl(String dgzl) {
        this.dgzl = dgzl;
    }
    public String getSxjrm() {
        return this.sxjrm;
    }
    public void setSxjrm(String sxjrm) {
        this.sxjrm = sxjrm;
    }
    public void setId(Long id) {
        this.id = id;
    }

}

