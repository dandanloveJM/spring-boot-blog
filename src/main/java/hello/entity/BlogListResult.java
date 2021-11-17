package hello.entity;

import java.util.List;

public class BlogListResult extends Result<List<Blog>>{
    private int total;
    private int page;
    private int totalPage;

    private BlogListResult(String status, String msg, List<Blog> data, int total, int page, int totalPage){
        super(status, msg, data);
        this.total = total;
        this.page = page;
        this.totalPage = totalPage;
    }
    public static BlogListResult success(List<Blog> data, int total, int page, int totalPage){
        return new BlogListResult("ok", "获取成功", data, total, page, totalPage);
    }

    public static BlogListResult failure(String msg){
        return new BlogListResult("fail", msg, null, 0, 0, 0);
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


}
