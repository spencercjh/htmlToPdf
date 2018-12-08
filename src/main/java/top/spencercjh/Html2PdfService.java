package top.spencercjh;

import org.springframework.stereotype.Service;
import top.spencercjh.utils.*;

import java.util.Objects;

/**
 * html转pdf的服务
 *
 * @author 欧阳洁
 * @since 2018-03-28 11:50
 */
@Service
public class Html2PdfService {
    /**
     * windows执行文件
     */
    private String windowExePath;
    /**
     * linux执行文件
     */
    private String linuxExePath;

    /**
     * 解析生成PDF
     *
     * @param pageUrl
     * @return
     */
    public String excute(String pageUrl) throws Exception {
        String outputPath = "/output/" + BaseUtils.getDateStr("yyyyMMdd") + "/pdf/" + BaseUtils.uuid2() + ".pdf";
        String cmdStr = getCmdStr(pageUrl, outputPath);
        boolean success = CmdUtils.excute(cmdStr);
        if (success) {
            return outputPath;
        } else {
            if (FilesUtils.isExistNotCreate(outputPath)) {
                return outputPath;
            } else {

                throw new Exception("转化异常！[" + outputPath + "]");
            }
        }
    }

    /**
     * 根据操作系统类别，获取不同的cmd字符串
     *
     * @param pageUrl
     * @param outputPath
     * @return
     */
    private String getCmdStr(String pageUrl, String outputPath) {
        StringBuilder cmdStr = new StringBuilder();
        String absoultOutputPath = PathUtils.getClassRootPath(outputPath);
        FilesUtils.checkFolderAndCreate(Objects.requireNonNull(absoultOutputPath));
        String absoultExePath = "";
        if (OsInfo.isWindows()) {//windows系统
            absoultExePath = getWindowExePath();
            absoultOutputPath = PathUtils.getWindowsRightPath(absoultOutputPath);
        } else {//默认linux系统
            absoultExePath = getLinuxExePath();
            //需要给脚本授权
            //cmdStr.append("chmod +x ").append(absoultExePath).append(" && ");
            CmdUtils.excute("chmod +x " + absoultExePath);
        }
        System.out.println("+++++++++++++++++++++++++++++++++++ /" + absoultExePath);
        if (!OsInfo.isWindows()) {
            CmdUtils.excute("chmod +x " + "/" + absoultExePath);
            cmdStr.append("/").append(absoultExePath).append(" --page-width 88 --page-height 125 -B 0 -L 0 -R 0 -T 0 ").append(pageUrl).append(" /").append(absoultOutputPath);
        } else {
            cmdStr.append(absoultExePath).append(" --page-width 88 --page-height 125 -B 0 -L 0 -R 0 -T 0 ").append(pageUrl).append(" ").append(absoultOutputPath);
        }
        return cmdStr.toString();
    }

    public String getWindowExePath() {
        if (BaseUtils.isBlank(this.windowExePath)) {
            String absoultExePath = PathUtils.getClassRootPath("/plugin/window/wkhtmltopdf/bin/wkhtmltopdf");
            this.windowExePath = PathUtils.getWindowsRightPath(absoultExePath);
        }
        return this.windowExePath;
    }

    public void setWindowExePath(String windowExePath) {
        this.windowExePath = windowExePath;
    }

    public String getLinuxExePath() {
        if (BaseUtils.isBlank(this.linuxExePath)) {
            this.linuxExePath = PathUtils.getClassRootPath("/plugin/linux/wkhtmltox/bin/wkhtmltopdf");
        }
        return this.linuxExePath;
    }

    public void setLinuxExePath(String linuxExePath) {
        this.linuxExePath = linuxExePath;
    }
}