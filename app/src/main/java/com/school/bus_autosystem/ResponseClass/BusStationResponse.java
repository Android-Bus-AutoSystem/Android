package com.school.bus_autosystem.ResponseClass;

import java.util.List;

import java.util.List;

public class BusStationResponse {
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static class Response {
        private Header header;
        private Body body;

        public Header getHeader() {
            return header;
        }

        public void setHeader(Header header) {
            this.header = header;
        }

        public Body getBody() {
            return body;
        }

        public void setBody(Body body) {
            this.body = body;
        }
    }

    public static class Header {
        private String resultCode;
        private String resultMsg;

        public String getResultCode() {
            return resultCode;
        }

        public void setResultCode(String resultCode) {
            this.resultCode = resultCode;
        }

        public String getResultMsg() {
            return resultMsg;
        }

        public void setResultMsg(String resultMsg) {
            this.resultMsg = resultMsg;
        }
    }

    public static class Body {
        private Items items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;

        public Items getItems() {
            return items;
        }

        public void setItems(Items items) {
            this.items = items;
        }

        public int getNumOfRows() {
            return numOfRows;
        }

        public void setNumOfRows(int numOfRows) {
            this.numOfRows = numOfRows;
        }

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
    }

    public static class Items {
        private List<Item> item;

        public List<Item> getItem() {
            return item;
        }

        public void setItem(List<Item> item) {
            this.item = item;
        }
    }

    public static class Item {
        private int citycode;
        private double gpslati;
        private double gpslong;
        private String nodeid;
        private String nodenm;
        private Integer nodeno; // nodeno는 null일 수 있기 때문에 Integer로 선언

        public int getCitycode() {
            return citycode;
        }

        public void setCitycode(int citycode) {
            this.citycode = citycode;
        }

        public double getGpslati() {
            return gpslati;
        }

        public void setGpslati(double gpslati) {
            this.gpslati = gpslati;
        }

        public double getGpslong() {
            return gpslong;
        }

        public void setGpslong(double gpslong) {
            this.gpslong = gpslong;
        }

        public String getNodeid() {
            return nodeid;
        }

        public void setNodeid(String nodeid) {
            this.nodeid = nodeid;
        }

        public String getNodenm() {
            return nodenm;
        }

        public void setNodenm(String nodenm) {
            this.nodenm = nodenm;
        }

        public Integer getNodeno() {
            return nodeno;
        }

        public void setNodeno(Integer nodeno) {
            this.nodeno = nodeno;
        }
    }
}
