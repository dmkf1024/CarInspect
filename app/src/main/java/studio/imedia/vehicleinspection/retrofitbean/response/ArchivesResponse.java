package studio.imedia.vehicleinspection.retrofitbean.response;

import studio.imedia.vehicleinspection.retrofitbean.ArchivesBean;

/**
 * Created by 代码咖啡 on 2017/4/19
 * <p>
 * Email: wjnovember@icloud.com
 */

public class ArchivesResponse extends BaseResponse {

    private ArchivesBean archives;

    public ArchivesBean getArchives() {
        return archives;
    }

    public void setArchives(ArchivesBean archives) {
        this.archives = archives;
    }
}
