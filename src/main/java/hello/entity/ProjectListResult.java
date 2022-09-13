package hello.entity;

import java.util.List;

public class ProjectListResult extends Result<List<Project>>{
    private int total;
    private int page;
    private int totalPage;
    private  int sum;

    protected ProjectListResult(String status, String msg, List<Project> data, int total, int page, int totalPage, int sum) {
        super(status, msg, data);
        this.total = total;
        this.page = page;
        this.totalPage = totalPage;
        this.sum = sum;
    }


    public static ProjectListResult success(List<Project> data, int total, int page, int totalPage, int sum){
        return new ProjectListResult("ok", "获取成功", data, total, page, totalPage, sum);
    }

    public static ProjectListResult success(List<Project> data) {
        return new ProjectListResult("ok", "获取成功", data, data.size(), 1, 10, 0);
    }

    public static ProjectListResult failure(String msg){
        return new ProjectListResult("fail", msg, null, 0, 0, 0,0);
    }

    public int getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getSum(){return sum;}


}
