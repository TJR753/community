package com.example.community.domain.vo;

import lombok.Data;

@Data
public class Page {
    private int current=1; //当前页
    private int limit=10;  //每页数目
    private int rows;   //总行数
    private String path;  //访问路径

    public int getOffset(){
        return limit*(current-1);
    }

    public int getTotal(){
        if(rows%limit==0){
            return rows/limit;
        }
        return rows/limit+1;
    }

    public int getFrom(){
        int from=current-2;
        return from<=0?1:from;
    }

    public int getTo(){
        int to=current+2;
        return to>getTotal()?getTotal():to;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows>=0){
            this.rows = rows;
        }
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current>1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit>0&&limit<=100){
            this.limit = limit;
        }
    }
}
