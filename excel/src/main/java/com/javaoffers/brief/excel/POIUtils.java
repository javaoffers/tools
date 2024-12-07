package com.javaoffers.brief.excel;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFObjectData;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 导出excel 工具类
 * @author cmj
 *
 */
public class POIUtils {

	//ThreadLocal
	private final static ThreadLocal<POIUtils>  pois = new ThreadLocal<POIUtils>();
	//为空
	private final static Object isnull = null;
	 
	/**
	 * 设定规定
	 */
	private final Map<Object,Object> pois_map = new ConcurrentHashMap<Object,Object>();

	/**   
	 * @Title: getInstance   
	 * @Description: TODO(作用：为当前线程创建一个对应的实例)   
	 * @param: @return      
	 * @return: POIUtils  
	 * @Auther: cmj
	 * @throws   
	 */  
	private static POIUtils getInstance(){
		POIUtils poiUtils = pois.get();
		if(poiUtils==null){
			 poiUtils = new POIUtils();
			 pois.set(poiUtils);
		}
		return poiUtils;
	}
	
	/**
	 * @throws IOException 
	 * @throws FileNotFoundException    创建属于当前线程的 workBook 对象实例
	 * @Title: createWorkBook   
	 * @Description: TODO(作用：)   
	 * @param: @param path      
	 * @return: HSSFWorkbook  
	 * @Auther: cmj
	 * @throws   
	 */  
	public static HSSFWorkbook createWorkBook(String path) throws Exception{
		if(path==null||"".equals(path))
			throw new Exception("path is null");
		putV(new Integer(5), path);

		HSSFWorkbook workbook = (HSSFWorkbook)getV(new Integer(1));
		if(workbook==null){
			 workbook = new HSSFWorkbook();
			 putV(new Integer(1), workbook);
		}
		//HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(new File(pathname)));
		return workbook;
	}
	private static HSSFWorkbook getBooking(){
		HSSFWorkbook book = (HSSFWorkbook)getV(new Integer(1));
    	return book;
	}
	
    private static String getBookPath(){
    	String path = (String)getV(new Integer(5));
    	return path;
    }
    
