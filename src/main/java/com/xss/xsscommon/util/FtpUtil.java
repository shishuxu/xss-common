package com.xss.xsscommon.util;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.List;
import java.util.Objects;

/**
 * ftp協議上傳文件工具类
 * @author xss
 * @version 1.0.0
 * @date 2020-09-18 18:00
 */
@Slf4j
public class FtpUtil {


    /**
     * 生成文件並將内容写入txt文件
     * @param result 组装好的一行字符串
     * @param fileName 文件名含后缀
     * @param filePath 文件路径
     * @param title 文件标题行
     * @return 写入文件成功返回true 其它返回false
     */
    public static boolean writeUploadData(List<String> result, String fileName, String filePath, String title) {
        long start = System.currentTimeMillis();
        boolean flag = false;
        BufferedWriter out = null;
        try {
            if (result != null && !result.isEmpty() && StringUtils.isNotEmpty(fileName)) {
//                fileName +=".txt";
                File pathFile = new File(filePath);
                if (!pathFile.exists()) {
                    pathFile.mkdirs();
                }
                String relFilePath = filePath + File.separator + fileName;
                File file = new File(relFilePath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
                if (StringUtils.isNotBlank(title)) {//标题头
                    out.write(title);
                    out.newLine();
                }

                for (String info : result) {
                    out.write(info);
                    out.newLine();
                }
                flag = true;
                log.info("写入文件耗时：*********************************" + (System.currentTimeMillis() - start) + "毫秒");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return flag;
        }
    }


    /**
     * 利用JSch包实现SFTP上传文件到服務器
     *
     * @param bytes           文件字節數據
     * @param fileName        文件名
     * @param ip              服務器ip
     * @param userName        用戶名
     * @param password        密碼
     * @param uploadDirectory 上傳目錄
     * @param port            端口
     */
    public static boolean sshSftp(byte[] bytes, String fileName, String ip, String userName, String password, String uploadDirectory, int port) throws Exception {
        log.info("fileName: " + fileName);
        log.info("ip: " + ip);
        log.info("userName: " + userName);
        log.info("password: " + password);
        log.info("uploadDirectory: " + uploadDirectory);
        log.info("port: " + port);
        boolean flag = false;

        Session session = null;
        Channel channel = null;
        JSch jsch = new JSch();


        if (port <= 0) {
            //连接服务器，采用默认端口
            session = jsch.getSession(userName, ip);
        } else {
            //采用指定的端口连接服务器
            session = jsch.getSession(userName, ip, port);
        }

        //如果服务器连接不上，则抛出异常
        if (session == null) {
            throw new Exception("session is null");
        }

        //设置登陆主机的密码
        session.setPassword(password);//设置密码
        //设置第一次登陆的时候提示，可选值：(ask | yes | no)
        session.setConfig("StrictHostKeyChecking", "no");
        //设置登陆超时时间
        session.connect(30000);


        OutputStream outstream = null;
        try {
            //创建sftp通信通道
            channel = (Channel) session.openChannel("sftp");
            channel.connect(1000);
            ChannelSftp sftp = (ChannelSftp) channel;


            //进入服务器指定的文件夹
            sftp.cd(uploadDirectory);

            //列出服务器指定的文件列表
//            Vector v = sftp.ls("*");
//            for(int i=0;i<v.size();i++){
//                System.out.println(v.get(i));
//            }

            //以下代码实现从本地上传一个文件到服务器，如果要实现下载，对换以下流就可以了
            outstream = sftp.put(fileName);
            outstream.write(bytes);
            flag = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关流操作
            if (outstream != null) {
                outstream.flush();
                outstream.close();
            }
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
            return flag;
        }
    }

    /**
     * 将文件转换成byte数组/
     * @param file 文件
     */
    public static byte[] file2byte(File file) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }


    /**
     * ftp协议上传文件
     * @param userName  用户名
     * @param password 密码
     * @param ip 服务器ip
     * @param port 端口
     * @param file  文件
     * @return 返回是否上传成功
     */
    public static boolean ftpUpload(String userName, String password, String ip, int port, File file) {
        boolean flag = false;
        try {
            FTPClient ftp = new FTPClient();

            int reply;
            ftp.connect(ip, port);

            ftp.login(userName, password);
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            reply = ftp.getReplyCode();
            System.out.println(reply);
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
            }

            upload(file, ftp);
            flag = true;
            if (ftp.isConnected()) {
                try {
                    ftp.logout();
                    ftp.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;

    }


    /**
     * ftp上传文件
     */
    public static boolean upload(File f, FTPClient ftp) throws Exception {
        boolean flag = false;
        if (f.isDirectory()) {
            ftp.makeDirectory(f.getName());
            ftp.changeWorkingDirectory(f.getName());//切換路徑
            String[] files = f.list();
            for (String fstr : files) {
                File file1 = new File(f.getPath() + "/" + fstr);
                if (file1.isDirectory()) {
                    upload(file1, ftp);
                    ftp.changeToParentDirectory();
                } else {
                    File file2 = new File(f.getPath() + "/" + fstr);
                    FileInputStream input = new FileInputStream(file2);
                    ftp.enterLocalPassiveMode();//設置被動模式,解決正式服務器端口被禁時，storeFile方法沒響應的問題
                    flag = ftp.storeFile(file2.getName(), input);
                    input.close();
                }
            }
        } else {
//            File file2 = new File(f.getPath());
            FileInputStream input = new FileInputStream(f);
            flag = ftp.storeFile(f.getName(), input);
            input.close();
        }
        return flag;
    }

    /**
     * 生成csv文件
     * @param result 需要写入文件的字符串
     * @param fileName 文件名字
     * @param filePath 文件路径
     * @param headArr 第一行
     * @return 是否生成文件并写入成功。成功返回true 失败返回false
     */
    public static boolean writeToCsvFile(List<String> result, String fileName, String filePath, String[] headArr) {
        boolean flag = false;

        // 表格头
//            String[] headArr = new String[]{"PointId", "X", "Y"};
//            //CSV文件路径及名称
//            LocalDateTime localDateTime = LocalDateTime.now();
//            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        File csvFile;
        BufferedWriter csvWriter = null;
        try {
            csvFile = new File(filePath + File.separator + fileName);
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();

            // GB2312使正确读取分隔符","
            csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GB2312"), 1024);

            //这部分在第一行居中展示文件名称，根据实际情况，可选择取消注释
            /*int num = headArr.length / 2;
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < num; i++) {
                buffer.append(",");
            }
            csvWriter.write(buffer.toString() + fileName + buffer.toString());
            csvWriter.newLine();*/

            // 写入文件头部标题行
//                csvWriter.write(String.join(",", headArr));
//                csvWriter.newLine();
            if (!CollectionUtils.isEmpty(result)) {
                // 写入文件内容
                for (String str : result) {
                    csvWriter.write(str);
                    csvWriter.newLine();
                }
            }
            csvWriter.flush();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(csvWriter)) {
                try {
                    csvWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return flag;
    }


}


