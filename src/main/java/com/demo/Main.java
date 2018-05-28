package com.demo;

import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.sun.deploy.net.*;

/**
 * Created by tdp on 2018/5/28.
 */
public class Main {

    //AK/SK
    static String accessKey = "1r1cmmWuTYaf-zW8WvqwnB_7mwXXXNOThKViA9_Z";
    static String secretKey = "Xog5mzs9Ex25BXTC7hCK5TTLN-4UcE4ouv7dzvb-";
    //是否使用自定义域名下载对象
    static boolean USE_CUSTOM_DOMAIN= true;
    //下载的文件保存路径
    static String SAVE_PATH = "/Users/tdp/IdeaProjects/qiniu-test/download";

    public static void main(String[] args) throws Exception {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.huadong());
        String bucket = "pri-test";
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);

        String[] domains =  bucketManager.domainList(bucket);
        String domainName = "";
        if (domains == null || domains.length == 0) {
            System.out.println("zero domains");
            System.exit(1);
        }
        for(int i=0 ; i< domains.length; i++){
            System.out.println(domains[i]);

            if(domains[i].contains("clouddn.com") && USE_CUSTOM_DOMAIN) {
                domainName = domains[i];
                break;
            } else if (domains[i].contains("clouddn.com") && !USE_CUSTOM_DOMAIN){
                continue;
            } else {
                domainName = domains[i];
                break;
            }
        }

        //文件名前缀
        String prefix = "";
        //每次迭代的长度限制，最大1000，推荐值 1000
        int limit = 1000;
        //指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
        String delimiter = "";

        //列举空间文件列表
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(bucket, prefix, limit, delimiter);
        while(fileListIterator.hasNext()){
            //处理获取的file list结果
            FileInfo[] items = fileListIterator.next();
            if (items == null || items.length == 0) {
                System.out.println("zero objects.");
                break;
            }
            for (FileInfo item : items) {
                String finalUrl = getObjectKey("http://"+ domainName,item.key);
                System.out.println("Begin to download "+ finalUrl);
                HttpRequestUtil.downLoadFromUrl(finalUrl, item.key, SAVE_PATH);
                System.out.println("End to download "+ finalUrl);
            }
        }
    }

    //获取对象的url， 带临时签名
    public static String  getObjectKey(String domainOfBucket, String key) throws Exception {
        String encodedFileName = URLEncoder.encode(key, "utf-8");
        String publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
        Auth auth = Auth.create(accessKey, secretKey);
        long expireInSeconds = 3600;//1小时，可以自定义链接过期时间
        String finalUrl = auth.privateDownloadUrl(publicUrl, expireInSeconds);
        System.out.println(finalUrl);
        return finalUrl;
    }
}
