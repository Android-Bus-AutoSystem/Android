package com.school.bus_autosystem.ResponseClass;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BusstopArriveResponse {
    @SerializedName("response")
    public Response response;

    public static class Response {
        @SerializedName("header")
        public Header header;
        @SerializedName("body")
        public Body body;

        public static class Header {
            @SerializedName("resultCode")
            public String resultCode;
            @SerializedName("resultMsg")
            public String resultMsg;
        }

        public static class Body {
            @SerializedName("items")
            public Items items;
            @SerializedName("numOfRows")
            public int numOfRows;
            @SerializedName("pageNo")
            public int pageNo;
            @SerializedName("totalCount")
            public int totalCount;

            public static class Items {
                @SerializedName("item")
                public List<Item> itemList;

                public static class Item {
                    @SerializedName("arrprevstationcnt")
                    public int arrprevstationcnt;
                    @SerializedName("arrtime")
                    public int arrtime;
                    @SerializedName("nodeid")
                    public String nodeid;
                    @SerializedName("nodenm")
                    public String nodenm;
                    @SerializedName("routeid")
                    public String routeid;
                    @SerializedName("routeno")
                    public String routeno;
                    @SerializedName("routetp")
                    public String routetp;
                    @SerializedName("vehicletp")
                    public String vehicletp;
                }
            }
        }
    }
}


