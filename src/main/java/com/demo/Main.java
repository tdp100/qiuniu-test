package com.demo;

import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.sun.xml.internal.fastinfoset.sax.SystemIdResolver;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;

/**
 * Created by tdp on 2018/5/28.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.huadong());

        //...其他参数参考类注释
        String accessKey = "1r1cmmWuTYaf-zW8WvqwnB_7mwXXXNOThKViA9_Z";
        String secretKey = "Xog5mzs9Ex25BXTC7hCK5TTLN-4UcE4ouv7dzvb-";

        String bucket = "prl-test";
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);

        //文件名前缀
        String prefix = "";
        //每次迭代的长度限制，最大1000，推荐值 1000
        int limit = 1000;
        //指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
        String delimiter = "";

        String[] domains =  bucketManager.domainList(bucket);
        if (domains == null || domains.length == 0) {
            System.out.println("zero domains");
        } else {
            System.out.println(domains);
        }

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
                System.out.println(item.key);
            }
        }
    }
}
