package xyz.zzyitj.mybatis.entity;

/**
 * xyz.zzyitj.mybatis.entity
 *
 * @author intent zzy.main@gmail.com
 * @date 2020/6/30 1:03 下午
 * @since 1.0
 */

public class Page<T> implements Paginable<T> {

    public static final int DEFAULT_PAGE_SIZE = 10; // 默认每页记录数

    public static final int PAGE_COUNT = 10;

    private int pageNo = 1; // 页码

    private int pageSize = DEFAULT_PAGE_SIZE; // 每页记录数

    private int totalCount = 0; // 总记录数

    private int totalPage = 0; // 总页数

    private long timestamp = 0; // 查询时间戳

    private boolean full = false; // 是否全量更新 //false 不更新totalcount

    @Override
    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        int totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
        this.setTotalPage(totalPage);
    }

    @Override
    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    @Override
    public boolean isFirstPage() {
        return pageNo <= 1;
    }

    @Override
    public boolean isLastPage() {
        return pageNo >= totalPage;
    }

    @Override
    public int getNextPage() {
        return isLastPage() ? pageNo : (pageNo + 1);
    }

    @Override
    public int getPrePage() {
        return isFirstPage() ? pageNo : (pageNo - 1);
    }

    @Override
    public int getBeginIndex() {
        if (pageNo > 0) {
            return (pageSize * (pageNo - 1));
        } else {
            return 0;
        }
    }

    @Override
    public int getEndIndex() {
        if (pageNo > 0) {
            return Math.min(pageSize * pageNo, totalCount);
        } else {
            return 0;
        }
    }

    @Override
    public int getBeginPage() {
        if (pageNo > 0) {
            return (PAGE_COUNT * ((pageNo - 1) / PAGE_COUNT)) + 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getEndPage() {
        if (pageNo > 0) {
            return Math.min(PAGE_COUNT * ((pageNo - 1) / PAGE_COUNT + 1), getTotalPage());
        } else {
            return 0;
        }
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

