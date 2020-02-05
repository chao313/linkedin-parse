package demo.spring.boot.demospringboot.controller.generate;

import demo.spring.boot.demospringboot.framework.Code;
import demo.spring.boot.demospringboot.framework.Response;
import demo.spring.boot.demospringboot.parse.mysql.parse.db.GenerateFile;
import demo.spring.boot.demospringboot.parse.mysql.parse.vo.JavaTable;
import demo.spring.boot.demospringboot.parse.mysql.parse.vo.mysql.ftl.AllFtl;
import demo.spring.boot.demospringboot.util.ZipUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.Date;

/**
 * 2018/4/6    Created by   juan
 */

@RestController
@RequestMapping(value = "/generate")
public class GenerateController {

    private static Logger LOGGER =
            LoggerFactory.getLogger(GenerateController.class);

    public static final String demoMasterBasePackage = "demoMaster";//这里是作为package存在的
    public static final String demoMasterDirPath = "demoMaster/";//这里是作为package存在的

    private static final String tmpPath = "tmp/";


    @ApiOperation(value = "生成code", notes = "生成code:需要提供" +
            "<br>1.数据库名称" +
            "<br>2.表务名" +
            "<br>3.包名称")
    @GetMapping("/generateFile")
    @Deprecated
    public Response<JavaTable> GenerateFile(@ApiParam(value = "数据库名称", required = true) @RequestParam(value = "dataBase") String dataBase,
                                            @ApiParam(value = "表务名", required = true) @RequestParam(value = "ptableName") String ptableName,
                                            @ApiParam(value = "包名称", required = true) @RequestParam(value = "basePackage") String basePackage) {
        Response<JavaTable> response = new Response<>();
        try {
            JavaTable javaTable = GenerateFile.GenerateFile(dataBase, ptableName, basePackage);
            response.setCode(Code.System.OK);
            response.setContent(javaTable);
        } catch (Exception e) {
            response.setCode(Code.System.FAIL);
            response.setMsg(e.getMessage());
            response.addException(e);
            LOGGER.error("异常 ：{} ", e.getMessage(), e);
        }
        return response;

    }

    @ApiOperation(value = "生成code", notes = "生成code:需要提供" +
            "<br>1.数据库名称" +
            "<br>2.表务名" +
            "<br>3.包名称")
    @GetMapping("/generateFileV2")
    public Response<AllFtl> GenerateFileV2(@ApiParam(value = "数据库名称", required = true) @RequestParam(value = "dataBase") String dataBase,
                                           @ApiParam(value = "表务名", required = true) @RequestParam(value = "ptableName") String ptableName,
                                           @ApiParam(value = "包名称", required = true) @RequestParam(value = "basePackage") String basePackage) {
        Response<AllFtl> response = new Response<>();
        try {
            AllFtl allFtl = GenerateFile.GenerateFileV2(dataBase, ptableName, basePackage);
            response.setCode(Code.System.OK);
            response.setContent(allFtl);
        } catch (Exception e) {
            response.setCode(Code.System.FAIL);
            response.setMsg(e.getMessage());
            response.addException(e);
            LOGGER.error("异常 ：{} ", e.getMessage(), e);
        }
        return response;

    }


    @GetMapping("/download")
    @Deprecated
    public ResponseEntity<byte[]> fileDownLoad(@RequestParam(value = "dataBase") String dataBase,
                                               @RequestParam(value = "ptableName") String ptableName,
                                               @RequestParam(value = "basePackage") String basePackage) throws Exception {

        JavaTable javaTable = GenerateFile.GenerateFile(dataBase, ptableName, basePackage);
        String dirPath = javaTable.getBasePackagePath().indexOf("/") > 0 ? javaTable.getBasePackagePath().substring(0,
                javaTable.getBasePackagePath().indexOf("/")) : javaTable.getBasePackagePath();
        String fileNameZip = dirPath + ".zip";
        InputStream inputStream = ZipUtils.createFilesAndZip(javaTable, fileNameZip, dirPath);

        byte[] body = null;
        body = new byte[inputStream.available()];
        inputStream.read(body);//读入到输入流里面

        HttpHeaders headers = new HttpHeaders();//设置响应头
        headers.add("Content-Disposition", "attachment;filename=" + fileNameZip);//下载的文件名称
        HttpStatus statusCode = HttpStatus.OK;//设置响应吗
        ResponseEntity<byte[]> response = new ResponseEntity<>(body, headers, statusCode);
        return response;
    }

    /**
     * 注意->这里的下载目前通过swagger-ui的url是不行的，但是通过url直接访问是可以的
     *
     * @param dataBase
     * @param ptableName
     * @param basePackage
     * @return
     * @throws Exception
     */
    @GetMapping("/downloadV2")
    public ResponseEntity<byte[]> fileDownLoadV2(@RequestParam(value = "dataBase") String dataBase,
                                                 @RequestParam(value = "ptableName") String ptableName,
                                                 @RequestParam(value = "basePackage") String basePackage) throws Exception {

        AllFtl allFtl = GenerateFile.GenerateFileV2(dataBase, ptableName, basePackage);
        String fileNameZip = new Date().getTime() + ".zip";
        byte[] body = ZipUtils.createFilesAndZipV2(allFtl, fileNameZip);

        HttpHeaders headers = new HttpHeaders();//设置响应头
        headers.add("Content-Disposition", "attachment;filename=" + fileNameZip);//下载的文件名称
        HttpStatus statusCode = HttpStatus.OK;//设置响应吗
        ResponseEntity<byte[]> response = new ResponseEntity<>(body, headers, statusCode);
        return response;
    }


