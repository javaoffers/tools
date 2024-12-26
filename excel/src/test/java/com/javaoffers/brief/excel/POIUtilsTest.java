package com.javaoffers.brief.excel;

import com.javaoffers.brief.common.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import javax.print.attribute.standard.Media;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class POIUtilsTest {

    String path = POIUtilsTest.class.getClassLoader().getResource(".").getPath()+"/sampleWrite/" + UUID.randomUUID().toString() +"sample.xls";
    POIExcel instance = POIExcel.getInstance();
    @Test
    public void samplePicAndVideo() throws Exception {
        List<Map<String, Object>> rowsData = parseExcelData();
        Map<String, Object> da = rowsData.get(2);
        List<MediaData> mediaDataList = (List<MediaData>)da.get("testAssetV");
//        for(MediaData mediaData : mediaDataList){
//            byte[] data = mediaData.getData();
//            File file = new File(POIUtilsTest.class.getClassLoader().getResource(".").getPath()+"/sample/" + UUID.randomUUID().toString() + "." + mediaData.getSuggestFileExtension());
//            FileUtils.touch(file);
//            FileUtils.writeByteArrayToFile(file, data);
//        }
    }

    private static List<Map<String, Object>> parseExcelData() throws Exception {
        /**
         * id	图片	视频	类型	级别	审核意见	拒审原因
         */
        //key 表示excel里的title，value表示解析出来的自定义key
        Map<String, String> map = MapUtils
                .startBuildParam("id", "id")
                .buildParam("图片","testAssetP")
                .buildParam("视频","testAssetV")
                .buildParam("类型","deriveType")
                .buildParam("级别","level")
                .buildParam("审核意见","auditComments")
                .buildParam("拒审原因","rejection")
                .endBuildStringParam();
        String file1 = POIUtilsTest.class.getClassLoader().getResource("excelSample.xlsx").getFile();
        SheetData test = POIExcel.getInstance().parseExcelFileData(file1, "Sheet1", 0, 1, map);
        List<Map<String, Object>> rowsData = test.getRowsData();
        return rowsData;
    }

    @Test
    public void testWrite() throws Exception {
        String[][] k = new String[][]{
                {"id","testAssetP","testAssetV","deriveType","level","auditComments","rejection"},
                {"id", "图片","视频","类型","级别","审核意见","拒审原因",},
        };

        ArrayList<Map> list = new ArrayList<>();
        List<Map<String, Object>> maps = parseExcelData();
        maps.forEach(mapTmp->{
            HashMap<Object, Object> data = new HashMap<>();
            data.putAll(mapTmp);
            Object object = mapTmp.get("testAssetP");
            if(object != null){
                List ls = (List<Object>) object;
                MediaData mediaData = (MediaData)ls.get(0);
                data.put("testAssetP", mediaData.getData() );
            }

            Object object1 = mapTmp.get("testAssetV");
            if(object1 != null){
                List ls = (List<Object>) object1;
                MediaData mediaData = (MediaData)ls.get(0);
                data.put("testAssetV", mediaData.getData() );
            }

            list.add(data);
        });

        instance.exportExcel(path, "sample", list, k, "");
    }

    @Test
    public void testWriteUrl(){
        String[][] k = new String[][]{
                {"id","imageUrl","level"},
                {"id", "图片","级别"},
        };
        List<Map> list = new ArrayList<>();
        Map<String, Object> map = MapUtils.startBuildParam("id", 1)
                .buildParam("imageUrl", "https://www.geeksforgeeks.org\nhttps://www.cnblogs.com/")
                .buildParam("level", null).endBuildParam();
        list.add(map);
//        instance.getPoisMap().
        instance.exportExcel(path, "sample", list, k, "");
    }
}