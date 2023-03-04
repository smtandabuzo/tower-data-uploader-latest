package com.emailattachment.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UploadDetails {

    @SequenceGenerator(
            name = "upload_sequence",
            sequenceName = "upload_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "upload_sequence"
    )
    private long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false,updatable = false)
    private Date date_uploaded;

    private String twr_pref;
    private String twr_no;
    private String pl_no;
    private String isabend;
    private String twr_type;
    private String sub_type;
    private double cond_att;
    private String tube_no;
    private String sht_no;
    private String crd_type;
    private String data_source;
    private String accuracy;
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy")
    private String date_captured;
    private String tower_no;
    private double lat;
    private double long_;
    private double height;

    public long getId() {
        return id;
    }

    public String getTwr_pref() {
        return twr_pref;
    }

    public void setTwr_pref(String twr_pref) {
        this.twr_pref = twr_pref;
    }

    public String getTwr_no() {
        return twr_no;
    }

    public void setTwr_no(String twr_no) {
        this.twr_no = twr_no;
    }

    public String getPl_no() {
        return pl_no;
    }

    public void setPl_no(String pl_no) {
        this.pl_no = pl_no;
    }

    public String getIsabend() {
        return isabend;
    }

    public void setIsabend(String isabend) {
        this.isabend = isabend;
    }

    public String getTwr_type() {
        return twr_type;
    }

    public void setTwr_type(String twr_type) {
        this.twr_type = twr_type;
    }

    public String getSub_type() {
        return sub_type;
    }

    public void setSub_type(String sub_type) {
        this.sub_type = sub_type;
    }

    public double getCond_att() {
        return cond_att;
    }

    public void setCond_att(double cond_att) {
        this.cond_att = cond_att;
    }

    public String getTube_no() {
        return tube_no;
    }

    public void setTube_no(String tube_no) {
        this.tube_no = tube_no;
    }

    public String getSht_no() {
        return sht_no;
    }

    public void setSht_no(String sht_no) {
        this.sht_no = sht_no;
    }

    public String getCrd_type() {
        return crd_type;
    }

    public void setCrd_type(String crd_type) {
        this.crd_type = crd_type;
    }

    public String getData_source() {
        return data_source;
    }

    public void setData_source(String data_source) {
        this.data_source = data_source;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getDate_captured() {
        return date_captured;
    }

    public void setDate_captured(String date_captured) {
        this.date_captured = date_captured;
    }

    public String getTower_no() {
        return tower_no;
    }

    public void setTower_no(String tower_no) {
        this.tower_no = tower_no;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLong_() {
        return long_;
    }

    public void setLong_(double long_) {
        this.long_ = long_;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Date getDate_uploaded() {
        return date_uploaded;
    }

    @PrePersist
    private void onCreate() {
        date_uploaded = new Date();
    }
}
