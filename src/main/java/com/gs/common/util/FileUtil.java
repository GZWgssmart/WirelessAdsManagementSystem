package com.gs.common.util;

import javax.servlet.http.HttpSession;
import java.io.File;

/**
 * Created by WangGenshen on 5/26/16.
 */
public class FileUtil {

    public static String customerDirPath(HttpSession session, String customerId) {
        String rootPath = session.getServletContext().getRealPath("/");
        File uploads = new File(rootPath, "uploads");
        if (!uploads.exists()) {
            uploads.mkdir();
        }
        File customerDir = new File(uploads, customerId);
        if (!customerDir.exists()) {
            customerDir.mkdir();
        }
        return customerDir.getAbsolutePath();
    }

    public static String uploadFilePath(File file) {
        String path = file.getAbsolutePath();
        return path.substring(path.indexOf("uploads"));
    }
}
