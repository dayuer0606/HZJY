package com.android.weischool.appinfo;

import java.util.List;

public class CommunityBean {
    /**
     * code : 200
     * data : {"uid":1,"status":1,"title":"问答标题","details":"这个老师特别好，知识讲解很详细","picture":"","label":"消防安全实务"}
     */

    private int code;
    private CommunityDataBean data;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CommunityDataBean getData() {
        return data;
    }

    public void setData(CommunityDataBean data) {
        this.data = data;
    }

    public static class CommunityDataBean {
        private Integer total;
        private List<ListDataBean> list;

        public void setList(List<ListDataBean> list) {
            this.list = list;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public List<ListDataBean> getList() {
            return list;
        }
    }

    public static class ListDataBean {
        private List<String> subject_id;
        private String creation_time;
        private Integer questions_id;
        private String nicename;
        private String title;
        private String content;
        private String picture;
        private String head;
        private Integer publisher;
        private Integer state;
        private Integer visit_num;
        private Integer huida_num;
        private Integer collection_status;
        private List<DataBean> huida;

        public Integer getHuida_num() {
            return huida_num;
        }

        public void setHuida_num(Integer huida_num) {
            this.huida_num = huida_num;
        }

        public String getCreation_time() {
            return creation_time;
        }

        public void setCreation_time(String creation_time) {
            this.creation_time = creation_time;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public List<String> getSubject_id() {
            return subject_id;
        }

        public Integer getVisit_num() {
            return visit_num;
        }

        public Integer getQuestions_id() {
            return questions_id;
        }

        public void setSubject_id(List<String> subject_id) {
            this.subject_id = subject_id;
        }

        public void setQuestions_id(Integer questions_id) {
            this.questions_id = questions_id;
        }

        public void setVisit_num(Integer visit_num) {
            this.visit_num = visit_num;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getPublisher() {
            return publisher;
        }

        public Integer getState() {
            return state;
        }

        public String getHead() {
            return head;
        }

        public String getNicename() {
            return nicename;
        }

        public String getPicture() {
            return picture;
        }

        public void setHead(String head) {
            this.head = head;
        }

        public void setNicename(String nicename) {
            this.nicename = nicename;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public void setPublisher(Integer publisher) {
            this.publisher = publisher;
        }

        public void setState(Integer state) {
            this.state = state;
        }

        public void setHuida(List<DataBean> huida) {
            this.huida = huida;
        }

        public List<DataBean> getHuida() {
            return huida;
        }

        public Integer getCollection_status() {
            return collection_status;
        }

        public void setCollection_status(Integer collection_status) {
            this.collection_status = collection_status;
        }
    }
    public static class DataBean {

        private Integer a_publisher;
        private String q_head;
        private Integer a_st;
        private String q_nicename;
        private Integer q_publisher;
        private Integer q_st;
        private Integer qID;
        private Integer aID;
        private String a_nicename;
        private String content;
        private String a_head;
        private String creation_time;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setCreation_time(String creation_time) {
            this.creation_time = creation_time;
        }

        public String getCreation_time() {
            return creation_time;
        }

        public void setqID(Integer qID) {
            this.qID = qID;
        }

        public void setQ_st(Integer q_st) {
            this.q_st = q_st;
        }

        public void setQ_publisher(Integer q_publisher) {
            this.q_publisher = q_publisher;
        }

        public void setQ_nicename(String q_nicename) {
            this.q_nicename = q_nicename;
        }

        public void setQ_head(String q_head) {
            this.q_head = q_head;
        }

        public void setaID(Integer aID) {
            this.aID = aID;
        }

        public void setA_st(Integer a_st) {
            this.a_st = a_st;
        }

        public void setA_publisher(Integer a_publisher) {
            this.a_publisher = a_publisher;
        }

        public void setA_nicename(String a_nicename) {
            this.a_nicename = a_nicename;
        }

        public void setA_head(String a_head) {
            this.a_head = a_head;
        }

        public String getQ_nicename() {
            return q_nicename;
        }

        public String getQ_head() {
            return q_head;
        }

        public String getA_nicename() {
            return a_nicename;
        }

        public String getA_head() {
            return a_head;
        }

        public Integer getqID() {
            return qID;
        }

        public Integer getQ_st() {
            return q_st;
        }

        public Integer getQ_publisher() {
            return q_publisher;
        }

        public Integer getaID() {
            return aID;
        }

        public Integer getA_st() {
            return a_st;
        }

        public Integer getA_publisher() {
            return a_publisher;
        }
    }
}