    private static File getBookFile(){
    	File file = new File(getBookPath());
    	return file;
    }
	/**   
	 * @Title: createSheet   
	 * @Description: TODO(作用：创建sheet对象，并放入List《Sheet》中)   
	 * @param: @throws Exception      
	 * @return: void  
	 * @Auther: cmj
	 * @throws   
	 */  
	private static void createSheet() throws Exception{
		HSSFWorkbook wb = (HSSFWorkbook) getV(new Integer(1));
		String sheetName = (String)getV(new Integer(2));
		if (sheetName==null)
			sheetName = "sheetName";
		HSSFSheet createSheet = wb.createSheet(sheetName);
		ArrayList arrayList = (ArrayList)getV(new Integer(3));
		if(arrayList==null){
			arrayList = new ArrayList<>();
		}

		arrayList.add(createSheet);
		putV(new Integer(3), arrayList); //存放sheet历史
		putV(new Integer(4),createSheet); //存放最新创建的sheet
	}
	//创建sheet
	private static void createSheet(String sheetName) throws Exception{
		putV(new Integer(2), sheetName);
		createSheet();
	}
	//获得最新所创建的sheet
	private static HSSFSheet getSheet(){
		HSSFSheet v = (HSSFSheet)getV(new Integer(4));
		return v;
	}
	
	
	/**   
	 * @Title: exportExcel   
	 * @Description: TODO(作用：创建Excel 并填充数据)   
	 * @param: @param excelPath  excel的全名
	 * @param: @param sheetName  sheet的名字
	 * @param: @param data       要填充的数据，每一条是一个Map
	 * @param: @param en_clomAndcn_clom      二维数组：
	 *                                              一维：存放数据map中的key
	 *                                              二维：存放输出excel后的列名字段
	 * @return: void  
	 * @Auther: cmj
	 * @throws   
	 */  
	public static <T> void exportExcel(String excelPath,String sheetName,List<Map> data,String[][] en_clomAndcn_clom,String title){
		
		try {
			//获得当钱的workbook
			HSSFWorkbook workBook = createWorkBook(excelPath);

			//创建sheet
			createSheet(sheetName);
			
			//填充数据
			fillData(data,en_clomAndcn_clom,title);
			
			//写入文件
			writeFile();
			
			workBook.close();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public static byte[]  exportExcel(String sheetName,List<Map> data,String[][] en_clomAndcn_clom,String title){

		try {
			//获得当钱的workbook
			HSSFWorkbook workBook = createWorkBook("null");

			//创建sheet
			createSheet(sheetName);

			//TODO 设置样式

			//填充数据
			fillData(data,en_clomAndcn_clom,title);

			//写入文件
			byte[] bytes = writeBytes();

			workBook.close();

			return bytes;

		} catch (Exception e) {

			e.printStackTrace();
		}
		return new byte[0];
	}

	/**
	 * 将数据输出到浏览器(下载)
	 * @param response
	 * @param exceFilelName
	 * @param sheetName
	 * @param data
	 * @param en_clomAndcn_clom
	 * @param title
	 */
	public static void  exportExcel2Web(HttpServletResponse response, String exceFilelName, String sheetName, List<Map> data, String[][] en_clomAndcn_clom, String title) throws Exception {
		byte[] bytes = exportExcel(sheetName, data, en_clomAndcn_clom, title);

		ServletOutputStream servletOutputStream = response.getOutputStream();

		response.setCharacterEncoding("UTF-8");

		exceFilelName = URLEncoder.encode(exceFilelName,"UTF-8");//解决中文名称不显示

		response.setHeader("Content-Disposition", "attachment;fileName="+exceFilelName+".xls");

		servletOutputStream.write(bytes);

		servletOutputStream.flush();

		servletOutputStream.close();
	}


	/**
	 * 解析excel数据
	 * @param datas
	 * @return
	 */
	public static SheetData parseExcelFileData(byte[] datas, String sheetName, int rowNameIndex , int rowDataStartIndex,Map<String,String> cnName2EnName) throws Exception {

		ByteArrayInputStream in = new ByteArrayInputStream(datas);

		HSSFWorkbook sheets = new HSSFWorkbook(in);

		return getSheetData(sheetName, rowNameIndex, rowDataStartIndex, cnName2EnName, sheets);
	}

	public static SheetData parseExcelFileData(String in, String sheetName, int rowNameIndex , int rowDataStartIndex,Map<String,String> cnName2EnName) throws Exception {

		FileInputStream inputStream = new FileInputStream(in);
		Workbook workbook ;
		try {
			workbook = new HSSFWorkbook(inputStream);
		}catch (Exception e){
			inputStream.close();
			inputStream = new FileInputStream(in);
			workbook = new XSSFWorkbook(inputStream);
		}

		return getSheetData(sheetName, rowNameIndex, rowDataStartIndex, cnName2EnName, workbook);
	}

	/**
	 * 获取sheetData
	 * @param sheetName
	 * @param rowNameIndex
	 * @param rowDataStartIndex
	 * @param cnName2EnName
	 * @param sheets
	 * @return
	 */
	private static SheetData getSheetData(String sheetName, int rowNameIndex,
										  int rowDataStartIndex, Map<String, String> cnName2EnName,
										  Workbook sheets) throws Exception {

		Sheet sheet = sheets.getSheet(sheetName);
		final Map<String, List<MediaData>> pictures = new HashMap<>();
		if(sheet instanceof HSSFSheet){
			pictures.putAll(getPictures((HSSFSheet) sheet));
		}else if(sheet instanceof XSSFSheet){
			pictures.putAll(getPictures((XSSFSheet) sheet));
		}

		Iterator<Row> iterator = sheet.iterator(); //获取行迭代器

		HashMap<String, String> cloNameIndexMappingCloVal = new HashMap<>();//key记录cloName的索引,v 记录英文名称

		SheetData sheetData = new SheetData();

		int rowIndex = 0;//记录行的索引
		while (iterator.hasNext()) {
			Row next = iterator.next();
			Iterator<Cell> cellIte = next.cellIterator();
			if (rowIndex == rowNameIndex) { //列名称
				processCells(cellIte, (i, cell) -> {
					String cloName = "";
					try {
						cloName = cell.getStringCellValue(); //列名称
					}catch (Exception e){
						cloName = BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString()+"";
					}
					String cloEnName = cnName2EnName.get(cloName);//英文列名称
					if (StringUtils.isNotBlank(cloEnName)) {
						cloNameIndexMappingCloVal.put(i + "", cloEnName);//记录
					}
				});
			} else if (rowIndex >= rowDataStartIndex) { //列开始的索引
				HashMap<String, Object> enCloNameMappingCloVal = new HashMap<>();//英文名称对应的值
				int finalRowIndex = rowIndex;
				processCells(cellIte, (idx, cell) -> {
					int columnIndex = cell.getColumnIndex();
					for(;columnIndex > idx.intValue();){
						//填充空数据. 因为cell会有缺少。当cell从未编辑过并且不存在任何值.
						String cloEnName = cloNameIndexMappingCloVal.get(idx.getAndIncrement() + "");//获取英文名称
						if (StringUtils.isNotBlank(cloEnName)) {
							enCloNameMappingCloVal.put(cloEnName, "");
						}
					}
					Object cloValue = "";
					try {
						CellType cellTypeEnum = cell.getCellTypeEnum();
						if(cellTypeEnum != CellType.BLANK){
							cloValue = cell.getStringCellValue();
						}else{
							String key = finalRowIndex+"-"+idx;
							cloValue = pictures.get(key);
						}

					}catch (Exception e){
						if(HSSFDateUtil.isCellDateFormatted(cell)){
							Date date = cell.getDateCellValue();
							cloValue = DateFormatUtils.format(date, "yyyy-MM-dd");
						}else {
							cloValue = BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString()+"";
						}
					}

					String cloEnName = cloNameIndexMappingCloVal.get(columnIndex + "");//获取英文名称
					if (StringUtils.isNotBlank(cloEnName)) {
						enCloNameMappingCloVal.put(cloEnName, cloValue);
					}
				});
				sheetData.addRowData(enCloNameMappingCloVal);
			}
			rowIndex++;

		}

		return sheetData;
	}

	/**
	 * 获取图片和位置 (xls)
	 * @param sheet
	 * @return
	 * @throws IOException
	 */
	public static Map<String, List<MediaData>> getPictures (HSSFSheet sheet) throws IOException {
		Map<String, List<MediaData>> map = new HashMap();
		List<HSSFShape> list = sheet.getDrawingPatriarch().getChildren();
		for (HSSFShape shape : list) {
			if (shape instanceof HSSFPicture) {
				HSSFPicture picture = (HSSFPicture) shape;
				HSSFClientAnchor cAnchor = picture.getClientAnchor();
				HSSFPictureData pdata = picture.getPictureData();
				String key = cAnchor.getRow1() + "-" + cAnchor.getCol1(); // 行号-列号
				MediaData mediaData = MediaData.builder()
						.data(pdata.getData())
						.format(pdata.getFormat())
						.mimeType(pdata.getMimeType())
						.pictureType(pdata.getPictureType())
						.suggestFileExtension(pdata.suggestFileExtension()).build();
				List<MediaData> mediaDataList = map.get(key);
				if(mediaDataList == null){
					mediaDataList = new ArrayList<>();
					map.put(key, mediaDataList);
				}
				mediaDataList.add(mediaData);
			}
		}
		return map;
	}

	/**
	 * 获取图片和位置 (xls)
	 * @param sheet
	 * @return
	 * @throws IOException
	 */
	public static Map<String, List<MediaData>> getPictures (XSSFSheet sheet) throws Exception {
		Map<String, List<MediaData>> map = new HashMap();
		List<XSSFShape> list = sheet.getDrawingPatriarch().getShapes();
		for (XSSFShape shape : list) {
			if (shape instanceof XSSFPicture) {
				XSSFPicture picture = (XSSFPicture) shape;
				XSSFClientAnchor cAnchor = picture.getClientAnchor();
				XSSFPictureData pdata = picture.getPictureData();
				String key = cAnchor.getRow1() + "-" + cAnchor.getCol1(); // 行号-列号
				MediaData mediaData = MediaData.builder()
						.data(pdata.getData())
						.format(-1)
						.mimeType(pdata.getMimeType())
						.pictureType(pdata.getPictureType())
						.suggestFileExtension(pdata.suggestFileExtension()).build();
				List<MediaData> mediaDataList = map.get(key);
				if(mediaDataList == null){
					mediaDataList = new ArrayList<>();
					map.put(key, mediaDataList);
				}
				mediaDataList.add(mediaData);

			}else if(shape instanceof XSSFObjectData){
				XSSFObjectData objectData = (XSSFObjectData) shape;
				int row = ((XSSFClientAnchor) objectData.getAnchor()).getRow2();
				int col = ((XSSFClientAnchor) objectData.getAnchor()).getCol2();
				String key = row + "-" + col; // 行号-列号

				if(objectData.getFileName().contains("bin")) {
					// .bin文件
					InputStream embeddedStream = objectData.getObjectPart().getInputStream();
					POIFSFileSystem fs = new POIFSFileSystem(embeddedStream);
					Ole10Native ole10 = Ole10Native.createFromEmbeddedOleObject(fs.getRoot());
					// 文件名称
					String fileName = ole10.getLabel();
					// 后缀名
					String suffix = fileName.substring(fileName.lastIndexOf('.') + 1);
					// 字节
					byte[] bytes = ole10.getDataBuffer();
					MediaData mediaData = MediaData.builder()
							.data(bytes)
							.format(-1)
							.mimeType("")
							.pictureType(-1)
							.suggestFileExtension(suffix).build();
					List<MediaData> mediaDataList = map.get(key);
					if(mediaDataList == null){
						mediaDataList = new ArrayList<>();
						map.put(key, mediaDataList);
					}
					mediaDataList.add(mediaData);
				}
			}
		}
		return map;
	}

	private static void processCells(Iterator<Cell> cellIte, FunctionCell<Cell> fc) {
		AtomicInteger cellIndex = new AtomicInteger(0);//记录cell的索引
		while (cellIte != null && cellIte.hasNext()) {
			Cell next = cellIte.next();
			fc.apply(cellIndex, next);
			cellIndex.getAndIncrement();
		}
	}

	/**
	 * @throws Exception    
	 * @Title: writeFile   
	 * @Description: TODO(作用：写入文件)   
	 * @param:       
	 * @return: void  
	 * @Auther: cmj
	 * @throws   
	 */  
	private static void writeFile() throws Exception {
	    HSSFWorkbook booking = getBooking();
	    booking.write(getBookFile());
	}

	private static byte[] writeBytes() throws Exception {
		HSSFWorkbook booking = getBooking();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		booking.write(stream);
		byte[] bytes = stream.toByteArray();
		return bytes;
	}


	/**   
	 * @Title: fillData   
	 * @Description: TODO(作用：填充数据)   
	 * @param: @param data
	 * @param: @param en_clomAndcn_clom      
	 * @return: void  
	 * @Auther: cmj
	 * @throws   
	 */  
	private static void fillData(List<Map> data, String[][] en_clomAndcn_clom,String title) {

		int length = en_clomAndcn_clom[1].length;//获取长度
		
		String[] en_clom =  en_clomAndcn_clom[0];  //英文
		
		String[] cn_clom =  en_clomAndcn_clom[1];  //中文
		
	    //获得当前所创建的sheet
		HSSFSheet sheet = getSheet();
		
		createTitle(title, length, sheet); //创建 title
		
		//创建 列名
		createClom(cn_clom, sheet);
		
		//真实填充数据
		fillData(data, en_clom, sheet);

	}

	/**   
	 * @Title: fillData   
	 * @Description: TODO(作用：填充cell数据)
	 * @param: @param data
	 * @param: @param length
	 * @param: @param en_clom
	 * @param: @param sheet      
	 * @return: void  
	 * @Auther: cmj
	 * @throws   
	 */  
	private static void fillData(List<Map> data, String[] en_clom,
			HSSFSheet sheet) {
		HSSFCellStyle RowCellStyle = sheet.getWorkbook().createCellStyle(); //HSSFCellStyle 不能连续创建超过4030，这里使用单例重用
		HSSFCellStyle dataCellStyle = sheet.getWorkbook().createCellStyle();
		for(int i=0; i<data.size();  i++){  //第一行默认是  title  第二行是字段名，所以要从第3行开始 对应的索引是2
			HSSFRow data_row = sheet.createRow(i+2);
			getDataRow(data_row,RowCellStyle);
			//创建cell
			Map map = data.get(i);
			for(int j=0;j<en_clom.length;j++){
				String key = en_clom[j];
				String value = (String)map.get(key);
				HSSFCell cell = data_row.createCell(j);
				getDataCell(cell,dataCellStyle,j);
				cell.setCellValue(value);
			}
		}
	}

	public static void processDataRow(FunctionComponent2<HSSFRow,CellStyle> titleRow)  {
		putV(new Integer(8),titleRow);
	}

	private static void getDataRow(HSSFRow titleRow,CellStyle cellStyle)  {
		FunctionComponent2<HSSFRow,CellStyle> cell = (FunctionComponent2)getV(new Integer(8));
		if(cell!=null){
			cell.apply(titleRow,cellStyle);
		}

	}

	public static void processDataCell(FunctionComponent3<HSSFCell,CellStyle,Integer> titleCell)  {
		putV(new Integer(9),titleCell);
	}

	public static void getDataCell(HSSFCell titleCell,CellStyle cellStyle,Integer cellIndex)  {
		FunctionComponent3<HSSFCell,CellStyle,Integer> v = (FunctionComponent3<HSSFCell,CellStyle,Integer>)getV(new Integer(9));
		if(v!=null){
			v.apply(titleCell,cellStyle,cellIndex);
		}

	}


	/**   
	 * @Title: createClom   
	 * @Description: TODO(作用：)   
	 * @param: @param cn_clom
	 * @param: @param sheet      
	 * @return: void  
	 * @Auther: cmj
	 * @throws   
	 */  
	private static void createClom(String[] cn_clom, HSSFSheet sheet) {
		HSSFRow data_row = sheet.createRow(1);
		for(int i=0;i<cn_clom.length;i++){
			HSSFCell clom_cell = data_row.createCell(i);
			clom_cell.setCellValue(cn_clom[i]);
		}
	}

	/**   
	 * @Title: createTitle   
	 * @Description: TODO(作用：创建 title )   
	 * @param: @param title
	 * @param: @param length
	 * @param: @param sheet      
	 * @return: void  
	 * @Auther: cmj
	 * @throws   
	 */  
	private static void createTitle(String title, int length, HSSFSheet sheet) {

		CellRangeAddress region1 = new CellRangeAddress(0,0,(short)0,(short)length-1);
		
		HSSFRow title_row = sheet.createRow(0);

		title_row.setHeight((short) 250);

		HSSFCell title_cell = title_row.createCell(0);

		HSSFWorkbook workbook = sheet.getWorkbook();

		HSSFCellStyle cellStyle = workbook.createCellStyle();

		title_cell.setCellValue(title);

		cellStyle.setAlignment(HorizontalAlignment.CENTER);

		title_cell.setCellStyle(cellStyle);

		//处理自定义的样式
		getTitleRow(title_row,workbook.createCellStyle()); //处理title行的样式
		getTitleCell(title_cell,workbook.createCellStyle());//处理titleCell 的样式

		sheet.addMergedRegion(region1);
	}

	public static void processTitleRow(FunctionComponent2<HSSFRow,CellStyle> titleRow)  {
		putV(new Integer(6),titleRow);
	}

	private static void getTitleRow(HSSFRow titleRow,CellStyle cellStyle)  {
		FunctionComponent2<HSSFRow,CellStyle> cell = (FunctionComponent2)getV(new Integer(6));
		if(cell!=null){
			cell.apply(titleRow,cellStyle);
		}

	}

	public static void processTitleCell(FunctionComponent2<HSSFCell,CellStyle> titleCell)  {
		putV(new Integer(7),titleCell);
	}

	public static void getTitleCell(HSSFCell titleCell,CellStyle cellStyle)  {
		FunctionComponent2<HSSFCell,CellStyle> v = (FunctionComponent2<HSSFCell,CellStyle>)getV(new Integer(7));
		if(v!=null){
			v.apply(titleCell,cellStyle);
		}
	}

	@FunctionalInterface
	public interface FunctionComponent<T>{
		void apply(T t);
	}
	@FunctionalInterface
	public interface FunctionComponent2<T,S>{
		void apply(T t, S s);
	}
	@FunctionalInterface
	public interface FunctionComponent3<T,S,I>{
		void apply(T t, S s, I i);
	}
	/**
	 * @Title: getV
	 * @Description: TODO(作用：获得当前实例中的key值
	 *                        k1: workbook
	 *                        k2:sheetname
	 *                        k3: List<sheet> 对象,存放sheet历史 ，如果不存在则返回 isnull
	 *                        k4: 存放最新创建的sheet
	 *                        k5: 获取path
	 *                        k6: 获取 processTitleRow
	 *                        k7: 获取 processTitleCell
	 *                        k8: 获取 processDataRow
	 *                        k9: 获取 processDataCell
	 *
	 * @param: @param key
	 * @param: @return
	 * @return: Object
	 * @Auther: cmj
	 * @throws
	 */
	private static Object getV(Object key){
		Object object = getInstance().getPois_map().get(key);
		if(object==null)
			return isnull;
		return object;
	}

	/**
	 * @throws Exception
	 * @Title: setV
	 * @Description: TODO(作用：设置v)
	 * @param: @param val
	 * @return: void
	 * @Auther: cmj
	 * @throws
	 */
	private static void setV(Object key,Object val) throws Exception{
		if(key==null||val==null)
			throw new Exception("key and val is null");
		getInstance().getPois_map().put(key, val);
	}

	private static void putV(Object key,Object val){
		try {
			setV(key, val);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * @return the pois_map
	 */
	public Map<Object, Object> getPois_map() {
		return pois_map;
	}

	/**
	 * @Title: clean
	 * @Description: TODO(作用：清空所有设定，如果不清空，他会一直进行保持到当前的线程存活范围内)
	 * @param:
	 * @return: void
	 * @Auther: cmj
	 * @throws
	 */
	public static void clean(){
		pois.remove(); //移除当前线程的POIUtils对象
	}

	//每一个sheet数据封装
	public static class SheetData{
		private String titile;
		private List<Map<String,Object>> rowsData = new ArrayList<>();

		public String getTitile() {
			return titile;
		}

		public void setTitile(String titile) {
			this.titile = titile;
		}

		public List<Map<String, Object>> getRowsData() {
			return rowsData;
		}

		public void setRowsData(List<Map<String, Object>> rowsData) {
			this.rowsData = rowsData;
		}

		public void addRowData(Map<String,Object> rowData_){
			rowsData.add(rowData_);
		}
	}

	@FunctionalInterface
	public interface FunctionCell<T>{
		void apply(AtomicInteger index, T t);
	}
}