    /**
     * 注意->这里的下载目前通过swagger-ui的url是不行的，但是通过url直接访问是可以的
     *
     * @param dataBase
     * @param ptableName
     * @return
     * @throws Exception
     */
    @GetMapping("/downloadMavenDemoMaster")
    public ResponseEntity<byte[]> downloadMavenDemoMaster(@RequestParam(value = "dataBase") String dataBase,
                                                          @RequestParam(value = "ptableName") String ptableName) throws Exception {

        /**
         *
         */
        String basePackage = demoMasterBasePackage;
        AllFtl allFtl = GenerateFile.GenerateFileV2(dataBase, ptableName, basePackage);
        String operateDir = String.valueOf(new Date().getTime());
        String fileNameZip = operateDir + ".zip";
        byte[] body = ZipUtils.createFilesAndZipMavenDemoMaster(allFtl, fileNameZip, operateDir);

        HttpHeaders headers = new HttpHeaders();//设置响应头
        headers.add("Content-Disposition", "attachment;filename=" + fileNameZip);//下载的文件名称
        HttpStatus statusCode = HttpStatus.OK;//设置响应吗
        ResponseEntity<byte[]> response = new ResponseEntity<>(body, headers, statusCode);
        return response;
    }


//    /**
//     * 返回的是压缩后的文件流,
//     *
//     * @param javaTable
//     * @param zipFileName 文件地址
//     * @return
//     * @throws IOException
//     */
//    private InputStream createFilesAndZip(JavaTable javaTable, String zipFileName, String dirPath) throws IOException {
//        BufferedOutputStream voOutputStream = null;
//        BufferedOutputStream daoOutputStream = null;
//        BufferedOutputStream serviceOutputStream = null;
//        BufferedOutputStream serviceImplOutputStream = null;
//        BufferedOutputStream mapperFileOutputStream = null;
//
//        //创建文件夹
//        mysql File(tmpPath + javaTable.getBasePackagePath()).mkdirs();
//
//        File voFile = mysql File(tmpPath + javaTable.getClassVoPath());
//        voFile.createNewFile();
//        voOutputStream = mysql BufferedOutputStream(mysql FileOutputStream(voFile));
//        voOutputStream.write(javaTable.getClassVoStr().getBytes());
//        voOutputStream.flush();
//
//        File daoFile = mysql File(tmpPath + javaTable.getClassDaoPath());
//        daoFile.createNewFile();
//        daoOutputStream = mysql BufferedOutputStream(mysql FileOutputStream(daoFile));
//        daoOutputStream.write(javaTable.getClassDaoStr().getBytes());
//        daoOutputStream.flush();
//
//        File serviceFile = mysql File(tmpPath + javaTable.getClassServicePath());
//        serviceFile.createNewFile();
//        serviceOutputStream = mysql BufferedOutputStream(mysql FileOutputStream(serviceFile));
//        serviceOutputStream.write(javaTable.getClassServiceStr().getBytes());
//        serviceOutputStream.flush();
//
//        File serviceImplFile = mysql File(tmpPath + javaTable.getClassServiceImplPath());
//        serviceFile.createNewFile();
//        serviceImplOutputStream = mysql BufferedOutputStream(mysql FileOutputStream(serviceImplFile));
//        serviceImplOutputStream.write(javaTable.getClassServiceImplStr().getBytes());
//        serviceImplOutputStream.flush();
//
//        File mapperFile = mysql File(tmpPath + javaTable.getMapperPath());
//        mapperFile.createNewFile();
//        mapperFileOutputStream = mysql BufferedOutputStream(mysql FileOutputStream(mapperFile));
//        mapperFileOutputStream.write(javaTable.getMapperStr().getBytes());
//        mapperFileOutputStream.flush();
//
//        voOutputStream.close();
//        daoOutputStream.close();
//        serviceOutputStream.close();
//        serviceImplOutputStream.close();
//        mapperFileOutputStream.close();
//
//
//        File file = mysql File(tmpPath + zipFileName);
//        OutputStream outputStream = mysql FileOutputStream(file);
//        ZipUtils.toZip(tmpPath + dirPath, outputStream, true);
//        outputStream.flush();
//        outputStream.close();
//
//        InputStream inputStream = mysql FileInputStream(tmpPath + zipFileName);
//
//
//        voFile.delete();
//        daoFile.delete();
//        serviceFile.delete();
//        serviceImplFile.delete();
//        mapperFile.delete();
//        FileUtils.deleteDirectory(tmpPath + dirPath);
//        file.delete();
//
//        return inputStream;
//    }


}
