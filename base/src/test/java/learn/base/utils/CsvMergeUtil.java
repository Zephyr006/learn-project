package learn.base.utils;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvMergeUtil {

    @Test
    public void testCsv() throws IOException {
        File file = new File("/Users/dong/Desktop/线上金额.csv");
        String claimTemp = "update %s set total_amount=%s where id=%s;";
        String template = "update mr_claim_item set price=%s,total_amount=%s where claim_id=%s and data_type=%s and ssu_code='%s' and price=%s;";
        String selecttemp = "select * from mr_claim_item where claim_id=%s and data_type=%s and ssu_code='%s' and price=%s;";

        try (BufferedReader bufferedReader = Files.newBufferedReader(file.toPath());) {
            String headerLine = bufferedReader.readLine();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                List<String> cellList = Arrays.asList(line.split(","));
                Integer dataType = 1;
                String tableName = "mr_pre_authorization";
                if (cellList.get(1).startsWith("SP")) {
                    dataType = 2;
                    tableName = "mr_claim";
                }
                String claimId = cellList.get(0);
                String ssuCode = cellList.get(2);
                String wrongPrice = cellList.get(4);
                String price = cellList.get(5);
                String itemTotal = cellList.get(8);

                //String itemSql = String.format(selecttemp, claimId, dataType, ssuCode, wrongPrice);
                String itemSql = String.format(template, price, itemTotal, claimId, dataType, ssuCode, wrongPrice);
                System.out.println(itemSql);

                String claimSql = String.format(claimTemp, tableName, cellList.get(9), claimId);
                System.out.println(claimSql);
                System.out.println();
            }
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        String dirPath = "/Users/dong/Desktop/";
        List<String> fragmentFileNames = Arrays.asList("claim_old_part_1 .csv");
        File[] srcFiles = fragmentFileNames.stream()
            .map(fileName -> fileName.replaceAll(" ", "\\ "))  // Mac替换空格
            //.peek(fileName -> Assert.assertFalse("文件名不能包含空格: " + fileName, fileName.contains(" ")))
            .map(fileName -> new File(dirPath + fileName)).toArray(File[]::new);

        Map<String, String> map = new HashMap<>();
        map.put("0", "默认");
        map.put("10", "合格");
        map.put("20", "不合格");
        Map<String, String> map2 = new HashMap<String, String>(){{
            put("0", "不返运");
            put("1", "返运");
            put("2", "暂存");
        }};

        Map<Integer, Map<String, String>> lineNoReplacerMap = new HashMap<Integer, Map<String, String>>(){{
            put(5, map2);
            put(8, map);
        }};
        mergeCsvFiles(srcFiles, new File(dirPath + "索赔旧件.csv"), lineNoReplacerMap);
    }


    /**
     * 合并 csv 文件，默认每个文件都有一行表头
     * @param srcFiles Source csv files, must have same header
     * @param destFile Destination csv file
     * @param lineNoReplacerMap index start from 1
     */
    public static void mergeCsvFiles(File[] srcFiles, File destFile, Map<Integer, Map<String, String>> lineNoReplacerMap)
        throws RuntimeException, IOException {
        mergePreCheck(srcFiles, destFile);
        try {
            // 获取源文件表头长度（假设每个源文件表头相同）
            int headerLength = 0;
            try (BufferedReader br = Files.newBufferedReader(srcFiles[0].toPath());) {
                String line = br.readLine();
                if (line == null) {
                    throw new RuntimeException("Empty source file: " + srcFiles[0]);
                }
                headerLength = line.length();
            }

            // 合并文件
            ByteBuffer lineBuffer = ByteBuffer.allocate(2048);
            try (FileOutputStream out = new FileOutputStream(destFile, true);
                 FileChannel destChannel = out.getChannel();){

                for (int i = 0; i < srcFiles.length; i++) {
                    // 数据不需要转换，直接整体 copy
                    if (lineNoReplacerMap == null || lineNoReplacerMap.isEmpty()) {
                        try (FileInputStream in = new FileInputStream(srcFiles[i]);
                             FileChannel srcChannel = in.getChannel();) {
                            // 非第一个文件时，跳过表头
                            if (i > 0) {
                                srcChannel.position(headerLength);
                            }
                            destChannel.transferFrom(srcChannel, destChannel.size(), srcChannel.size());
                        }
                    } else {

                        try (BufferedReader bufferedReader = Files.newBufferedReader(srcFiles[i].toPath());) {
                            String headerLine = bufferedReader.readLine();
                            if (i == 0) {
                                lineBuffer.put((headerLine + "\n").getBytes(StandardCharsets.UTF_8));
                                lineBuffer.flip();
                                int writeLen = destChannel.write(lineBuffer, destChannel.size());
                            }
                            String line = "\"200\",\"P000000834001\",\"1\",\"2024-05-14 17:29:49\"";
                            while ((line = bufferedReader.readLine()) != null) {
                                String[] lineItems = line.split(",");
                                boolean changed = false;
                                for (int lineIdx = 0; lineIdx < lineItems.length; lineIdx++) {
                                    Map<String, String> keyValMap = lineNoReplacerMap.get(lineIdx + 1);
                                    if (keyValMap == null || lineItems[lineIdx] == null || lineItems[lineIdx].isEmpty()) {
                                        continue;
                                    }
                                    String originalVal = lineItems[lineIdx];
                                    String cellNewVal = keyValMap.get(originalVal);
                                    if (StringUtils.isEmpty(cellNewVal)) {
                                        throw new IllegalStateException("没有找到对应的替换值: " + lineItems[lineIdx]);
                                    }
                                    lineItems[lineIdx] = cellNewVal;
                                    changed = true;
                                }
                                if (changed) {
                                    line = String.join(",", lineItems);
                                }

                                lineBuffer.clear();
                                // csv 格式每一行的行尾需要换行
                                lineBuffer.put((line + "\n").getBytes(StandardCharsets.UTF_8));
                                // 将 ByteBuffer 切换为读取模式
                                lineBuffer.flip();
                                // 将文件指针移动到文件末尾，用于追加写
                                int writeLen = destChannel.write(lineBuffer, destChannel.size());
                            }
                        }
                    }
                }
            }

            System.out.println("合并操作执行成功，合并后的文件大小：" + destFile.length()/ 1024 + "KB");
        } catch (IOException e) {
            //e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

    private static void mergePreCheck(File[] srcFiles, File destFile) throws IOException {
        if (destFile.exists()) {
            destFile.delete();
        }
        destFile.createNewFile();
        if (!destFile.getPath().toUpperCase().contains("CSV")) {
            throw new RuntimeException("Only csv file is allowed: " + destFile.getPath());
        }
        if (srcFiles.length == 0) {
            throw new RuntimeException("Please specify at least one source file");
        }
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        for (File srcFile : srcFiles) {
            if (!srcFile.exists()) {
                throw new IllegalArgumentException("Source file does not exist: " + srcFile.getPath());
            }

            if (!srcFile.canRead() && !srcFile.setReadable(true, false)) {
                throw new IllegalStateException("Source file cannot be read: " + srcFile.getPath());
            }
        }
    }

}
