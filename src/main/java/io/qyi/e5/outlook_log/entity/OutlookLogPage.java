package io.qyi.e5.outlook_log.entity;
import java.util.List;


public class OutlookLogPage {
    private int pageIndex;
    private int pageSize;
    private int pagesNum;
    private List<OutlookLog> outlookLogList;

    public OutlookLogPage() {
    }
    
    public OutlookLogPage(int pageIndex, int pageSize, int pagesNum, List<OutlookLog> outlookLogList) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.pagesNum = pagesNum;
        this.outlookLogList = outlookLogList;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPagesNum() {
        return pagesNum;
    }

    public void setPagesNum(int pagesNum) {
        this.pagesNum = pagesNum;
    }

    public List<OutlookLog> getOutlookLogList() {
        return outlookLogList;
    }

    public void setOutlookLogList(List<OutlookLog> outlookLogList) {
        this.outlookLogList = outlookLogList;
    }

    @Override
    public String toString() {
        return "OutlookLogPage{" +
                "pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", pagesNum=" + pagesNum +
                ", outlookLogList=" + outlookLogList +
                '}';
    }


}