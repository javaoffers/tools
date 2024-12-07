package com.javaoffers.brief.excel;

import com.javaoffers.brief.common.MapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class POIUtilsTest {

    @Test
    public void samplePicAndVideo() throws Exception {
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
        POIUtils.SheetData test = POIUtils.parseExcelFileData(file1, "Sheet1", 0, 1, map);
        List<Map<String, Object>> rowsData = test.getRowsData();
        Map<String, Object> da = rowsData.get(2);
        List<MediaData> mediaDataList = (List<MediaData>)da.get("testAssetV");
        for(MediaData mediaData : mediaDataList){
            byte[] data = mediaData.getData();
            File file = new File(POIUtilsTest.class.getClassLoader().getResource(".").getPath()+"/sample/" + UUID.randomUUID().toString() + "." + mediaData.getSuggestFileExtension());
            FileUtils.touch(file);
            FileUtils.writeByteArrayToFile(file, data);
        }

    }
}