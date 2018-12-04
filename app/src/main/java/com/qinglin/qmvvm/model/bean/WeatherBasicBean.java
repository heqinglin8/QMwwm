package com.qinglin.qmvvm.model.bean;

public class WeatherBasicBean {
   public String cid;
   public String location;
   public String parent_city;
   public String admin_area;
   public String cnty;
   public String lat;
   public String lon;
   public String tz;

   public String getCid() {
      return cid;
   }

   public void setCid(String cid) {
      this.cid = cid;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public String getParent_city() {
      return parent_city;
   }

   public void setParent_city(String parent_city) {
      this.parent_city = parent_city;
   }

   public String getAdmin_area() {
      return admin_area;
   }

   public void setAdmin_area(String admin_area) {
      this.admin_area = admin_area;
   }

   public String getCnty() {
      return cnty;
   }

   public void setCnty(String cnty) {
      this.cnty = cnty;
   }

   public String getLat() {
      return lat;
   }

   public void setLat(String lat) {
      this.lat = lat;
   }

   public String getLon() {
      return lon;
   }

   public void setLon(String lon) {
      this.lon = lon;
   }

   public String getTz() {
      return tz;
   }

   public void setTz(String tz) {
      this.tz = tz;
   }
}
